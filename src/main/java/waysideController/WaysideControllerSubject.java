package waysideController;

import Common.WaysideController;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class WaysideControllerSubject {
    private WaysideController controller;
    private BooleanProperty manualMode;
    private StringProperty PLCName;
    private ObjectProperty<Paint> activePLCColor;

    public WaysideControllerSubject(WaysideController controller) {
        this.controller = controller;
        manualMode = new SimpleBooleanProperty(controller.isManualMode());
        PLCName = new SimpleStringProperty();
        activePLCColor = new SimpleObjectProperty<>(Color.GRAY);
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
