package trackModel;

import Common.TrackModel;
import Common.TrainModel;
import Framework.Support.ObservableHashMap;
import Utilities.BasicBlock;
import Utilities.BasicBlock.Connection;
import Utilities.Beacon;
import Utilities.Enums.Lines;
import trainModel.TrainModelImpl;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Utilities.Constants.MAX_PASSENGERS;

public class TrackLine implements TrackModel {

    Lines line;
    
    ExecutorService blockUpdateExecutor = Executors.newSingleThreadExecutor();
    ExecutorService trackUpdateExecutor = Executors.newCachedThreadPool();

    //calls all listeners when a train enters or exits a block
    private final ObservableHashMap<TrainModel, Integer> trackOccupancyMap;

    //maps blocks to block numbers
    private final ConcurrentSkipListMap<Integer, TrackBlock> trackLayout;

    private int outsideTemperature = 70;
    private int ticketSales = 0;

    private final TrackLineSubject subject;

    public TrackLine(Lines line, ConcurrentSkipListMap<Integer, BasicBlock> basicTrackLayout) {
        this.subject = new TrackLineSubject(this);

        trackLayout = new ConcurrentSkipListMap<>();
        trackOccupancyMap = new ObservableHashMap<>();

        ArrayList<Integer> blockIndices = new ArrayList<>(basicTrackLayout.keySet());

        for (Integer blockIndex : blockIndices) {
            TrackBlock block = new TrackBlock(basicTrackLayout.get(blockIndex));
            trackLayout.put(block.blockID, block);
        }

        this.line = line;
        setupListeners();
    }


    public void trainDispatch(int trainID) {
        TrainModel train = new TrainModelImpl(trainID, this);
        trackOccupancyMap.put(train, 0);
    }


    private void handleTrainEntry(TrainModel train, Integer blockID) {
        TrackBlock block = trackLayout.get(blockID);
        //...
    }


    private void handleTrainExit(TrainModel train, Integer blockID) {
        TrackBlock block = trackLayout.get(blockID);
        //...
    }

    /**
     * Updates the location of a train on the track
     *
     * @param train
     * @return the length of the block the train is now on (for the purpose of the train model)
     */

    public double updateTrainLocation(TrainModel train) {
        if (!trackOccupancyMap.containsKey(train)) {
            throw new IllegalArgumentException("Train is not on the track");
        }

        int currentBlockId = trackOccupancyMap.get(train);
        TrackBlock currentBlock = trackLayout.get(currentBlockId);
        Connection newConnection = currentBlock.getNextBlock(train.getDirection());

        if (newConnection.directionChange()) {
            train.changeDirection();
        }

        TrackBlock nextBlock = trackLayout.get(newConnection.blockNumber());

        trackOccupancyMap.remove(train, currentBlockId);
        trackOccupancyMap.put(train, newConnection.blockNumber());

        return nextBlock.length;
    }

    // Used to add a task to the work queue
    private void executeTrackUpdate(Runnable task) {
        trackUpdateExecutor.submit(task);
    }
    private void executeBlockUpdate(Runnable task) {
        blockUpdateExecutor.submit(task);
    }

