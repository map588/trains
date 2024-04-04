package trainModel;

import Common.TrainController;
import Integration.BaseTest;
import Utilities.Records.UpdatedTrainValues;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class trainModelImplTest extends BaseTest {

    private static TrainModelImpl model;


    @BeforeAll
    static void setUp() {
        model = new TrainModelImpl(1, mock(TrainController.class)); // Assuming '1' is a valid trainID
    }

    @Test
    void testSettersAndGetters() throws InterruptedException {
        // Test set and get methods for authority
        model.setAuthority(10);
        assertEquals(10, model.getAuthority());

        // Test set and get methods for command speed
        model.setCommandSpeed(50.0);
        assertEquals(50.0, model.getCommandSpeed());

        // Test set and get methods for actual speed
        model.setActualSpeed(40.0);
        assertEquals(40.0, model.getSpeed());

        // Test set and get methods for acceleration
        model.setAcceleration(10.0);
        assertEquals(10.0, model.getAcceleration());

        // Test set and get methods for power
        model.setPower(100.0);
        assertEquals(100.0, model.getPower());

        // Test set and get methods for service brake
        model.setServiceBrake(true);
        assertTrue(model.getServiceBrake());

        // Test set and get methods for emergency brake
        model.setEmergencyBrake(true);
        assertTrue(model.getEmergencyBrake());

        // Test set and get methods for brake failure
        model.setBrakeFailure(true);
        assertTrue(model.getBrakeFailure());

        // Test set and get methods for power failure
        model.setPowerFailure(true);
        assertTrue(model.getPowerFailure());

        // Test set and get methods for signal failure
        model.setSignalFailure(true);
        assertTrue(model.getSignalFailure());

        // Test set and get methods for set temperature
        model.setSetTemperature(20.0);
        assertEquals(20.0, model.getSetTemperature());

        // Test set and get methods for real temperature
        model.setRealTemperature(18.0);
        assertEquals(18.0, model.getRealTemperature());

        // Test set and get methods for exterior lights
        model.setExtLights(true);
        assertTrue(model.getExtLights());

        // Test set and get methods for interior lights
        model.setIntLights(true);
        assertTrue(model.getIntLights());

        // Test set and get methods for left doors
        model.setLeftDoors(true);
        assertTrue(model.getLeftDoors());

        // Test set and get methods for right doors
        model.setRightDoors(true);
        assertTrue(model.getRightDoors());

        // Test set and get methods for number of cars
        model.setNumCars(2);
        assertEquals(2, model.getNumCars());

        // Test set and get methods for number of passengers
        model.setNumPassengers(50);
        assertEquals(50, model.getPassengerCount());

        // Test set and get methods for crew count
        model.setCrewCount(5);
        assertEquals(5, model.getCrewCount());

        // Test set and get methods for time delta
        model.changeTimeDelta(20);
        assertEquals(20, model.getTimeDelta());

        // Test set and get methods for mass
        model.setMass(10000.0);
        assertEquals(10000.0, model.getMass());

        // Test set and get methods for distance traveled
        model.setDistanceTraveled(1000.0);
        assertEquals(1000.0, model.getDistanceTraveled());

        // Test set and get methods for length
        model.setLength(200.0);
        assertEquals(200.0, model.getlength());

        // Test set and get methods for announcement
        model.setAnnouncement("Test announcement");
        assertEquals("Test announcement", model.getAnnouncement());
    }

    @Test
    void testTrainModelTimeStep() throws Exception {
        // Create a mock of Future<UpdatedTrainValues>
        Future<UpdatedTrainValues> futureMock = mock(Future.class);

        // Define the behavior of the mock
        when(futureMock.get()).thenReturn(new UpdatedTrainValues(300, true, false, 70, true, true, false, false));

        // Call the trainModelTimeStep() method with the mock as an argument
        model.trainModelTimeStep(futureMock);

        // Verify that the get() method of the mock was called
        verify(futureMock).get();
    }

    @Test
    void testTimeStepWithFailures() throws Exception {
        // Set failure states
        model.setBrakeFailure(true);
        model.setPowerFailure(true);

        // Create a mock of Future<UpdatedTrainValues>
        Future<UpdatedTrainValues> futureMock = mock(Future.class);

        // Define the behavior of the mock
        when(futureMock.get()).thenReturn(new UpdatedTrainValues(300, true, true, 70, true, true, false, false));

        // Call the trainModelTimeStep() method with the mock as an argument
        model.trainModelTimeStep(futureMock);

        // Verify that the get() method of the mock was called
        verify(futureMock).get();

        assertEquals(0.0, model.getPower());
        assertFalse(model.getServiceBrake());
        assertFalse(model.getEmergencyBrake());
    }
}
