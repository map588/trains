package Framework.Simulation;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectEnum;
import Framework.Support.TrackSubject;
import PLCInput.PLCInput;
import PLCOutput.PLCOutput;
import ctcOffice.CTCOfficeImpl;
import ctcOffice.CTCOffice;
import javafx.application.Platform;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.Linecolor;
import trackModel.Interfaces.ITrackLine;
import trackModel.TrackBlock;
import trackModel.TrackLine;
import trackModel.TrackLineMap;
import utils.BasicBlockParser;
import utils.Tuple;
import waysideController.WaysideController;
import waysideController.WaysideControllerImpl; // Assuming this is the concrete class
import waysideController.WaysideSystem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static utils.Constants.*;

@DisplayName("WaysideSystem Concurrency Tests")
public class WaysideSystemConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(WaysideSystemConcurrencyTest.class);

    // Test Configuration
    private static final int SIMULATION_TICKS = 100;
    private static final long TICK_INTERVAL_MS = 50; // Interval for simulation steps
    private static final int THREAD_POOL_SIZE = 10; // For occupancy update threads
    private static final int TEST_TIMEOUT_SECONDS = 60;

    private WaysideSystem waysideSystem;
    private ITrackLine greenLine;
    private CTCOffice ctcOffice;
    private TrackLineMap trackLineMap;


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
            if (!e.getMessage().contains("Toolkit already running")) {
                logger.error("Failed to initialize JavaFX toolkit", e);
                throw e;
            }
            logger.info("JavaFX toolkit was already running.");
        } catch (Exception e) {
            logger.error("Unexpected error during JavaFX setup", e);
        }
    }

    @BeforeEach
    public void setup() throws IOException {
        BasicBlockParser.getInstance().updateFile(GREEN_LINE_FILE_PATH);
        
        // Initialize TrackLineMap and add the Green Line
        trackLineMap = TrackLineMap.getInstance();
        trackLineMap.clearLines(); // Clear any existing lines from other tests
        greenLine = trackLineMap.addLine(Linecolor.GREEN, GREEN_LINE_FILE_PATH);
        assertNotNull(greenLine, "Green Line should be initialized and added to TrackLineMap.");

        ctcOffice = new CTCOfficeImpl(); // Using a real CTCOffice

        // Initialize WaysideSystem. It should internally create controllers.
        // WaysideSystem's constructor takes TrackLineMap and CTCOffice.
        waysideSystem = new WaysideSystem(trackLineMap, ctcOffice);
        
        // WaysideSystem needs to be configured with controllers.
        // This might happen in its constructor if it auto-detects lines and PLC files,
        // or it might need explicit setup. Assuming WaysideSystem's constructor
        // handles loading controllers for lines present in TrackLineMap.
        // If not, we'd need:
        // List<Tuple<List<Integer>, List<Integer>>> greenLineSections = ...; (define sections)
        // waysideSystem.addController(Linecolor.GREEN, greenLineSections);
        // For this test, we rely on WaysideSystem's default init for Green Line.
        // We need to ensure PLC files are where WaysideControllerImpl expects them.
        // (e.g., "src/main/antlr/GreenLine1.plc")

        // Verify controllers were created for the Green Line
        assertNotNull(waysideSystem.getControllerMap(Linecolor.GREEN), "Controller map for Green Line should not be null.");
        assertFalse(waysideSystem.getControllerMap(Linecolor.GREEN).isEmpty(), "Should have controllers for Green Line.");
        logger.info("WaysideSystem, TrackLineMap, and CTCOffice initialized. Green Line controllers expected to be loaded.");
    }

    @AfterEach
    public void tearDown() {
        if (waysideSystem != null) {
            // WaysideSystem might have an executor to shut down.
            // If WaysideSystem has a shutdown() method:
            // waysideSystem.shutdown();
        }
        if (trackLineMap != null) {
            trackLineMap.clearLines();
        }
        BasicBlockParser.getInstance().clearData();
        TrackSubject.getInstance().clearSubjects();
        // Reset any other relevant singletons if necessary
        logger.info("Finished test, cleaned up resources.");
    }

    @Test
    @Timeout(value = TEST_TIMEOUT_SECONDS, unit = TimeUnit.SECONDS)
    @DisplayName("Concurrent PLC Execution Stability Test")
    public void concurrentPLCExecutionTest() throws InterruptedException {
        logger.info("Starting test: concurrentPLCExecutionTest");
        List<Throwable> exceptions = new CopyOnWriteArrayList<>();

        // The main test thread will call waysideSystem.update()
        for (int tick = 0; tick < SIMULATION_TICKS; tick++) {
            try {
                waysideSystem.update(); // This triggers concurrent PLC execution via waysideExecutor
                Thread.sleep(TICK_INTERVAL_MS / 10); // Small delay, or match tick rate
            } catch (Throwable e) {
                logger.error("Exception during waysideSystem.update(): {}", e.getMessage(), e);
                exceptions.add(e);
                break; // Stop test on first major failure
            }
        }

        assertTrue(exceptions.isEmpty(), "Exceptions occurred during concurrent PLC execution: " +
                exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", ")));

        // Check basic properties (highly PLC-dependent, focus on stability)
        // Example: Check if any controller has non-default output if PLC is expected to run
        Map<Integer, WaysideController> greenLineControllers = waysideSystem.getControllerMap(Linecolor.GREEN);
        assertNotNull(greenLineControllers, "Green Line controllers map is null.");
        assertFalse(greenLineControllers.isEmpty(), "No controllers found for Green Line.");

        for (WaysideController controller : greenLineControllers.values()) {
            assertTrue(controller instanceof WaysideControllerImpl, "Controller should be an instance of WaysideControllerImpl");
            WaysideControllerImpl controllerImpl = (WaysideControllerImpl) controller;
            
            // Simple check: PLC has run, so some output might exist.
            // This is a very generic check. Specific PLC knowledge would allow better assertions.
            PLCOutput output = controllerImpl.getPLCOutput();
            assertNotNull(output, "PLC output for controller " + controllerImpl.getID() + " should not be null if PLC ran.");
            
            // Example: If PLCs are expected to set initial signal states or switch positions.
            // This requires knowing the PLC logic (e.g. "GreenLine1.plc").
            // For instance, if a switch defaults to a certain position:
            // int knownSwitchBlockId = ...; // A switch block ID controlled by this PLC
            // if (controllerImpl.controlsBlock(knownSwitchBlockId)) {
            //    TrackBlock switchBlock = greenLine.getBlock(knownSwitchBlockId);
            //    assertNotNull(switchBlock, "Switch block " + knownSwitchBlockId + " should exist.");
            //    // Specific assertion based on expected PLC outcome
            //    // assertEquals(expectedSwitchPosition, switchBlock.getSwitchState(), "Switch state not as expected by PLC");
            // }
            logger.info("Controller {} PLC output processed. Authority calculations size: {}", controllerImpl.getID(), output.getAuthorities().size());
        }
        logger.info("Finished test: concurrentPLCExecutionTest. PLC executions were stable.");
    }


    @Test
    @Timeout(value = TEST_TIMEOUT_SECONDS, unit = TimeUnit.SECONDS)
    @DisplayName("Concurrent Track Occupancy Updates While PLC Executing")
    public void concurrentTrackOccupancyUpdatesTest() throws InterruptedException, ExecutionException {
        logger.info("Starting test: concurrentTrackOccupancyUpdatesTest");
        List<Throwable> exceptions = new CopyOnWriteArrayList<>();
        ExecutorService occupancyUpdaterExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        List<Future<?>> occupancyTasks = new ArrayList<>();

        Map<Integer, WaysideController> greenLineControllers = waysideSystem.getControllerMap(Linecolor.GREEN);
        if (greenLineControllers == null || greenLineControllers.isEmpty()) {
            fail("No Green Line controllers available to test occupancy updates.");
        }

        // Start waysideSystem.update() in a separate thread to run continuously
        Thread waysideUpdateThread = new Thread(() -> {
            try {
                for (int tick = 0; tick < SIMULATION_TICKS * 2; tick++) { // Longer run for this test
                    waysideSystem.update();
                    Thread.sleep(TICK_INTERVAL_MS / 10);
                }
            } catch (Throwable e) {
                logger.error("Exception in waysideSystem.update() thread: {}", e.getMessage(), e);
                exceptions.add(e);
            }
        });
        waysideUpdateThread.start();

        Random random = new Random();
        List<TrackBlock> allGreenLineBlocks = greenLine.getBlocks();
        if (allGreenLineBlocks.isEmpty()) {
            fail("Green Line has no blocks to simulate occupancy on.");
        }

        // Create tasks to simulate concurrent occupancy changes
        for (int i = 0; i < greenLineControllers.size() * 5; i++) { // Multiple updates per controller
            Runnable occupancyTask = () -> {
                try {
                    // Pick a random controller
                    WaysideControllerImpl controller = (WaysideControllerImpl) new ArrayList<>(greenLineControllers.values())
                                                                .get(random.nextInt(greenLineControllers.size()));
                    
                    List<Integer> controlledBlockIds = controller.getControlledBlockIds();
                    if (controlledBlockIds.isEmpty()) {
                        logger.warn("Controller {} has no controlled blocks, skipping occupancy update for it.", controller.getID());
                        return;
                    }

                    // Pick a random block controlled by this controller
                    int targetBlockId = controlledBlockIds.get(random.nextInt(controlledBlockIds.size()));
                    boolean isOccupied = random.nextBoolean();

                    logger.trace("Simulating occupancy: controller {}, block {}, occupied={}", controller.getID(), targetBlockId, isOccupied);
                    controller.trackModelSetOccupancy(targetBlockId, isOccupied); // Test this method

                    // Occasionally simulate a move occupancy
                    if (random.nextDouble() < 0.3 && isOccupied) { // If setting to occupied, sometimes also simulate a move
                        int oldBlockId = -1;
                        // Find a non-occupied block also controlled by this controller to be the "old" block
                        for(int controlledId : controlledBlockIds) {
                            if (controlledId != targetBlockId && !greenLine.getBlock(controlledId).isOccupied()) {
                                oldBlockId = controlledId;
                                break;
                            }
                        }
                        if (oldBlockId != -1) {
                             logger.trace("Simulating move: controller {}, from oldBlock {}, to newBlock {}", controller.getID(), oldBlockId, targetBlockId);
                             controller.trackModelMoveOccupancy(oldBlockId, targetBlockId);
                        }
                    }
                    Thread.sleep(random.nextInt((int)TICK_INTERVAL_MS) + 10); // Random small delay
                } catch (Throwable e) {
                    logger.error("Exception in occupancy update task: {}", e.getMessage(), e);
                    exceptions.add(e);
                }
            };
            occupancyTasks.add(occupancyUpdaterExecutor.submit(occupancyTask));
        }

        // Wait for all occupancy update tasks to complete
        for (Future<?> future : occupancyTasks) {
            try {
                future.get(TEST_TIMEOUT_SECONDS / 2, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                exceptions.add(new TimeoutException("Occupancy updater task timed out."));
            }
        }

        // Wait for waysideSystem update thread to finish its run
        waysideUpdateThread.join((long) (SIMULATION_TICKS * 2 * (TICK_INTERVAL_MS / 10.0) + 5000));
        if (waysideUpdateThread.isAlive()) {
            waysideUpdateThread.interrupt(); // Attempt to stop it
            exceptions.add(new TimeoutException("WaysideSystem update thread did not complete in time."));
        }

        occupancyUpdaterExecutor.shutdown();
        if (!occupancyUpdaterExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
            occupancyUpdaterExecutor.shutdownNow();
        }

        assertTrue(exceptions.isEmpty(), "Exceptions occurred during concurrent occupancy updates test: " +
                exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", ")));

        // Assertions: Check that controllers' internal states (e.g., perceived occupancy) are plausible.
        // This is PLC-dependent. A basic check is that the occupancy information was received.
        for (WaysideController controller : greenLineControllers.values()) {
            WaysideControllerImpl controllerImpl = (WaysideControllerImpl) controller;
            PLCInput plcInput = controllerImpl.getPLCInput();
            assertNotNull(plcInput, "PLCInput object for controller " + controllerImpl.getID() + " should not be null.");
            
            // Check if PLC input reflects some occupancy (if any was set to true and not later cleared)
            // This is a weak check as final state is random.
            // More robust: verify specific scenarios if PLC logic is known.
            boolean anyBlockOccupiedInInput = false;
            for (int blockId : controllerImpl.getControlledBlockIds()) {
                if (plcInput.getOccupancy(blockId)) {
                    anyBlockOccupiedInInput = true;
                    break;
                }
            }
            if (!controllerImpl.getControlledBlockIds().isEmpty()) {
                 logger.info("Controller {} final PLC input state: Any block occupied? {}. (Note: depends on random final occupancy settings)",
                            controllerImpl.getID(), anyBlockOccupiedInInput);
            }
            // No specific assertion on anyBlockOccupiedInInput due to randomness, unless we track last states.
            // The main assertion is stability (no exceptions).
        }

        logger.info("Finished test: concurrentTrackOccupancyUpdatesTest. System remained stable.");
    }
}
