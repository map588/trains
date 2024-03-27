package waysideController;

import Common.CTCOffice;
import Common.TrackModel;
import Utilities.Enums.Lines;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WaysideControllerImplTests {
    private WaysideControllerImpl controller;
    private TrackModel trackModel;
    private CTCOffice ctcOffice;

    @BeforeEach
    void setUp() {
        trackModel = mock(TrackModel.class); // Mocking TrackModel
        ctcOffice = mock(CTCOffice.class); // Mocking CTCOffice
        controller = new WaysideControllerImpl(1, Lines.GREEN, new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24}, trackModel, ctcOffice);
    }

    @Test
    void testBlockMapCreation() {
        Map<Integer, WaysideBlock> blockMap = controller.getBlockMap();
        assertEquals(blockMap.size(), 24);

        for (int i = 1; i <= 24; i++) {
            assertTrue(blockMap.containsKey(i));
            assertEquals(blockMap.get(i).getBlockID(), i);
        }
    }

    @Test
    void testMaintenanceMode() {
        assertFalse(controller.isMaintenanceMode());

        // Test that maintenance functions do nothing outside of maintenance mode
        assertFalse(controller.getBlockMap().get(12).getSwitchState());
        controller.maintenanceSetSwitch(12, true);
        assertFalse(controller.getBlockMap().get(12).getSwitchState());

        assertFalse(controller.getBlockMap().get(12).getLightState());
        controller.maintenanceSetTrafficLight(12, true);
        assertFalse(controller.getBlockMap().get(12).getLightState());

        assertFalse(controller.getBlockMap().get(12).getCrossingState());
        controller.maintenanceSetCrossing(12, true);
        assertFalse(controller.getBlockMap().get(12).getCrossingState());

        // Enable maintenance mode
        controller.setMaintenanceMode(true);
        assertTrue(controller.isMaintenanceMode());

        // Test that maintenance mode functions work
        assertFalse(controller.getBlockMap().get(12).getSwitchState());
        controller.maintenanceSetSwitch(12, true);
        assertTrue(controller.getBlockMap().get(12).getSwitchState());
        verify(trackModel).setSwitchState(12, true);

        assertFalse(controller.getBlockMap().get(12).getLightState());
        controller.maintenanceSetTrafficLight(12, true);
        assertTrue(controller.getBlockMap().get(12).getLightState());
        verify(trackModel).setSignalState(12, true);

        assertFalse(controller.getBlockMap().get(12).getCrossingState());
        controller.maintenanceSetCrossing(12, true);
        assertTrue(controller.getBlockMap().get(12).getCrossingState());
        verify(trackModel).setCrossing(12, true);
    }
}
