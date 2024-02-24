package stubObjects;

import Common.TrainController;
import Common.TrainModel;
import trainController.trainControllerSubject;

public class stubTrainController implements TrainController{

    int trainID;
    TrainModel train;

    public stubTrainController(int trainID) {
        this.trainID = trainID;
    }

    public void assignTrainModel(TrainModel train) {
        this.train = train;
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

    public void setIntLights(boolean intLights) {

    }

    public void setExtLights(boolean extLights) {

    }

    public void setLeftDoors(boolean leftDoors) {

    }

    public void setRightDoors(boolean rightDoors) {

    }

    public void setLeftPlatform(boolean leftPlatform){

    }
    public void setRightPlatform(boolean rightPlatform){

    }
    public void setInTunnel(boolean inTunnel){

    }


    public void setValue(String propertyName, Object newValue) {

    }

    public double getGrade() {
        return 0;
    }

    public int getID() {
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

    public double getKi() {
        return 0;
    }

    public double getKp() {
        return 0;
    }

    public double getOverrideSpeed() {
        return 0;
    }

    public double getSpeedLimit() {
        return 0;
    }

    public double getMaxSpeed() {
        return 0;
    }

    public boolean getServiceBrake() {
        return false;
    }

    public boolean getEmergencyBrake() {
        return false;
    }

    public boolean getAutomaticMode() {
        return false;
    }

    public boolean getExtLights() {
        return false;
    }

    public boolean getIntLights() {
        return false;
    }

    public boolean getLeftDoors() {
        return false;
    }

    public boolean getRightDoors() {
        return false;
    }

    public int getSamplingPeriod() {
        return 0;
    }

    public double getTemperature() {
        return 0;
    }

    public double getCommandSpeed() {
        return 0;
    }

    public int getAuthority() {
        return 0;
    }

    public void setTemperature(double newTemperature) {

    }

    public trainControllerSubject getSubject() {
        return null;
    }

    public int getBlocksToNextStation() {
        return 0;
    }

    public String getStationName(){return "Yard";}

    public boolean getAnnouncements() {
        return false;
    }

    public boolean getSignalFailure() {
        return false;
    }

    public boolean getBrakeFailure() {
        return false;
    }

    public boolean getPowerFailure() {
        return false;
    }

    public boolean getLeftPlatform(){return false;}
    public boolean getRightPlatform(){return  false;}
    public boolean getInTunnel(){return false;}

    public void calculatePower(){}
    public void notifyChange(String property, Object newValue) {

    }
}
