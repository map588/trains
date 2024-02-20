package trainController;

import Common.TrainController;
import Framework.AbstractSubject;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;

import javax.swing.*;
import java.util.List;

public class trainControllerSubject implements AbstractSubject {
    private IntegerProperty authority, trainID, blocksToNextStation;
    private DoubleProperty commandSpeed, currentSpeed, overrideSpeed, maxSpeed, Ki, Kp, power, temperature;
    private BooleanProperty serviceBrake, emergencyBrake, automaticMode, intLights, extLights, leftDoors, rightDoors;
    private BooleanProperty announcements, signalFailure, brakeFailure, powerFailure;

    private TrainController controller;
    private boolean isGuiUpdate = false;

    public trainControllerSubject() {
        this.authority = new SimpleIntegerProperty(this, "authority");
        this.trainID = new SimpleIntegerProperty(this, "trainID");
        this.blocksToNextStation = new SimpleIntegerProperty(this, "blocksToNextStation");
        this.commandSpeed = new SimpleDoubleProperty(this, "commandSpeed");
        this.currentSpeed = new SimpleDoubleProperty(this, "currentSpeed");
        this.overrideSpeed = new SimpleDoubleProperty(this, "overrideSpeed");
        this.maxSpeed = new SimpleDoubleProperty(this, "maxSpeed");
        this.Ki = new SimpleDoubleProperty(this, "Ki");
        this.Kp = new SimpleDoubleProperty(this, "Kp");
        this.power = new SimpleDoubleProperty(this, "power");
        this.serviceBrake = new SimpleBooleanProperty(this, "serviceBrake");
        this.emergencyBrake = new SimpleBooleanProperty(this, "emergencyBrake");
        this.automaticMode = new SimpleBooleanProperty(this, "automaticMode");
        this.intLights = new SimpleBooleanProperty(this, "intLights");
        this.extLights = new SimpleBooleanProperty(this, "extLights");
        this.leftDoors = new SimpleBooleanProperty(this, "leftDoors");
        this.rightDoors = new SimpleBooleanProperty(this, "rightDoors");
        this.announcements = new SimpleBooleanProperty(this, "announcements");
        this.signalFailure = new SimpleBooleanProperty(this, "signalFailure");
        this.brakeFailure = new SimpleBooleanProperty(this, "brakeFailure");
        this.powerFailure = new SimpleBooleanProperty(this, "powerFailure");
    }

    public trainControllerSubject(TrainController controller) {
        this();
        this.controller = controller;
        initializePropertiesFromController();

        controller.addChangeListener(this::handleControllerChange);
    }

    private void handleControllerChange(String propertyName, Object newValue) {
        // Update the property based on the change notification from the controller
        Platform.runLater(() -> setProperty(propertyName, newValue));
    }

