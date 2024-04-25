package trackModel;

import CTCOffice.CTCOfficeImpl;
import Common.TrackModel;
import Common.TrainModel;
import Framework.Simulation.WaysideSystem;
import Framework.Support.ObservableHashMap;
import Utilities.HelperObjects.BasicTrackLine;
import Utilities.BeaconParser;
import Utilities.Enums.Lines;
import Utilities.BasicBlockParser;
import Utilities.HelperObjects.TrackBlockLine;
import Utilities.Records.Beacon;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainModel.TrainModelImpl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.concurrent.*;
import java.util.function.Supplier;


import static Utilities.Constants.MAX_PASSENGERS;

public class TrackLine implements TrackModel {

    private static final Logger logger = LoggerFactory.getLogger(TrackLine.class.getName());

    Lines line;

    private final ThreadLocal<TrackLine> thisLineThread = new ThreadLocal<>();
    ExecutorService trackUpdateExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //map of dynamic Track Blocks
    private final TrackBlockLine mainTrackLine = new TrackBlockLine();

    //Object Lookups
    private final ConcurrentHashMap<Integer, Beacon> beaconBlocks = new ConcurrentHashMap<>();
    private final LinkedHashSet<Integer> lightBlocks = new LinkedHashSet<>();

    //Occupancy Map
    private final ObservableHashMap<TrainModel, Integer> trackOccupancyMap;

    //Task Queue
    private final ConcurrentLinkedQueue<Callable<Object>> trackUpdateQueue = new ConcurrentLinkedQueue<>();

    private long time = 0;

    private final BeaconParser beaconParser = BeaconParser.getInstance();

    TrackBlockSubject subject;
    //    ObservableList<TrackBlockSubject> subjectList = FXCollections.observableArrayList();
    ObservableList<TrackBlockSubject> subjectList;

    private int ticketSales = 0;
    public  int outsideTemperature = 40;
    BasicBlockParser allTracks = BasicBlockParser.getInstance();

    public TrackLine(Lines line) {
        this.line = line;
        if(allTracks.containsLine(line)) {
            BasicTrackLine basicBlocks = allTracks.getBasicLine(line);
            trackOccupancyMap = new ObservableHashMap<>(basicBlocks.size());

            //keeps track of which blocks are occupied

            ArrayList<Integer> blockIndices = new ArrayList<>(basicBlocks.keySet());

            for (Integer blockIndex : blockIndices) {
                TrackBlock block = new TrackBlock(basicBlocks.get(blockIndex));
                mainTrackLine.put(block.blockID, block);
                if (block.isLight) {
                    lightBlocks.add(block.blockID);
                }
                TrackBlockSubject subject = new TrackBlockSubject(line, block);
                LineSubjectMap.addLineSubject(line, subject);
            }

            //Needs more testing, but the beacon parser seems to work.
            beaconBlocks.putAll(beaconParser.getBeaconLine(line));
            setupListeners();

            subjectList = LineSubjectMap.getLineSubject(line);
        }else{
            this.trackOccupancyMap = new ObservableHashMap<>(0);
        }

        newTemperature();
    }

    public TrackLine() {
        this(Lines.NULL);
    }

    public void update() {
        thisLineThread.set(this);
        // Execute all pending track update tasks
        for (Callable<Object> task : trackUpdateQueue) {
            try {
                task.call();
            } catch (Exception e) {
                logger.error("Track update task failed", e);
            }
        }
        trackUpdateQueue.clear();
    }





    public TrainModel trainDispatch(int trainID) {
        TrainModel train = new TrainModelImpl(this, trainID);
        Integer alreadyPlacedID = trackOccupancyMap.putIfAbsent(train, 0);
        if(alreadyPlacedID != null) {
            logger.error("Train {} already exists and was not dispatched.", trainID);
            return null;
        }
        return train;
    }

    //Note: Train could be on different Line
    public void trainDispatch(TrainModel train) {
        Integer alreadyPlacedID = trackOccupancyMap.putIfAbsent(train, 0);
        if(alreadyPlacedID != null) {
            logger.error("Train {} already exists and was not dispatched.", train.getTrainNumber());
        }
    }

    public int trainCount() {
        return trackOccupancyMap.size();
    }

