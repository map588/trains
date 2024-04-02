package Framework.Simulation;

import Common.TrainController;
import Common.TrainModel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrainSystem {
    ExecutorService trainExecutor = Executors.newWorkStealingPool();

    ConcurrentHashMap<Integer, TrainController> controllerMap;
    ConcurrentHashMap<Integer, TrainModel> trainMap;

    public TrainSystem() {
        controllerMap = new ConcurrentHashMap<>();
        trainMap = new ConcurrentHashMap<>();
    }


    void submitTrainWork(Runnable work) {
        trainExecutor.submit(work);
    }

    public void update() {
    }
}
