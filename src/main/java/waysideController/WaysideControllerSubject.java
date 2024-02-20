package waysideController;

import Common.WaysideController;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class WaysideControllerSubject {
    private WaysideController controller;
    private BooleanProperty maintenanceMode;
    private StringProperty PLCName;
    private ObjectProperty<Paint> activePLCColor;

    public WaysideControllerSubject(WaysideController controller) {
        this.controller = controller;
        maintenanceMode = new SimpleBooleanProperty(controller.isMaintenanceMode());
        PLCName = new SimpleStringProperty();
        activePLCColor = new SimpleObjectProperty<>(Color.GRAY);

        maintenanceMode.addListener((observableValue, oldValue, newVal) -> {
            this.controller.setMaintenanceModeNoUpdate(newVal);
            System.out.println("Setting maintenance mode to " + newVal);
        });
    }

    public BooleanProperty maintenanceModeProperty() {
        return maintenanceMode;
    }
    public StringProperty PLCNameProperty() {
        return PLCName;
    }
    public ObjectProperty<Paint> activePLCColorProperty() {
        return activePLCColor;
    }
    public WaysideController getController() {
        return this.controller;
    }
}