    /**
     * A function called by the train when it has travelled
     * the distance of the block it is currently on.
     * Updates the location of a train on the track
     * @return the block the train is moving to
     */
    public TrackBlock updateTrainLocation(TrainModel train) {
        Integer currentBlockID = trackOccupancyMap.getOrDefault(train, -2);
        if (currentBlockID == -1 || currentBlockID == -2) {
            logger.error("![ TrainModel {}:  {} -> _ ] deleted train." , train.getTrainNumber(), currentBlockID);
            if(train != null) {
                trackOccupancyMap.remove(train);
            }
            return null;
        }
        Integer nextBlockID = mainTrackLine.get(currentBlockID).getNextBlock(train.getDirection());

        if(nextBlockID == -1) {
            logger.error("![ TrainModel {}:  {} -> {} ], deleted train." , train.getTrainNumber(), currentBlockID, nextBlockID);
            trackOccupancyMap.remove(train);
            return null;
        }

        logger.info("TrainModel {}:  {} -> {}  ", train.getTrainNumber(), currentBlockID , nextBlockID);

        if(nextBlockID > currentBlockID) {
            Platform.runLater(() -> subjectList.get(currentBlockID).setDirection(false));
        } else {
            Platform.runLater(() -> subjectList.get(currentBlockID).setDirection(true));
        }

        asyncTrackUpdate(() -> {
            trackOccupancyMap.replace(train, currentBlockID, nextBlockID);
            return null;
        });
        return mainTrackLine.get(nextBlockID);
    }


    private void handleTrainEntry(TrainModel train, Integer newBlockID, Integer oldBlockID) {

        if (newBlockID == 0 && oldBlockID != 0) {
            logger.info("Train {} exited the track", train.getTrainNumber());
            trackOccupancyMap.remove(train);
        }else if(newBlockID == 0) {
            logger.info("Train {} => Entry", train.getTrainNumber());
        }

        asyncTrackUpdate(() -> {
            setUnoccupied(oldBlockID);
            setOccuppied(train, newBlockID);

            WaysideSystem.getController(this.line,newBlockID).trackModelSetOccupancy(newBlockID, true);
            WaysideSystem.getController(this.line,oldBlockID).trackModelSetOccupancy(oldBlockID, false);
            return null;
        });

        if(beaconBlocks.containsKey(newBlockID)) {
            train.passBeacon(beaconBlocks.get(newBlockID));
            logger.info("Beacon {}: => T{}", train.getTrainNumber(), newBlockID);
        }
    }

    private void handleTrainExit(TrainModel train, Integer blockID) {
        if(blockID == 0) {
            logger.info("  Registered T{} exit at {}", train.getTrainNumber(), blockID);
        }else {
            logger.error(" T{} was removed from occupancy map at block {}", train.getTrainNumber(), blockID);
        }

        setUnoccupied(blockID);
        CTCOfficeImpl.OFFICE.notifyTrainReturn(train.getTrainNumber());
        train.delete();
    }

    private void setOccuppied(TrainModel train, int blockID){
        mainTrackLine.get(blockID).addOccupation(train);
        Platform.runLater(() -> subjectList.get(blockID).setIsOccupied(true));
    }

    private void setUnoccupied(int blockID){
        mainTrackLine.get(blockID).removeOccupation();
        Platform.runLater(() -> subjectList.get(blockID).setIsOccupied(false));
    }

    private <T> CompletableFuture<T> asyncTrackUpdate(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, trackUpdateExecutor);
    }

    private void queueTrackUpdate(Runnable task) {
        Callable<Object> callableTask = Executors.callable(task);
        trackUpdateQueue.add(callableTask);
    }

