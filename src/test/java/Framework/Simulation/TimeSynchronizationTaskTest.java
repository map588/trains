package Framework.Simulation;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectEnum;
import Framework.Support.TrackSubject;
import ctcOffice.CTCOffice;
import ctcOffice.CTCOfficeImpl;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import shared.Linecolor;
import trackModel.TrackLineMap;
import waysideController.WaysideSystem;
import trainModel.TrainModelImpl; // Needed for TrainSystem if it uses concrete types

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TimeSynchronizationTask Concurrency and Order Test")
public class TimeSynchronizationTaskTest {

    private static final Logger logger = LoggerFactory.getLogger(TimeSynchronizationTaskTest.class);
    private static final AtomicLong eventCounter = new AtomicLong(0); // Global counter for event sequencing

    // --- Mock Classes ---

    static class CallRecord {
        String methodName;
        long sequenceNumber;
        long timestamp;

        CallRecord(String methodName) {
            this.methodName = methodName;
            this.sequenceNumber = eventCounter.incrementAndGet();
            this.timestamp = System.nanoTime();
        }

        @Override
        public String toString() {
            return methodName + "@" + sequenceNumber;
        }
    }

    static class MockCTCOffice extends CTCOfficeImpl { // Extends Impl to avoid interface issues if any
        List<CallRecord> callLog = new CopyOnWriteArrayList<>();
        private final long workDurationMs;

        MockCTCOffice(long workDurationMs) {
            super(); // CTCOfficeImpl has a default constructor
            this.workDurationMs = workDurationMs;
        }

