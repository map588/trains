package trackModel;

import ctcOffice.CTCOffice;
import javafx.application.Platform;
import main.Main;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.Linecolor;
import shared.Suggestion;
import shared.TrainCrash;
import trackModel.Enums.TrackProperty;
import trackModel.Interfaces.ITrackLine;
import trackModel.TrackBlock;
import trackModel.TrackLine;
import trainModel.Interfaces.ITrainModel;
import trainModel.TrainModelImpl;
import trainModel.Enums.Antenna;
import trainModel.Enums.BrakeStatus;
import utils.BasicBlockParser;
import utils.Tuple;
import waysideController.WaysideController;
import trackModel.TrackBlockSubject;
import trackModel.LineSubjectMap;
import trackModel.LineSubject;
import waysideController.WaysideSystem;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static utils.Constants.*;

@DisplayName("TrackLine Concurrency Tests")
public class TrackLineConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(TrackLineConcurrencyTest.class);
    private ITrackLine trackLine;
    private WaysideSystem waysideSystem;
    private CTCOffice ctcOffice;

    // Constants for test configuration
    private static final int NUM_TRAINS = 5;
    private static final int SIMULATION_TICKS = 100; // Number of simulation steps
    private static final long TICK_INTERVAL_MS = 50; // Duration of each tick in milliseconds
    private static final int THREAD_POOL_SIZE = 10;
    private static final int TEST_TIMEOUT_SECONDS = 60; // Increased timeout for concurrency tests

    @BeforeAll
    public static void setupJavaFX() throws InterruptedException {
        try {
            // Try to initialize Main which sets up JavaFX
            // This is a bit of a workaround. Ideally, Main.java would have a static init method for JavaFX.
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(() -> {
                // No actual UI needed, just toolkit init
                latch.countDown();
            });
            if (!latch.await(5, TimeUnit.SECONDS)) {
                logger.warn("JavaFX toolkit startup timed out.");
            }
             logger.info("JavaFX toolkit initialized or already running.");
        } catch (IllegalStateException e) {
            // Toolkit might already be initialized if other tests did it
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
        // Initialize BasicBlockParser for the Green Line
        BasicBlockParser.getInstance().updateFile(GREEN_LINE_FILE_PATH);
        // Initialize TrackLine for the Green Line
        trackLine = new TrackLine(Linecolor.GREEN, GREEN_LINE_FILE_PATH);

        // Initialize WaysideSystem and CTCOffice as they might be needed by TrackLine
        // This mimics part of the Main.java setup
        Main. waysideSystem = WaysideSystem.getInstance();
        waysideSystem = Main.waysideSystem;
        Main.ctcOffice = new CTCOffice();
        ctcOffice = Main.ctcOffice;

        // Create and add wayside controllers for the Green Line
        // Assuming sections are predefined or can be dynamically created for the test
        List<Tuple<List<Integer>, List<Integer>>> greenLineSections = new ArrayList<>();
        // Example: Add all blocks to one controller for simplicity in test
        // In a real scenario, these would be actual sections.
        List<Integer> allBlockIds = trackLine.getBlocks().stream().map(TrackBlock::getBlockNumber).collect(Collectors.toList());
        greenLineSections.add(new Tuple<>(allBlockIds, new ArrayList<>())); // Assuming no overlap for simplicity

        waysideSystem.addController(Linecolor.GREEN, greenLineSections);
        logger.info("TrackLine and dependencies initialized for Green Line.");
    }

    @AfterEach
    public void tearDown() {
        // Clean up resources if necessary
        if (trackLine != null) {
            // Any specific cleanup for trackLine, e.g., stopping trains, clearing occupancy
            trackLine.closeLine(); // Assuming a method like this exists or should be added
        }
        if (waysideSystem != null) {
            waysideSystem.destroy(); // Assuming a method to clean up wayside controllers
        }
        // Reset singletons or static data if needed for test isolation, though this can be tricky
        BasicBlockParser.getInstance().clearData(); // Example: method to clear parsed data
        logger.info("Finished test, cleaned up resources.");
    }

    private ITrainModel createAndDispatchTrain(int trainId, int startingBlockId) {
        // Create a new train
        ITrainModel train = new TrainModelImpl(
                trainId,
                Linecolor.GREEN,
                startingBlockId,
                ctcOffice, // Pass the mock/real CTC
                trackLine, // Pass the TrackLine instance
                null // Assuming no GUI update listener for this test
        );

        // Initial dispatch: place train on track and set initial speed/authority
        // TrackLine might handle initial placement internally or require explicit calls
        trackLine.dispatchTrain(trainId, train); // Assuming dispatchTrain adds to occupancyMap
        
        // Set some initial authority and speed to allow movement
        // These might need to be set on the block or directly to the train via TrackLine
        TrackBlock startingBlock = trackLine.getBlock(startingBlockId);
        if (startingBlock != null) {
            trackLine.setTrainAuthority(startingBlockId, 10); // Authority for 10 blocks
            trackLine.setCommandedSpeed(startingBlockId, 30); // Commanded speed 30 km/h
            logger.info("Dispatched train {} on block {} with initial authority and speed.", trainId, startingBlockId);
        } else {
            logger.warn("Starting block {} not found for train {}.", startingBlockId, trainId);
            // Fail test if starting block is invalid, as train cannot be placed.
            fail("Starting block " + startingBlockId + " not found for train " + trainId);
        }
        
        // Train needs to pick up initial command speed and authority
        // This might happen via its update loop or an explicit call
        // For simplicity, assume train's internal update will eventually pick these up.
        // Or, directly set on train if necessary for test setup, though ideally TrackLine interaction is key.
        // train.setCommandedSpeed(30);
        // train.setAuthority(10);

        return train;
    }


    @Test
    @Timeout(value = TEST_TIMEOUT_SECONDS, unit = TimeUnit.SECONDS)
    @DisplayName("Multiple Trains Moving Simultaneously")
    public void multipleTrainsMovingSimultaneously() throws InterruptedException, ExecutionException {
        logger.info("Starting test: multipleTrainsMovingSimultaneously");
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        List<ITrainModel> trains = new ArrayList<>();
        Map<Integer, Future<?>> trainTasks = new HashMap<>();
        List<Throwable> exceptions = new CopyOnWriteArrayList<>(); // Thread-safe list for exceptions

        // Dispatch trains onto unique starting blocks if possible, or handle sequential dispatch
        // For Green Line, block 77 is yard. Let's dispatch from there or nearby.
        // Ensure enough blocks for each train if dispatched simultaneously.
        // For simplicity, let's use the yard (block 0 or a designated yard block)
        // The Green Line's yard is block 0 in the CSV.
        // However, block 0 is often just a virtual block. Let's use block 1 if valid.
        // Green line has blocks 1-150. Yard is usually just before block 1 or after 150.
        // The provided CSV seems to use block 0 as the yard.
        // TrackLine constructor: "The first block listed in the CSV is assumed to be the yard."
        // So, trains should emerge from block 0. updateTrainLocation should handle this.

        for (int i = 0; i < NUM_TRAINS; i++) {
            // Dispatching all from yard (block 0). TrackLine should handle placing them on the main track.
            ITrainModel train = createAndDispatchTrain(i + 1, 0); // Train IDs 1 to NUM_TRAINS
            trains.add(train);
        }

        // Simulate train movement
        for (ITrainModel train : trains) {
            Runnable trainMovementTask = () -> {
                try {
                    // Simulate train picking up initial commands
                    train.setCommandedSpeed(trackLine.getBlock(train.getCurrentBlockID()).getCommandedSpeed());
                    train.setAuthority(trackLine.getBlock(train.getCurrentBlockID()).getAuthority());
                    
                    for (int tick = 0; tick < SIMULATION_TICKS; tick++) {
                        // Train internal physics update (simplified)
                        // This should ideally call train.update(TICK_INTERVAL_MS) or similar
                        // For this test, we'll focus on TrackLine interaction
                        double currentSpeed = train.getSpeed(); // km/h
                        // Simulate covering distance based on speed and time step
                        double distanceToCoverOnBlock = train.getDistanceCoveredOnBlock();
                        double distanceCoveredThisTick = (currentSpeed / 3600.0) * (TICK_INTERVAL_MS / 1000.0) * 1000; // meters
                        
                        TrackBlock currentBlock = trackLine.getBlock(train.getCurrentBlockID());
                        if (currentBlock == null) {
                             logger.error("Train {} is on an invalid block ID {}. Aborting task.", train.getTrainID(), train.getCurrentBlockID());
                             throw new IllegalStateException("Train " + train.getTrainID() + " on invalid block " + train.getCurrentBlockID());
                        }

                        // Simple simulation of train moving along its current block
                        train.setDistanceCoveredOnBlock(distanceToCoverOnBlock + distanceCoveredThisTick);

                        if (train.getDistanceCoveredOnBlock() >= currentBlock.getLength()) {
                            // Train moves to the next block
                            logger.debug("Train {} attempting to move from block {}. Distance covered: {}, Block length: {}",
                                train.getTrainID(), train.getCurrentBlockID(), train.getDistanceCoveredOnBlock(), currentBlock.getLength());
                            
                            // Reset distance for the new block (TrackLine might do this, or train itself)
                            train.setDistanceCoveredOnBlock(0); 
                            trackLine.updateTrainLocation(train); // This is the key method to test under concurrency

                             logger.info("Train {} moved. New block: {}", train.getTrainID(), train.getCurrentBlockID());
                            // Update train's commanded speed and authority from the new block
                             TrackBlock newBlock = trackLine.getBlock(train.getCurrentBlockID());
                             if (newBlock != null) {
                                 train.setCommandedSpeed(newBlock.getCommandedSpeed());
                                 train.setAuthority(newBlock.getAuthority());
                             } else {
                                 logger.warn("Train {} moved to a null block ID {}", train.getTrainID(), train.getCurrentBlockID());
                             }
                        }
                        // No Thread.sleep here, ScheduledExecutorService handles timing
                    }
                } catch (Throwable e) {
                    logger.error("Exception in train {} movement task: {}", train.getTrainID(), e.getMessage(), e);
                    exceptions.add(e); // Collect exceptions
                }
            };
            // Schedule task to run once after a small delay to allow all setup
            // For a recurring task, use scheduleAtFixedRate or scheduleWithFixedDelay
            // For this test, a single execution per train that internally loops is simpler to manage for assertions
            Future<?> taskFuture = executorService.submit(trainMovementTask);
            trainTasks.put(train.getTrainID(), taskFuture);
        }

        // Wait for all tasks to complete
        for (Future<?> future : trainTasks.values()) {
            try {
                future.get(TEST_TIMEOUT_SECONDS / 2, TimeUnit.SECONDS); // Wait for each task to finish
            } catch (TimeoutException e) {
                exceptions.add(new TimeoutException("Train task timed out. Potential deadlock or very slow operation."));
            }
        }
        executorService.shutdown();
        if (!executorService.awaitTermination(TEST_TIMEOUT_SECONDS / 2, TimeUnit.SECONDS)) {
            logger.warn("Executor service did not terminate cleanly. Forcing shutdown.");
            executorService.shutdownNow();
        }

        // Assertions after simulation
        assertTrue(exceptions.isEmpty(), "Unexpected exceptions during simulation: " +
                exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", ")));

        Map<Integer, Integer> occupancyMap = trackLine.getTrackOccupancyMap();
        assertNotNull(occupancyMap, "Track occupancy map should not be null.");
        assertEquals(NUM_TRAINS, occupancyMap.size(), "Occupancy map should contain all dispatched trains.");

        for (ITrainModel train : trains) {
            assertTrue(occupancyMap.containsKey(train.getTrainID()), "Train " + train.getTrainID() + " should be in occupancy map.");
            int blockId = occupancyMap.get(train.getTrainID());
            assertTrue(blockId >= 0, "Train " + train.getTrainID() + " is on a valid block ID: " + blockId); // Assuming block 0 is yard/valid
            
            TrackBlock occupiedBlock = trackLine.getBlock(blockId);
            assertNotNull(occupiedBlock, "Block " + blockId + " occupied by train " + train.getTrainID() + " should exist.");
            assertTrue(occupiedBlock.isOccupied(), "Block " + blockId + " (from map) should be marked as occupied.");

            // Also check LineSubjectMap consistency
            LineSubject lineSubject = LineSubjectMap.getInstance().getLineSubject(trackLine.getLineColor());
            assertNotNull(lineSubject, "LineSubject for " + trackLine.getLineColor() + " should exist.");
            TrackBlockSubject blockSubject = lineSubject.get(blockId);
            assertNotNull(blockSubject, "TrackBlockSubject for block " + blockId + " should exist.");
            assertEquals(occupiedBlock.isOccupied(), blockSubject.isOccupied(),
                         "TrackBlockSubject occupancy for block " + blockId + " should match TrackBlock's status.");
            assertEquals(occupiedBlock.getBlockNumber(), blockSubject.getBlockNumber(),
                         "TrackBlockSubject block number should match TrackBlock's block number.");
        }
        logger.info("Finished test: multipleTrainsMovingSimultaneously");
    }


    @Test
    @Timeout(value = TEST_TIMEOUT_SECONDS, unit = TimeUnit.SECONDS)
    @DisplayName("Concurrent External Commands While Trains Moving")
    public void concurrentExternalCommands() throws InterruptedException, ExecutionException {
        logger.info("Starting test: concurrentExternalCommands");
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        List<ITrainModel> trains = new ArrayList<>();
        Map<Integer, Future<?>> trainTasks = new HashMap<>();
        List<Future<?>> commandTasks = new ArrayList<>();
        List<Throwable> exceptions = new CopyOnWriteArrayList<>();

        // Dispatch trains
        for (int i = 0; i < NUM_TRAINS; i++) {
            ITrainModel train = createAndDispatchTrain(i + 1, 0); // Dispatch from yard
            trains.add(train);
        }

        // Start train movement tasks (similar to the first test)
        for (ITrainModel train : trains) {
             Runnable trainMovementTask = () -> {
                try {
                    train.setCommandedSpeed(trackLine.getBlock(train.getCurrentBlockID()).getCommandedSpeed());
                    train.setAuthority(trackLine.getBlock(train.getCurrentBlockID()).getAuthority());
                    for (int tick = 0; tick < SIMULATION_TICKS; tick++) {
                        double currentSpeed = train.getSpeed();
                        double distanceToCoverOnBlock = train.getDistanceCoveredOnBlock();
                        double distanceCoveredThisTick = (currentSpeed / 3600.0) * (TICK_INTERVAL_MS / 1000.0) * 1000;
                        
                        TrackBlock currentBlock = trackLine.getBlock(train.getCurrentBlockID());
                        if (currentBlock == null) {
                             logger.error("Train {} on invalid block {} in movement task.", train.getTrainID(), train.getCurrentBlockID());
                             throw new IllegalStateException("Train " + train.getTrainID() + " on invalid block " + train.getCurrentBlockID());
                        }
                        
                        train.setDistanceCoveredOnBlock(distanceToCoverOnBlock + distanceCoveredThisTick);

                        if (train.getDistanceCoveredOnBlock() >= currentBlock.getLength()) {
                            train.setDistanceCoveredOnBlock(0);
                            trackLine.updateTrainLocation(train);
                             logger.info("Train {} moved to block {}.", train.getTrainID(), train.getCurrentBlockID());
                             TrackBlock newBlock = trackLine.getBlock(train.getCurrentBlockID());
                             if (newBlock != null) {
                                 train.setCommandedSpeed(newBlock.getCommandedSpeed());
                                 train.setAuthority(newBlock.getAuthority());
                             }
                        }
                        // Minimal delay to allow command tasks to interleave, executor handles actual scheduling
                        // Thread.sleep(TICK_INTERVAL_MS / 10); 
                    }
                } catch (Throwable e) {
                    logger.error("Exception in train {} movement task (concurrentExternalCommands): {}", train.getTrainID(), e.getMessage(), e);
                    exceptions.add(e);
                }
            };
            trainTasks.put(train.getTrainID(), executorService.submit(trainMovementTask));
        }

        // Schedule concurrent command tasks
        Random random = new Random();
        List<TrackBlock> allBlocks = trackLine.getBlocks();
        if (allBlocks.isEmpty()) {
            fail("No blocks available on the track line to issue commands to.");
        }

        for (int i = 0; i < SIMULATION_TICKS / 2; i++) { // Issue commands over a period
            Runnable commandTask = () -> {
                try {
                    TrackBlock targetBlock = allBlocks.get(random.nextInt(allBlocks.size()));
                    int blockId = targetBlock.getBlockNumber();

                    // Set Light State (assuming lights exist on some blocks)
                    // Store commands to verify later (optional, can make test complex)
                    // For now, focus on stability and final state consistency for some properties

                    if (targetBlock.hasLight()) {
                        boolean newLightState = random.nextBoolean();
                        trackLine.setLightState(blockId, newLightState);
                        logger.trace("Set light state for block {} to {}", blockId, newLightState);
                    }

                    if (targetBlock.isSwitch()) {
                        boolean newSwitchState = random.nextBoolean(); // true for main, false for alternate
                        // Ensure the switch is not occupied before toggling to avoid issues if TrackLine has such checks
                        if (!targetBlock.isOccupied()) {
                           trackLine.setSwitchState(blockId, newSwitchState);
                           logger.trace("Set switch state for block {} to {}", blockId, newSwitchState);
                        } else {
                            logger.trace("Skipped setting switch state for occupied block {}", blockId);
                        }
                    }
                    
                    Integer[] occupiedBlockIds = trackLine.getTrackOccupancyMap().values().toArray(new Integer[0]);
                    if (occupiedBlockIds.length > 0) {
                        int targetOccupiedBlockId = occupiedBlockIds[random.nextInt(occupiedBlockIds.length)];
                        // Ensure the block chosen is still part of the trackLine (can happen if train exits line)
                        if (trackLine.getBlock(targetOccupiedBlockId) != null) {
                            int newAuthority = random.nextInt(5) + 1; 
                            double newSpeed = random.nextDouble() * trackLine.getBlock(targetOccupiedBlockId).getSpeedLimit(); // Use speed limit
                            trackLine.setTrainAuthority(targetOccupiedBlockId, newAuthority);
                            trackLine.setCommandedSpeed(targetOccupiedBlockId, newSpeed);
                            logger.trace("Set authority ({}) and speed ({}) for block {}", newAuthority, newSpeed, targetOccupiedBlockId);
                        }
                    }
                } catch (Throwable e) {
                     logger.error("Exception in command task (concurrentExternalCommands): {}", e.getMessage(), e);
                    exceptions.add(e);
                }
            };
            commandTasks.add(executorService.submit(commandTask));
            // Stagger command task submission slightly
            // Thread.sleep(TICK_INTERVAL_MS / 20); 
        }

        // Wait for all train tasks to complete
        for (Future<?> future : trainTasks.values()) {
            try {
                future.get(TEST_TIMEOUT_SECONDS * 3/4, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                exceptions.add(new TimeoutException("Train task timed out in concurrentExternalCommands."));
            }
        }
        // Wait for all command tasks to complete
        for (Future<?> future : commandTasks) {
             try {
                future.get(TEST_TIMEOUT_SECONDS / 4, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                exceptions.add(new TimeoutException("Command task timed out in concurrentExternalCommands."));
            }
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        assertTrue(exceptions.isEmpty(), "Unexpected exceptions during concurrent command simulation: " +
                exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", ")));

        // Assertions: Check trackOccupancyMap consistency and potentially last command effects
        // Verifying last command effect is hard. Focus on stability and consistency.
        // Check general consistency of TrackBlock and TrackBlockSubject for a sample of blocks.
        Map<Integer, Integer> occupancyMap = trackLine.getTrackOccupancyMap();
        assertNotNull(occupancyMap, "Track occupancy map should not be null after concurrent commands.");
        
        // Check that all trains are still accounted for
        assertEquals(NUM_TRAINS, occupancyMap.keySet().size(), "All trains should remain in the occupancy map.");

        LineSubject lineSubject = LineSubjectMap.getInstance().getLineSubject(trackLine.getLineColor());
        assertNotNull(lineSubject, "LineSubject for " + trackLine.getLineColor() + " should exist.");

        for (ITrainModel train : trains) {
            assertTrue(occupancyMap.containsKey(train.getTrainID()),
                    "Train " + train.getTrainID() + " should be in occupancy map after concurrent commands.");
            int currentBlockId = occupancyMap.get(train.getTrainID());
            TrackBlock block = trackLine.getBlock(currentBlockId);
            TrackBlockSubject subject = lineSubject.get(currentBlockId);

            assertNotNull(block, "Block " + currentBlockId + " for train " + train.getTrainID() + " must exist.");
            assertNotNull(subject, "BlockSubject " + currentBlockId + " for train " + train.getTrainID() + " must exist.");

            // Check a few properties for consistency between TrackBlock and TrackBlockSubject
            assertEquals(block.isOccupied(), subject.isOccupied(), "Occupancy state mismatch for block " + currentBlockId);
            assertEquals(block.getCommandedSpeed(), subject.getCommandedSpeed(), 0.01, "Commanded speed mismatch for block " + currentBlockId);
            assertEquals(block.getAuthority(), subject.getAuthority(), "Authority mismatch for block " + currentBlockId);
            if (block.hasLight()) {
                assertEquals(block.getLightState(), subject.getLightState(), "Light state mismatch for block " + currentBlockId);
            }
            if (block.isSwitch()) {
                assertEquals(block.getSwitchState(), subject.getSwitchState(), "Switch state mismatch for block " + currentBlockId);
            }
        }
        logger.info("Finished test: concurrentExternalCommands");
    }


    @Test
    @Timeout(value = TEST_TIMEOUT_SECONDS, unit = TimeUnit.SECONDS)
    @DisplayName("Concurrent Murphy Failures While Trains Moving")
    public void concurrentMurphyFailures() throws InterruptedException, ExecutionException {
        logger.info("Starting test: concurrentMurphyFailures");
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        List<ITrainModel> trains = new ArrayList<>();
        Map<Integer, Future<?>> trainTasks = new HashMap<>();
        List<Future<?>> murphyTasks = new ArrayList<>();
        List<Throwable> exceptions = new CopyOnWriteArrayList<>();

        // Dispatch trains
        for (int i = 0; i < NUM_TRAINS; i++) {
            ITrainModel train = createAndDispatchTrain(i + 1, 0);
            trains.add(train);
        }

        // Start train movement tasks
         for (ITrainModel train : trains) {
             Runnable trainMovementTask = () -> {
                try {
                    train.setCommandedSpeed(trackLine.getBlock(train.getCurrentBlockID()).getCommandedSpeed());
                    train.setAuthority(trackLine.getBlock(train.getCurrentBlockID()).getAuthority());
                    for (int tick = 0; tick < SIMULATION_TICKS; tick++) {
                        double currentSpeed = train.getSpeed();
                        double distanceToCoverOnBlock = train.getDistanceCoveredOnBlock();
                        double distanceCoveredThisTick = (currentSpeed / 3600.0) * (TICK_INTERVAL_MS / 1000.0) * 1000;
                        
                        TrackBlock currentBlock = trackLine.getBlock(train.getCurrentBlockID());
                         if (currentBlock == null) {
                             logger.error("Train {} on invalid block {} in Murphy test movement.", train.getTrainID(), train.getCurrentBlockID());
                             throw new IllegalStateException("Train " + train.getTrainID() + " on invalid block " + train.getCurrentBlockID());
                        }

                        train.setDistanceCoveredOnBlock(distanceToCoverOnBlock + distanceCoveredThisTick);

                        if (train.getDistanceCoveredOnBlock() >= currentBlock.getLength()) {
                            train.setDistanceCoveredOnBlock(0);
                            trackLine.updateTrainLocation(train);
                            logger.info("Train {} moved to block {} (Murphy test).", train.getTrainID(), train.getCurrentBlockID());
                             TrackBlock newBlock = trackLine.getBlock(train.getCurrentBlockID());
                             if (newBlock != null) {
                                 train.setCommandedSpeed(newBlock.getCommandedSpeed());
                                 train.setAuthority(newBlock.getAuthority());
                             }
                        }
                        // Thread.sleep(TICK_INTERVAL_MS / 10); // Allow interleaving
                    }
                } catch (Throwable e) {
                    logger.error("Exception in train {} movement task (concurrentMurphyFailures): {}", train.getTrainID(), e.getMessage(), e);
                    exceptions.add(e);
                }
            };
            trainTasks.put(train.getTrainID(), executorService.submit(trainMovementTask));
        }


        // Schedule concurrent Murphy failure tasks
        Random random = new Random();
        List<TrackBlock> allBlocks = trackLine.getBlocks();
        if (allBlocks.isEmpty()) {
            fail("No blocks available on the track line to set failures.");
        }
        // Store last set failure type for a few blocks to verify
        Map<Integer, TrackProperty> lastSetFailure = new ConcurrentHashMap<>();


        for (int i = 0; i < SIMULATION_TICKS / 2; i++) { // Issue failures over a period
            Runnable murphyTask = () -> {
                try {
                    TrackBlock targetBlock = allBlocks.get(random.nextInt(allBlocks.size()));
                    int blockId = targetBlock.getBlockNumber();
                    TrackProperty failureType = TrackProperty.values()[random.nextInt(TrackProperty.values().length)];

                    // Apply a failure or fix it
                    boolean shouldFix = random.nextBoolean() && targetBlock.isFailed(); // Only fix if already failed

                    if (shouldFix) {
                        // To fix, we need to know which failure was active.
                        // TrackLine.fixTrackFailure(blockId) might be generic or need specific failure type.
                        // Let's assume it's generic for now.
                        trackLine.fixTrackFailure(blockId);
                        logger.trace("Attempted to fix failure on block {}", blockId);
                        lastSetFailure.remove(blockId); // Assume it's fixed
                    } else {
                        switch (failureType) {
                            case BROKEN_RAIL:
                                trackLine.setBrokenRail(blockId);
                                lastSetFailure.put(blockId, TrackProperty.BROKEN_RAIL);
                                logger.trace("Set BROKEN_RAIL on block {}", blockId);
                                break;
                            case POWER_FAILURE:
                                trackLine.setPowerFailure(blockId);
                                lastSetFailure.put(blockId, TrackProperty.POWER_FAILURE);
                                logger.trace("Set POWER_FAILURE on block {}", blockId);
                                break;
                            case TRACK_CIRCUIT_FAILURE:
                                trackLine.setTrackCircuitFailure(blockId);
                                lastSetFailure.put(blockId, TrackProperty.TRACK_CIRCUIT_FAILURE);
                                logger.trace("Set TRACK_CIRCUIT_FAILURE on block {}", blockId);
                                break;
                            default:
                                // Other track properties are not failures set this way
                                break;
                        }
                    }
                   // Thread.sleep(random.nextInt(20) + 10); // Small random delay
                } catch (Throwable e) {
                    logger.error("Exception in Murphy task: {}", e.getMessage(), e);
                    exceptions.add(e);
                }
            };
            murphyTasks.add(executorService.submit(murphyTask));
            // Thread.sleep(TICK_INTERVAL_MS / 20); // Stagger submission
        }

        // Wait for all tasks to complete
        for (Future<?> future : trainTasks.values()) {
             try {
                future.get(TEST_TIMEOUT_SECONDS * 3/4, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                exceptions.add(new TimeoutException("Train task timed out in concurrentMurphyFailures."));
            }
        }
        for (Future<?> future : murphyTasks) {
             try {
                future.get(TEST_TIMEOUT_SECONDS / 4, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                exceptions.add(new TimeoutException("Murphy task timed out in concurrentMurphyFailures."));
            }
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
        
        assertTrue(exceptions.isEmpty(), "Unexpected exceptions during Murphy failure simulation: " +
                exceptions.stream().map(Throwable::getMessage).collect(Collectors.joining(", ")));

        // Assertions
        Map<Integer, Integer> occupancyMap = trackLine.getTrackOccupancyMap();
        assertNotNull(occupancyMap, "Track occupancy map should not be null after Murphy failures.");
        // Check consistency of actual trains
        for (ITrainModel train : trains) {
            assertTrue(occupancyMap.containsKey(train.getTrainID()),
                    "Train " + train.getTrainID() + " should be in occupancy map after Murphy failures.");
        }

        // Verify failure statuses for blocks.
        // Check consistency between TrackBlock and TrackBlockSubject for failures.
        LineSubject lineSubjectMur = LineSubjectMap.getInstance().getLineSubject(trackLine.getLineColor());
        assertNotNull(lineSubjectMur, "LineSubject for " + trackLine.getLineColor() + " should exist for Murphy test.");

        for (TrackBlock block : trackLine.getBlocks()) {
            if (block == null || block.getBlockNumber() == 0) continue; // Skip yard or invalid blocks

            TrackBlockSubject subject = lineSubjectMur.get(block.getBlockNumber());
            assertNotNull(subject, "BlockSubject for block " + block.getBlockNumber() + " must exist.");

            assertEquals(block.isBrokenRail(), subject.isBrokenRail(), "Broken rail state mismatch for block " + block.getBlockNumber());
            assertEquals(block.isPowerFailed(), subject.isPowerFailed(), "Power failure state mismatch for block " + block.getBlockNumber());
            assertEquals(block.isTrackCircuitFailed(), subject.isTrackCircuitFailed(), "Track circuit failure state mismatch for block " + block.getBlockNumber());
            
            // If we tracked lastSetFailure and it wasn't randomly fixed, it should be reflected.
            // This is harder to assert definitively due to randomness of fixes.
            // The above consistency check is more robust for all blocks.
            if (lastSetFailure.containsKey(block.getBlockNumber())) {
                 TrackProperty expectedFailure = lastSetFailure.get(block.getBlockNumber());
                 boolean failurePresent = false;
                 switch (expectedFailure) {
                     case BROKEN_RAIL: failurePresent = block.isBrokenRail(); break;
                     case POWER_FAILURE: failurePresent = block.isPowerFailed(); break;
                     case TRACK_CIRCUIT_FAILURE: failurePresent = block.isTrackCircuitFailed(); break;
                 }
                 // This assertion might be flaky if a random fix occurred on this specific block.
                 // So, it's more of an observation point.
                 if (failurePresent) {
                    logger.debug("Block {} correctly reflects tracked failure {}.", block.getBlockNumber(), expectedFailure);
                 } else {
                    logger.warn("Block {} does NOT reflect tracked failure {}. It might have been fixed randomly or overridden.", block.getBlockNumber(), expectedFailure);
                 }
            }
        }
        logger.info("Finished test: concurrentMurphyFailures");
    }
}
