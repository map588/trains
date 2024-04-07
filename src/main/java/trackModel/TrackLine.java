package trackModel;

import Common.TrackModel;
import Common.TrainModel;
import Framework.Simulation.WaysideSystem;
import Framework.Support.ObservableHashMap;
import Utilities.BasicTrackLine;
import Utilities.BeaconParser;
import Utilities.Enums.Lines;
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

    //maps blocks to block numbers
    private final TrackBlockLine trackBlocks;
    //maps beacons to block numbers
    private final ConcurrentHashMap<Integer, Beacon> beaconBlocks;
    //maps trains to block numbers
    private final ObservableHashMap<TrainModel, Integer> trackOccupancyMap;

    //queue of track update tasks
    private final ConcurrentLinkedQueue<TrackUpdateTask> trackUpdateQueue = new ConcurrentLinkedQueue<>();

    private long time = 0;


    private int ticketSales = 0;
    private TrackLineSubject subject;
    private BeaconParser beaconParser;
    public int outsideTemperature = 40;
    private WaysideSystem waysideSystem;

    public TrackLine(Lines line, BasicTrackLine basicTrackLayout) {

        trackBlocks = new TrackBlockLine();
        trackOccupancyMap = new ObservableHashMap<>();

        ArrayList<Integer> blockIndices = new ArrayList<>(basicTrackLayout.keySet());

        for (Integer blockIndex : blockIndices) {
            TrackBlock block = new TrackBlock(basicTrackLayout.get(blockIndex));
            trackBlocks.put(block.blockID, block);
        }

        //Needs more testing, but the beacon parser seems to work.
        beaconBlocks = BeaconParser.parseBeacons(line);

        this.subject = new TrackLineSubject(this, trackBlocks);

        //TODO: Add beacons to the beaconBlocks map
        setupListeners();
        this.line = line;
    }


    public void update(){
        time += TIME_STEP_MS;
        // Execute all pending track update tasks
        while (!trackUpdateQueue.isEmpty()) {
            trackUpdateQueue.poll().execute();
        }
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
        asyncTrackUpdate(() -> {
            trackOccupancyMap.put(train, 0);
            WaysideSystem.getController(this.line, 0).trackModelSetOccupancy(0, true);
            return null;
        });
        return train;
    }

    //Note: Train could be on different Line
    public void trainDispatch(TrainModel train) {
        asyncTrackUpdate(() -> {
            trackOccupancyMap.put(train, 0);
            WaysideSystem.getController(this.line, 0).trackModelSetOccupancy(0, true);
            return null;
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
        BasicBlock.Connection next = trackBlocks.get(currentBlockID).getNextBlock(train.getDirection());
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

        return trackBlocks.get(nextBlockID);
    }


    private void handleTrainEntry(TrainModel train, Integer blockID) {

        asyncTrackUpdate( () -> {
            if (trackBlocks.get(blockID).isOccupied()) {
                logger.log(Level.WARNING, "Block {0} is already occupied", blockID);
            }
            trackBlocks.get(blockID).setOccupied(true);
            trackBlocks.get(blockID).occupiedBy = train;
            WaysideSystem.getController(this.line, blockID).trackModelSetOccupancy(blockID, true);
            if(beaconBlocks.containsKey(blockID)) {
                train.passBeacon(beaconBlocks.get(blockID));
            }
            return null;
        });

    }

    private void handleTrainExit(TrainModel train, Integer blockID) {

        asyncTrackUpdate( () -> {
            if (!trackBlocks.get(blockID).isOccupied()) {
                logger.log(Level.WARNING, "Block {0} is not occupied", blockID);
            }
            trackBlocks.get(blockID).setOccupied(false);
            trackBlocks.get(blockID).occupiedBy = null;
            WaysideSystem.getController(this.line, blockID).trackModelSetOccupancy(blockID, false);
            return null;
        });

        return;
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

    private <T> CompletableFuture<T> queueTrackUpdate(Supplier<T> task) {
        TrackUpdateTask<T> updateTask = new TrackUpdateTask<>(task);
        trackUpdateQueue.add(updateTask);
        return updateTask.getFuture();
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
                trackBlocks.get(block).lightState = state;
            } else {
                logger.log(Level.SEVERE, "Block {0} does not have a light", block);
            }
            return null;
        });
    }

//TODO: Beacon communication with the train

//    public void setBeacon(int block, Beacon beacon) {
//        asyncTrackUpdate( () -> {
//            beaconBlocks.put(block, beacon);
//        });
//    }

    public Beacon beaconTransmit(int block){
    return asyncTrackUpdate( () -> {
            if(beaconBlocks.containsKey(block)) {
                return beaconBlocks.get(block);
            } else{
                logger.log(Level.SEVERE, "Block {0} does not have a beacon", block);
                return null;
            }
        }).join();
    }


