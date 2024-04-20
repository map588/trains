package trackModel;

import Common.TrackModel;
import Common.TrainModel;
import Framework.Simulation.WaysideSystem;
import Framework.Support.ObservableHashMap;
import Utilities.BasicTrackLine;
import Utilities.BeaconParser;
import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
import Utilities.Records.Beacon;
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

    //Subject Map
    private static final LineSubjectMap trackSubjectMap = LineSubjectMap.getInstance();

    private long time = 0;

    private final BeaconParser beaconParser = new BeaconParser();

    private final TrackLineSubject subject;

    private int ticketSales = 0;
    public  int outsideTemperature = 40;
    GlobalBasicBlockParser allTracks = GlobalBasicBlockParser.getInstance();

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
            }

            //Needs more testing, but the beacon parser seems to work.
            beaconBlocks.putAll(beaconParser.parseBeacons(line));
            this.subject = new TrackLineSubject(this, mainTrackLine);
            trackSubjectMap.getInstance().addLineSubject(line.toString(), subject);
            setupListeners();
        }else{
            this.trackOccupancyMap = new ObservableHashMap<>(0);
            this.subject = new TrackLineSubject(this, new TrackBlockLine());
        }
    }

    public TrackLine() {
        this(Lines.NULL);
    }

    public void update() {
        // Execute all pending track update tasks
        try {
            trackUpdateExecutor.invokeAll(trackUpdateQueue);
        } catch (RejectedExecutionException e) {
            logger.error("Track update task rejected", e);
        } catch (InterruptedException e) {
            logger.error( "Track update task failed", e);
            throw new RuntimeException(e);
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
        Integer alreadyPlacedID = trackOccupancyMap.putIfAbsent(train,0);
        if(alreadyPlacedID != null) {
            logger.error("Train {} already exists and was not dispatched.", train.getTrainNumber());
        }
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

        logger.info("    TrainModel {}:  {} -> {}  ", train.getTrainNumber(), currentBlockID , nextBlockID);

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
             asyncTrackUpdate(() -> {
                 setUnoccupied(oldBlockID);
                 return null;
             });
            return;
        }else if(newBlockID == 0) {
            logger.info("Train {} => Entry", train.getTrainNumber());
        }

         asyncTrackUpdate(() -> {
            setUnoccupied(oldBlockID);
            setOccuppied(train, newBlockID);
            return null;
         });

        logger.info("  Registered T{} : {}  ->  {}  ", train.getTrainNumber(), oldBlockID, newBlockID);

        if(beaconBlocks.containsKey(newBlockID)) {
            train.passBeacon(beaconBlocks.get(newBlockID));
            logger.info("Beacon {}: => T{}", train.getTrainNumber(), newBlockID);
        }
    }

    private void handleTrainExit(TrainModel train, Integer blockID) {
        if(blockID == 0) {
            logger.info("  Registered T{} exit at {}", train.getTrainNumber(), blockID);
            return;
        }

        logger.error(" T{} was removed from occupancy map at block {}", train.getTrainNumber(), blockID);


        setUnoccupied(blockID);
        train.delete();
    }

    private void setOccuppied(TrainModel train, int blockID){
        mainTrackLine.get(blockID).addOccupation(train);
        WaysideSystem.getController(this.line,blockID).trackModelSetOccupancy(blockID, true);
    }

    private void setUnoccupied(int blockID){
        mainTrackLine.get(blockID).removeOccupation();
        WaysideSystem.getController(this.line,blockID).trackModelSetOccupancy(blockID, false);
    }

    private <T> CompletableFuture<T> asyncTrackUpdate(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, trackUpdateExecutor);
    }

    private void queueTrackUpdate(Runnable task) {
        Callable<Object> callableTask = Executors.callable(task);
        trackUpdateQueue.add(callableTask);
    }

//--------------------------Getters and Setters--------------------------

    public TrackLineSubject getSubject() {
        return this.subject;
    }

    //------------------From interface------------------

    //light blocks will be wherever there is a beacon
    //false red, true is green
    @Override
    public void setLightState(int block, boolean state) {
        asyncTrackUpdate( () -> {
            if(lightBlocks.contains(block)) {
                mainTrackLine.get(block).setLightState(state);
                logger.info("Light set to: {} at block: {}", state, block);
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
                logger.info("Authority => {} at block: {}", authority, blockID);
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
       queueTrackUpdate( () -> {
           TrackBlock brokenBlock = this.mainTrackLine.get(blockID);
           if (brokenBlock != null) {
               brokenBlock.setFailure(true, false, false);
           } else {
               logger.error("Broken Rail called on Block: {} does not exist", blockID);
           }
       });
    }

    @Override
    public void setPowerFailure(Integer blockID, boolean state) {
        queueTrackUpdate( () -> {
            TrackBlock failedBlock = this.mainTrackLine.get(blockID);
            if (failedBlock != null) {
                failedBlock.setFailure(false,false,true);
            } else {
                logger.error("Power Failure called on Block: {} does not exist", blockID);
            }
        });
    }

    @Override
    public void setTrackCircuitFailure(Integer blockID, boolean state) {
        queueTrackUpdate( () -> {
            TrackBlock failedBlock = this.mainTrackLine.get(blockID);
            if (failedBlock != null) {
                failedBlock.setFailure(false,true,false);
            } else {
                logger.error("Circuit Failure called on Block: {} does not exist", blockID);
            }
        });
    }

    @Override
    public void fixTrackFailure(Integer blockID) {
        queueTrackUpdate( () -> {
            TrackBlock failedBlock = this.mainTrackLine.get(blockID);
            if (failedBlock != null) {
                failedBlock.setFailure(false,false,false);
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
                    Integer newTotal = train.getPassengerCount() - disembarked;
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
                    return random.nextInt(0, MAX_PASSENGERS - train.getPassengerCount());
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
            subject.setOutsideTemp(newTemp);
            this.outsideTemperature += newTemp;
        });
    }


    private void setupListeners() {
        ObservableHashMap.MapListener<TrainModel, Integer> trackListener = new ObservableHashMap.MapListener<>() {
            public void onAdded(TrainModel train, Integer blockID) {
                // A train enters the track (hopefully from the yard)
                handleTrainEntry(train, blockID, 0);
            }
            public void onRemoved(TrainModel train, Integer blockID) {
                // A train is removed from the track
                handleTrainExit(train, blockID);
            }
            public void onUpdated(TrainModel train, Integer oldBlockID, Integer newBlockID) {
                // A train moves from one block to another
                handleTrainEntry(train, newBlockID, oldBlockID);
            }
        };
        trackOccupancyMap.addChangeListener(trackListener);
    }

}