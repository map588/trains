package trackModel;

import Common.TrainModel;
import Framework.Support.ObservableHashMap;
import Utilities.BasicBlock;
import trainModel.TrainModelImpl;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackPseudoCode {

    ExecutorService trackUpdateExecutor = Executors.newCachedThreadPool();

    //calls all listeners when a train enters or exits a block
    final ObservableHashMap<TrainModel, Integer> trackOccupancyMap = new ObservableHashMap<>();

    //maps blocks to block numbers
    ConcurrentSkipListMap<Integer, BasicBlock> trackLayout;


    public TrackPseudoCode() {
        setupListeners();
    }


    public void trainDispatch(int startBlock, int trainID) {
        TrainModel train = new TrainModelImpl(trainID, this, startBlock);
        trackOccupancyMap.put(train, startBlock);
    }

    public int updateTrainLocation(TrainModel train) {
        int prevBlockID = trackOccupancyMap.get(train);
        BasicBlock prevBlock = trackLayout.get(prevBlockID);

        Integer newBlockID;

        //Figure out the next block based on the previous block type
        switch (prevBlock.blockType()) {
            case REGULAR:
                newBlockID =  nextFromRegularBlock(train, prevBlock);
            case STATION:
                newBlockID =  nextFromStationBlock(train, prevBlock);
            case SWITCH:
                newBlockID =  nextFromSwitchBlock(train, prevBlock);
            case CROSSING:
                newBlockID =  nextFromCrossingBlock(train, prevBlock);
            case YARD:
                newBlockID =  nextFromYardBlock(train, prevBlock);
            default:
                newBlockID = prevBlockID++;
        }

        BasicBlock nextBlock = trackLayout.get(newBlockID);

        // Both of these modifications call the listeners
        trackOccupancyMap.remove(train);
        trackOccupancyMap.put(train, nextBlock.blockNumber());


        return nextBlock.blockLength();
    }

    private void handleTrainEntry(TrainModel train, Integer blockID) {
        // Logic to handle a train entering a segment
    }

    private void handleTrainExit(TrainModel train, Integer blockID) {
        // Logic to handle a train leaving a segment
    }


    /*
     * The following methods perform the logic to determine the next block for a train
     */
    private Integer nextFromRegularBlock(TrainModel train, BasicBlock prevBlock) {
        return 0;
    }

    private Integer nextFromStationBlock(TrainModel train, BasicBlock prevBlock) {
        return 0;
    }

    private Integer nextFromSwitchBlock(TrainModel train, BasicBlock prevBlock) {
        return 0;
    }

    private Integer nextFromCrossingBlock(TrainModel train, BasicBlock prevBlock) {
        return 0;
    }

    private Integer nextFromYardBlock(TrainModel train, BasicBlock prevBlock) {
        return 0;
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