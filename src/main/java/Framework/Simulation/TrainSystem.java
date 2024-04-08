package Framework.Simulation;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.ObservableHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainModel.Records.UpdatedTrainValues;

import java.util.concurrent.*;

public class TrainSystem {
    ExecutorService trainExecutor = Executors.newWorkStealingPool();

    final static Logger logger = LoggerFactory.getLogger(TrainSystem.class);

    private final ConcurrentHashMap<TrainModel, TrainUpdateTask> updateTasks = new ObservableHashMap<>();


    public TrainSystem() {
        logger.info("TrainSystem initialized");
    }

    public void addTrainProcess(TrainModel train) {
        TrainUpdateTask trainUpdate = new TrainUpdateTask(train, train.getController());
        updateTasks.put(train, trainUpdate);
    }

    public void update() {
        // Submit the cached list of tasks using invokeAll()
        try {
            trainExecutor.invokeAll(updateTasks.values());
        } catch (InterruptedException e) {
            throw new RuntimeException(e + " in TrainSystem update()");
        }
    }

    public void deleteTrainTask(TrainModel train) {
        updateTasks.remove(train);
    }

    public void shutdown() {
        updateTasks.clear();
        trainExecutor.shutdown();
    }

    private class TrainUpdateTask implements Callable<Void> {
        private final TrainModel train;
        private final TrainController controller;

        TrainUpdateTask(TrainModel train, TrainController controller) {
            this.train = train;
            this.controller = controller;
        }

        @Override
        public Void call() {

            if(controller == null || train == null) {
                deleteTrainTask(train);
            }
            try {

                Future<UpdatedTrainValues> utvFuture = trainExecutor.submit(controller::sendUpdatedTrainValues);
                train.trainModelTimeStep(utvFuture);

            } catch (Exception e) {
                throw new RuntimeException(e + " in TrainUpdateTask for train " + train.getTrainNumber());
            }

            return null;
        }
    }
}
