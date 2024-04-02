package Framework.Simulation;

import Common.WaysideController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import waysideController.WaysideControllerSubject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WaysideSystem {

    private static final ObjectProperty<ObservableList<WaysideController>> controllerList = new SimpleObjectProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private static final Map<Integer, WaysideController> controllerMap = new HashMap<>();

    public static ObjectProperty<ObservableList<WaysideController>> getControllerList() {
        return controllerList;
    }

    public static WaysideController getController(int blockID) {
        return controllerMap.get(blockID);
    }

    public static void addSubject(WaysideControllerSubject subject) {
        WaysideController controller = subject.getController();
        controllerList.get().add(controller);
        controller.getBlockMap().forEach((blockID, waysideBlock) -> {
            controllerMap.put(blockID, controller);
        });
    }

    public static void addController(WaysideController controller) {
        controllerList.get().add(controller);
        controller.getBlockMap().forEach((blockID, waysideBlock) -> {
            controllerMap.put(blockID, controller);
        });
    }

    public static int size() {
        return controllerList.get().size();
    }
    public void update() {
    }
}
