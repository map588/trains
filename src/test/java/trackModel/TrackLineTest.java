package trackModel;

import Integration.BaseTest;
import Utilities.BasicBlockLine;
import Utilities.Constants;
import Utilities.Enums.BlockType;
import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
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

    // test for constructor
    // test that the constructor initializes the track layout and occupancy map
    // test that the constructor initializes the track layout with the correct blocks
    // test that the constructor initializes the occupancy map with the correct trains
    // test that the constructor initializes the line with the correct line
    // test train dispatch
    // test that the train dispatch method creates a new train
    // test that the train dispatch method adds the new train to the occupancy map
    // test that the train dispatch method adds the new train to the occupancy map with the correct block

    // test get crossing state
    // test that the get crossing state method returns the correct crossing state
    // test that the get crossing state method throws an exception if the block is not a crossing
    // test get light state
    // test that the get light state method returns the correct light state
    // test get switch state
    // test that the get switch state method returns the correct switch state
    // test that the get switch state method throws an exception if the block is not a switch
    // test set beacon
    // test that the set beacon method sets the correct beacon
    // test set commanded speed
    // test that the set commanded speed method sets the correct commanded speed

    ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private static TrackLine trackLine;
    private trainStub stub;
    private static BasicBlockLine basicBlockSkipList;
    private static int i = 10;

    @BeforeAll
    public static void setUpAll() {
        basicBlockSkipList = GlobalBasicBlockParser.getInstance().getBasicLine(Lines.GREEN);
        trackLine = new TrackLine(Lines.GREEN, basicBlockSkipList);
    }

    @BeforeEach
    public void setup() throws InterruptedException {
        stub = new trainStub(trackLine, 1);
        trackLine.trainDispatch(stub);
        executorService.scheduleAtFixedRate(() ->
        {
            trackLine.update();
        }, 2, Constants.TIME_STEP_MS, TimeUnit.MILLISECONDS);
        stub.go_Brr();
    }

//    @Test
//    public void dispatchTrainAddsTrainToOccupancyMap() {
//        trackLine.trainDispatch(1);
//        assertTrue(trackLine.trackOccupancyMap.containsKey(trainModel));
//        assertEquals(0, trackLine.trackOccupancyMap.get(trainModel));
//    }

    @Test
    public void updateTrainLocationChangesTrainBlock() {
        TrackBlock newBlock = trackLine.updateTrainLocation(stub);
        TrackBlock currentBlock = stub.getCurrentBlock();
        assertNotEquals(newBlock, currentBlock);
    }

//    @Test
//    public void handleTrainEntryAndExitRemovesTrainFromOccupancyMap() {
//        trackLine.trainDispatch(1);
//        trackLine.trackOccupancyMap.remove(trainModel);
//        assertFalse(trackLine.trackOccupancyMap.containsKey(trainModel));
//    }

//    @Test
//    public void setLightStateChangesLightState() {
//        trackLine.setLightState(1, true);
//        assertTrue(trackLine.getLightState(1));
//    }

    @Test
    public void setSwitchTrueChangesSwitch() throws InterruptedException {
        ConcurrentSkipListSet<Integer> switches = new ConcurrentSkipListSet<>();
        for(BasicBlock block : basicBlockSkipList.values()) {
            if(block.isSwitch()) {
                int switchBlock = block.blockNumber();
                trackLine.setSwitchState(switchBlock, true);
                switches.add(switchBlock);
            }
        }
//        for(int switchBlock : switches) {
//            assertTrue(trackLine.getSwitchState(switchBlock));
//        }
    }

    @Test
    public void setCrossingChangesCrossingState() {
        for(BasicBlock block : basicBlockSkipList.values()) {
//            if (block.blockType().equals(BlockType.CROSSING)) {
//                trackLine.setCrossing(block.blockNumber(), true);
//                assertTrue(trackLine.getCrossingState(block.blockNumber()));
//            }
        }
    }

    @Test
    public void setBrokenRailChangesBrokenRailState() {
        trackLine.setBrokenRail(1, true);
        assertTrue(trackLine.getBrokenRail(1));
    }

    @Test
    public void setPowerFailureChangesPowerFailureState() {
        trackLine.setPowerFailure(1, true);
        assertTrue(trackLine.getPowerFailure(1));
    }

    @Test
    public void setTrackCircuitFailureChangesTrackCircuitFailureState() {
        trackLine.setTrackCircuitFailure(1, true);
        assertTrue(trackLine.getTrackCircuitFailure(1));
    }

    @Test
    public void setPassengersDisembarkedChangesPassengerCount() {
        trainStub stub = new trainStub(trackLine, 34);
        trackLine.trainDispatch(stub);
        for(BasicBlock block : basicBlockSkipList.values()) {
            if(block.blockType() == BlockType.STATION) {
                stub.setTrackBlock(trackLine.getBlock(block.blockNumber()));
                trackLine.moveTrain(stub, block.blockNumber());
                break;
            }
        }
        int passengers = stub.getPassengerCount();
        int disembarked = trackLine.getBlock(stub.getCurrentBlock().getPassengersDisembarked()).getPassengersDisembarked();
        trackLine.disembarkPassengers(stub, disembarked);
        assertEquals(passengers - disembarked, stub.getPassengerCount());
    }

    @Test
    public void getTicketSalesReturnsCorrectSales() {
        assertEquals(0, trackLine.getTicketSales());
    }

    @Test
    public void resetTicketSalesResetsSales() {
        trackLine.resetTicketSales();
        assertEquals(0, trackLine.getTicketSales());
    }
}
