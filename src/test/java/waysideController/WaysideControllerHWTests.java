package waysideController;

import Integration.BaseTest;
import Utilities.Enums.Lines;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WaysideControllerHWTests extends BaseTest {
    private WaysideControllerHWBridge controllerBridge;

    @BeforeEach
    void setUp() {
        controllerBridge = new WaysideControllerHWBridge(1, Lines.GREEN, new int[]{
                1, 2, 3,
                4, 5, 6,
                7, 8, 9, 10, 11, 12,
                13, 14, 15, 16,
                17, 18, 19, 20,
                21, 22, 23, 24, 25, 26, 27, 28,
                29, 30, 31, 32,
                33, 34, 35,
                36, 37, 38, 39,
                144, 145, 146,
                147, 148, 149,
                150},
                new int[] {0,1},
                "COM8",
                null, null,
                "src/main/antlr/GreenLine1.plc");
    }

    @Test
    void testMaintenanceMode() {
        assertFalse(controllerBridge.isMaintenanceMode());

        // Enable maintenance mode
        controllerBridge.setMaintenanceMode(true);
        assertTrue(controllerBridge.isMaintenanceMode());

        // Test that maintenance mode functions work
        assertFalse(controllerBridge.getBlockMap().get(13).getSwitchState());
        controllerBridge.maintenanceSetSwitch(13, true);
        assertTrue(controllerBridge.getBlockMap().get(13).getSwitchState());

        assertFalse(controllerBridge.getBlockMap().get(13).getLightState());
        controllerBridge.maintenanceSetTrafficLight(13, true);
        assertTrue(controllerBridge.getBlockMap().get(13).getLightState());

        assertFalse(controllerBridge.getBlockMap().get(13).getCrossingState());
        controllerBridge.maintenanceSetCrossing(13, true);
        assertTrue(controllerBridge.getBlockMap().get(13).getCrossingState());
    }
}
