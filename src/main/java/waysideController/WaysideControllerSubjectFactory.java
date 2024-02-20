package waysideController;

import Common.WaysideController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class WaysideControllerSubjectFactory {

    private static final ObjectProperty<ObservableList<WaysideController>> controllerList = new SimpleObjectProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

    public static ObjectProperty<ObservableList<WaysideController>> getControllerList() {
        return controllerList;
    }

    public static void addSubject(WaysideControllerSubject subject) {
        controllerList.get().add(subject.getController());
    }

    public static void addController(WaysideController controller) {
        controllerList.get().add(controller);
    }

    public static int size() {
        return controllerList.get().size();
    }
}
