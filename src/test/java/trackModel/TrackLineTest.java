package trackModel;

import Common.TrainModel;
import Utilities.BlockParser;
import Utilities.Enums.BlockType;
import Utilities.Records.BasicBlock;
import Utilities.Enums.Lines;


import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import stubs.trainStub;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class TrackLineTest {

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



    private static TrackLine trackLine;
    private TrainModel trainModel;
    private static ConcurrentSkipListMap<Integer, BasicBlock> basicBlockSkipList;
    private static int i = 10;

    @BeforeAll
    public static void setUpAll() {
        Platform.startup(() -> {
        });
        ConcurrentHashMap<Lines, ConcurrentSkipListMap<Integer, BasicBlock>> trackLines = BlockParser.parseCSV();
        basicBlockSkipList = trackLines.get(Lines.GREEN);
    }

    @BeforeEach
    public void setup() {
        trackLine = new TrackLine(Lines.GREEN, basicBlockSkipList);
        trainModel = trackLine.trainDispatch(i);
        i++;
    }

//    @Test
//    public void dispatchTrainAddsTrainToOccupancyMap() {
//        trackLine.trainDispatch(1);
//        assertTrue(trackLine.trackOccupancyMap.containsKey(trainModel));
//        assertEquals(0, trackLine.trackOccupancyMap.get(trainModel));
//    }

    @Test
    public void updateTrainLocationChangesTrainBlock() {
        TrackBlock newBlock = trackLine.updateTrainLocation(trainModel);
        assertEquals(100.0, newBlock.getLength());
        assertEquals(63, newBlock.getBlockID());
    }

//    @Test
//    public void handleTrainEntryAndExitRemovesTrainFromOccupancyMap() {
//        trackLine.trainDispatch(1);
//        trackLine.trackOccupancyMap.remove(trainModel);
//        assertFalse(trackLine.trackOccupancyMap.containsKey(trainModel));
//    }

    @Test
    public void setLightStateChangesLightState() {
        trackLine.setLightState(1, true);
        assertTrue(trackLine.getLightState(1));
    }

    @Test
    public void setSwitchTrueChangesSwitch() {
        for(BasicBlock block : basicBlockSkipList.values()) {
            if(block.isSwitch()) {
                trackLine.setSwitchState(block.blockNumber(), true);
                assertTrue(trackLine.getSwitchState(block.blockNumber()));
            }
        }
    }

    @Test
    public void setCrossingChangesCrossingState() {
        for(BasicBlock block : basicBlockSkipList.values()) {
            if (block.blockType().equals(BlockType.CROSSING)) {
                trackLine.setCrossing(block.blockNumber(), true);
                assertTrue(trackLine.getCrossingState(block.blockNumber()));
            }
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

        trackLine.setPassengersDisembarked(stub, 10);
        assertEquals(10, trackLine.getPassengersEmbarked(stub));
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
