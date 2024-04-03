package trackModel;

import Common.TrackModel;
import Common.TrainModel;
import Framework.Support.ObservableHashMap;
import Utilities.Records.BasicBlock;
import Utilities.Records.BasicBlock.Connection;
import Utilities.Enums.Lines;
import Utilities.Records.Beacon;
import trainModel.TrainModelImpl;

import java.util.ArrayList;
import java.util.concurrent.*;

import static Utilities.Constants.MAX_PASSENGERS;

public class TrackLine implements TrackModel {

    Lines line;
    
    ExecutorService trackUpdateExecutor = Executors.newCachedThreadPool();


    //calls all listeners when a train enters or exits a block
    private final ObservableHashMap<TrainModel, Integer> trackOccupancyMap;

    //maps blocks to block numbers
    private final ConcurrentSkipListMap<Integer, TrackBlock> trackBlocks;

    private final ConcurrentHashMap<Integer, Beacon> beaconBlocks;

    private int outsideTemperature = 70;
    private int ticketSales = 0;

    private final TrackLineSubject subject;

    public TrackLine(Lines line, ConcurrentSkipListMap<Integer, BasicBlock> basicTrackLayout) {
        this.subject = new TrackLineSubject(this);

        trackBlocks = new ConcurrentSkipListMap<>();
        beaconBlocks = new ConcurrentHashMap<>();
        trackOccupancyMap = new ObservableHashMap<>();

        ArrayList<Integer> blockIndices = new ArrayList<>(basicTrackLayout.keySet());

        for (Integer blockIndex : blockIndices) {
            TrackBlock block = new TrackBlock(basicTrackLayout.get(blockIndex));
            trackBlocks.put(block.blockID, block);
        }

        this.line = line;
        setupListeners();
    }


    public TrainModel trainDispatch(int trainID) {
        TrainModel train = new TrainModelImpl(trainID, this);
        trackOccupancyMap.put(train, 0);
        return train;
    }

    //Note: Train could be on different Line
    public void trainDispatch(TrainModel train) {
        syncTrackUpdate(() -> {
            trackOccupancyMap.put(train, 0);
        });
    }



    private void handleTrainEntry(TrainModel train, Integer blockID) {
        TrackBlock block = trackBlocks.get(blockID);
        //...
    }


    private void handleTrainExit(TrainModel train, Integer blockID) {
        TrackBlock block = trackBlocks.get(blockID);
        //...
    }

    private ConcurrentLinkedQueue<Callable<Void>> trackUpdateQueue = new ConcurrentLinkedQueue<>();

    // Used to add a task to the work queue
    private void asyncTrackUpdate(Runnable task) {
        trackUpdateExecutor.submit(task);
    }

    private void syncTrackUpdate(Runnable task) {
        Callable<Void> updateTask = new TrackUpdateTask(task);
        trackUpdateQueue.add(updateTask);
    }

    public void update() {
        while (!trackUpdateQueue.isEmpty()) {
            try {
                trackUpdateExecutor.invokeAll(trackUpdateQueue);
            } catch (InterruptedException e) {
                throw new RuntimeException(e + " in TrackLine update()");
            }
        }
    }

    /**
     * Updates the location of a train on the track
     * @param train
     * @return the block the train is moving to
     */
    public TrackBlock updateTrainLocation(TrainModel train) {
        CompletableFuture<TrackBlock> futureBlock = CompletableFuture.supplyAsync(() -> {
            Integer currentBlockID = trackOccupancyMap.getOrDefault(train, -1);

            System.out.println("Train: " + train.getTrainNumber() + " is on block: " + currentBlockID);

            if (currentBlockID == -1) {
                throw new IllegalArgumentException("Train: " + train.getTrainNumber() + " is not on the track");
            }

            Connection next = trackBlocks.get(currentBlockID).getNextBlock(train.getDirection());

            if (next.directionChange()) {
                train.changeDirection();
            }
            Integer nextBlockID = next.blockNumber();

            System.out.println("Train: " + train.getTrainNumber() + " is moving to block: " + nextBlockID);

            syncTrackUpdate(() -> {
                trackOccupancyMap.remove(train, currentBlockID);
                trackOccupancyMap.put(train, next.blockNumber());
            });

            return trackBlocks.get(nextBlockID);
        }, trackUpdateExecutor);

        return futureBlock.join();
    }


    private record TrackUpdateTask(Runnable task) implements Callable<Void> {
        @Override
        public Void call() {
            task.run();
            return null;
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
            // Assuming updates are less common, but you could implement it as needed
            public void onUpdated(TrainModel train, Integer oldBlockID, Integer newBlockID) {
               handleTrainExit(train, oldBlockID);
               handleTrainEntry(train, newBlockID);
            }
        };

        trackOccupancyMap.addChangeListener(trackListener);
    }

    public void setTemperature(int i) {
        outsideTemperature = i;
    }



    //------------------From interface------------------

    //TODO: We don't have light states figured out yet
    @Override
    public void setLightState(int block, boolean state) {
        syncTrackUpdate( () -> {
            trackBlocks.get(block).lightState = state;
        });
    }

