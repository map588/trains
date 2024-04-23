package trainModel;

import Integration.BaseTest;
import trainModel.Records.UpdatedTrainValues;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class trainModelImplTest extends BaseTest {

    private static TrainModelImpl model;
    private static trackModel.TrackLine track;


    @BeforeAll
    static void setUp() {
        track = mock(trackModel.TrackLine.class);
        model = new TrainModelImpl(track, 1); // Assuming '1' is a valid trainID
    }

    @Test
    void testEmergencyBrake() {
        model.setEmergencyBrake(true);
        assertTrue(model.getEmergencyBrake());
    }

    @Test
    void testServiceBrake() {
        model.setServiceBrake(true);
        assertTrue(model.getServiceBrake());
    }

    @Test
    void testRightDoors() {
        model.setRightDoors(true);
        assertTrue(model.getRightDoors());
    }

    @Test
    void testLeftDoors() {
        model.setLeftDoors(true);
        assertTrue(model.getLeftDoors());
    }

    @Test
    void testInteriorLights() {
        model.setIntLights(true);
        assertTrue(model.getIntLights());
    }

    @Test
    void testExteriorLights() {
        model.setExtLights(true);
        assertTrue(model.getExtLights());
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

//    @Test
//    void testPhysics() throws Exception {
//        model.setPower(300);
//        model.setServiceBrake(false);
//        model.setEmergencyBrake(false);
//        model.setMass(37103.86);
//        model.setGrade(0.0);
//        model.setAcceleration(0.0);
//        model.setActualSpeed(10.0);
//        model.setSetTemperature(70.0);
//        model.setRealTemperature(70.0);
//
//        model.trainModelPhysics();
//
//
//    }
}
