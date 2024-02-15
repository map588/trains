package trainController;

import Common.trainController;
import Common.trainModel;


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

    private trainControllerSubject subject;


    public void assignTrainModel(trainModel train) {

    }

    public void setAutomaticMode(boolean mode) {

    }

    public void setAuthority(int authority) {

    }

    public void setOverrideSpeed(double speed) {

    }

    public void setCommandSpeed(double speed) {

    }

    public void setServiceBrake(boolean brake) {

    }

    public void setEmergencyBrake(boolean brake) {

    }

    public void setKi(double Ki) {

    }

    public void setKp(double Kp) {

    }

    public int getTrainNumber() {
        return 0;
    }

    public int getTrainID() {
        return 0;
    }

    public double getSpeed() {
        return 0;
    }

    public double getAcceleration() {
        return 0;
    }

    public double getPower() {
        return 0;
    }

    public boolean getServiceBrake() {
        return false;
    }

    public boolean getEmergencyBrake() {
        return false;
    }

    public double getCommandSpeed() {
        return 0;
    }

    public int getAuthority() {
        return 0;
    }

    public double getKi() {
        return 0;
    }

    public double getKp() {
        return 0;
    }

    public double getOverrideSpeed() {
        return 0;
    }

    public boolean getAutomaticMode() {
        return false;
    }
}