package trainController;

import Common.trainController;
import javafx.beans.property.*;


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
    private IntegerProperty trainNumber;
    private trainController controller;

    public trainControllerSubject(trainController controller) {
        this.trainNumber = new SimpleIntegerProperty(controller.getTrainNumber());
        this.currentSpeed = new SimpleDoubleProperty(controller.getSpeed());
        this.commandSpeed = new SimpleDoubleProperty(controller.getCommandSpeed());
        this.overrideSpeed = new SimpleDoubleProperty(controller.getOverrideSpeed());
        this.automaticMode = new SimpleBooleanProperty(controller.getAutomaticMode());
        this.trainNumber = new SimpleIntegerProperty(controller.getTrainNumber());
        this.Ki = new SimpleDoubleProperty(1.0);
        this.Kp = new SimpleDoubleProperty(22.0);
        this.power = new SimpleDoubleProperty(0.0);
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);
        this.authority = new SimpleIntegerProperty(2000);
        this.maxSpeed = new SimpleDoubleProperty(50.0);
    }

    public void setCommandSpeed(double speed) {
        if(speed == controller.getCommandSpeed())
            return;
        System.out.println("Setting command speed to " + speed);
        this.controller.setCommandSpeed(speed);
    }

    public void setAutomaticMode() {
        System.out.println("Setting automatic mode");
        setAutomaticMode(true);
    }

    public void setAutomaticMode(boolean mode) {
        if(mode == controller.getAutomaticMode())
            return;
        System.out.println("Setting automatic mode to " + mode);
        this.automaticMode.set(mode);
        this.overrideSpeed.set(0.0);
    }

    public void setManualSpeed(double speed) {
        if(speed == controller.getOverrideSpeed())
            return;
        System.out.println("Setting override speed to " + speed);
        this.overrideSpeed.set(speed);
    }

    public void setAuthority(int authority) {
        if(authority == controller.getAuthority())
            return;
        System.out.println("Setting authority to " + authority);
        this.authority.set(authority);
    }

    public void setKi(double Ki) {
        System.out.println("Setting Ki to " + Ki);
        this.Ki.set(Ki);
    }

    public void setKp(double Kp) {
        System.out.println("Setting Kp to " + Kp);
        this.Kp.set(Kp);
    }

    public void setServiceBrake(boolean brake) {
        if(brake == controller.getServiceBrake())
            return;
        System.out.println("Setting service brake to " + brake);
        this.serviceBrake.set(brake);
    }

    public void setEmergencyBrake(boolean brake) {
        this.setAutomaticMode(false);
        if(brake == controller.getEmergencyBrake())
            return;
        System.out.println("Setting emergency brake to " + brake);
        this.emergencyBrake.set(brake);
    }


    public void setPower(double power) {
        System.out.println("Setting power to " + power);
        this.power.set(power);
    }


    public int getTrainNumber() {
        System.out.println("Getting train ID");
        return this.trainNumber.get();
    }

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

    public IntegerProperty trainNumberProperty() {
        return this.trainNumber;
    }
}









