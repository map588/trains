package stubs;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Records.Beacon;
import trainModel.Records.UpdatedTrainValues;
import trainController.TrainControllerSubject;

public class trainControllerStub implements TrainController {

    private final TrainModel train;
    private int trainID;

    private double speed;
    private double targetSpeed;


 public trainControllerStub(TrainModel train, int trainID) {
            this.train = train;
    }

//    @Override
    public void setAutomaticMode(boolean mode) {

    }

    @Override
    public void setAuthority(int authority) {

    }

//    @Override
    public void setOverrideSpeed(double speed) {

    }

    @Override
    public void setCommandSpeed(double speed) {

    }

//    @Override
    public void setServiceBrake(boolean brake) {

    }

    @Override
    public void setEmergencyBrake(boolean brake) {

    }

    @Override
    public void setPassengerEBrake() {

    }

    //    @Override
    public void setKi(double Ki) {

    }

//    @Override
    public void setKp(double Kp) {

    }

//    @Override
    public void setIntLights(boolean intLights) {

    }

//    @Override
    public void setExtLights(boolean extLights) {

    }

//    @Override
    public void setLeftDoors(boolean leftDoors) {

    }

//    @Override
    public void setRightDoors(boolean rightDoors) {

    }

    @Override
    public int getID() {
        return 0;
    }

//    @Override
    public double getSpeed() {
        return 0;
    }

//    @Override
    public double getAcceleration() {
        return 0;
    }

    @Override
    public double getPower() {
        return 0;
    }

    @Override
    public double getKi() {
        return 0;
    }

    @Override
    public double getKp() {
        return 0;
    }

    @Override
    public double getOverrideSpeed() {
        return 0;
    }

    @Override
    public double getSpeedLimit() {
        return 0;
    }

    @Override
    public boolean getServiceBrake() {
        return false;
    }

    @Override
    public boolean getEmergencyBrake() {
        return false;
    }

    @Override
    public boolean getAutomaticMode() {
        return false;
    }

    @Override
    public boolean getExtLights() {
        return false;
    }

    @Override
    public boolean getIntLights() {
        return false;
    }

    @Override
    public boolean getLeftDoors() {
        return false;
    }

    @Override
    public boolean getRightDoors() {
        return false;
    }

//    @Override
    public int getSamplingPeriod() {
        return 0;
    }

    @Override
    public double getSetTemperature() {
        return 0;
    }
    @Override
    public double getCurrentTemperature(){return 0;}

    @Override
    public double getCommandSpeed() {
        return 0;
    }

    @Override
    public int getAuthority() {
        return 0;
    }

    @Override
    public void setSetTemperature(double newTemperature) {

    }

    @Override
    public void setCurrentTemperature(double newTemperature){}

    @Override
    public TrainControllerSubject getSubject() {
        return null;
    }

    @Override
    public boolean getAnnouncements() {
        return false;
    }

    @Override
    public boolean getSignalFailure() {
        return false;
    }

    @Override
    public boolean getBrakeFailure() {
        return false;
    }

    @Override
    public boolean getPowerFailure() {
        return false;
    }

    @Override
    public void delete() {

    }

    @Override
    public boolean isHW() {
        return false;
    }

    //    @Override
    public boolean getLeftPlatform() {
        return false;
    }

//    @Override
    public boolean getRightPlatform() {
        return false;
    }

//    @Override
    public String getNextStationName() {
        return null;
    }

//    @Override
    public void setLeftPlatform(boolean leftPlatform) {

    }

//    @Override
    public void setRightPlatform(boolean rightPlatform) {

    }

//    @Override
    public boolean getInTunnel() {
        return false;
    }

    @Override
    public Beacon getBeacon() {
        return null;
    }

    @Override
    public void onBlock() {

    }

    //    @Override
    public void setInTunnel(boolean inTunnel) {

    }

    @Override
    public double calculatePower(double currentVelocity) {
        return 0;
    }

    @Override
    public double getGrade() {
        return 0;
    }

    @Override
    public void updateBeacon(Beacon beacon) {

    }

    @Override
    public UpdatedTrainValues sendUpdatedTrainValues() {
        return null;
    }

    @Override
    public TrainModel getTrain() {
        return null;
    }

    @Override
    public void checkFailures(double power) {

    }

    @Override
    public void setValue(Enum<?> propertyName, Object newValue) {

    }
}
