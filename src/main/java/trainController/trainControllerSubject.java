package trainController;

import Common.TrainController;
import Framework.AbstractSubject;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;

import javax.swing.*;
import java.util.List;

import static org.controlsfx.tools.Platform.*;

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
                });
        }));

    }

    public void updateProperty(BooleanProperty property, boolean newValue) {
        if (property.get() != newValue) {
            property.set(newValue);
            System.out.println("Property " + property.getName() + " updated to " + newValue + " in trainControllerSubject");
        }
    }

    public void updateProperty(DoubleProperty property, double newValue) {
        if (property.get() != newValue) {
            property.set(newValue);
            System.out.println("Property " + property.getName() + " updated to " + newValue + " in trainControllerSubject");
        }
    }

    public void updateProperty(IntegerProperty property, int newValue) {
        if (property.get() != newValue) {
            property.set(newValue);
            System.out.println("Property " + property.getName() + " updated to " + newValue + " in trainControllerSubject");
        }
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

}
