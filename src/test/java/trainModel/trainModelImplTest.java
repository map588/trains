package trainModel;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class trainModelImplTest {

    private static TrainModelImpl model;


    @BeforeAll
    static void setUp() {
        model = new TrainModelImpl(1, null); // Assuming '1' is a valid trainID
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
        model.setSignalFailure(true);
        model.setBrakeFailure(true);

        model.setServiceBrake(true);
        model.setEmergencyBrake(true);
        model.setPower(100);
        model.setAuthority(10);
        model.setCommandSpeed(50);

        model.trainModelPhysics();

        assertTrue(model.getPowerFailure());
        assertEquals(0, model.getPower());

        assertTrue(model.getSignalFailure());
        assertEquals(-1, model.getAuthority());
        assertEquals(-1, model.getCommandSpeed());


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

    @Test
    void testPhysics() {
        model.setPower(100);
        model.setAuthority(10);
        model.setCommandSpeed(50);

        model.trainModelPhysics();

        assertEquals(100, model.getPower());
        assertEquals(10, model.getAuthority());
        assertEquals(50, model.getCommandSpeed());
        assertEquals(50, model.getSpeed());
    }
}
