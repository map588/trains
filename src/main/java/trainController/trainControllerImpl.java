package trainController;

import Common.trainController;
import Common.trainModel;
import Framework.GUI.Managers.trainControllerManager;


public class trainControllerImpl implements trainController{
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
        this.subject.setCommandSpeed(train.getSpeed());
        this.subject.setCurrentSpeed(train.getSpeed());
        this.subject.setAutomaticMode(true);
        this.subject.setTrainNumber(trainNumber);
    }

    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        this.subject.setAutomaticMode(mode);
    }

    public void setAuthority(int authority) {
        this.authority = authority;
        this.subject.setAuthority(authority);
    }

    public void setOverrideSpeed(double speed) {
        this.overrideSpeed = speed;
        this.subject.setOverrideSpeed(speed);
    }

    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        this.subject.setCommandSpeed(speed);
    }

    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        this.subject.setServiceBrake(brake);
    }

    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        this.subject.setEmergencyBrake(brake);
    }

    public void setKi(double Ki) {
        this.Ki = Ki;
        this.subject.setKi(Ki);
    }

    public void setKp(double Kp) {
        this.Kp = Kp;
        this.subject.setKp(Kp);
    }

    public void setPower(double power) {
        this.power = power;
        this.subject.setPower(power);
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

    public double getMaxSpeed() {
        return 0;
    }
}