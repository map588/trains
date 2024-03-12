package trackModel;

import Common.TrainModel;
import Framework.Support.ObservableHashMap;
import Utilities.BasicBlock;
import trainModel.TrainModelImpl;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackPseudoCode {

    final ObservableHashMap<TrainModel, Integer> trackOccupancyMap = new ObservableHashMap<>();
    ExecutorService trackUpdateExecutor = Executors.newCachedThreadPool();

    //Fancy Array
    ConcurrentSkipListMap<Integer, BasicBlock> trackLayout;


    public TrackPseudoCode() {
        setupListeners();
    }


    public void trainDispatch(int startBlock, int trainID) {
        TrainModel train = new TrainModelImpl(trainID, this, startBlock);
        trackOccupancyMap.put(train, startBlock);
    }

    public int updateTrainLocation(TrainModel train) {
        int prevBlock = trackOccupancyMap.get(train);
        BasicBlock nextBlock = trackLayout.get(prevBlock).next();
        trackOccupancyMap.remove(train);
        trackOccupancyMap.put(train, nextBlock.blockNumber());
        return nextBlock.blockLength();
    }

    private void handleTrainEntry(Integer segmentId, String trainId) {
        // Logic to handle a train entering a segment
    }

    private void handleTrainExit(Integer segmentId, String trainId) {
        // Logic to handle a train leaving a segment
    }

    // Used to add a task to the work queue
    private void executeTrackUpdate(Runnable task) {
        trackUpdateExecutor.submit(task);
    }


    //
    private void setupListeners() {

        ObservableHashMap.MapListener<Integer, TrainModel> trackListener = new ObservableHashMap.MapListener<>() {

            private void onAdded(Integer segmentId, String trainId) {
                // A train enters a new block
                executeTrackUpdate(() -> handleTrainEntry(segmentId, trainId));
            }

            private void onRemoved(Integer segmentId, String trainId) {
                // A train leaves a block
                executeTrackUpdate(() -> handleTrainExit(segmentId, trainId));
            }

            // Assuming up dates are less common, but you could implement it as needed
            private void onUpdated(Integer segmentId, String oldTrainId, String newTrainId) {
                executeTrackUpdate(() -> {
                    handleTrainExit(segmentId, oldTrainId);
                    handleTrainEntry(segmentId, newTrainId);
                });
            }
        };

        trackOccupancyMap.addChangeListener(trackListener);
    }
}