    @Override
    public void setSwitchState(int block, boolean state) {
        syncTrackUpdate( () -> {
            if(!trackBlocks.get(block).isSwitch){
                throw new IllegalArgumentException("Block: " + block + " is not a switch");
            }
            trackBlocks.get(block).setSwitchState(state);
        });
    }

    @Override
    public void setCrossing(int block, boolean state) {
        syncTrackUpdate( () -> {
            if (trackBlocks.get(block).feature.isCrossing()) {
                trackBlocks.get(block).setCrossingState(state);
            } else {
                throw new IllegalArgumentException("Block: " + block + " is not a crossing");
            }
        });
    }

    //TODO: We don't know how beacons will work yet
//    @Override
    public void setBeacon(int block, Beacon beacon){
        syncTrackUpdate( () -> {
            if(beaconBlocks.containsKey(block)){
                beaconBlocks.remove(block);
            }
            beaconBlocks.put(block, beacon);
            //trackBlocks.get(block).setBeacon(beacon);

        });
    }

    @Override
    public void setTrainAuthority(Integer blockID, int authority) {
        syncTrackUpdate( () -> {
            trackBlocks.get(blockID).setAuthority(authority);
        });
    }

    @Override
    public void setCommandedSpeed(Integer blockID, double commandedSpeed) {
        syncTrackUpdate( () -> {
            trackBlocks.get(blockID).setCommandSpeed(commandedSpeed);
        });
    }

    @Override
    public boolean getLightState(int block) {
        CompletableFuture<Boolean> futureLightState = CompletableFuture.supplyAsync(() -> trackBlocks.get(block).lightState, trackUpdateExecutor);
        return futureLightState.join();
    }

    @Override
    public boolean getSwitchState(int block) {
        if(!trackBlocks.get(block).isSwitch){
            throw new IllegalArgumentException("Block: " + block + " is not a switch");
        }
        return trackBlocks.get(block).getSwitchState();
    }

    @Override
    public boolean getCrossingState(int block) {
        if (trackBlocks.get(block).feature.isCrossing()) {
            return trackBlocks.get(block).getCrossingState();
        } else {
            throw new IllegalArgumentException("Block: " + block + " is not a crossing");
        }
    }

    @Override
    public void setBrokenRail(Integer blockID, boolean state) {
        syncTrackUpdate( () -> {
            TrackBlock brokenBlock = this.trackBlocks.get(blockID);
            if (brokenBlock != null) {
                brokenBlock.setBrokenRail(state);
            } else {
                throw new IllegalArgumentException("Block: " + blockID + " does not exist");
            }
        });
    }

    @Override
    public void setPowerFailure(Integer blockID, boolean state) {
        syncTrackUpdate( () -> {;
            TrackBlock failedBlock = this.trackBlocks.get(blockID);
            if (failedBlock != null) {
                failedBlock.setPowerFailure(state);
            } else {
                throw new IllegalArgumentException("Block: " + blockID + " does not exist");
            }
        });
    }

    @Override
    public void setTrackCircuitFailure(Integer blockID, boolean state) {
        syncTrackUpdate( () -> {;
            TrackBlock failedBlock = this.trackBlocks.get(blockID);
            if (failedBlock != null) {
                failedBlock.setTrackCircuitFailure(state);
            } else {
                throw new IllegalArgumentException("Block: " + blockID + " does not exist");
            }
        });
    }

    @Override
    public boolean getBrokenRail(Integer blockID) {
        return this.trackBlocks.get(blockID).isBrokenRail();
    }

    @Override
    public boolean getPowerFailure(Integer blockID) {
        return this.trackBlocks.get(blockID).isPowerFailure();
    }

    @Override
    public boolean getTrackCircuitFailure(Integer blockID) {
        return this.trackBlocks.get(blockID).isTrackCircuitFailure();
    }

    @Override
    public void setPassengersDisembarked(TrainModel train, int disembarked) {
        syncTrackUpdate( () -> {
            if(trackOccupancyMap.containsKey(train)){
                TrackBlock block = trackBlocks.get(trackOccupancyMap.get(train));
                if (block.feature.isStation()) {
                    block.feature.setPassengersDisembarked(disembarked);
                } else {
                    throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on a station block");
                }
            } else {
                throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on the track");
            }
        });
    }

    @Override
    public int getPassengersEmbarked(TrainModel train) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        if(trackOccupancyMap.containsKey(train)){
            TrackBlock block = trackBlocks.get(trackOccupancyMap.get(train));
            if (block.feature.isStation()) {
                int embarked = (random.nextInt(0, MAX_PASSENGERS - train.getPassengerCount()));
                block.feature.setPassengersEmbarked(embarked);
                this.ticketSales += embarked;
                return embarked;
            } else {
                throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on a station block");
            }
        } else {
            throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on the track");
        }
    }


    @Override
    public int getTicketSales() {
        return this.ticketSales;
    }

    public void resetTicketSales() {
        this.ticketSales = 0;
    }


    //Testing purposes
    public TrackBlock getBlock(int blockID) {
        return trackBlocks.get(blockID);
    }
    public void moveTrain(TrainModel train, int blockID) {
        if(trackOccupancyMap.containsKey(train)){
            trackOccupancyMap.remove(train);
        }
        trackOccupancyMap.put(train, blockID);
    }
}