    private void setupListeners() {

        ObservableHashMap.MapListener<TrainModel, Integer> trackListener = new ObservableHashMap.MapListener<>() {

            public void onAdded(TrainModel train, Integer blockID) {
                // A train enters a new block
                executeTrackUpdate(() -> handleTrainEntry(train, blockID));
            }

            public void onRemoved(TrainModel train, Integer blockID) {
                // A train leaves a block
                executeTrackUpdate(() -> handleTrainExit(train, blockID));
            }

            // Assuming up dates are less common, but you could implement it as needed
            public void onUpdated(TrainModel train, Integer oldBlockID, Integer newBlockID) {
                executeTrackUpdate(() -> {
                    handleTrainExit(train, oldBlockID);
                    handleTrainEntry(train, newBlockID);
                });
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
        blockUpdateExecutor.submit(() ->{
            trackLayout.get(block).lightState = state;
        });
    }

    @Override
    public void setSwitchState(int block, boolean state) {
        blockUpdateExecutor.submit(() ->{
        if(!trackLayout.get(block).isSwitch){
            throw new IllegalArgumentException("Block: " + block + " is not a switch");
        }
        trackLayout.get(block).setSwitchState(state);
        });
    }

    @Override
    public void setCrossing(int block, boolean state) {
        blockUpdateExecutor.submit(() -> {
            if (trackLayout.get(block).crossingInfo.isPresent()) {
                trackLayout.get(block).setCrossingState(state);
            } else {
                throw new IllegalArgumentException("Block: " + block + " is not a crossing");
            }
        });
    }

    //TODO: We don't know how beacons will work yet
    @Override
    public void setBeacon(int block, Beacon beacon) {
    }

    @Override
    public void setTrainAuthority(TrainModel train, int authority) {
            train.setAuthority(authority);
    }

    @Override
    public void setCommandedSpeed(TrainModel train, double commandedSpeed) {
        train.setCommandSpeed(commandedSpeed);
    }

    @Override
    public boolean getLightState(int block) {
        return trackLayout.get(block).lightState;
    }

    @Override
    public boolean getSwitchState(int block) {
        if(!trackLayout.get(block).isSwitch){
            throw new IllegalArgumentException("Block: " + block + " is not a switch");
        }
        return trackLayout.get(block).isSwitch;
    }

    @Override
    public boolean getCrossingState(int block) {
        if (trackLayout.get(block).crossingInfo.isPresent()) {
            return trackLayout.get(block).isSwitch;
        } else {
            throw new IllegalArgumentException("Block: " + block + " is not a crossing");
        }
    }

    @Override
    public void setBrokenRail(Integer blockID, boolean state) {
        blockUpdateExecutor.submit(() -> {
            TrackBlock brokenBlock = this.trackLayout.get(blockID);
            if (brokenBlock != null) {
                brokenBlock.setBrokenRail(state);
            } else {
                throw new IllegalArgumentException("Block: " + blockID + " does not exist");
            }
        });
    }

    @Override
    public void setPowerFailure(Integer blockID, boolean state) {
        blockUpdateExecutor.submit(() -> {
            TrackBlock failedBlock = this.trackLayout.get(blockID);
            if (failedBlock != null) {
                failedBlock.setPowerFailure(state);
            } else {
                throw new IllegalArgumentException("Block: " + blockID + " does not exist");
            }
        });
    }

    @Override
    public void setTrackCircuitFailure(Integer blockID, boolean state) {
        blockUpdateExecutor.submit(() -> {
            TrackBlock failedBlock = this.trackLayout.get(blockID);
            if (failedBlock != null) {
                failedBlock.setTrackCircuitFailure(state);
            } else {
                throw new IllegalArgumentException("Block: " + blockID + " does not exist");
            }
        });
    }

    @Override
    public boolean getBrokenRail(Integer blockID) {
        return this.trackLayout.get(blockID).failureInfo.isBrokenRail();
    }

    @Override
    public boolean getPowerFailure(Integer blockID) {
        return this.trackLayout.get(blockID).failureInfo.isPowerFailure();
    }

    @Override
    public boolean getTrackCircuitFailure(Integer blockID) {
        return this.trackLayout.get(blockID).failureInfo.isTrackCircuitFailure();
    }

    @Override
    public void setPassengersDisembarked(TrainModel train, int disembarked) {
        blockUpdateExecutor.submit(() -> {
            if (trackOccupancyMap.containsKey(train)) {
                TrackBlock block = trackLayout.get(trackOccupancyMap.get(train));
                block.stationInfo.ifPresent(stationInfo -> stationInfo.setPassengersDisembarked(disembarked));
            } else {
                throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on a station block");
            }
        });
    }

    @Override
    public int getTicketSales() {
        return this.ticketSales;
    }

    @Override
    public int getPassengersEmbarked(TrainModel train) {
        try {
            return blockUpdateExecutor.submit(() -> {
                int embarked = (int) (Math.round(Math.random() * MAX_PASSENGERS));
                if (trackOccupancyMap.containsKey(train)) {
                    TrackBlock block = trackLayout.get(trackOccupancyMap.get(train));
                    block.stationInfo.ifPresent(stationInfo -> stationInfo.setPassengersEmbarked(embarked));
                    this.ticketSales += embarked;
                } else {
                    throw new IllegalArgumentException("Train " + train.getTrainNumber() + " is not on a station block");
                }
                return embarked;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetTicketSales() {
        this.ticketSales = 0;
    }
}
