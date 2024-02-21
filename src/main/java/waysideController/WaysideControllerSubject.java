package waysideController;

import Common.WaysideController;
import Utilities.BlockInfo;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class WaysideControllerSubject {
    private WaysideController controller;
    private BooleanProperty maintenanceMode;
    private StringProperty PLCName;
    private ObjectProperty<Paint> activePLCColor;
    private ObservableList<WaysideBlockInfo> blockList;

    public WaysideControllerSubject(WaysideController controller) {
        this.controller = controller;
        maintenanceMode = new SimpleBooleanProperty(controller.isMaintenanceMode());
        PLCName = new SimpleStringProperty();
        activePLCColor = new SimpleObjectProperty<>(Color.GRAY);
        blockList = FXCollections.observableArrayList();

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
    public ObservableList<WaysideBlockInfo> blockListProperty() {
        return blockList;
    }
    public void addBlock(WaysideBlockInfo block) {
        blockList.add(block);
        block.occupationProperty().addListener((observable, oldValue, newValue) -> controller.trackModelSetOccupancy(block.getBlockID(), newValue));
    }
    public WaysideController getController() {
        return this.controller;
    }
}
