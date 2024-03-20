package Framework.Simulation;

import Common.TrackModel;
import trackModel.TrackModelImpl;

import java.util.concurrent.*;

public class Main {

    private static final int NUM_THREADS = 3;
    private static final long TIMESTEP = 1000; // Timestep in milliseconds


    private static final ExecutorService synchronizationPool = Executors.newFixedThreadPool(NUM_THREADS);
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        TrackModel trackModel = new TrackModelImpl();
        WaysideSystem waysideController = new WaysideSystem();
        TrainSystem trainSystem = new TrainSystem();

        // Initialize and start modules
        // ...

        // Schedule the time synchronization task
        scheduledExecutorService.scheduleAtFixedRate(new TimeSynchronizationTask(trackModel, waysideController, trainSystem),
                0, TIMESTEP, TimeUnit.MILLISECONDS);

    }

    private static class TimeSynchronizationTask implements Runnable {
        private final TrackModel trackModel;
        private final WaysideSystem waysideSystem;
        private final TrainSystem trainSystem;

        public TimeSynchronizationTask(TrackModel trackModel, WaysideSystem waysideController, TrainSystem trainSystem) {
            this.trackModel = trackModel;
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
