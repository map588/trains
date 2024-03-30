package trackModel;

import Common.TrainModel;
import Framework.Support.ObservableHashMap;
import Utilities.BasicBlock;
import Utilities.BasicBlock.Connection;
import Utilities.Enums.Direction;
import Utilities.Enums.Lines;
import trainModel.TrainModelImpl;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Utilities.Enums.Direction.NORTH;
import static Utilities.Enums.Direction.SOUTH;

public class TrackLine {

    Lines line;

    ExecutorService trackUpdateExecutor = Executors.newCachedThreadPool();

    //calls all listeners when a train enters or exits a block
    final ObservableHashMap<TrainModel, Integer> trackOccupancyMap = new ObservableHashMap<>();

    //maps blocks to block numbers
    ConcurrentSkipListMap<Integer, TrackBlock> trackLayout;

    int outsideTemperature = 0;

    TrackModelSubject subject;

    public TrackLine(Lines line, ConcurrentSkipListMap<Integer, BasicBlock> basicTrackLayout) {

        ArrayList<Integer> blockIndices = new ArrayList<>(basicTrackLayout.keySet());

        for (Integer blockIndex : blockIndices) {
            TrackBlock block = new TrackBlock(basicTrackLayout.get(blockIndex));
            trackLayout.put(block.blockID, block);
        }

        this.line = line;
        setupListeners();
    }


    public void setSubject(TrackModelSubject subject) {
        this.subject = subject;
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

}