package Framework.Simulation;

import CTCOffice.CTCOfficeImpl;
import Framework.GUI.mainMenu;
import Utilities.Constants;
import Utilities.BasicBlockParser;
import javafx.application.Application;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static Framework.Simulation.BaseApplication.initializeJavaFX;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final BasicBlockParser blockParser = BasicBlockParser.getInstance();


    public static double simSecond = 0;
    public static int    simMinute = 0;
    public static int    simHour   = 6;
    public static long   TIMESTEP  = (long) Constants.TIME_STEP_MS;
    public static double updatesPerSimSecond = 1/(Constants.TIME_STEP_S);

    private static ScheduledFuture<?> scheduledTask;
    public static TimeSynchronizationTask syncTask;

    public static double timeMultiplier = 1.0;

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
        WaysideSystem waysideSystem = new WaysideSystem(CTC);

        syncTask = new TimeSynchronizationTask(trackSystem, waysideSystem, trainSystem, CTC);
        syncTask.startScheduling(scheduledExecutorService, TIMESTEP);

        Application.launch(mainMenu.class, args);
    }

    public static void modifyTimeMultiplier(double newMultiplier) {
        Main.timeMultiplier = Math.floor(newMultiplier*2)/2; //Intervals of 0.5
        TIMESTEP = (long)(Constants.TIME_STEP_MS / timeMultiplier);

        logger.info("Modifying TIMESTEP to {}", TIMESTEP);
        Platform.runLater(() -> mainMenu.timeScaleLabel.setText(timeMultiplier + "x Speed"));
        syncTask.modifyTimestep(TIMESTEP);
    }

    public static void stopSimulation() {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(false);
        }
        scheduledExecutorService.shutdown();
        synchronizationPool.shutdown();
    }




    private static class TimeSynchronizationTask implements Runnable {
        private final TrackSystem trackSystem;
        private final WaysideSystem waysideSystem;
        private final TrainSystem trainSystem;
        private final CTCOfficeImpl CTC;
        private int timeIndex = 0;


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


        public TimeSynchronizationTask(TrackSystem trackSystem, WaysideSystem waysideController, TrainSystem trainSystem, CTCOfficeImpl ctcOffice) {
            this.trackSystem = trackSystem;
            this.waysideSystem = waysideController;
            this.trainSystem = trainSystem;
            this.CTC = ctcOffice;
        }


        @Override
        public void run() {
            long startTime = System.nanoTime();

            CTC.incrementTime();
            CountDownLatch latch = new CountDownLatch(2);

            // Submit tasks to the thread pool for waysideSystem and trainSystem



            synchronizationPool.submit(() -> {
                trainSystem.update();
                latch.countDown();
            });



            try {
                synchronizationPool.submit(() -> {
                    waysideSystem.update();
                    latch.countDown();
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }


            try {
                // Wait for both update methods to complete
                latch.await();
            } catch (InterruptedException e) {
                logger.error("Error in simulation at time {} : {}", simSecond, e.getMessage());
            }

            // Call trackSystem.update() after both update methods have finished
            synchronizationPool.submit(trackSystem::update);
            simSecond += Constants.TIME_STEP_S;


            if(++timeIndex >= updatesPerSimSecond) { //We only need to update the time label once per simulated second
                timeIndex = 0;

                if (simSecond % 60 == 0) {
                    simMinute++;
                }
                if (simMinute > 60) {
                    simMinute = 0;
                    simHour++;
                }
                if (simHour > 23) {
                    simHour = 0;
                }

                Platform.runLater(() -> mainMenu.timeLabel.setText(String.format("Time: %02d:%02d:%02d", simHour, simMinute, ((int) simSecond) % 60)));

            }

            long endTime = System.nanoTime();

            long duration = (endTime - startTime) / 1000; //microseconds
            if(duration > TIMESTEP * 1000) {
                double lag = (double)(duration/1000) - TIMESTEP; //milliseconds
                logger.warn("Simulation is running behind by {} ms", lag);
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
}
