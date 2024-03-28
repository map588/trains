package waysideController;

import Common.CTCOffice;
import Common.TrackModel;
import Utilities.Enums.Lines;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
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

        File plcFile = new File("src/main/antlr/GreenLine1.plc");
        controller.loadPLC(plcFile);
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
        assertFalse(controller.getBlockMap().get(13).getSwitchState());
        controller.maintenanceSetSwitch(13, true);
        assertFalse(controller.getBlockMap().get(13).getSwitchState());

        assertFalse(controller.getBlockMap().get(13).getLightState());
        controller.maintenanceSetTrafficLight(13, true);
        assertFalse(controller.getBlockMap().get(13).getLightState());

        assertFalse(controller.getBlockMap().get(13).getCrossingState());
        controller.maintenanceSetCrossing(13, true);
        assertFalse(controller.getBlockMap().get(13).getCrossingState());

        // Enable maintenance mode
        controller.setMaintenanceMode(true);
        assertTrue(controller.isMaintenanceMode());

        // Test that maintenance mode functions work
        assertFalse(controller.getBlockMap().get(13).getSwitchState());
        controller.maintenanceSetSwitch(13, true);
        assertTrue(controller.getBlockMap().get(13).getSwitchState());
        verify(trackModel).setSwitchState(13, true);
        verify(ctcOffice).setSwitchState(true, 13, true);

        assertFalse(controller.getBlockMap().get(13).getLightState());
        controller.maintenanceSetTrafficLight(13, true);
        assertTrue(controller.getBlockMap().get(13).getLightState());
        verify(trackModel).setSignalState(13, true);
        verify(ctcOffice).setLightState(true, 13, true);

        assertFalse(controller.getBlockMap().get(13).getCrossingState());
        controller.maintenanceSetCrossing(13, true);
        assertTrue(controller.getBlockMap().get(13).getCrossingState());
        verify(trackModel).setCrossing(13, true);
        verify(ctcOffice).setCrossingState(true, 13, true);
    }

    @Test
    void testSwitchBlocks() {
        WaysideBlock block = controller.getBlockMap().get(13);
        assertFalse(block.getSwitchState());
        assertEquals(block.getSwitchBlockParent(), 13);
        assertEquals(block.getSwitchBlockDef(), 12);
        assertEquals(block.getSwitchBlockAlt(), 1);
        block.setDirection(true);
        assertEquals(block.nextBlock(), 12);
        block.setDirection(false);
        assertEquals(block.nextBlock(), 14);

        controller.setMaintenanceMode(true);
        controller.maintenanceSetSwitch(13, true);
        assertTrue(block.getSwitchState());
        block.setDirection(true);
        assertEquals(block.nextBlock(), 1);
        block.setDirection(false);
        assertEquals(block.nextBlock(), 14);

        verify(trackModel).setSwitchState(13, true);
    }

    @Test
    void testGreenLine1() {
        Map<Integer, WaysideBlock> blockMap = controller.getBlockMap();
        assertTrue(controller.waysideRequestDirection(20, true));
        assertTrue(blockMap.get(20).getBooleanAuth());

        controller.waysideIncomingTrain(1, 20, 0);
        assertFalse(blockMap.get(20).hasTrain());
        assertTrue(blockMap.get(20).getBooleanAuth());

        controller.trackModelSetOccupancy(20, true);
        assertEquals(1, blockMap.get(20).getTrainID());
        assertTrue(blockMap.get(20).getBooleanAuth());

        for(int i = 19; i >= 14; i--) {
            controller.trackModelSetOccupancy(i, true);
            assertEquals(1, blockMap.get(i).getTrainID());
            assertTrue(blockMap.get(i).getBooleanAuth());

            controller.trackModelSetOccupancy(i+1, false);
            assertFalse(blockMap.get(i+1).hasTrain());
        }

        controller.trackModelSetOccupancy(13, true);
        assertEquals(1, blockMap.get(13).getTrainID());
        assertTrue(blockMap.get(13).getBooleanAuth());
        assertFalse(blockMap.get(13).getSwitchState());

        controller.trackModelSetOccupancy(14, false);
        assertFalse(blockMap.get(14).hasTrain());

        for(int i = 12; i >= 5; i--) {
            controller.trackModelSetOccupancy(i, true);
            assertEquals(1, blockMap.get(i).getTrainID());
            assertTrue(blockMap.get(i).getBooleanAuth());

            controller.trackModelSetOccupancy(i+1, false);
            assertFalse(blockMap.get(i+1).hasTrain());
        }
    }
}