        @Override
        public void incrementTime() {
            callLog.add(new CallRecord("ctcOffice.incrementTime_start"));
            try {
                Thread.sleep(workDurationMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            callLog.add(new CallRecord("ctcOffice.incrementTime_end"));
            // super.incrementTime(); // Don't call real logic
        }
         @Override
        public Map<Integer, ObservableHashMap<SubjectEnum, Object>> getTrainSubjects() {
            // Return an empty map or a mock map if needed by other parts of the system
            // that TimeSynchronizationTask might indirectly touch.
            // For this test, assuming it's not critical for the direct path of run().
            return new java.util.HashMap<>();
        }

    }

    static class MockTrainSystem extends TrainSystem {
        List<CallRecord> callLog = new CopyOnWriteArrayList<>();
        private final long workDurationMs;
        // This internal latch is for the mock's own simulated work,
        // not the one in TimeSynchronizationTask.
        CountDownLatch internalWorkLatch = new CountDownLatch(1);


        MockTrainSystem(long workDurationMs) {
            super(); // TrainSystem has a default constructor
            this.workDurationMs = workDurationMs;
        }

        @Override
        public void update() {
            callLog.add(new CallRecord("trainSystem.update_start"));
            try {
                Thread.sleep(workDurationMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            callLog.add(new CallRecord("trainSystem.update_end"));
            internalWorkLatch.countDown();
            // super.update(); // Don't call real logic
        }
         public void waitForInternalWorkComplete() throws InterruptedException {
            if (!internalWorkLatch.await(5, TimeUnit.SECONDS)) {
                logger.warn("MockTrainSystem internal work did not complete in time.");
            }
        }
    }

    static class MockWaysideSystem extends WaysideSystem {
        List<CallRecord> callLog = new CopyOnWriteArrayList<>();
        private final long workDurationMs;
        CountDownLatch internalWorkLatch = new CountDownLatch(1);


        MockWaysideSystem(long workDurationMs) {
            // WaysideSystem constructor needs TrackLineMap and CTCOffice.
            // Provide minimal, non-null instances.
            super(TrackLineMap.getInstance(), new CTCOfficeImpl());
            this.workDurationMs = workDurationMs;
             TrackLineMap.getInstance().clearLines(); // Ensure clean state for each test run
        }

        @Override
        public void update() {
            callLog.add(new CallRecord("waysideSystem.update_start"));
            try {
                Thread.sleep(workDurationMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            callLog.add(new CallRecord("waysideSystem.update_end"));
            internalWorkLatch.countDown();
            // super.update(); // Don't call real logic
        }
        public void waitForInternalWorkComplete() throws InterruptedException {
            if(!internalWorkLatch.await(5, TimeUnit.SECONDS)) {
                logger.warn("MockWaysideSystem internal work did not complete in time.");
            }
        }
    }

    static class MockTrackSystem extends TrackSystem {
        List<CallRecord> callLog = new CopyOnWriteArrayList<>();
        private final long workDurationMs;

        MockTrackSystem(long workDurationMs) {
            super(TrackLineMap.getInstance()); // TrackSystem constructor needs TrackLineMap
            this.workDurationMs = workDurationMs;
            TrackLineMap.getInstance().clearLines(); // Ensure clean state for each test run
        }

        @Override
        public void update() {
            callLog.add(new CallRecord("trackSystem.update_start"));
            try {
                Thread.sleep(workDurationMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            callLog.add(new CallRecord("trackSystem.update_end"));
            // super.update(); // Don't call real logic
        }
    }

    // --- Test Class ---

    private MockCTCOffice mockCtcOffice;
    private MockTrainSystem mockTrainSystem;
    private MockWaysideSystem mockWaysideSystem;
    private MockTrackSystem mockTrackSystem;
    private Main.TimeSynchronizationTask timeSyncTask;

    @BeforeEach
    void setUp() {
        eventCounter.set(0); // Reset global event counter for each test
        Main.initializeSynchronizationPool(); // Ensure the pool is ready

        mockCtcOffice = new MockCTCOffice(20); // Simulate 20ms work
        mockTrainSystem = new MockTrainSystem(50); // Simulate 50ms work
        mockWaysideSystem = new MockWaysideSystem(40); // Simulate 40ms work
        mockTrackSystem = new MockTrackSystem(10);  // Simulate 10ms work

        // TimeSynchronizationTask uses Main.ctcOffice, Main.trainSystem, etc.
        // So, we need to assign our mocks to these static fields in Main.
        Main.ctcOffice = mockCtcOffice;
        Main.trainSystem = mockTrainSystem;
        Main.waysideSystem = mockWaysideSystem;
        Main.trackSystem = mockTrackSystem;
        
        // TrackSubject is also used by TimeSynchronizationTask, ensure it's minimally functional or mocked if necessary.
        // For this test, assuming its default state or simple interactions are fine.
        // TrackSubject.getInstance().clearSubjects(); // If it holds state

        timeSyncTask = new Main.TimeSynchronizationTask();
    }
    
    @AfterEach
    void tearDown() throws InterruptedException {
        // Shutdown the main synchronization pool to ensure tests don't interfere
        // if it's being recreated or managed elsewhere.
        // This is tricky as it's a static pool in Main.
        // A better approach for robust testing would be to inject the ExecutorService into TimeSynchronizationTask.
        // For now, we assume Main.synchronizationPool is available and tasks complete.
        ExecutorService pool = Main.getSynchronizationPool(); // Assuming a getter, or manage manually if not
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.error("Main.synchronizationPool did not shut down cleanly.");
                pool.shutdownNow();
            }
        }
         TrackLineMap.getInstance().clearLines(); // Clean up singleton state
         // Reset Main's static fields to null or original state if possible to avoid test interference.
         Main.ctcOffice = null;
         Main.trainSystem = null;
         Main.waysideSystem = null;
         Main.trackSystem = null;
    }


    @Test
    @Timeout(value = 20, unit = TimeUnit.SECONDS) // Generous timeout for the test itself
    @DisplayName("Verify Update Order Synchronized by CountDownLatch")
    void testUpdateOrderSynchronization() throws InterruptedException {
        logger.info("Starting testUpdateOrderSynchronization...");

        // Run the task. This will block until its internal CountDownLatch (waiting for
        // trainSystem.update and waysideSystem.update from the pool) completes.
        timeSyncTask.run();
        
        // Additional wait for our mock's internal latches, just to be absolutely sure their
        // "work" (including adding to callLog) is done before assertions.
        // This is mostly redundant if task.run() is fully synchronous with respect to
        // the completion of the tasks it submits to its CountDownLatch.
        mockTrainSystem.waitForInternalWorkComplete();
        mockWaysideSystem.waitForInternalWorkComplete();


        logger.info("TimeSynchronizationTask.run() completed.");
        logger.info("CTC Call Log: " + mockCtcOffice.callLog);
        logger.info("TrainSystem Call Log: " + mockTrainSystem.callLog);
        logger.info("WaysideSystem Call Log: " + mockWaysideSystem.callLog);
        logger.info("TrackSystem Call Log: " + mockTrackSystem.callLog);

        // --- Assertions ---

        // 1. Check all expected methods were called at least once for start/end
        assertFalse(mockCtcOffice.callLog.isEmpty(), "CTC Office should have call logs.");
        assertTrue(mockCtcOffice.callLog.stream().anyMatch(r -> r.methodName.equals("ctcOffice.incrementTime_start")), "CTC incrementTime_start missing.");
        assertTrue(mockCtcOffice.callLog.stream().anyMatch(r -> r.methodName.equals("ctcOffice.incrementTime_end")), "CTC incrementTime_end missing.");

        assertFalse(mockTrainSystem.callLog.isEmpty(), "TrainSystem should have call logs.");
        assertTrue(mockTrainSystem.callLog.stream().anyMatch(r -> r.methodName.equals("trainSystem.update_start")), "TrainSystem update_start missing.");
        assertTrue(mockTrainSystem.callLog.stream().anyMatch(r -> r.methodName.equals("trainSystem.update_end")), "TrainSystem update_end missing.");

        assertFalse(mockWaysideSystem.callLog.isEmpty(), "WaysideSystem should have call logs.");
        assertTrue(mockWaysideSystem.callLog.stream().anyMatch(r -> r.methodName.equals("waysideSystem.update_start")), "WaysideSystem update_start missing.");
        assertTrue(mockWaysideSystem.callLog.stream().anyMatch(r -> r.methodName.equals("waysideSystem.update_end")), "WaysideSystem update_end missing.");
        
        assertFalse(mockTrackSystem.callLog.isEmpty(), "TrackSystem should have call logs.");
        assertTrue(mockTrackSystem.callLog.stream().anyMatch(r -> r.methodName.equals("trackSystem.update_start")), "TrackSystem update_start missing.");

        // 2. Get sequence numbers for critical "end" and "start" points
        CallRecord ctcEnd = mockCtcOffice.callLog.stream().filter(r -> r.methodName.equals("ctcOffice.incrementTime_end")).findFirst().orElse(null);
        CallRecord trainEnd = mockTrainSystem.callLog.stream().filter(r -> r.methodName.equals("trainSystem.update_end")).findFirst().orElse(null);
        CallRecord waysideEnd = mockWaysideSystem.callLog.stream().filter(r -> r.methodName.equals("waysideSystem.update_end")).findFirst().orElse(null);
        CallRecord trackStart = mockTrackSystem.callLog.stream().filter(r -> r.methodName.equals("trackSystem.update_start")).findFirst().orElse(null);

        assertNotNull(ctcEnd, "CTC incrementTime_end record not found.");
        assertNotNull(trainEnd, "TrainSystem update_end record not found.");
        assertNotNull(waysideEnd, "WaysideSystem update_end record not found.");
        assertNotNull(trackStart, "TrackSystem update_start record not found.");

        // 3. Verify order: TrackSystem update must start after CTC, Train, and Wayside updates have ended.
        String orderFailMsg = String.format(
            "Order violation: TrackSystem.update_start (%s) should be after CTC (%s), Train (%s), and Wayside (%s) updates.",
            trackStart, ctcEnd, trainEnd, waysideEnd
        );

        assertTrue(trackStart.sequenceNumber > ctcEnd.sequenceNumber, orderFailMsg + " (CTC vs Track)");
        assertTrue(trackStart.sequenceNumber > trainEnd.sequenceNumber, orderFailMsg + " (Train vs Track)");
        assertTrue(trackStart.sequenceNumber > waysideEnd.sequenceNumber, orderFailMsg + " (Wayside vs Track)");

        logger.info("Order verification successful: TrackSystem update ({}) occurred after CTC ({}), Train ({}), and Wayside ({}) updates.",
                trackStart.sequenceNumber, ctcEnd.sequenceNumber, trainEnd.sequenceNumber, waysideEnd.sequenceNumber);
        
        // 4. Verify that CTC incrementTime_start is one of the earliest calls (likely first or second, but order with initial pool submissions not strictly guaranteed by problem)
        CallRecord ctcStart = mockCtcOffice.callLog.stream().filter(r -> r.methodName.equals("ctcOffice.incrementTime_start")).findFirst().orElse(null);
        assertNotNull(ctcStart, "CTC incrementTime_start not found.");
        // Check it's before the "end" markers of pooled tasks
        assertTrue(ctcStart.sequenceNumber < trainEnd.sequenceNumber, "CTC start should be before TrainSystem end.");
        assertTrue(ctcStart.sequenceNumber < waysideEnd.sequenceNumber, "CTC start should be before WaysideSystem end.");

        logger.info("All assertions passed.");
    }
}
