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
        if (property.get() != newValue)
            property.set(newValue);
    }

    public void updateProperty(DoubleProperty property, double newValue) {
        if (property.get() != newValue)
            property.set(newValue);
    }

    public void updateProperty(IntegerProperty property, int newValue) {
        if (property.get() != newValue)
            property.set(newValue);
    }

    public BooleanProperty getBooleanProperty (String propertyName) {
        switch (propertyName) {
            case "serviceBrake":
                return serviceBrake;
            case "emergencyBrake":
                return emergencyBrake;
            case "automaticMode":
                return automaticMode;
            case "intLights":
                return intLights;
            case "extLights":
                return extLights;
            case "leftDoors":
                return leftDoors;
            case "rightDoors":
                return rightDoors;
            case "announcements":
                return announcements;
            case "signalFailure":
                return signalFailure;
            case "brakeFailure":
                return brakeFailure;
            case "powerFailure":
                return powerFailure;
            default:
                return null;
        }
    }

    public DoubleProperty getDoubleProperty(String propertyName) {
        switch (propertyName) {
            case "commandSpeed":
                return commandSpeed;
            case "currentSpeed":
                return currentSpeed;
            case "overrideSpeed":
                return overrideSpeed;
            case "maxSpeed":
                return maxSpeed;
            case "Ki":
                return Ki;
            case "Kp":
                return Kp;
            case "power":
                return power;
            case "temperature":
                return temperature;
            default:
                return null;
        }
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        switch (propertyName) {
            case "authority":
                return authority;
            case "trainID":
                return trainID;
            case "blocksToNextStation":
                return blocksToNextStation;
            default:
                return null;
        }
    }

}
