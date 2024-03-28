package Framework.Simulation;

import Common.TrackModel;
import trackModel.TrackModelImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int NUM_THREADS = 3;
    private static final long TIMESTEP = 1000; // Timestep in milliseconds


    private static final ExecutorService synchronizationPool = Executors.newFixedThreadPool(NUM_THREADS);
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        TrackSystem trackLines = new TrackSystem();
        WaysideSystem waysideController = new WaysideSystem();
        TrainSystem trainSystem = new TrainSystem();

        // Initialize and start modules
        // ...

        // Schedule the time synchronization task
        scheduledExecutorService.scheduleAtFixedRate(new TimeSynchronizationTask(trackLines, waysideController, trainSystem),
                0, TIMESTEP, TimeUnit.MILLISECONDS);

    }

    private static class TimeSynchronizationTask implements Runnable {
        private final TrackSystem trackLines;
        private final WaysideSystem waysideSystem;
        private final TrainSystem trainSystem;

        public TimeSynchronizationTask(TrackSystem trackLines, WaysideSystem waysideController, TrainSystem trainSystem) {
            this.trackLines = trackLines;
            this.waysideSystem = waysideController;
            this.trainSystem = trainSystem;
        }

        @Override
        public void run() {
            // Submit tasks to the thread pool for each module
//            synchronizationPool.submit(() -> trackModel.update());
//            synchronizationPool.submit(() -> waysideSystem.update());
//            synchronizationPool.submit(() -> trainSystem.update());
        }
    }
}
