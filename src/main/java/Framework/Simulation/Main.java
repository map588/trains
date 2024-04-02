package Framework.Simulation;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final int NUM_THREADS = 2;
    private static final long TIMESTEP = 10; // Timestep in milliseconds

    private static final ExecutorService synchronizationPool = Executors.newFixedThreadPool(NUM_THREADS);
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private static TrackSystem trackSystem;
    private static WaysideSystem waysideController;
    private static TrainSystem trainSystem;

    public static void main(String[] args) {
         trackSystem = new TrackSystem();
         waysideController = new WaysideSystem();
         trainSystem = new TrainSystem();

        // Initialize and start modules
        // ...

        // Schedule the time synchronization task
        scheduledExecutorService.scheduleAtFixedRate(new TimeSynchronizationTask(trackSystem, waysideController, trainSystem),
                0, TIMESTEP, TimeUnit.MILLISECONDS);
    }

    public void changeScheduledExecutionInterval(long newIntervalMs) {
        scheduledExecutorService.shutdown();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new TimeSynchronizationTask(trackSystem, waysideController, trainSystem),
                0, newIntervalMs, TimeUnit.MILLISECONDS);
    }

    private static class TimeSynchronizationTask implements Runnable {
        private final TrackSystem trackSystem;
        private final WaysideSystem waysideSystem;
        private final TrainSystem trainSystem;

        public TimeSynchronizationTask(TrackSystem trackSystem, WaysideSystem waysideController, TrainSystem trainSystem) {
            this.trackSystem = trackSystem;
            this.waysideSystem = waysideController;
            this.trainSystem = trainSystem;
        }

        @Override
        public void run() {
            CountDownLatch latch = new CountDownLatch(2);

            // Submit tasks to the thread pool for waysideSystem and trainSystem
            synchronizationPool.submit(() -> {
                waysideSystem.update();
                latch.countDown();
            });

            synchronizationPool.submit(() -> {
                trainSystem.update();
                latch.countDown();
            });

            try {
                // Wait for both update methods to complete
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Call trackSystem.update() after both update methods have finished
            trackSystem.update();
        }
    }
}
