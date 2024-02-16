package trainController;

import Common.trainController;
import Common.trainModel;
import Framework.GUI.Manangers.trainControllerManager;


class trainControllerImpl implements trainController{
    private int authority;
    private double commandSpeed;
    private double currentSpeed;
    private double overrideSpeed;
    private double maxSpeed;

    private double Ki;
    private double Kp;

    private double power;
    private boolean serviceBrake;
    private boolean emergencyBrake;
    private boolean automaticMode;

    private int trainNumber;
    private trainModel train;


    private trainControllerSubject subject;
    private trainControllerManager manager;


    //-----------------Setters-----------------
    public void assignTrainModel(trainModel train) {
        this.train = train;
        this.subject.commandSpeedProperty().set(train.getSpeed());
        this.subject.currentSpeedProperty().set(train.getSpeed());
        this.subject.authorityProperty().set(train.getAuthority());
        this.subject.trainNumberProperty().set(this.trainNumber);
    }

    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        this.subject.automaticModeProperty().set(mode);
    }

    public void setAuthority(int authority) {
        this.authority = authority;
        this.subject.authorityProperty().set(authority);
    }

    public void setOverrideSpeed(double speed) {
        this.overrideSpeed = speed;
        this.subject.overrideSpeedProperty().set(speed);
    }

    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        this.subject.commandSpeedProperty().set(speed);
    }

    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        this.subject.serviceBrakeProperty().set(brake);
    }

    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        this.subject.emergencyBrakeProperty().set(brake);
    }

    public void setKi(double Ki) {
        this.Ki = Ki;
        this.subject.KiProperty().set(Ki);
    }

    public void setKp(double Kp) {
        this.Kp = Kp;
        this.subject.KpProperty().set(Kp);
    }

    public void setPower(double power) {
        this.power = power;
        this.subject.powerProperty().set(power);
    }



    //-----------------Getters-----------------
    public int getTrainNumber() {
        return this.trainNumber;
    }


    public double getSpeed() {
        return this.currentSpeed;
    }

    public double getAcceleration() {
        return this.train.getAcceleration();
    }

    public double getPower() {
        return this.power;
    }

    public boolean getServiceBrake() {
        return this.serviceBrake;
    }

    public boolean getEmergencyBrake() {
        return this.emergencyBrake;
    }

    public double getCommandSpeed() {
        return this.commandSpeed;
    }

    public int getAuthority() {
        return this.authority;
    }

    public double getKi() {
        return this.Ki;
    }

    public double getKp() {
        return this.Kp;
    }

    public double getOverrideSpeed() {
        return this.overrideSpeed;
    }

    public boolean getAutomaticMode() {
        return this.automaticMode;
    }
}