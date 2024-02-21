package trainController;

import Common.TrainController;
import Framework.Support.AbstractSubject;
import javafx.beans.property.*;
import javafx.application.Platform;

public class trainControllerSubject implements AbstractSubject {
    private IntegerProperty authority, trainID;
    private DoubleProperty commandSpeed, currentSpeed, overrideSpeed, maxSpeed, Ki, Kp, power, temperature;
    private BooleanProperty serviceBrake, emergencyBrake, automaticMode, intLights, extLights, leftDoors, rightDoors,inTunnel,leftPlatform,rightPlatform;
    private BooleanProperty announcements, signalFailure, brakeFailure, powerFailure;

    private TrainController controller;
    private boolean isGuiUpdate = false;



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

    public void setProperty(String propertyName, Object newValue) {
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
            //case "blocksToNextStation" -> updateProperty(blocksToNextStation, newValue);
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
            case "inTunnel" -> updateProperty(inTunnel,newValue);
            case "leftPlatform" -> updateProperty(leftPlatform,newValue);
            case "rightPlatform" -> updateProperty(rightPlatform,newValue);
            default -> System.err.println("Unknown property " + propertyName);
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
            case "inTunnel" -> inTunnel;
            case "leftPlatform" -> leftPlatform;
            case "rightPlatform" -> rightPlatform;
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
            //case "blocksToNextStation" -> blocksToNextStation;
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

    private trainControllerSubject() {
        this.authority = new SimpleIntegerProperty(this, "authority",0);
        this.trainID = new SimpleIntegerProperty(this, "trainID",0);
        //this.blocksToNextStation = new SimpleIntegerProperty(this, "blocksToNextStation",0);
        this.commandSpeed = new SimpleDoubleProperty(this, "commandSpeed",0);
        this.currentSpeed = new SimpleDoubleProperty(this, "currentSpeed",0);
        this.overrideSpeed = new SimpleDoubleProperty(this, "overrideSpeed",0);
        this.maxSpeed = new SimpleDoubleProperty(this, "maxSpeed",0);
        this.Ki = new SimpleDoubleProperty(this, "Ki",0);
        this.Kp = new SimpleDoubleProperty(this, "Kp",0);
        this.power = new SimpleDoubleProperty(this, "power",0);
        this.serviceBrake = new SimpleBooleanProperty(this, "serviceBrake",false);
        this.emergencyBrake = new SimpleBooleanProperty(this, "emergencyBrake",false);
        this.automaticMode = new SimpleBooleanProperty(this, "automaticMode",false);
        this.intLights = new SimpleBooleanProperty(this, "intLights",false);
        this.extLights = new SimpleBooleanProperty(this, "extLights",false);
        this.leftDoors = new SimpleBooleanProperty(this, "leftDoors",false);
        this.rightDoors = new SimpleBooleanProperty(this, "rightDoors",false);
        this.announcements = new SimpleBooleanProperty(this, "announcements",false);
        this.signalFailure = new SimpleBooleanProperty(this, "signalFailure",false);
        this.brakeFailure = new SimpleBooleanProperty(this, "brakeFailure",false);
        this.powerFailure = new SimpleBooleanProperty(this, "powerFailure",false);
        this.temperature = new SimpleDoubleProperty(this, "temperature",0);
        this.inTunnel = new SimpleBooleanProperty(this,"inTunnel",false);
        this.leftPlatform = new SimpleBooleanProperty(this,"leftPlatform",false);
        this.rightPlatform = new SimpleBooleanProperty(this,"rightPlatform",false);
        trainControllerSubjectFactory.getInstance().registerSubject(0, this);
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
        //this.blocksToNextStation.set(controller.getBlocksToNextStation());
        this.announcements.set(controller.getAnnouncements());
        this.signalFailure.set(controller.getSignalFailure());
        this.brakeFailure.set(controller.getBrakeFailure());
        this.powerFailure.set(controller.getPowerFailure());
        this.rightPlatform.set(controller.getRightPlatform());
        this.leftPlatform.set(controller.getLeftPlatform());
        this.inTunnel.set(controller.getInTunnel());
    }

}