//--------------------------Getters and Setters--------------------------

    public TrackBlockSubject getSubject(int block) {
        return this.subjectList.get(block);
    }

    //------------------From interface------------------

    //light blocks will be wherever there is a beacon
    //false red, true is green
    @Override
    public void setLightState(int block, boolean state) {
        logger.info("Setting light state to: {} at block: {}", state, block);
        asyncTrackUpdate( () -> {
            logger.info("Setting light state to: {} at block: {} in async", state, block);
            if(lightBlocks.contains(block)) {
                mainTrackLine.get(block).setLightState(state);

                Platform.runLater(() -> subjectList.get(block).setSignalState(state ? "GREEN" : "RED"));

            } else {
                logger.error("Block {} does not have a light", block);
            }
            return null;
        });
    }

    @Override
    public void setSwitchState(int block, boolean state) {
        asyncTrackUpdate( () -> {
            if (mainTrackLine.get(block).feature.isSwitch()) {
                mainTrackLine.get(block).setSwitchState(state);

                if (state) {
                    Platform.runLater(() -> subjectList.get(block).setSwitchState("ALTERNATE"));
                } else {
                    Platform.runLater(() -> subjectList.get(block).setSwitchState("MAIN"));
                }

                logger.info("Switch set to: {} at block: {}", state, block);
            } else {
                logger.warn("Block {} is not a switch", block);
            }
            return null;
        });
    }

    @Override
    public void setCrossing(int block, boolean state) {
        queueTrackUpdate(() -> {
            if (mainTrackLine.get(block).feature.isCrossing()) {
                mainTrackLine.get(block).setCrossingState(state);

                if (state) {
                    Platform.runLater(() -> subjectList.get(block).setCrossingState("DOWN"));
                } else {
                    Platform.runLater(() -> subjectList.get(block).setCrossingState("UP"));
                }
                logger.info("Crossing set to: {} at block: {}", state, block);
            } else {
                logger.warn("Block {} is not a crossing", block);
            }
        });
    }

    @Override
    synchronized public void setTrainAuthority(Integer blockID, int authority){
        try {
            asyncTrackUpdate( () -> {
                mainTrackLine.get(blockID).setAuthority(authority);
                if(authority != -3) {
                    logger.info("Authority => {} at block: {}", authority, blockID);
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    synchronized public void setCommandedSpeed(Integer blockID, double commandedSpeed) {
        try {
            asyncTrackUpdate(() -> {
                mainTrackLine.get(blockID).setCommandSpeed(commandedSpeed);
                logger.info("Command speed => {} MPH at block: {}", commandedSpeed, blockID);
                return null;
            }).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setBrokenRail(Integer blockID, boolean state) {
        logger.info("Broken Rail called on Block: {} with state: {}", blockID, state);
        queueTrackUpdate( () -> {
            TrackBlock brokenBlock = this.mainTrackLine.get(blockID);
            if (brokenBlock != null) {
                brokenBlock.setRailFailure(true);
                this.setBrokenRail(blockID, state);
            } else {
                logger.error("Broken Rail called on Block: {} does not exist", blockID);
            }

            TrackBlockSubject subject = this.subjectList.get(blockID);
            if(subject != null) {
                Platform.runLater(() -> subject.setBrokenRail(state));
                Platform.runLater(() -> subject.setFailure("BROKEN RAIL"));
            } else {
                logger.error("Broken Rail called on Block: {} does not exist", blockID);
            }
        });
    }

    @Override
    public void setPowerFailure(Integer blockID, boolean state) {
        logger.info("Power Failure called on Block: {} with state: {}", blockID, state);
        queueTrackUpdate( () -> {
            TrackBlock failedBlock = this.mainTrackLine.get(blockID);
            this.setPowerFailure(blockID, state);
            if (failedBlock != null) {
                failedBlock.setPowerFailure(true);
            } else {
                logger.error("Power Failure called on Block: {} does not exist", blockID);
            }

            TrackBlockSubject subject = this.subjectList.get(blockID);
            if(subject != null) {
                Platform.runLater(() -> subject.setPowerFailure(state));
                Platform.runLater(() -> subject.setFailure("POWER FAILURE"));
            } else {
                logger.error("Power Failure called on Block: {} does not exist", blockID);
            }
        });
    }

    @Override
    public void setTrackCircuitFailure(Integer blockID, boolean state) {
        logger.info("Track Circuit Failure called on Block: {} with state: {}", blockID, state);
        queueTrackUpdate( () -> {
            TrackBlock failedBlock = this.mainTrackLine.get(blockID);
            if (failedBlock != null) {
                failedBlock.setCircuitFailure(true);
                this.setTrackCircuitFailure(blockID, state);
            } else {
                logger.error("Circuit Failure called on Block: {} does not exist", blockID);
            }

            TrackBlockSubject subject = this.subjectList.get(blockID);
            if(subject != null) {
                Platform.runLater(() -> subject.setTrackCircuitFailure(state));
                Platform.runLater(() -> subject.setFailure("TRACK CIRCUIT FAILURE"));
            } else {
                logger.error("Track Circuit Failure called on Block: {} does not exist", blockID);
            }
        });
    }

    @Override
    public void fixTrackFailure(Integer blockID) {
        queueTrackUpdate( () -> {
            TrackBlock failedBlock = this.mainTrackLine.get(blockID);
            if (failedBlock != null) {
                failedBlock.clearFailures();
            } else {
                logger.error("Fix Track Failure called on Block: {} does not exist", blockID);
            }

            TrackBlockSubject subject = this.subjectList.get(blockID);
            if(subject != null) {
                Platform.runLater(() -> subject.setTrackCircuitFailure(false));
                Platform.runLater(() -> subject.setPowerFailure(false));
                Platform.runLater(() -> subject.setBrokenRail(false));
                Platform.runLater(() -> subject.setFailure("NONE"));
            } else {
                logger.error("Fix Track Failure called on Block: {} does not exist", blockID);
            }
        });
    }

    @Override
    public boolean getBrokenRail(Integer blockID) {
        return  this.mainTrackLine.get(blockID).brokenRail;
    }

    @Override
    public boolean getPowerFailure(Integer blockID) {
        return this.mainTrackLine.get(blockID).powerFailure;
    }

    @Override
    public boolean getTrackCircuitFailure(Integer blockID) {
        return this.mainTrackLine.get(blockID).trackCircuitFailure;
    }

    //This operation is being done on the block, not the train
    @Override
    public void disembarkPassengers(TrainModel train, int disembarked) {
        queueTrackUpdate( () -> {
            if(trackOccupancyMap.containsKey(train)){
                TrackBlock block = mainTrackLine.get(trackOccupancyMap.get(train));
                if (block.feature.isStation()) {
                    block.feature.setPassengersDisembarked(disembarked);
                    Platform.runLater(() -> subjectList.get(block.blockID).setPassDisembarked(Integer.toString(disembarked)));

                } else {
                    logger.warn("Train: {} is not on a STATION block", train.getTrainNumber());
                }
            } else {
                logger.warn("Train: {} is not on the track", train.getTrainNumber());
            }
        });
    }

    @Override
    public int embarkPassengers(TrainModel train) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return asyncTrackUpdate(() -> {
            if(trackOccupancyMap.containsKey(train)){
                TrackBlock block = mainTrackLine.get(trackOccupancyMap.get(train));
                if (block.feature.isStation()) {
                    int embarked = random.nextInt(0, MAX_PASSENGERS - train.getPassengerCount());
                    block.feature.setPassengersEmbarked(embarked);
                    Platform.runLater(() -> subjectList.get(block.blockID).setPassEmbarked(Integer.toString(embarked)));
                    this.ticketSales += embarked;
                    return embarked;
                } else {
                    logger.warn("Train: {} is not on a station block", train.getTrainNumber());
                    return 0;
                }
            } else {
                logger.error("Train: {} is not on the track", train.getTrainNumber());
                return 0;
            }
        }).join();
    }

    //every tick is a second to ticket sales will reset every 3600 seconds

    @Override
    public int getTicketSales() {
        if (time % 3600 == 0) {
            Platform.runLater(() -> subjectList.forEach(subject -> subject.setTicketSales(this.ticketSales)));
            this.ticketSales = 0;
            newTemperature();

        }
        return ticketSales;
    }

    @Override
    public Lines getLine() {
        return this.line;
    }

    public void resetTicketSales() {
        this.ticketSales = 0;
    }


    public void newTemperature(){
        queueTrackUpdate(() -> {
            int newTemp = ThreadLocalRandom.current().nextInt(-5, 5);
            this.outsideTemperature += newTemp;
            logger.info("Temperature: {}", this.outsideTemperature);
            Platform.runLater(() -> subjectList.forEach(subject -> subject.setOutsideTemp(this.outsideTemperature)));
            if(outsideTemperature < 40) {
                Platform.runLater(() -> subjectList.forEach(subject -> subject.setTrackHeater("ON")));
            } else {
                Platform.runLater(() -> subjectList.forEach(subject -> subject.setTrackHeater("OFF")));
            }
        });
    }


    private void setupListeners() {
        ObservableHashMap.MapListener<TrainModel, Integer> trackListener = new ObservableHashMap.MapListener<>() {
            public void onAdded(TrainModel train, Integer blockID) {
                // A train enters the track (hopefully from the yard)
                handleTrainEntry(train, blockID, 0);

                Platform.runLater(() -> subjectList.get(blockID).setIsOccupied(true));
            }
            public void onRemoved(TrainModel train, Integer blockID) {
                // A train is removed from the track
                handleTrainExit(train, blockID);

                Platform.runLater(() -> subjectList.get(blockID).setIsOccupied(false));
            }
            public void onUpdated(TrainModel train, Integer oldBlockID, Integer newBlockID) {
                // A train moves from one block to another
                handleTrainEntry(train, newBlockID, oldBlockID);

                Platform.runLater(() -> subjectList.get(oldBlockID).setIsOccupied(false));
                Platform.runLater(() -> subjectList.get(newBlockID).setIsOccupied(true));
            }
        };
        trackOccupancyMap.addChangeListener(trackListener);
    }

    public TrackBlock getBlock(int i) {
        return mainTrackLine.get(i);
    }
}