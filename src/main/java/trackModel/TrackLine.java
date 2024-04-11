package trackModel;

import Common.TrackModel;
import Common.TrainModel;
import Framework.Simulation.WaysideSystem;
import Framework.Support.ObservableHashMap;
import Utilities.BasicTrackLine;
import Utilities.BeaconParser;
import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
import Utilities.Records.BasicBlock;
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
    private final ConcurrentHashMap<Integer, Beacon> beaconBlocks;
    private final LinkedHashSet<Integer> lightBlocks = new LinkedHashSet<>();

    //Occupancy Map
    private final ObservableHashMap<TrainModel, Integer> trackOccupancyMap;

    //Task Queue
    private final ConcurrentLinkedQueue<Callable<Object>> trackUpdateQueue = new ConcurrentLinkedQueue<>();

    private long time = 0;


    private final TrackLineSubject subject;

    private int ticketSales = 0;
    public  int outsideTemperature = 40;

    public TrackLine(Lines line) {

        this.line = line;

        GlobalBasicBlockParser allTracks = GlobalBasicBlockParser.getInstance();

        //maps blocks to block numbers
        BasicTrackLine basicBlocks = allTracks.getBasicLine(line);

        //keeps track of which blocks are occupied
        trackOccupancyMap = new ObservableHashMap<>(basicBlocks.size());
        ArrayList<Integer> blockIndices = new ArrayList<>(basicBlocks.keySet());

        for (Integer blockIndex : blockIndices) {
            TrackBlock block = new TrackBlock(basicBlocks.get(blockIndex));
            mainTrackLine.put(block.blockID, block);
            if(block.isLight) {
                lightBlocks.add(block.blockID);
            }
        }

        //Needs more testing, but the beacon parser seems to work.
        this.beaconBlocks = BeaconParser.parseBeacons(line);
        this.subject = new TrackLineSubject(this, mainTrackLine);

        setupListeners();
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

    public TrackLine() {
        this(Lines.NULL);
    }

    public TrainModel trainDispatch(int trainID) {
        TrainModel train = new TrainModelImpl(this, trainID);
        trackOccupancyMap.put(train,0);
        return train;
    }

    //Note: Train could be on different Line
    public void trainDispatch(TrainModel train) {
        trackOccupancyMap.put(train,0);
    }

    /**
     * Updates the location of a train on the track
     * @return the block the train is moving to
     */
    public TrackBlock updateTrainLocation(TrainModel train) {
        Integer currentBlockID = trackOccupancyMap.getOrDefault(train, -1);
        if (currentBlockID == -1) {
            logger.error("Train {} cannot change location" , train.getTrainNumber());
            return null;
        }
        BasicBlock.Connection next = mainTrackLine.get(currentBlockID).getNextBlock(train.getDirection());
        if (next.directionChange()) {
            train.changeDirection();
        }
        Integer nextBlockID = next.blockNumber();

        System.out.println("T" + train.getTrainNumber() + " " + currentBlockID + " ->  " + nextBlockID);

        queueTrackUpdate(() -> {
            trackOccupancyMap.remove(train);
            trackOccupancyMap.put(train, nextBlockID);
        });

        return mainTrackLine.get(nextBlockID);
    }


    private void handleTrainEntry(TrainModel train, Integer blockID) {
        logger.info("Train {} entered block {}", train.getTrainNumber(), blockID);
         if (mainTrackLine.get(blockID).isOccupied()) {
             logger.warn("Block {} is already occupied by train {} ", blockID, mainTrackLine.get(blockID).getOccupiedBy().getTrainNumber());
         }
            setOccuppied(train, blockID);

            if(beaconBlocks.containsKey(blockID)) {
                train.passBeacon(beaconBlocks.get(blockID));
                logger.info("Train {} received beacon number {}", train.getTrainNumber(), blockID);
            }
    }

    private void handleTrainExit(TrainModel train, Integer blockID) {
        logger.info("Train {} exited block {}", train.getTrainNumber(), blockID);
        if (!mainTrackLine.get(blockID).isOccupied()) {
            logger.warn("Block {} was exited but not occupied", blockID);
        }

        setUnoccupied(blockID);
    }

    private void handleYardEntry(TrainModel train, Integer newBlockID, Integer oldBlockID) {
        if(newBlockID == 0) {
            train.delete();
        }else if(newBlockID.equals(oldBlockID)) {
            logger.warn("Error: False Occupancy Update from {} to {} ", oldBlockID , newBlockID);
        }
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

    //TODO: We don't have light states figured out yet
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

//TODO: Maybe refactor set to pass for clearer communication.

    @Override
    public void setSwitchState(int block, boolean state) {
        queueTrackUpdate( () -> {
            if (mainTrackLine.get(block).feature.isSwitch()) {
                mainTrackLine.get(block).setSwitchState(state);
                logger.info("Switch set to: {} at block: {}", state, block);
            } else {
                logger.warn("Block {} is not a switch", block);
            }
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
    public void setTrainAuthority(Integer blockID, int authority){
        queueTrackUpdate( () -> {
            mainTrackLine.get(blockID).setAuthority(authority);
            logger.info("Authority set to: {} at block: {}", authority, blockID);
        });
    }

    @Override
    public void setCommandedSpeed(Integer blockID, double commandedSpeed) {
        queueTrackUpdate( () -> {
            mainTrackLine.get(blockID).setCommandSpeed(commandedSpeed);
            logger.info("Command speed set to: {} at block: {}", commandedSpeed, blockID);
        });
    }

    @Override
    public void setBrokenRail(Integer blockID, boolean state) {
       queueTrackUpdate( () -> {
            TrackBlock brokenBlock = this.mainTrackLine.get(blockID);
            if (brokenBlock != null) {
                brokenBlock.setFailure(true,false,false);
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

    //TODO: make sure occupancies are set in the map and to wayside for failures
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

    //TODO: make sure to reset ticket sales when needed
    //every tick is a second to ticket sales will reset every 3600 seconds
    @Override
    public int getTicketSales() {
            if (time % 3600 == 0) {
                this.ticketSales = 0;
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
        });
    }


    private void setupListeners() {
        ObservableHashMap.MapListener<TrainModel, Integer> trackListener = new ObservableHashMap.MapListener<>() {
            public void onAdded(TrainModel train, Integer blockID) {
                // A train enters a new block
                handleTrainEntry(train, blockID);
            }
            public void onRemoved(TrainModel train, Integer blockID) {
                // A train leaves a block
                handleTrainExit(train, blockID);
            }
            public void onUpdated(TrainModel train, Integer oldBlockID, Integer newBlockID) {
                // A train moves from one block to another
                handleYardEntry(train, newBlockID, oldBlockID);
            }
        };
        trackOccupancyMap.addChangeListener(trackListener);
    }

}