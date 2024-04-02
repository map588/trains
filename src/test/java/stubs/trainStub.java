package stubs;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Enums.Direction;
import Utilities.Records.Beacon;
import trackModel.TrackBlock;
import trackModel.TrackLine;

import java.util.concurrent.Future;

public class trainStub implements TrainModel {

    int trainID;
    TrainController controller;
    int authority;
    double commandedSpeed;
    double speed;
    double acceleration;
    double power;
    boolean serviceBrake;
    boolean emergencyBrake;
    double weightKG;
    Direction direction;
    boolean brakeFailure;
    boolean powerFailure;
    boolean signalFailure;
    double realTemperature;
    boolean extLights;
    boolean intLights;
    boolean leftDoors;
    boolean rightDoors;
    double length;
    int crewCount;
    int passengerCount;
    int numCars;
    double distanceTraveled;
    double mass;
    double temperature;
    String announcement;

    TrackLine track;
    TrackBlock currentBlock;

    public trainStub(TrackLine track, int trainID) {
        this.trainID = trainID;
        this.track = track;
        this.controller = new trainControllerStub(this, trainID);
        initializeValues();
    }

    private void initializeValues(){
        this.authority = 0;
        this.commandedSpeed = 0.0;
        this.speed = 0.1;
        this.acceleration = 0.1;
        this.power = 0.0;
        this.serviceBrake = false;
        this.emergencyBrake = false;
        this.weightKG = 0.0;
        this.direction = Direction.NORTH; // Assuming NORTH as default direction
        this.brakeFailure = false;
        this.powerFailure = false;
        this.signalFailure = false;
        this.realTemperature = 0.0;
        this.extLights = false;
        this.intLights = false;
        this.leftDoors = false;
        this.rightDoors = false;
        this.length = 0.0;
        this.crewCount = 10;
        this.passengerCount = 30;
        this.numCars = 1;
        this.distanceTraveled = 0.0;
        this.mass = 1000.0;
        this.temperature = 70.0;
        this.announcement = "All Aboard!";
    }

    public void setTrackBlock(TrackBlock block){
        this.currentBlock = block;
    }

    @Override
    public void setEmergencyBrake(boolean brake) {

    }

    @Override
    public void setServiceBrake(boolean brake) {

    }

    @Override
    public void setPower(double power) {

    }

    @Override
    public void setCommandSpeed(double speed) {

    }

    @Override
    public void setAuthority(int authority) {

    }

    @Override
    public void setNumCars(int numCars) {

    }

    @Override
    public void setNumPassengers(int numPassengers) {

    }

    @Override
    public void setCrewCount(int crewCount) {

    }

    @Override
    public void setGrade(double grade) {

    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void changeDirection() {
        if(this.direction == Direction.NORTH){
            this.direction = Direction.SOUTH;
        } else {
            this.direction = Direction.NORTH;
        }
    }

    @Override
    public void passBeacon(Beacon beacon) {
        controller.updateBeacon(beacon);
    }

    @Override
    public void setBrakeFailure(boolean failure) {

    }

    @Override
    public void setPowerFailure(boolean failure) {

    }

    @Override
    public void setSignalFailure(boolean failure) {

    }

    @Override
    public void setLeftDoors(boolean doors) {

    }

    @Override
    public void setRightDoors(boolean doors) {

    }

    @Override
    public void setExtLights(boolean lights) {

    }

    @Override
    public void setIntLights(boolean lights) {

    }

    @Override
    public void setSetTemperature(double temp) {

    }

    @Override
    public void setAnnouncement(String announcement) {

    }

    @Override
    public int getAuthority() {
        return 0;
    }

    @Override
    public int getTrainNumber() {
        return 0;
    }

    @Override
    public double getCommandSpeed() {
        return 0;
    }

    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    public double getAcceleration() {
        return 0;
    }

    @Override
    public double getPower() {
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
    public double getWeightKG() {
        return 0;
    }

    @Override
    public Direction getDirection() {
        return null;
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
    public boolean getSignalFailure() {
        return false;
    }

    @Override
    public double getRealTemperature() {
        return 0;
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

    @Override
    public double getlength() {
        return 0;
    }

    @Override
    public void setValue(String propertyName, Object newValue) {

    }

    @Override
    public void changeTimeDelta(int v) {

    }

    @Override
    public int getCrewCount() {
        return 0;
    }

    @Override
    public int getPassengerCount() {
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
    public TrainController getController() {
        return this.controller;
    }

    @Override
    public void trainModelPhysics(Future<Double> power) {

    }

    @Override
    public void trainModelPhysics() {

    }
}
