package Framework.Simulation;

import Common.TrainController;
import Common.TrainModel;
import trackModel.TrackLine;
import trainModel.TrainModelImpl;

import java.util.concurrent.*;

public class TrainSystem {
    ExecutorService trainExecutor = Executors.newWorkStealingPool();

    private final ConcurrentHashMap<TrainModel, TrainUpdateTask> updateTasks = new ConcurrentHashMap<>();

    public TrainSystem() {
    }

    public void dispatchTrain(TrackLine line, int trainID) {
        TrainModel train = new TrainModelImpl(trainID, line);
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

        public TrainController getController() {
            return controller;
        }

        @Override
        public Void call() {

            try {
                //Passes the power calculation to the work stealing pool
                Future<Double> powerFuture = trainExecutor.submit(() -> controller.calculatePower(train.getSpeed()));
                //Calls the physics simulation, passing it the future value of the power calculation
                //train.trainModelPhysics(powerFuture);

            } catch (Exception e) {
                throw new RuntimeException(e + " in TrainUpdateTask for train " + train.getTrainNumber());
            }

            return null;
        }
    }
}
