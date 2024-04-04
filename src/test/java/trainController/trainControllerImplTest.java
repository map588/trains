package trainController;

import Common.TrainModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class trainControllerImplTest {
    private TrainControllerImpl controller;
    private TrainModel trainModel;

    @BeforeEach
    void setUp() {
        trainModel = mock(TrainModel.class);
        controller = new TrainControllerImpl(trainModel, 1);
    }

    @Test
    void testSetValue() {
        controller.setValue(Properties.SPEED_LIMIT_PROPERTY, 70.0);
        assertEquals(70.0, controller.getSpeedLimit());
    }

    @Test
    void testCalculatePower() {
        when(trainModel.getMass()).thenReturn(50000.0);
        controller.setCommandSpeed(60.0);
        controller.setAutomaticMode(true);
        double power = controller.calculatePower(50.0);
        assertTrue(power > 0.0);
    }

    // Add more test methods for other properties and scenarios
    @Test
    void testDoors(){
        System.out.println("Testing Opening Doors");
        controller.setValue(Properties.LEFT_DOORS_PROPERTY,true);
        controller.setValue(Properties.RIGHT_DOORS_PROPERTY,true);
        assertTrue(controller.getLeftDoors());
        assertTrue(controller.getRightDoors());

        System.out.println("Testing Closing Doors");
        controller.setValue(Properties.LEFT_DOORS_PROPERTY,false);
        controller.setValue(Properties.RIGHT_DOORS_PROPERTY,false);
        assertFalse(controller.getLeftDoors());
        assertFalse(controller.getRightDoors());
    }
    @Test
    void testLights(){
        System.out.println("Testing Turning Lights On");
        controller.setValue(Properties.INT_LIGHTS_PROPERTY,true);
        controller.setValue(Properties.EXT_LIGHTS_PROPERTY, true);
        assertTrue(controller.getExtLights());
        assertTrue(controller.getIntLights());

        System.out.println("Testing Turning Lights Off");
        controller.setValue(Properties.INT_LIGHTS_PROPERTY,false);
        controller.setValue(Properties.EXT_LIGHTS_PROPERTY, false);
        assertFalse(controller.getExtLights());
        assertFalse(controller.getIntLights());
    }
    @Test
    void testSetTemperature(){
        controller.setValue(Properties.SET_TEMPERATURE_PROPERTY, 70.0);
        assertEquals(70.0,controller.getSetTemperature());
    }
    @Test
    void testSetKiAndKp(){
        controller.setValue(Properties.KI_PROPERTY,100.0);
        controller.setValue(Properties.KP_PROPERTY,50.0);

        assertEquals(100,controller.getKi());
        assertEquals(50, controller.getKp());
    }
    @Test
    void testEBrake(){

        // Change the trainmodel by engaging the emergency button

        // Check if both eBrakes are engage
        //assertTrue(trainModel.getEmergencyBrake());
        //assertTrue(controller.getEmergencyBrake());

        // Check if both eBrakes are disengage
        controller.setValue(Properties.EMERGENCY_BRAKE_PROPERTY,false);
        assertFalse(controller.getEmergencyBrake());
        //assertFalse(trainModel.getEmergencyBrake());

        // Check if both eBrake are engage again
        controller.setValue(Properties.EMERGENCY_BRAKE_PROPERTY,true);
        assertTrue(controller.getEmergencyBrake());
        //assertTrue(trainModel.getEmergencyBrake());

    }
    @Test
    void testServiceBrake(){
        controller.setValue(Properties.SERVICE_BRAKE_PROPERTY,true);
        assertTrue(controller.getServiceBrake());

        controller.setValue(Properties.SERVICE_BRAKE_PROPERTY,false);
        assertFalse(controller.getServiceBrake());
    }

    @Test
    void testFailures(){

    }
}