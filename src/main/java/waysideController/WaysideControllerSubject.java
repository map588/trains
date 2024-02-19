package waysideController;

import Common.WaysideController;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class WaysideControllerSubject {
    private WaysideControllerImpl controller;
    private BooleanProperty manualMode;
    private StringProperty PLCName;
    private ObjectProperty<Paint> activePLCColor;

    public WaysideControllerSubject(WaysideControllerImpl controller) {
        this.controller = controller;
        manualMode = new SimpleBooleanProperty(controller.isManualMode());
        PLCName = new SimpleStringProperty();
        activePLCColor = new SimpleObjectProperty<>(Color.GRAY);

        manualMode.addListener((observableValue, oldValue, newVal) -> {
            controller.manualMode = newVal;
            updateActivePLCProp();
            System.out.println("Setting manual mode to " + newVal);
        });
    }

    private void updateActivePLCProp() {
        if(!controller.manualMode && controller.PLC != null)
            activePLCColor.set(Color.BLUE);
        else
            activePLCColor.set(Color.GRAY);
    }

    public BooleanProperty manualModeProperty() {
        return manualMode;
    }
    public StringProperty PLCNameProperty() {
        return PLCName;
    }
    public ObjectProperty<Paint> activePLCColorProperty() {
        return activePLCColor;
    }
}
