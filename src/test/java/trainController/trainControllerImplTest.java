package trainController;

import Common.TrainModel;
import Integration.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class trainControllerImplTest extends BaseTest {
    private TrainControllerImpl controller;
    private TrainModel trainModel;

    @BeforeEach
    void setUp() {
        trainModel = mock(TrainModel.class);
        controller = new TrainControllerImpl(trainModel, 1);
    }

    @Test
    void testSetValue() {
        controller.setValue(Controller_Property.SPEED_LIMIT, 70.0);
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
        controller.setValue(Controller_Property.LEFT_DOORS,true);
        controller.setValue(Controller_Property.RIGHT_DOORS,true);
        assertTrue(controller.getLeftDoors());
        assertTrue(controller.getRightDoors());

        System.out.println("Testing Closing Doors");
        controller.setValue(Controller_Property.LEFT_DOORS,false);
        controller.setValue(Controller_Property.RIGHT_DOORS,false);
        assertFalse(controller.getLeftDoors());
        assertFalse(controller.getRightDoors());
    }
    @Test
    void testLights(){
        System.out.println("Testing Turning Lights On");
        controller.setValue(Controller_Property.INT_LIGHTS,true);
        controller.setValue(Controller_Property.EXT_LIGHTS, true);
        assertTrue(controller.getExtLights());
        assertTrue(controller.getIntLights());

        System.out.println("Testing Turning Lights Off");
        controller.setValue(Controller_Property.INT_LIGHTS,false);
        controller.setValue(Controller_Property.EXT_LIGHTS, false);
        assertFalse(controller.getExtLights());
        assertFalse(controller.getIntLights());
    }
    @Test
    void testSetTemperature(){
        controller.setValue(Controller_Property.SET_TEMPERATURE, 70.0);
        assertEquals(70.0,controller.getSetTemperature());
    }
    @Test
    void testSetKiAndKp(){
        controller.setValue(Controller_Property.KI,100.0);
        controller.setValue(Controller_Property.KP,50.0);

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
        controller.setValue(Controller_Property.EMERGENCY_BRAKE,false);
        assertFalse(controller.getEmergencyBrake());
        //assertFalse(trainModel.getEmergencyBrake());

        // Check if both eBrake are engage again
        controller.setValue(Controller_Property.EMERGENCY_BRAKE,true);
        assertTrue(controller.getEmergencyBrake());
        //assertTrue(trainModel.getEmergencyBrake());

    }
    @Test
    void testServiceBrake(){
        controller.setValue(Controller_Property.SERVICE_BRAKE,true);
        assertTrue(controller.getServiceBrake());

        controller.setValue(Controller_Property.SERVICE_BRAKE,false);
        assertFalse(controller.getServiceBrake());
    }

    @Test
    void testFailures(){

    }
}