//TODO: Maybe refactor set to pass for clearer communication.

    @Override
    public void setSwitchState(int block, boolean state) {
        asyncTrackUpdate( () -> {
            if (trackBlocks.get(block).feature.isSwitch()) {
                trackBlocks.get(block).setSwitchState(state);
            } else {
                logger.log(Level.SEVERE, "Block {0} is not a switch", block);
            }
            return null;
        });
    }

    @Override
    public void setCrossing(int block, boolean state) {
        asyncTrackUpdate(() -> {
            if (trackBlocks.get(block).feature.isCrossing()) {
                trackBlocks.get(block).setCrossingState(state);
            } else {
                logger.log(Level.SEVERE, "Block {0} is not a crossing", block);
            }
            return null;
        });
    }

    @Override
    public void setTrainAuthority(Integer blockID, int authority){
        asyncTrackUpdate( () -> {
            trackBlocks.get(blockID).getOccupiedBy().setAuthority(authority);
            trackBlocks.get(blockID).setAuthority(authority);
            return null;
        }).join();
    }

    @Override
    public void setCommandedSpeed(Integer blockID, double commandedSpeed) {
        asyncTrackUpdate( () -> {
            System.out.println("Setting speed to: " + commandedSpeed + " at block " + blockID);
            trackBlocks.get(blockID).setCommandSpeed(commandedSpeed);
            trackBlocks.get(blockID).getOccupiedBy().setCommandSpeed(commandedSpeed);
            return null;
        });
    }

    @Override
    public void setBrokenRail(Integer blockID, boolean state) {
        asyncTrackUpdate( () -> {
            TrackBlock brokenBlock = this.trackBlocks.get(blockID);
            if (brokenBlock != null) {
                brokenBlock.setFailure(true,false,false);
            } else {
                logger.log(Level.SEVERE, "Block {0} does not exist", blockID);
            }
            return null;
        });
    }

    @Override
    public void setPowerFailure(Integer blockID, boolean state) {
        //asyncTrackUpdate( () -> {
            TrackBlock failedBlock = this.trackBlocks.get(blockID);
            if (failedBlock != null) {
                failedBlock.setFailure(false,false,true);
            } else {
                logger.log(Level.SEVERE, "Block {0} does not exist", blockID);
            }
            //return null;
       // });
    }

    @Override
    public void setTrackCircuitFailure(Integer blockID, boolean state) {
       // asyncTrackUpdate( () -> {
            TrackBlock failedBlock = this.trackBlocks.get(blockID);
            if (failedBlock != null) {
                failedBlock.setFailure(false,true,false);
            } else {
                logger.log(Level.SEVERE, "Block {0} does not exist", blockID);
            }
            //return null;
      //  });
    }

    //TODO: make sure occupancies are set in the map and to wayside for failures
    @Override
    public boolean getBrokenRail(Integer blockID) {
       return asyncTrackUpdate( () -> this.trackBlocks.get(blockID).brokenRail).join();
    }

    @Override
    public boolean getPowerFailure(Integer blockID) {
        return asyncTrackUpdate(() -> this.trackBlocks.get(blockID).powerFailure).join();
    }

    @Override
    public boolean getTrackCircuitFailure(Integer blockID) {
        return asyncTrackUpdate(() -> this.trackBlocks.get(blockID).trackCircuitFailure).join();
    }

    //This operation is being done on the block, not the train
    @Override
    public void disembarkPassengers(TrainModel train, int disembarked) {
        queueTrackUpdate( () -> {
            if(trackOccupancyMap.containsKey(train)){
                TrackBlock block = trackBlocks.get(trackOccupancyMap.get(train));
                if (block.feature.isStation()) {
                    block.feature.setPassengersDisembarked(disembarked);
                    Integer newTotal = train.getPassengerCount() - disembarked;
                } else {
                    logger.log(Level.SEVERE, "Train {0} is not on a station block", train.getTrainNumber());
                }
            } else {
                logger.log(Level.SEVERE, "Train {0} is not on the track", train.getTrainNumber());
            }
            return null;
        });
    }

    @Override
    public int embarkPassengers(TrainModel train) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
       return queueTrackUpdate(() -> {
            if(trackOccupancyMap.containsKey(train)){
                TrackBlock block = trackBlocks.get(trackOccupancyMap.get(train));
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
       return asyncTrackUpdate(() -> {
            if (time % 3600 == 0)
            {this.ticketSales = 0;}
            return ticketSales;
        }).join();
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
            return null;
        });
    }

    private class TrackUpdateTask<T> {
        private final Supplier<T> task;
        private final CompletableFuture<T> future;

        TrackUpdateTask(Supplier<T> task) {
            this.task = task;
            this.future = new CompletableFuture<>();
        }

        void execute() {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }

        CompletableFuture<T> getFuture() {
            return future;
        }
    }



    //Testing purposes
    public TrackBlock getBlock(int blockID) {
        return trackBlocks.get(blockID);
    }

    public TrackBlockLine getTrack(){
        return trackBlocks;
    }
    public void moveTrain(TrainModel train, int blockID) {
        trackOccupancyMap.remove(train);
        trackOccupancyMap.put(train, blockID);
    }



}