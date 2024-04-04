package Framework.Simulation;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.ObservableHashMap;
import Utilities.Records.UpdatedTrainValues;
import trainModel.TrainModelSubject;
import trainModel.TrainModelSubjectMap;

import java.util.concurrent.*;

public class TrainSystem {
    ExecutorService trainExecutor = Executors.newWorkStealingPool();

    private final ConcurrentHashMap<TrainModel, TrainUpdateTask> updateTasks = new ConcurrentHashMap<>();

    public TrainSystem() {
        ObserverSetup();
    }

    public void addTrainProcess(TrainModel train, int trainID) {
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

        public TrainController getController() {
            return controller;
        }

        @Override
        public Void call() {

            try {
                //Passes the power calculation to the work stealing pool
                //Future<Double> powerFuture = trainExecutor.submit(() -> controller.calculatePower(train.getSpeed()));
                Future<UpdatedTrainValues> utvFuture = trainExecutor.submit(() -> controller.sendUpdatedTrainValues());                //Calls the physics simulation, passing it the future value of the power calculation
                train.trainModelTimeStep(utvFuture);

            } catch (Exception e) {
                throw new RuntimeException(e + " in TrainUpdateTask for train " + train.getTrainNumber());
            }

            return null;
        }
    }

    final TrainModelSubjectMap trainSubjectMap = TrainModelSubjectMap.getInstance();

    private void ObserverSetup() {
        ObservableHashMap<Integer, TrainModelSubject> subjects = trainSubjectMap.getSubjects();

        // Create a listener that reacts to any change (add, remove, update) by updating choice box items
        ObservableHashMap.MapListener<Integer, TrainModelSubject> mapListener = new ObservableHashMap.MapListener<>() {
            @Override
            public void onAdded(Integer key, TrainModelSubject value) {
                addTrainProcess(value.getModel(), value.getModel().getTrainNumber());
            }
            @Override
            public void onRemoved(Integer key, TrainModelSubject value) {
               deleteTrainTask(value.getModel());
            }
        };
        subjects.addChangeListener(mapListener);
    }
}
