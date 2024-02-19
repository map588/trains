package trainController;

import Common.TrainController;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.HashMap;
import java.util.Map;

public class trainControllerSubject {
    private IntegerProperty authority;
    private DoubleProperty commandSpeed;
    private DoubleProperty currentSpeed;
    private DoubleProperty overrideSpeed;
    private DoubleProperty maxSpeed;
    private DoubleProperty Ki;
    private DoubleProperty Kp;
    private DoubleProperty power;
    private BooleanProperty serviceBrake;
    private BooleanProperty emergencyBrake;
    private BooleanProperty automaticMode;
    private IntegerProperty trainID;

    private BooleanProperty intLights;
    private BooleanProperty extLights;
    private BooleanProperty leftDoors;
    private BooleanProperty rightDoors;
    private DoubleProperty temperature;

    private BooleanProperty brakeFailure;
    private BooleanProperty powerFailure;
    private BooleanProperty signalFailure;

    private TrainController controller;

    private boolean isGuiUpdate = false;


    //Null Constructor
    public trainControllerSubject(){
        this.authority = new SimpleIntegerProperty(0);
        this.commandSpeed = new SimpleDoubleProperty(0);
        this.currentSpeed = new SimpleDoubleProperty(0);
        this.overrideSpeed = new SimpleDoubleProperty(0);
        this.maxSpeed = new SimpleDoubleProperty(0);
        this.Ki = new SimpleDoubleProperty(0);
        this.Kp = new SimpleDoubleProperty(0);
        this.power = new SimpleDoubleProperty(0);
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);
        this.automaticMode = new SimpleBooleanProperty(false);
        this.trainID = new SimpleIntegerProperty(0);
    }

    public trainControllerSubject(TrainController controller) {
        this.controller = controller;
        this.trainID = new SimpleIntegerProperty(controller.getID());
        this.currentSpeed = new SimpleDoubleProperty(controller.getSpeed());
        this.commandSpeed = new SimpleDoubleProperty(controller.getCommandSpeed());
        this.overrideSpeed = new SimpleDoubleProperty(controller.getOverrideSpeed());
        this.automaticMode = new SimpleBooleanProperty(controller.getAutomaticMode());
        this.trainID = new SimpleIntegerProperty(controller.getID());
        this.Ki = new SimpleDoubleProperty(1.0);
        this.Kp = new SimpleDoubleProperty(22.0);
        this.power = new SimpleDoubleProperty(0.0);
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);
        this.authority = new SimpleIntegerProperty(2000);
        this.maxSpeed = new SimpleDoubleProperty(50.0);
        controllerSubjectFactory.subjectMap.put(controller.getID(), this);

        controller.addChangeListener(((propertyName, newValue) -> {
            Platform.runLater(() -> {

            switch (propertyName) {
                case "Speed":
                    currentSpeed.set((Double) newValue);
                    break;
                case "CommandSpeed":
                    commandSpeed.set((Double) newValue);
                    break;
                case "OverrideSpeed":
                    overrideSpeed.set((Double) newValue);
                    break;
                case "AutomaticMode":
                    automaticMode.set((Boolean) newValue);
                    break;
                case "Ki":
                    Ki.set((Double) newValue);
                    break;
                case "Kp":
                    Kp.set((Double) newValue);
                    break;
                case "Power":
                    power.set((Double) newValue);
                    break;
                case "ServiceBrake":
                    serviceBrake.set((Boolean) newValue);
                    break;
                case "EmergencyBrake":
                    emergencyBrake.set((Boolean) newValue);
                    break;
                case "Authority":
                    authority.set((Integer) newValue);
                    break;
                case "MaxSpeed":
                    maxSpeed.set((Double) newValue);
                    break;
            }
        });
        }));
    }

    //Update methods are modified by the GUI from Manager listeners

    public void updateSetSpeed(double newSetSpeed) {
        if (controller.getOverrideSpeed() != newSetSpeed) {
            controller.setOverrideSpeed(newSetSpeed); // Update the model only if there's an actual change
        }
    }
    public void updateAuthority(int newAuthority) {
        if (controller.getAuthority() != newAuthority) {
            controller.setAuthority(newAuthority); // Update the model only if there's an actual change
        }
    }

    public void updateAutomaticMode(boolean newMode) {
        if (controller.getAutomaticMode() != newMode) {
            controller.setAutomaticMode(newMode); // Update the model only if there's an actual change
        }
    }

    public void updateServiceBrake(boolean newServiceBrake) {
        if (controller.getServiceBrake() != newServiceBrake) {
            controller.setServiceBrake(newServiceBrake); // Update the model only if there's an actual change
        }
    }

    public void updateEmergencyBrake(boolean newEmergencyBrake) {
        if (controller.getEmergencyBrake() != newEmergencyBrake) {
            controller.setEmergencyBrake(newEmergencyBrake); // Update the model only if there's an actual change
        }
    }

    public void updateKi(double newKi) {
        if (controller.getKi() != newKi) {
            controller.setKi(newKi); // Update the model only if there's an actual change
        }
    }

    public void updateKp(double newKp) {
        if (controller.getKp() != newKp) {
            controller.setKp(newKp); // Update the model only if there's an actual change
        }
    }

    public void updateTemperature(double newTemperature) {
        if (controller.getTemperature() != newTemperature) {
            controller.setTemperature(newTemperature); // Update the model only if there's an actual change
        }
    }

    public void updateExtLights(boolean newExtLights) {
        if (controller.getExtLights() != newExtLights) {
            controller.setExtLights(newExtLights); // Update the model only if there's an actual change
        }
    }

    public void updateIntLights(boolean newIntLights) {
        if (controller.getIntLights() != newIntLights) {
            controller.setIntLights(newIntLights); // Update the model only if there's an actual change
        }
    }

    public void updateLeftDoors(boolean newLeftDoors) {
        if (controller.getLeftDoors() != newLeftDoors) {
            controller.setLeftDoors(newLeftDoors); // Update the model only if there's an actual change
        }
    }

    public void updateRightDoors(boolean newRightDoors) {
        if (controller.getRightDoors() != newRightDoors) {
            controller.setRightDoors(newRightDoors); // Update the model only if there's an actual change
        }
    }