    private void setProperty(String propertyName, Object newValue) {
        if (isGuiUpdate) {
            return;
        }

        if (newValue == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        switch (propertyName) {
            case "authority" -> updateProperty(authority, newValue);
            case "trainID" -> updateProperty(trainID, newValue);
            case "blocksToNextStation" -> updateProperty(blocksToNextStation, newValue);
            case "commandSpeed" -> updateProperty(commandSpeed, newValue);
            case "currentSpeed" -> updateProperty(currentSpeed, newValue);
            case "overrideSpeed" -> updateProperty(overrideSpeed, newValue);
            case "maxSpeed" -> updateProperty(maxSpeed, newValue);
            case "Ki" -> updateProperty(Ki, newValue);
            case "Kp" -> updateProperty(Kp, newValue);
            case "power" -> updateProperty(power, newValue);
            case "serviceBrake" -> updateProperty(serviceBrake, newValue);
            case "emergencyBrake" -> updateProperty(emergencyBrake, newValue);
            case "automaticMode" -> updateProperty(automaticMode, newValue);
            case "intLights" -> updateProperty(intLights, newValue);
            case "extLights" -> updateProperty(extLights, newValue);
            case "leftDoors" -> updateProperty(leftDoors, newValue);
            case "rightDoors" -> updateProperty(rightDoors, newValue);
            case "announcements" -> updateProperty(announcements, newValue);
            case "signalFailure" -> updateProperty(signalFailure, newValue);
            case "brakeFailure" -> updateProperty(brakeFailure, newValue);
            case "powerFailure" -> updateProperty(powerFailure, newValue);
            case "temperature" -> updateProperty(temperature, newValue);
            default -> System.err.println("Unknown property " + propertyName);
        }
    }

    // Method to safely cast and update the property value
    // This can be extended to handle different property types more gracefully
    @SuppressWarnings("unchecked")
    public <T> void updateProperty(Property<T> property, Object newValue) {
        if (property instanceof DoubleProperty && newValue instanceof Number) {
            ((DoubleProperty) property).set(((Number) newValue).doubleValue());
        } else if (property instanceof IntegerProperty && newValue instanceof Number) {
            ((IntegerProperty) property).set(((Number) newValue).intValue());
        } else if (property instanceof BooleanProperty && newValue instanceof Boolean) {
            ((BooleanProperty) property).set((Boolean) newValue);
        } else {
            System.err.println("Mismatch in property type and value type for " + property.getName());
        }

        System.out.println("Property " + property.getName() + " updated to " + newValue + " in Subject");
    }

    public BooleanProperty getBooleanProperty (String propertyName) {
        return switch (propertyName) {
            case "serviceBrake" -> serviceBrake;
            case "emergencyBrake" -> emergencyBrake;
            case "automaticMode" -> automaticMode;
            case "intLights" -> intLights;
            case "extLights" -> extLights;
            case "leftDoors" -> leftDoors;
            case "rightDoors" -> rightDoors;
            case "announcements" -> announcements;
            case "signalFailure" -> signalFailure;
            case "brakeFailure" -> brakeFailure;
            case "powerFailure" -> powerFailure;
            default -> null;
        };
    }

    public DoubleProperty getDoubleProperty(String propertyName) {
        return switch (propertyName) {
            case "commandSpeed" -> commandSpeed;
            case "currentSpeed" -> currentSpeed;
            case "overrideSpeed" -> overrideSpeed;
            case "maxSpeed" -> maxSpeed;
            case "Ki" -> Ki;
            case "Kp" -> Kp;
            case "power" -> power;
            case "temperature" -> temperature;
            default -> null;
        };
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return switch (propertyName) {
            case "authority" -> authority;
            case "trainID" -> trainID;
            case "blocksToNextStation" -> blocksToNextStation;
            default -> null;
        };
    }

    public void updateFromGui(Runnable updateLogic) {
        isGuiUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isGuiUpdate = false;
        }
    }

    private void initializePropertiesFromController() {
        this.trainID.set(controller.getID());
        this.currentSpeed.set(controller.getSpeed());
        this.commandSpeed.set(controller.getCommandSpeed());
        this.overrideSpeed.set(controller.getOverrideSpeed());
        this.automaticMode.set(controller.getAutomaticMode());
        this.Ki.set(controller.getKi());
        this.Kp.set(controller.getKp());
        this.power.set(controller.getPower());
        this.serviceBrake.set(controller.getServiceBrake());
        this.emergencyBrake.set(controller.getEmergencyBrake());
        this.authority.set(controller.getAuthority());
        this.maxSpeed.set(controller.getMaxSpeed());
        this.intLights.set(controller.getIntLights());
        this.extLights.set(controller.getExtLights());
        this.leftDoors.set(controller.getLeftDoors());
        this.rightDoors.set(controller.getRightDoors());
        this.temperature.set(controller.getTemperature());
        this.blocksToNextStation.set(controller.getBlocksToNextStation());
        this.announcements.set(controller.getAnnouncements());
        this.signalFailure.set(controller.getSignalFailure());
        this.brakeFailure.set(controller.getBrakeFailure());
        this.powerFailure.set(controller.getPowerFailure());
    }

}
