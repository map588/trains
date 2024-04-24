package trackModel;

import Integration.BaseTest;
import Utilities.HelperObjects.BasicTrackLine;
import Utilities.Constants;
import Utilities.Enums.Lines;
import Utilities.Records.BasicBlock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stubs.trainStub;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


public class TrackLineTest extends BaseTest {

    // unit tests for TrackLine class

    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private static TrackLine trackLine;
    private trainStub stub;

    @BeforeAll
    public static void setUpAll() {
//        basicBlockSkipList = GlobalBasicBlockParser.getInstance().getBasicLine(Lines.GREEN);
        trackLine = new TrackLine(Lines.GREEN);
    }

    @BeforeEach
    public void setup() throws InterruptedException {
        stub = new trainStub(trackLine, 1);
        trackLine.trainDispatch(stub);
        executorService.scheduleAtFixedRate(() ->
        {
            trackLine.update();
        }, 2, (long)Constants.TIME_STEP_MS, TimeUnit.MILLISECONDS);
        stub.go_Brr();
    }

    @Test
    public void testTrainDispatch() {
        assertEquals(1,trackLine.trainCount());
    }

    @Test
    public void testBrokenRail() {
        trackLine.setBrokenRail(1, true);
        assertTrue(trackLine.getBrokenRail(1));
    }

    @Test
    public void testPowerFailure() {
        trackLine.setPowerFailure(1, true);
        assertTrue(trackLine.getPowerFailure(1));
    }

    @Test
    public void testTrackCircuitFailure() {
        trackLine.setTrackCircuitFailure(1, true);
        assertTrue(trackLine.getTrackCircuitFailure(1));
    }


    // TODO: passenger test
//    @Test
//    public void testPassengers() {
//        trackLine.embarkPassengers(stub);
//        trackLine.disembarkPassengers(stub, 15);
//
//    }

    @Test
    public void testTicketSales() {
        assertEquals(0, trackLine.getTicketSales());
    }


    // TODO: beacon test
//    @Test
//    public void testBeacons() {
//
//    }
}

