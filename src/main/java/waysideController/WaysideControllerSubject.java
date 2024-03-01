package waysideController;

import Common.WaysideController;
import Framework.Support.AbstractSubject;
import Framework.Support.Notifications;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static waysideController.Properties.*;

public class WaysideControllerSubject implements AbstractSubject, Notifications {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private WaysideController controller;
    private ObservableList<WaysideBlockSubject> blockList;
    private ObservableList<TrainSpeedAuth> speedAuthList;

    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    public boolean isLogicUpdate = false;
    public boolean isGUIUpdate = false;

    public WaysideControllerSubject(WaysideController controller) {
        this.controller = controller;
        properties.put(maintenanceMode_p, new SimpleBooleanProperty(controller.isMaintenanceMode()));
        properties.put(PLCName_p, new SimpleStringProperty());
        properties.put(activePLCColor_p, new SimpleObjectProperty<>(Color.GRAY));
        blockList = FXCollections.observableArrayList();
        speedAuthList = FXCollections.observableArrayList();

//        properties.get(maintenanceMode_p).addListener((observable, oldValue, newValue) -> controller.setMaintenanceMode((Boolean) newValue));
    }

    public void notifyChange(String propertyName, Object newValue) {
        // Update property from controller, Internal Logic takes precedence over GUI updates
        if (!isGUIUpdate) {
            updateFromLogic(() -> {
                Property<?> property = properties.get(propertyName);
                updateProperty(property, newValue);
            });
        }
    }

    public void setProperty(String propertyName, Object newValue) {
        Runnable updateTask = () -> {
            Property<?> property = properties.get(propertyName);
            updateProperty(property, newValue);
            controller.setValue(propertyName, newValue);
        };

        if (isLogicUpdate) {
            executorService.scheduleWithFixedDelay(() -> {
                if (!isLogicUpdate) {
                    System.out.println("Delayed setProperty from GUI");
                    Platform.runLater(() -> updateFromGUI(updateTask));
                }
            }, 0, 10, TimeUnit.MILLISECONDS);
        } else {
            Platform.runLater(() -> updateFromGUI(updateTask));
        }
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    // Directly accessing typed properties for GUI binding
    public StringProperty getStringProperty(String propertyName){
        Property<?> property = getProperty(propertyName);
        try{
            return (StringProperty) property;
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Property " + propertyName + " is not a StringProperty");
        }
    }
    public BooleanProperty getBooleanProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        try {
            return (BooleanProperty) property;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property " + propertyName + " is not a BooleanProperty");
        }
    }
    public ObjectProperty<Paint> getPaintProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        try {
            return (ObjectProperty<Paint>) property;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property " + propertyName + " is not a BooleanProperty");
        }
    }

    // Handling updates from the GUI
    public void updateFromGUI(Runnable updateLogic) {
        System.out.println("Called from updateFromGUI.");
        isGUIUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isGUIUpdate = false;
        }
    }

    public void updateFromLogic(Runnable updateLogic) {
        isLogicUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isLogicUpdate = false;
        }
    }

    public ObservableList<WaysideBlockSubject> blockListProperty() {
        return blockList;
    }

    public void updateActivePLCProp() {
        if(!getBooleanProperty(maintenanceMode_p).get() && getStringProperty(PLCName_p).get() != null)
            getPaintProperty(activePLCColor_p).set(Color.BLUE);
        else
            getPaintProperty(activePLCColor_p).set(Color.GRAY);
    }
    public void addBlock(WaysideBlockSubject block) {
        blockList.add(block);
        block.occupationProperty().addListener((observable, oldValue, newValue) -> controller.trackModelSetOccupancy(block.getBlockID(), newValue));
        block.switchStateProperty().addListener((observable, oldValue, newValue) -> controller.maintenanceSetSwitch(block.getBlockID(), newValue));
        block.lightStateProperty().lightStateProperty().addListener((observable, oldValue, newValue) -> controller.maintenanceSetTrafficLight(block.getBlockID(), newValue));
        block.crossingStateProperty().addListener((observable, oldValue, newValue) -> controller.maintenanceSetCrossing(block.getBlockID(), newValue));
        block.switchRequestedStateProperty().addListener((observable, oldValue, newValue) -> controller.CTCRequestSwitchState(block.getBlockID(), newValue));
        block.authorityProperty().addListener((observable, oldValue, newValue) -> controller.maintenanceSetAuthority(block.getBlockID(), newValue));
    }
    public WaysideController getController() {
        return this.controller;
    }
    public ObservableList<TrainSpeedAuth> getSpeedAuthList() {
        return speedAuthList;
    }
    public void setSpeedAuth(TrainSpeedAuth speedAuth) {
        if(speedAuthList.contains(speedAuth)) {
            speedAuthList.set(speedAuthList.indexOf(speedAuth), speedAuth);
        } else {
            speedAuthList.add(speedAuth);
        }
    }
}
