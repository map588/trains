package Framework.Simulation;

import Common.CTCOffice;
import Common.TrackModel;
import Common.WaysideController;
import Utilities.Enums.Lines;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import trackModel.TrackLineMap;
import waysideController.WaysideControllerHWBridge;
import waysideController.WaysideControllerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WaysideSystem {

    private static final ObjectProperty<ObservableList<WaysideController>> controllerList = new SimpleObjectProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private static final Map<Integer, WaysideController> controllerMapGreen = new HashMap<>();
    private static final Map<Integer, WaysideController> controllerMapRed = new HashMap<>();

    public static ObjectProperty<ObservableList<WaysideController>> getControllerList() {
        return controllerList;
    }

    public static WaysideController getController(Lines line, int blockID) {
        if(line == Lines.GREEN)
            return controllerMapGreen.get(blockID);
        else
            return controllerMapRed.get(blockID);
    }

    public static void addController(WaysideController controller, Lines line) {
        controllerList.get().add(controller);
        controller.getBlockMap().forEach((blockID, waysideBlock) -> {
            if(line == Lines.RED)
                controllerMapRed.put(blockID, controller);
            else
                controllerMapGreen.put(blockID, controller);
        });
    }

    public static int size() {
        return controllerList.get().size();
    }

    ExecutorService waysideExecutor;

    public WaysideSystem(TrackSystem trackSystem, CTCOffice ctcOffice, boolean useHardware) {
        TrackModel greenLine = TrackLineMap.getTrackLine(Lines.GREEN);
//        TrackModel greenLine = null;
        addController(new WaysideControllerImpl(1, Lines.GREEN, new int[]{
                1, 2, 3,
                4, 5, 6,
                7, 8, 9, 10, 11, 12,
                13, 14, 15, 16,
                17, 18, 19, 20,
                21, 22, 23, 24, 25, 26, 27, 28,
                29, 30, 31, 32,
                33, 34, 35,
                144, 145, 146,
                147, 148, 149,
                150},
                greenLine, ctcOffice,
                "src/main/antlr/GreenLine1.plc"), Lines.GREEN);

        addController(new WaysideControllerImpl(2, Lines.GREEN, new int[]{
                69, 70, 71, 72, 73,
                74, 75, 76,
                77, 78, 79, 80, 81, 82, 83, 84, 85,
                86, 87, 88,
                89, 90, 91, 92, 93, 94, 95, 96, 97,
                98, 99, 100,
                101,
                102, 103, 104,
                105, 106, 107, 108, 109},
                greenLine, ctcOffice,
                "src/main/antlr/GreenLine2.plc"), Lines.GREEN);
        if(useHardware) {
            addController(new WaysideControllerHWBridge(3, Lines.GREEN, new int[]{
                    0,
                    36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
                    58, 59, 60, 61, 62,
                    63, 64, 65, 66, 67, 68,
                    110, 111, 112, 113, 114, 115, 116,
                    117, 118, 119, 120, 121,
                    122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143},
                    "COM6",
                    "src/main/antlr/GreenLine3.plc"), Lines.GREEN);
        }
        else {
            addController(new WaysideControllerImpl(3, Lines.GREEN, new int[]{
                    0,
                    36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
                    58, 59, 60, 61, 62,
                    63, 64, 65, 66, 67, 68,
                    110, 111, 112, 113, 114, 115, 116,
                    117, 118, 119, 120, 121,
                    122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143},
                    greenLine, ctcOffice,
                    "src/main/antlr/GreenLine3.plc"), Lines.GREEN);
        }

        waysideExecutor = Executors.newFixedThreadPool(size());
    }

    public void update() {
        // For each WaysideController in the controllerList, run its PLC
        try{
            for (WaysideController controller : controllerList.get()) {
                controller.runPLC();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
