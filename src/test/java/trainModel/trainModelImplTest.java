package trainModel;
import Common.TrainController;
import Common.TrackModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class trainModelImplTest {

    private TrainModelImpl model;
    private TrainController mockController;
    private TrackModel mockTrackModel;

    @BeforeEach
    void setUp() {
        model = new TrainModelImpl(1); // Assuming '1' is a valid trainID
        mockController = mock(TrainController.class); // Mocking TrainController
        mockTrackModel = mock(TrackModel.class); // Mocking TrackModel
    }

    @Test
    void testSetBooleans() {
        model.setLeftDoors(true);
        assertTrue(model.getLeftDoors());

        model.setRightDoors(true);
        assertTrue(model.getRightDoors());

        model.setIntLights(true);
        assertTrue(model.getIntLights());

        model.setExtLights(true);
        assertTrue(model.getExtLights());
    }

    @Test
    void testSetFailStates() {
        model.setPowerFailure(true);
        assertTrue(model.getPowerFailure());
        assertEquals(0, model.getPower());

        model.setSignalFailure(true);
        assertTrue(model.getSignalFailure());
        assertEquals(0, model.getAuthority());
        assertEquals(0, model.getCommandSpeed());

        model.setBrakeFailure(true);
        assertTrue(model.getBrakeFailure());
        assertFalse(model.getEmergencyBrake());
        assertFalse(model.getServiceBrake());
    }

    @Test
    void testSetBrakes() {
        model.setServiceBrake(true);
        assertTrue(model.getServiceBrake());

        model.setEmergencyBrake(true);
        assertTrue(model.getEmergencyBrake());
        assertEquals(0, model.getPower());
    }

    @Test
    void testSetValues() {
        model.setPower(100);
        assertEquals(100, model.getPower());

        model.setAuthority(10);
        assertEquals(10, model.getAuthority());

        model.setCommandSpeed(50);
        assertEquals(50, model.getCommandSpeed());
    }
}
