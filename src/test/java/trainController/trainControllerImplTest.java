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
}