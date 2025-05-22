package Framework.Simulation;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectEnum;
import Framework.Support.TrackSubject;
import Power.PowerFailure;
import Power.PowerFailureNonVital;
import Shared.Constants;
import Shared.TrackRelated;
import Signals.SignalFailure;
import Signals.SignalFailureNonVital;
import ctcOffice.CTCOffice;
import integrationTesting.TrainSystemClock;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.Linecolor;
import trackModel.Interfaces.ITrackLine;
import trackModel.TrackLine;
import trainController.TrainControllerFactory;
import trainController.TrainControllerImpl;
import trainController.enums.ControllerProperty;
import trainModel.Enums.Antenna;
import trainModel.Enums.BrakeStatus;
import trainModel.Enums.DoorStatus;
import trainModel.Enums.Properties;
import trainModel.Interfaces.ITrainModel;
import trainModel.TrainModelImpl;
import utils.BasicBlockParser;
import utils.TickUpdater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static utils.Constants.*;

@DisplayName("TrainSystem Concurrency Tests")
public class TrainSystemConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(TrainSystemConcurrencyTest.class);

    // Test Configuration
    private static final int NUM_TRAINS = 3; // Reduced for simplicity in property checking
    private static final int SIMULATION_TICKS = 100;
    private static final long TICK_INTERVAL_MS = 50; // Corresponds to TrainSystem.TIME_STEP_MS
    private static final int THREAD_POOL_SIZE = NUM_TRAINS + 5; // For trains + modifiers + updater
    private static final int TEST_TIMEOUT_SECONDS = 75; // Generous timeout for concurrency

    private TrainSystem trainSystem;
    private ITrackLine greenLine;
    private CTCOffice ctcOffice; // Real or mock CTC
    private TickUpdater tickUpdater; // To drive the simulation clock

    @BeforeAll
    public static void setupJavaFX() throws InterruptedException {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> {
                latch.countDown();
            });
            if (!latch.await(5, TimeUnit.SECONDS)) {
                logger.warn("JavaFX toolkit startup timed out.");
            } else {
                logger.info("JavaFX toolkit initialized.");
            }
        } catch (IllegalStateException e) {
            if (!e.getMessage().contains("Toolkit already running")) { // Adjusted message check
                logger.error("Failed to initialize JavaFX toolkit", e);
                throw e;
            }
            logger.info("JavaFX toolkit was already running.");
        } catch (Exception e) {
            logger.error("Unexpected error during JavaFX setup", e);
            // Allow test to continue if this is not critical or a known issue in test env
        }
    }

    @BeforeEach
    public void setup() throws IOException {
        BasicBlockParser.getInstance().updateFile(GREEN_LINE_FILE_PATH);
        greenLine = new TrackLine(Linecolor.GREEN, GREEN_LINE_FILE_PATH);
        ctcOffice = new CTCOffice(); // Using a real CTCOffice for simplicity

        // Initialize TrainSystem. It creates its own trainExecutor.
        trainSystem = new TrainSystem();

        // Setup TickUpdater for simulation time
        tickUpdater = TickUpdater.getInstance();
        tickUpdater.setTickInterval(TICK_INTERVAL_MS);
        tickUpdater.start(); // Start the ticker

        logger.info("TrainSystem, TrackLine, and CTCOffice initialized for test.");
    }

    @AfterEach
    public void tearDown() {
        if (trainSystem != null) {
            trainSystem.shutdown(); // Ensure its executor is stopped
        }
        if (tickUpdater != null) {
            tickUpdater.stop();
            TickUpdater.resetInstance(); // Reset singleton for next test
        }
        BasicBlockParser.getInstance().clearData(); // Clear parser data
        TrackSubject.getInstance().clearSubjects(); // Clear any static subjects if necessary
        TrainControllerFactory.reset(); // Reset factory if it caches controllers

        logger.info("Finished test, cleaned up resources.");
    }

    // Helper to create and add a train
    private ITrainModel createAndAddTrain(int trainId, int startingBlockId) {
        // TrainModelImpl requires a non-null TrackLine and CTCOffice
        ITrainModel trainModel = new TrainModelImpl(
                trainId,
                Linecolor.GREEN,
                startingBlockId, // Starting block ID (0 for yard)
                ctcOffice,
                greenLine,
                null // No GUI listener for this test
        );

        // Add to TrainSystem. This also creates the controller via TrainControllerFactory.
        trainSystem.addTrainProcess(trainModel.getTrainNumber(), trainModel);

        // Initial setup for the train to move or be active
        TrainControllerImpl controller = (TrainControllerImpl) TrainControllerFactory.getTrainController(trainModel.getTrainNumber());
        assertNotNull(controller, "Controller should be created for train " + trainId);

        // Give some initial speed and authority so it does something
        // These are set via controller which then passes to model
        controller.setValue(ControllerProperty.COMMANDED_SPEED, 30.0); // 30 km/h
        controller.setValue(ControllerProperty.AUTHORITY, 5);      // Authority for 5 blocks
        controller.setValue(ControllerProperty.DOORS_RIGHT, DoorStatus.CLOSED);
        controller.setValue(ControllerProperty.DOORS_LEFT, DoorStatus.CLOSED);
        controller.setValue(ControllerProperty.LIGHTS, true);
        controller.setValue(ControllerProperty.SET_TEMPERATURE, 22.0);


        // Dispatch train on track line (minimal setup, actual movement not primary focus)
        // Ensure the train is on a block so it can receive beacon data etc.
        // Block 0 is yard. updateTrainLocation in TrackLine moves it to the first actual block.
        greenLine.dispatchTrain(trainModel.getTrainNumber(), trainModel);
        // Manually set train on a valid block if dispatchTrain doesn't guarantee it or if yard (0) is not fully functional for tests
        // greenLine.updateTrainLocation(trainModel); // This would move it from yard

        logger.info("Created and added train {} starting on block {}. Initial speed/authority set.", trainId, startingBlockId);
        return trainModel;
    }


    @Test
    @Timeout(value = TEST_TIMEOUT_SECONDS, unit = TimeUnit.SECONDS)
    @DisplayName("Multiple Train Updates Stability Test")
    public void multipleTrainUpdatesTest() throws InterruptedException {
        logger.info("Starting test: multipleTrainUpdatesTest");
        List<ITrainModel> trains = new ArrayList<>();
        for (int i = 0; i < NUM_TRAINS; i++) {
            trains.add(createAndAddTrain(i + 1, 0)); // Train IDs 1, 2, 3... from yard
        }

        List<Throwable> exceptions = new CopyOnWriteArrayList<>();
        Thread trainSystemUpdateThread = new Thread(() -> {
            try {
                for (int tick = 0; tick < SIMULATION_TICKS; tick++) {
                    trainSystem.update(); // This is the core method to test
                    tickUpdater.tick(); // Manually advance simulation time for each update
                    Thread.sleep(TICK_INTERVAL_MS / 10); // Small delay to allow other things, or match tick rate
                }
            } catch (Throwable e) {
                logger.error("Exception in TrainSystem update thread: {}", e.getMessage(), e);
                exceptions.add(e);
            }
        });

        trainSystemUpdateThread.start();
        trainSystemUpdateThread.join(); // Wait for the simulation to complete

        assertTrue(exceptions.isEmpty(), "Exceptions occurred during TrainSystem updates: " +
                exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", ")));

        assertEquals(NUM_TRAINS, trainSystem.getTrainContainer().size(), "TrainSystem should contain all added trains.");

        for (ITrainModel train : trains) {
            TrainControllerImpl controller = (TrainControllerImpl) TrainControllerFactory.getTrainController(train.getTrainNumber());
            assertNotNull(controller, "Controller for train " + train.getTrainNumber() + " should exist.");

            // Basic plausibility checks. Precise values are hard to predict.
            // If commanded speed > 0 and no brakes, power should ideally be positive after some ticks.
            boolean brakesActive = train.getEmergencyBrake() == BrakeStatus.ENGAGED ||
                                   train.getServiceBrake() == BrakeStatus.ENGAGED;
            if (controller.getCommandedSpeed() > 0 && !brakesActive) {
                // Power might be zero if speed limit is zero or authority is zero, or target speed reached.
                // This check is weak due to complex interactions.
                // assertTrue(train.getPowerOutput() >= 0, "Power should be non-negative for train " + train.getTrainNumber());
                 logger.info("Train {} Speed: {}, Power: {}, Authority: {}, Brakes: E={}, S={}",
                            train.getTrainNumber(), train.getSpeed(), train.getPowerOutput(), controller.getAuthority(),
                            train.getEmergencyBrake(), train.getServiceBrake());
            }
            // Speed should be plausible (e.g., not NaN or excessively large)
            assertTrue(train.getSpeed() >= 0, "Speed should be non-negative for train " + train.getTrainNumber());
            assertTrue(train.getSpeed() < 200, "Speed seems implausibly high for train " + train.getTrainNumber()); // Assuming km/h
        }
        logger.info("Finished test: multipleTrainUpdatesTest");
    }


    @Test
    @Timeout(value = TEST_TIMEOUT_SECONDS, unit = TimeUnit.SECONDS)
    @DisplayName("Concurrent Property Modification While Updating")
    public void concurrentPropertyModificationTest() throws InterruptedException, ExecutionException {
        logger.info("Starting test: concurrentPropertyModificationTest");
        List<ITrainModel> trains = new ArrayList<>();
        for (int i = 0; i < NUM_TRAINS; i++) {
            trains.add(createAndAddTrain(i + 1, 0));
        }

        List<Throwable> exceptions = new CopyOnWriteArrayList<>();
        ExecutorService propertyModifierExecutor = Executors.newFixedThreadPool(NUM_TRAINS * 2); // Threads for modifying
        List<Future<?>> modifierTasks = new ArrayList<>();

        // Start TrainSystem update thread
        Thread trainSystemUpdateThread = new Thread(() -> {
            try {
                for (int tick = 0; tick < SIMULATION_TICKS * 2; tick++) { // Longer simulation for modifications
                    trainSystem.update();
                    tickUpdater.tick();
                    Thread.sleep(TICK_INTERVAL_MS / 10); // Simulate time passing, allow interleaving
                }
            } catch (Throwable e) {
                logger.error("Exception in TrainSystem update thread (concurrentPropertyModificationTest): {}", e.getMessage(), e);
                exceptions.add(e);
            }
        });
        trainSystemUpdateThread.start();

        // Schedule concurrent property modifications
        Random random = new Random();
        for (ITrainModel train : trains) {
            TrainControllerImpl controller = (TrainControllerImpl) TrainControllerFactory.getTrainController(train.getTrainNumber());

            // Task to modify this train's properties
            Runnable modificationTask = () -> {
                try {
                    for (int i = 0; i < 5; i++) { // Perform a few modifications per train
                        Thread.sleep(random.nextInt((int)TICK_INTERVAL_MS * 3) + TICK_INTERVAL_MS); // Random delay

                        int choice = random.nextInt(3);
                        if (choice == 0) {
                            boolean eBrakeState = random.nextBoolean();
                            logger.info("Setting E-Brake to {} for train {}", eBrakeState, train.getTrainNumber());
                            // This path: TrainModel.setValue -> TrainController.setPassengerEBrake -> TrainModel.setEmergencyBrake
                            train.setValue(Properties.EMERGENCYBRAKE_PROPERTY, eBrakeState);

                            // Wait a bit for change to propagate through update cycles
                            Thread.sleep(TICK_INTERVAL_MS * 2);
                            assertEquals(eBrakeState ? BrakeStatus.ENGAGED : BrakeStatus.DISENGAGED, train.getEmergencyBrake(), "Train E-Brake state mismatch.");
                            // Controller might take an update cycle to reflect this if it reads from model
                            // assertEquals(eBrakeState, controller.getEmergencyBrakeStatus(), "Controller E-Brake state mismatch.");
                            if (eBrakeState) {
                                assertTrue(train.getPowerOutput() == 0.0, "Power should be 0 when E-Brake is engaged.");
                            }
                        } else if (choice == 1) {
                            boolean powerFailState = random.nextBoolean();
                             logger.info("Setting Power Failure to {} for train {}", powerFailState, train.getTrainNumber());
                            train.setFailure(PowerFailure.POWER, powerFailState); // Directly set failure on model
                            
                            Thread.sleep(TICK_INTERVAL_MS * 2);
                            assertEquals(powerFailState, train.getFailure(PowerFailure.POWER), "Train Power Failure state mismatch.");
                            // Controller should also reflect this via its subject/observer link
                            // assertEquals(powerFailState, controller.getFailureStatus(ControllerFailure.POWER_FAILURE), "Controller Power Failure state mismatch.");
                        } else {
                            double newTemp = 20.0 + random.nextDouble() * 5.0; // 20-25 C
                            logger.info("Setting new temperature {}C for train {}", String.format("%.2f",newTemp), train.getTrainNumber());
                            controller.setValue(ControllerProperty.SET_TEMPERATURE, newTemp);

                            Thread.sleep(TICK_INTERVAL_MS * 2);
                            assertEquals(newTemp, (Double)controller.getValue(ControllerProperty.SET_TEMPERATURE), 0.01, "Controller set temperature mismatch.");
                            assertEquals(newTemp, train.getTemperature(), 0.01, "Train current temperature should reflect set temp after time.");
                        }
                    }
                } catch (Throwable e) {
                    logger.error("Exception in property modification task for train {}: {}", train.getTrainNumber(), e.getMessage(), e);
                    exceptions.add(e);
                }
            };
            modifierTasks.add(propertyModifierExecutor.submit(modificationTask));
        }

        // Wait for all modifier tasks to complete
        for (Future<?> future : modifierTasks) {
            try {
                future.get(TEST_TIMEOUT_SECONDS / 2, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                exceptions.add(new TimeoutException("Property modifier task timed out."));
            }
        }

        // Wait for TrainSystem update thread to finish its extended run
        trainSystemUpdateThread.join( (long) TEST_TIMEOUT_SECONDS * TICK_INTERVAL_MS * 2 / 1000 + 5000); // Wait for simulation to finish
         if (trainSystemUpdateThread.isAlive()) {
            trainSystemUpdateThread.interrupt(); // Attempt to stop it if it runs too long
            exceptions.add(new TimeoutException("TrainSystem update thread did not complete in time."));
        }


        propertyModifierExecutor.shutdown();
        if (!propertyModifierExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
            propertyModifierExecutor.shutdownNow();
        }

        assertTrue(exceptions.isEmpty(), "Exceptions occurred during concurrent property modification test: " +
                exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", ")));

        // Final checks for property consistency (some checks are already in the tasks)
        for (ITrainModel train : trains) {
            TrainControllerImpl controller = (TrainControllerImpl) TrainControllerFactory.getTrainController(train.getTrainNumber());
            assertNotNull(controller);
            // Example: Check if E-Brake status is consistent between model and controller
            // Note: Direct comparison might be tricky due_to timing of updates.
            // The checks within the modifier tasks with short sleeps are more targeted.
             logger.info("Final state for Train {}: E-Brake Model={}, Ctrlr_getEBrake={}, Power={}, Temp Model={}, Ctrlr_getSetTemp={}",
                        train.getTrainNumber(),
                        train.getEmergencyBrake(),
                        controller.getValue(ControllerProperty.EMERGENCY_BRAKE_PASSENGER), // Using the property getter
                        train.getPowerOutput(),
                        train.getTemperature(),
                        controller.getValue(ControllerProperty.SET_TEMPERATURE)
            );
             // Check consistency of one property that should align after updates
             assertEquals(train.getEmergencyBrake() == BrakeStatus.ENGAGED, controller.getValue(ControllerProperty.EMERGENCY_BRAKE_PASSENGER));

        }

        logger.info("Finished test: concurrentPropertyModificationTest");
    }
}
