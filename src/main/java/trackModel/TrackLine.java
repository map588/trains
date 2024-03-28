package trackModel;

import Common.TrainModel;
import Framework.Support.ObservableHashMap;
import Utilities.BasicBlock;
import Utilities.Enums.Lines;
import trainModel.TrainModelImpl;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackLine {

    Lines line;

    ExecutorService trackUpdateExecutor = Executors.newCachedThreadPool();

    //calls all listeners when a train enters or exits a block
    final ObservableHashMap<TrainModel, Integer> trackOccupancyMap = new ObservableHashMap<>();

    //maps blocks to block numbers
    ConcurrentSkipListMap<Integer, BasicBlock> trackLayout;

    public TrackLine(Lines line, ConcurrentSkipListMap<Integer, BasicBlock> trackLayout) {
        this.line = line;
        this.trackLayout = trackLayout;
        setupListeners();
    }


    public void trainDispatch(int trainID) {
        TrainModel train = new TrainModelImpl(trainID, this);
        trackOccupancyMap.put(train, 0);
    }


    private void handleTrainEntry(TrainModel train, Integer blockID) {
        BasicBlock block = trackLayout.get(blockID);
        //...
    }

    private void handleTrainExit(TrainModel train, Integer blockID) {
        BasicBlock block = trackLayout.get(blockID);
        //...
    }

    /**
     * Updates the location of a train on the track
     *
     * @param train
     * @return the length of the block the train is now on (for the purpose of the train model)
     */

    public synchronized double updateTrainLocation(TrainModel train) {
        int prevBlockID = trackOccupancyMap.get(train);

        //Direction dir = train.getDirection();  //This will be NORTH or SOUTH
        //Switches have north default and alt, and the track state determines which is the next block

        BasicBlock prevBlock = trackLayout.get(prevBlockID);

        Integer newBlockID = lookupNextBlock(train, prevBlock);

        BasicBlock nextBlock = trackLayout.get(newBlockID);

        // Both of these modifications call the listeners
        trackOccupancyMap.remove(train);
        trackOccupancyMap.put(train, nextBlock.blockNumber());

        return nextBlock.blockLength();
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

    private synchronized Integer lookupNextBlock(TrainModel train, BasicBlock prevBlock) {

        Integer newBlockID = 0;

        if(prevBlock.isSwitch()){

        }

        switch (prevBlock.blockType()) {
            case YARD:
                //newBlockID =  nextFromYardBlock(train, prevBlock);
            case STATION:
                //newBlockID =  nextFromStationBlock(train, prevBlock);
            default:
                //newBlockID = nextFromRegularBlock(train, prevBlock);
        }

        //This is wrong, it will need to be determined within the function whether the ascending or decending block is the next block,
        // or the switch case.
        return newBlockID;
    }

}