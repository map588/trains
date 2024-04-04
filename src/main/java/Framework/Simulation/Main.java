package Framework.Simulation;

import CTCOffice.CTCOfficeImpl;
import Framework.GUI.mainMenu;
import javafx.application.Application;

import java.util.concurrent.*;

import static Framework.Simulation.BaseApplication.initializeJavaFX;

public class Main {

    private static final int NUM_THREADS = 2;
    private static final long TIMESTEP = 100; // Timestep in milliseconds

    private static final ExecutorService synchronizationPool = Executors.newFixedThreadPool(NUM_THREADS);
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private static TrackSystem trackSystem;
    private static WaysideSystem waysideController;
    private static TrainSystem trainSystem;


    public static void main(String[] args) {
        initializeJavaFX();

        System.out.println("Starting simulation...");
         trackSystem = new TrackSystem();
        CTCOfficeImpl CTC = CTCOfficeImpl.OFFICE;
         waysideController = new WaysideSystem(trackSystem, CTC, false);
         trainSystem = new TrainSystem();
        CTC.setTrackSystem(trackSystem);






        // Schedule the time synchronization task
        scheduledExecutorService.scheduleAtFixedRate(new TimeSynchronizationTask(trackSystem, waysideController, trainSystem),
                0, TIMESTEP, TimeUnit.MILLISECONDS);

        Application.launch(mainMenu.class, args);
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

    public static void stopSimulation() {
        scheduledExecutorService.shutdown();
        synchronizationPool.shutdown();
    }
}
