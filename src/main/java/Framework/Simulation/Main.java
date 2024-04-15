package Framework.Simulation;

import CTCOffice.CTCOfficeImpl;
import Framework.GUI.mainMenu;
import Utilities.Constants;
import Utilities.GlobalBasicBlockParser;
import javafx.application.Application;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static Framework.Simulation.BaseApplication.initializeJavaFX;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final GlobalBasicBlockParser blockParser = GlobalBasicBlockParser.getInstance();


    public static double simTimeElapsed = 0;
    public static double simTimeMultiplier = 1;
    public static long TIMESTEP = 100;

    private static ScheduledFuture<?> scheduledTask;

    private static TimeSynchronizationTask syncTask;

    private static final int NUM_THREADS = 3;

    private static final ExecutorService synchronizationPool = Executors.newFixedThreadPool(NUM_THREADS);
    private static ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        initializeJavaFX();
        System.out.println("Starting simulation...");

        CTCOfficeImpl CTC = CTCOfficeImpl.OFFICE;

        TrainSystem trainSystem = new TrainSystem();
        TrackSystem trackSystem = new TrackSystem(trainSystem);
        CTC.setTrackSystem(trackSystem);
        WaysideSystem waysideSystem = new WaysideSystem(trackSystem, CTC, false);

        syncTask = new TimeSynchronizationTask(trackSystem, waysideSystem, trainSystem);
        syncTask.startScheduling(scheduledExecutorService, TIMESTEP);

        Application.launch(mainMenu.class, args);
    }

    public static void modifyTimeMultiplier(double newMultiplier) {
        long timestep = (long)(100 / newMultiplier);
        logger.info("Modifying timestep to {}", timestep);
        modifyTimestep(timestep);
    }

    public static void modifyTimestep(long newTimestep) {
        syncTask.modifyTimestep(newTimestep);
    }

    private static class TimeSynchronizationTask implements Runnable {
        private final TrackSystem trackSystem;
        private final WaysideSystem waysideSystem;
        private final TrainSystem trainSystem;


        public void startScheduling(ScheduledExecutorService scheduledExecutorService, long initialTimestep) {
            Main.scheduledExecutorService = scheduledExecutorService;
            Main.TIMESTEP = initialTimestep;
            scheduleTask();
        }

        public void modifyTimestep(long newTimestep) {
            if (scheduledTask != null && !scheduledTask.isDone()) {
                scheduledTask.cancel(false);
            }
            Main.TIMESTEP = newTimestep;
            scheduleTask();
        }

        private void scheduleTask() {
            Main.scheduledTask = scheduledExecutorService.scheduleAtFixedRate(this,
                    0,
                    Main.TIMESTEP,
                    TimeUnit.MILLISECONDS);
        }

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
                logger.error("Error in simulation at time {} : {}", simTimeElapsed,  e.getMessage());
            }

            // Call trackSystem.update() after both update methods have finished
            synchronizationPool.submit(trackSystem::update);
            simTimeElapsed += Constants.TIME_STEP_S;
            Platform.runLater(() -> mainMenu.timeLabel.setText("Time: " + simTimeElapsed + "s"));
        }
    }

    public static void stopSimulation() {
        // Cancel the scheduled task
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(false);
        }

        // Shutdown the scheduledExecutorService
        if (!scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
            try {
                if (!scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduledExecutorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // Shutdown the synchronizationPool
        if (!synchronizationPool.isShutdown()) {
            synchronizationPool.shutdown();
            try {
                if (!synchronizationPool.awaitTermination(1, TimeUnit.SECONDS)) {
                    synchronizationPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                synchronizationPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
