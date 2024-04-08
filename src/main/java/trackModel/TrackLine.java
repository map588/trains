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
import trainModel.TrainModelImpl;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Utilities.Constants.MAX_PASSENGERS;
import static Utilities.Constants.TIME_STEP_MS;

public class TrackLine implements TrackModel {

    private static final Logger logger = Logger.getLogger(TrackLine.class.getName());

    Lines line;

    ExecutorService trackUpdateExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //map of dynamic Track Blocks
    private final TrackBlockLine mainTrackLine = new TrackBlockLine();
    //maps beacons to block numbers
    private final ConcurrentHashMap<Integer, Beacon> beaconBlocks;
    //maps trains to block numbers
    private final ObservableHashMap<TrainModel, Integer> trackOccupancyMap;

    //queue of track update tasks
    private final ConcurrentLinkedQueue<Callable<Object>> trackUpdateQueue = new ConcurrentLinkedQueue<>();

    private long time = 0;


    private int ticketSales = 0;
    private TrackLineSubject subject;
    private BeaconParser beaconParser;
    public int outsideTemperature = 40;
    private WaysideSystem waysideSystem;

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
        }

        //Needs more testing, but the beacon parser seems to work.
        this.beaconBlocks = BeaconParser.parseBeacons(line);
        this.subject = new TrackLineSubject(this, mainTrackLine);

        setupListeners();
    }

    public void update() {
        time += TIME_STEP_MS;
        // Execute all pending track update tasks
            try {
                trackUpdateExecutor.invokeAll(trackUpdateQueue);
            } catch (RejectedExecutionException e) {
                logger.log(Level.SEVERE, "Track update task rejected", e);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Track update task failed", e);
                throw new RuntimeException(e);
            }
            trackUpdateQueue.clear();
    }

    public TrackLine() {
        this(Lines.NULL);
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

    public TrainModel trainDispatch(int trainID) {
        System.out.println("Train: " + trainID + " dispatched");
        TrainModel train = new TrainModelImpl(this, trainID);
        queueTrackUpdate(() -> {
            trackOccupancyMap.put(train, 0);
            mainTrackLine.get(0).occupiedBy = train;
            WaysideSystem.getController(this.line, 0).trackModelSetOccupancy(0, true);
        });
        return train;
    }

    //Note: Train could be on different Line
    public void trainDispatch(TrainModel train) {
        queueTrackUpdate(() -> {
            trackOccupancyMap.put(train, 0);
            mainTrackLine.get(0).occupiedBy = train;
            WaysideSystem.getController(this.line, 0).trackModelSetOccupancy(0, true);
        });
    }

    /**
     * Updates the location of a train on the track
     * @param train
     * @return the block the train is moving to
     */
    public TrackBlock updateTrainLocation(TrainModel train) {
        Integer currentBlockID = trackOccupancyMap.getOrDefault(train, -1);
        if (currentBlockID == -1) {
            logger.log(Level.SEVERE, "Train {0} is not on the track", train.getTrainNumber());
            return null;
        }
        BasicBlock.Connection next = mainTrackLine.get(currentBlockID).getNextBlock(train.getDirection());
        if (next.directionChange()) {
            train.changeDirection();
        }
        Integer nextBlockID = next.blockNumber();

        System.out.println("Train: " + train.getTrainNumber() + " " + currentBlockID + " ->  " + nextBlockID);
        trackOccupancyMap.replace(train, currentBlockID, next.blockNumber());

        asyncTrackUpdate(() -> {
            handleTrainExit(train, currentBlockID);
            handleTrainEntry(train, nextBlockID);
            return null;
        });

        return mainTrackLine.get(nextBlockID);
    }


    private void handleTrainEntry(TrainModel train, Integer blockID) {
         if (mainTrackLine.get(blockID).isOccupied()) {
                logger.log(Level.WARNING, "Block {0} is already occupied", blockID);
            }
            mainTrackLine.get(blockID).setOccupied(true);
            mainTrackLine.get(blockID).occupiedBy = train;
            WaysideSystem.getController(this.line, blockID).trackModelSetOccupancy(blockID, true);
            if(beaconBlocks.containsKey(blockID)) {
                train.passBeacon(beaconBlocks.get(blockID));
            }

    }

    private void handleTrainExit(TrainModel train, Integer blockID) {
        if (!mainTrackLine.get(blockID).isOccupied()) {
            logger.log(Level.WARNING, "Block {0} is not occupied", blockID);
        }
        mainTrackLine.get(blockID).setOccupied(false);
        mainTrackLine.get(blockID).occupiedBy = null;
        WaysideSystem.getController(this.line, blockID).trackModelSetOccupancy(blockID, false);
    }

    private void handleYardEntry(TrainModel train, Integer newBlockID, Integer oldBlockID) {
        if(newBlockID == 0 && oldBlockID == 57 && line == Lines.GREEN) {
            train.delete();
        }else if(newBlockID == oldBlockID) {
            logger.warning("Error: False Occupancy Update from " + oldBlockID + " to " + newBlockID);
        }else if(newBlockID == 0) {
            logger.info("Train " + train.getTrainNumber() + " was/is in the yard");
        }
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
            if(beaconBlocks.contains(block)) {
                mainTrackLine.get(block).lightState = state;
            } else {
                logger.log(Level.SEVERE, "Block {0} does not have a light", block);
            }
            return null;
        });
    }