//    public void updateBrakeFailure(boolean newBrakeFailure) {
//        if (controller.getBrakeFailure() != newBrakeFailure) {
//            controller.setBrakeFailure(newBrakeFailure); // Update the model only if there's an actual change
//        }
//    }
//
//    public void updatePowerFailure(boolean newPowerFailure) {
//        if (controller.getPowerFailure() != newPowerFailure) {
//            controller.setPowerFailure(newPowerFailure); // Update the model only if there's an actual change
//        }
//    }
//
//    public void updateSignalFailure(boolean newSignalFailure) {
//        if (controller.getSignalFailure() != newSignalFailure) {
//            controller.setSignalFailure(newSignalFailure); // Update the model only if there's an actual change
//        }
//    }
//
//    public void updateNumPassengers(int newNumPassengers) {
//        if (controller.getNumPassengers() != newNumPassengers) {
//            controller.setNumPassengers(newNumPassengers); // Update the model only if there's an actual change
//        }
//    }
//
//    public void updateTrainNumber(int newTrainNumber) {
//        if (controller.getTrainNumber() != newTrainNumber) {
//            controller.setTrainNumber(newTrainNumber); // Update the model only if there's an actual change
//        }
//    }
//
//    public void updateSpeed(double newSpeed) {
//        if (controller.getSpeed() != newSpeed) {
//            controller.setSpeed(newSpeed); // Update the model only if there's an actual change
//        }
//    }
//
//    public void updateAcceleration(double newAcceleration) {
//        if (controller.getAcceleration() != newAcceleration) {
//            controller.setAcceleration(newAcceleration); // Update the model only if there's an actual change
//        }
//    }




    public DoubleProperty overrideSpeedProperty() {
        return this.overrideSpeed;
    }

    public DoubleProperty currentSpeedProperty() {
        return this.currentSpeed;
    }

    public DoubleProperty powerProperty() {
        return this.power;
    }

    public DoubleProperty maxSpeedProperty() {
        return this.maxSpeed;
    }

    public DoubleProperty commandSpeedProperty() {
        return this.commandSpeed;
    }

    public DoubleProperty KiProperty() {
        return this.Ki;
    }

    public DoubleProperty KpProperty() {
        return this.Kp;
    }

    public BooleanProperty serviceBrakeProperty() {
        return this.serviceBrake;
    }

    public BooleanProperty emergencyBrakeProperty() {
        return this.emergencyBrake;
    }

    public BooleanProperty automaticModeProperty() {
        return this.automaticMode;
    }

    public IntegerProperty authorityProperty() {
        return this.authority;
    }

    public IntegerProperty trainIDProperty() {
        return this.trainID;
    }

    public BooleanProperty intLightsProperty() {
        return this.intLights;
    }

    public BooleanProperty extLightsProperty() {
        return this.extLights;
    }

    public BooleanProperty leftDoorsProperty() {
        return this.leftDoors;
    }

    public BooleanProperty rightDoorsProperty() {
        return this.rightDoors;
    }

    public DoubleProperty temperatureProperty() {
        return this.temperature;
    }

    public BooleanProperty brakeFailureProperty() {
        return this.brakeFailure;
    }

    public BooleanProperty powerFailureProperty() {
        return this.powerFailure;
    }

    public BooleanProperty signalFailureProperty() {
        return this.signalFailure;
    }


    public void updateFromGui(Runnable updateLogic) {
        isGuiUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isGuiUpdate = false;
        }
    }

    public void updateIntLights(Boolean newSelection) {
    }
}









