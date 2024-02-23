package waysideController;

import Common.WaysideController;
import javafx.beans.property.*;
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

    private ObservableList<TrainSpeedAuth> speedAuthList;

    /**
     * Constructor for the WaysideControllerSubject
     * @param controller The wayside controller that this subject is observing
     */
    public WaysideControllerSubject(WaysideController controller) {
        this.controller = controller;
        maintenanceMode = new SimpleBooleanProperty(controller.isMaintenanceMode());
        PLCName = new SimpleStringProperty();
        activePLCColor = new SimpleObjectProperty<>(Color.GRAY);
        blockList = FXCollections.observableArrayList();
        speedAuthList = FXCollections.observableArrayList();

        maintenanceMode.addListener((observableValue, oldValue, newVal) -> {
            this.controller.setMaintenanceModeNoUpdate(newVal);
            System.out.println("Setting maintenance mode to " + newVal);
        });
    }

    /**
     * Get the maintenance mode property
     * @return The maintenance mode property
     */
    public BooleanProperty maintenanceModeProperty() {
        return maintenanceMode;
    }

    /**
     * Get the PLC name property
     * @return The PLC name property
     */
    public StringProperty PLCNameProperty() {
        return PLCName;
    }

    /**
     * Get the active PLC color property
     * @return The active PLC color property
     */
    public ObjectProperty<Paint> activePLCColorProperty() {
        return activePLCColor;
    }

    /**
     * Get the block list property
     * @return The block list property
     */
    public ObservableList<WaysideBlockInfo> blockListProperty() {
        return blockList;
    }
    /**
     * Adds a block to the wayside controller's block list
     * @param block The block to add
     */
    public void addBlock(WaysideBlockInfo block) {
        blockList.add(block);
        block.occupationProperty().addListener((observable, oldValue, newValue) -> controller.trackModelSetOccupancy(block.getBlockID(), newValue));
        block.switchStateProperty().addListener((observable, oldValue, newValue) -> {
            controller.maintenanceSetSwitch(block.getBlockID(), newValue);
            System.out.println("Switch State Changed");
        });
        block.switchRequestedStateProperty().addListener((observable, oldValue, newValue) -> controller.CTCRequestSwitchState(block.getBlockID(), newValue));
    }

    /**
     * Get the wayside controller
     * @return The wayside controller
     */
    public WaysideController getController() {
        return this.controller;
    }

    /**
     * Get the speed and authority of a train
     * @return The speed and authority of the train
     */
    public ObservableList<TrainSpeedAuth> getSpeedAuthList() {
        return speedAuthList;
    }

    /**
     * Set the speed and authority of a train
     * @param speedAuth The speed and authority of the train
     */
    public void setSpeedAuth(TrainSpeedAuth speedAuth) {
        if(speedAuthList.contains(speedAuth)) {
            speedAuthList.set(speedAuthList.indexOf(speedAuth), speedAuth);
        } else {
            speedAuthList.add(speedAuth);
        }
    }
}