//TODO: Beacon communication with the train

    public void setBeacon(int block, Beacon beacon) {
        asyncTrackUpdate( () -> {
            beaconBlocks.put(block, beacon);
            return null;
        });
    }


//TODO: Maybe refactor set to pass for clearer communication.

    @Override
    public void setSwitchState(int block, boolean state) {
        asyncTrackUpdate( () -> {
            if (mainTrackLine.get(block).feature.isSwitch()) {
                mainTrackLine.get(block).setSwitchState(state);
            } else {
                logger.log(Level.SEVERE, "Block {0} is not a switch", block);
            }
            return null;
        });
    }

    @Override
    public void setCrossing(int block, boolean state) {
        asyncTrackUpdate(() -> {
            if (mainTrackLine.get(block).feature.isCrossing()) {
                mainTrackLine.get(block).setCrossingState(state);
            } else {
                logger.log(Level.SEVERE, "Block {0} is not a crossing", block);
            }
            return null;
        });
    }

    @Override
    public void setTrainAuthority(Integer blockID, int authority){
        asyncTrackUpdate( () -> {
            mainTrackLine.get(blockID).setAuthority(authority);
            logger.info("Track set authority to: " + authority + " at block " + blockID);
            return null;
        });
    }

    @Override
    public void setCommandedSpeed(Integer blockID, double commandedSpeed) {
        asyncTrackUpdate( () -> {
            mainTrackLine.get(blockID).setCommandSpeed(commandedSpeed);
            logger.info("Track set commanded speed to: " + commandedSpeed + " at block " + blockID);
            return null;
        });
    }

    @Override
    public void setBrokenRail(Integer blockID, boolean state) {
       queueTrackUpdate( () -> {
            TrackBlock brokenBlock = this.mainTrackLine.get(blockID);
            if (brokenBlock != null) {
                brokenBlock.setFailure(true,false,false);
            } else {
                logger.log(Level.SEVERE, "Block {0} does not exist", blockID);
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
                logger.log(Level.SEVERE, "Block {0} does not exist", blockID);
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
                logger.log(Level.SEVERE, "Block {0} does not exist", blockID);
            }
        });
    }

    //TODO: make sure occupancies are set in the map and to wayside for failures
    @Override
    public boolean getBrokenRail(Integer blockID) {
       return asyncTrackUpdate( () -> this.mainTrackLine.get(blockID).brokenRail).join();
    }

    @Override
    public boolean getPowerFailure(Integer blockID) {
        return asyncTrackUpdate(() -> this.mainTrackLine.get(blockID).powerFailure).join();
    }

    @Override
    public boolean getTrackCircuitFailure(Integer blockID) {
        return asyncTrackUpdate(() -> this.mainTrackLine.get(blockID).trackCircuitFailure).join();
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
                    logger.log(Level.SEVERE, "Train {0} is not on a station block", train.getTrainNumber());
                }
            } else {
                logger.log(Level.SEVERE, "Train {0} is not on the track", train.getTrainNumber());
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
                    logger.log(Level.SEVERE, "Train {0} is not on a station block", train.getTrainNumber());
                    return 0;
                }
            } else {
                logger.log(Level.SEVERE, "Train {0} is not on the track", train.getTrainNumber());
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


}