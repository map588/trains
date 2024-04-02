package trackModel;

import Common.TrainModel;
import Utilities.Records.BasicBlock;
import Utilities.Enums.Lines;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import trainModel.TrainModelImpl;

import java.util.concurrent.ConcurrentSkipListMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


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



    private TrackLine trackLine;
    private TrainModel trainModel;
    private ConcurrentSkipListMap<Integer, BasicBlock> basicTrackLayout;

    @BeforeEach
    public void setup() {
        basicTrackLayout = new ConcurrentSkipListMap<>();
        trackLine = new TrackLine(Lines.GREEN, basicTrackLayout);
        trainModel = mock(TrainModelImpl.class);
    }

//    @Test
//    public void dispatchTrainAddsTrainToOccupancyMap() {
//        trackLine.trainDispatch(1);
//        assertTrue(trackLine.trackOccupancyMap.containsKey(trainModel));
//        assertEquals(0, trackLine.trackOccupancyMap.get(trainModel));
//    }

    @Test
    public void updateTrainLocationChangesTrainBlock() {
        trackLine.trainDispatch(1);
        double length = trackLine.updateTrainLocation(trainModel);
        assertEquals(0, length);
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
    public void setSwitchStateChangesSwitchState() {
        trackLine.setSwitchState(1, true);
        assertTrue(trackLine.getSwitchState(1));
    }

    @Test
    public void setCrossingChangesCrossingState() {
        trackLine.setCrossing(1, true);
        assertTrue(trackLine.getCrossingState(1));
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
        trackLine.setPassengersDisembarked(trainModel, 10);
        assertEquals(10, trackLine.getPassengersEmbarked(trainModel));
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
