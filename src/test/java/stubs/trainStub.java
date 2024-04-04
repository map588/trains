package stubs;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Enums.Direction;
import Utilities.Records.Beacon;
import Utilities.Records.UpdatedTrainValues;
import trackModel.TrackBlock;
import trackModel.TrackLine;

import java.util.concurrent.ExecutionException;
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
    double grade;
    double temperature;
    String announcement;
    double currentBlockLength;
    double relativeDistance;

    TrackLine track;
    TrackBlock currentBlock;

    public trainStub(TrackLine track, int trainID) {
        this.trainID = trainID;
        this.track = track;
        this.currentBlock = track.getTrack().get(0);
        this.controller = new trainControllerStub(this, trainID);
        initializeValues();


    }

    public TrackBlock getCurrentBlock() {
        return currentBlock;
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

    public void go_Brr(){
        for(int i = 0; i < track.getTrack().size(); i++) {
            updateLocation(currentBlock.getLength()-0.001);
        }
    }

    public void setTrackBlock(TrackBlock block){
        this.currentBlock = block;
    }

    @Override
    public void delete() {

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

    public void enteredNextBlock() {
        currentBlock = track.updateTrainLocation(this);
        //controller.onBlock();
        relativeDistance = 0;
    }

    void updateLocation(double distance) {
        relativeDistance += distance;
        if (relativeDistance >= currentBlockLength) {
            enteredNextBlock();
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
        return authority;
    }

    @Override
    public int getTrainNumber() {
        return trainID;
    }

    @Override
    public double getCommandSpeed() {
        return commandedSpeed;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public double getAcceleration() {
        return acceleration;
    }

    @Override
    public double getPower() {
        return power;
    }

    @Override
    public boolean getServiceBrake() {
        return serviceBrake;
    }

    @Override
    public boolean getEmergencyBrake() {
        return emergencyBrake;
    }


    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean getBrakeFailure() {
        return brakeFailure;
    }

    @Override
    public boolean getPowerFailure() {
        return powerFailure;
    }

    @Override
    public boolean getSignalFailure() {
        return signalFailure;
    }

    @Override
    public double getRealTemperature() {
        return realTemperature;
    }

    @Override
    public boolean getExtLights() {
        return extLights;
    }

    @Override
    public boolean getIntLights() {
        return intLights;
    }

    @Override
    public boolean getLeftDoors() {
        return leftDoors;
    }

    @Override
    public boolean getRightDoors() {
        return rightDoors;
    }

    @Override
    public double getlength() {
        return length;
    }

    @Override
    public void setValue(String propertyName, Object newValue) {

    }

    @Override
    public void changeTimeDelta(int v) {

    }
    @Override
    public int getPassengerCount() {
        return passengerCount;
    }

    @Override
    public int getCrewCount() {
        return crewCount;
    }

    public double getGrade() { return grade; }

    @Override
    public int getNumCars() {
        return numCars;
    }

    @Override
    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    @Override
    public double getMass() {
        return mass;
    }

    @Override
    public TrainController getController() {
        return this.controller;
    }

    @Override
    public void trainModelTimeStep(Future<UpdatedTrainValues> updatedTrainValuesFuture) throws ExecutionException, InterruptedException {

    }

    @Override
    public void trainModelPhysics() {

    }
}
