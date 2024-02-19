package trainController;

import Common.TrainController;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import javax.swing.*;
import java.util.List;

public class trainControllerSubject {
    private IntegerProperty authority, trainID, blocksToNextStation;
    private DoubleProperty commandSpeed, currentSpeed, overrideSpeed, maxSpeed, Ki, Kp, power, temperature;
    private BooleanProperty serviceBrake, emergencyBrake, automaticMode, intLights, extLights, leftDoors, rightDoors;
    private BooleanProperty announcements, signalFailure, brakeFailure, powerFailure;
    private TrainController controller;

    public trainControllerSubject() {
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
        this.intLights = new SimpleBooleanProperty(false);
        this.extLights = new SimpleBooleanProperty(false);
        this.leftDoors = new SimpleBooleanProperty(false);
        this.rightDoors = new SimpleBooleanProperty(false);
        this.temperature = new SimpleDoubleProperty(0);
        this.blocksToNextStation = new SimpleIntegerProperty(0);
        this.announcements = new SimpleBooleanProperty(false);
        this.signalFailure = new SimpleBooleanProperty(false);
        this.brakeFailure = new SimpleBooleanProperty(false);
        this.powerFailure = new SimpleBooleanProperty(false);
    }

    //Botched way to avoid null pointer exception/ stack overflow
    //when trying to construct a trainControllerSubject without an existing controller
    public trainControllerSubject(int injection) {
        this();
        if (injection == -1) {
            controller = new trainControllerImpl(this);
        }
    }

    public trainControllerSubject(TrainController controller) {
        this();
        this.controller = controller;
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
        trainControllerSubjectFactory.subjectMap.put(controller.getID(), this);

        controller.addChangeListener(((propertyName, newValue) -> {
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
                case "IntLights":
                    intLights.set((Boolean) newValue);
                    break;
                case "ExtLights":
                    extLights.set((Boolean) newValue);
                    break;
                case "LeftDoors":
                    leftDoors.set((Boolean) newValue);
                    break;
                case "RightDoors":
                    rightDoors.set((Boolean) newValue);
                    break;
                case "Temperature":
                    temperature.set((Double) newValue);
                    break;
                case "BlocksToNextStation":
                    blocksToNextStation.set((Integer) newValue);
                    break;
                case "Announcements":
                    announcements.set((Boolean) newValue);
                    break;
                case "SignalFailure":
                    signalFailure.set((Boolean) newValue);
                    break;
                case "BrakeFailure":
                    brakeFailure.set((Boolean) newValue);
                    break;
                case "PowerFailure":
                    powerFailure.set((Boolean) newValue);
                    break;
            }
        }));

    }

    public void setTrainController(TrainController controller) {
        this.controller = controller;
    }

    public void updateProperty(BooleanProperty property, boolean newValue) {
        if (property.get() != newValue) {
            property.set(newValue);
        }
    }

    public void updateProperty(DoubleProperty property, double newValue) {
        if (property.get() != newValue) {
            property.set(newValue);
        }
    }

    public void updateProperty(IntegerProperty property, int newValue) {
        if (property.get() != newValue) {
            property.set(newValue);
        }
    }

    public void updateSetSpeed(double newSetSpeed) {
        updateProperty(overrideSpeed, newSetSpeed);
    }

    public void updateAuthority(int newAuthority) {
        updateProperty(authority, newAuthority);
    }

    public void updateAutomaticMode(boolean newMode) {
        updateProperty(automaticMode, newMode);
    }

    public void updateServiceBrake(boolean newServiceBrake) {
        updateProperty(serviceBrake, newServiceBrake);
    }

    public void updateEmergencyBrake(boolean newEmergencyBrake) {
        updateProperty(emergencyBrake, newEmergencyBrake);
    }

    public void updateKi(double newKi) {
        updateProperty(Ki, newKi);
    }

    public void updateKp(double newKp) {
        updateProperty(Kp, newKp);
    }

    public void updateTemperature(double newTemperature) {
        updateProperty(temperature, newTemperature);
    }

    public void updateExtLights(boolean newExtLights) {
        updateProperty(extLights, newExtLights);
    }

    public void updateIntLights(boolean newIntLights) {
        updateProperty(intLights, newIntLights);
    }

    public void updateLeftDoors(boolean newLeftDoors) {
        updateProperty(leftDoors, newLeftDoors);
    }

    public void updateRightDoors(boolean newRightDoors) {
        updateProperty(rightDoors, newRightDoors);
    }

    public void updateAnnouncements(boolean enabled) {
        updateProperty(announcements, enabled);
    }

    public void updateSignalFailure(boolean failure) {
        updateProperty(signalFailure, failure);
    }

    public void updateBrakeFailure(boolean failure) {
        updateProperty(brakeFailure, failure);
    }

    public void updatePowerFailure(boolean failure) {
        updateProperty(powerFailure, failure);
    }

    public TrainController getTrainController() {
        return controller;
    }

    public IntegerProperty authorityProperty() {
        return authority;
    }

    public IntegerProperty trainIDProperty() {
        return trainID;
    }

    public DoubleProperty commandSpeedProperty() {
        return commandSpeed;
    }

    public DoubleProperty currentSpeedProperty() {
        return currentSpeed;
    }

    public DoubleProperty overrideSpeedProperty() {
        return overrideSpeed;
    }

    public DoubleProperty maxSpeedProperty() {
        return maxSpeed;
    }

    public DoubleProperty KiProperty() {
        return Ki;
    }

    public DoubleProperty KpProperty() {
        return Kp;
    }

    public DoubleProperty powerProperty() {
        return power;
    }

    public BooleanProperty serviceBrakeProperty() {
        return serviceBrake;
    }

    public BooleanProperty emergencyBrakeProperty() {
        return emergencyBrake;
    }

    public BooleanProperty automaticModeProperty() {
        return automaticMode;
    }

    public BooleanProperty intLightsProperty() {
        return intLights;
    }

    public BooleanProperty extLightsProperty() {
        return extLights;
    }

    public BooleanProperty leftDoorsProperty() {
        return leftDoors;
    }

    public BooleanProperty rightDoorsProperty() {
        return rightDoors;
    }

    public DoubleProperty temperatureProperty() {
        return temperature;
    }

    public IntegerProperty blocksToNextStationProperty() {
        return blocksToNextStation;
    }

    public BooleanProperty makeAnnouncementsProperty() {
        return announcements;
    }

    public BooleanProperty signalFailureProperty() {
        return signalFailure;
    }

    public BooleanProperty brakeFailureProperty() {
        return brakeFailure;
    }

    public BooleanProperty powerFailureProperty() {
        return powerFailure;
    }

}
