package trainModel;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Beacon;
import Utilities.Constants;
import Utilities.Enums.Direction;

public class stubTrainModel implements TrainModel {
    private TrainController controller;

    private stubTrainModel() {}

    public static stubTrainModel createstubTrainModel() {
        return new stubTrainModel();
    }

    public void setTrainController(TrainController controller) {

    }

    public void setEmergencyBrake(boolean brake) {
    }

    public void setServiceBrake(boolean brake) {
    }

    public void setPower(double power) {
    }

    @Override
    public void setCommandSpeed(double speed) {

    }

    @Override
    public void setAuthority(int authority) {

    }

    public void setNumCars(int numCars) {
    }

    public void setNumPassengers(int numPassengers) {
    }

    public void setCrewCount(int crewCount) {

    }

    @Override
    public void setGrade(double grade) {

    }

    @Override
    public void setDirection(Direction direction) {

    }

    @Override
    public void changeDirection() {

    }

    @Override
    public void passBeacon(Beacon beacon) {

    }

    public void setBrakeFailure(boolean failure) {
    }

    public void setPowerFailure(boolean failure) {
    }

    public void setSignalFailure(boolean failure) {
    }

    public void setTrainNumber(int number) {
    }

    public void setLeftDoors(boolean doors) {
    }

    public void setRightDoors(boolean doors) {
    }

    public void setExtLights(boolean lights) {
    }

    public void setIntLights(boolean lights) {
    }

    public void setSetTemperature(double temp) {

    }

    @Override
    public void setAnnouncement(String announcement) {

    }

    public int getAuthority() {
        return 0;
    }

    public int getTrainNumber() {
        return controller.getID();
    }

    public double getCommandSpeed() {
        return 1.0;
    }

    public double getSpeed() {
        return 1.0;
    }

    public double getAcceleration() {
        return 1.0;
    }

    public double getPower() {
        return 1.0;
    }

    public boolean getServiceBrake() {
        return true;
    }

    public boolean getEmergencyBrake() {
        return true;
    }

    public double getWeightKG() {
        return Constants.LOADED_TRAIN_MASS;
    }

    @Override
    public Direction getDirection() {
        return null;
    }

    public boolean getBrakeFailure() {
        return true;
    }

    public boolean getPowerFailure() {
        return true;
    }

    public boolean getSignalFailure() {
        return true;
    }

    public double getRealTemperature() {
        return 1.0;
    }

    public boolean getExtLights() {
        return true;
    }

    public boolean getIntLights() {
        return true;
    }

    public boolean getLeftDoors() {
        return true;
    }

    public boolean getRightDoors() {
        return true;
    }

    @Override
    public double getlength() {
        return 0;
    }

    public void trainModelPhysics() {
    }

    @Override
    public void setValue(String propertyName, Object newValue) {

    }

    @Override
    public void setTimeDelta(int v) {

    }

    @Override
    public int getTimeDelta() {
        return 0;
    }

    @Override
    public double getGrade() {
        return 0;
    }

    @Override
    public int getCrewCount() {
        return 0;
    }



    @Override
    public int getNumPassengers() {
        return 0;
    }

    @Override
    public int getNumCars() {
        return 0;
    }

    @Override
    public double getDistanceTraveled() {
        return 0;
    }

    @Override
    public double getMass() {
        return 0;
    }

    @Override
    public String getBeacon() {
        return null;
    }

    @Override
    public String getTrackLine() {
        return null;
    }

    @Override
    public double getRelativePosition() {
        return 0;
    }


}