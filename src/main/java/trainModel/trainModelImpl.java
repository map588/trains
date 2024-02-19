package trainModel;

import Common.trackModel;
import Common.trainController;
import Common.trainModel;
import javafx.beans.property.*;

public class trainModelImpl implements trainModel{
    //Passed Variables
    private IntegerProperty authority;
    private DoubleProperty commandSpeed;

    //Vital Variables
    private DoubleProperty speed;
    private DoubleProperty acceleration;
    private DoubleProperty power;
    private BooleanProperty serviceBrake;
    private BooleanProperty emergencyBrake;

    //Murphy Variables
    private BooleanProperty brakeFailure;
    private BooleanProperty powerFailure;
    private BooleanProperty signalFailure;

    //NonVital Variables
    private BooleanProperty extLights;
    private BooleanProperty intLights;
    private BooleanProperty leftDoors;
    private BooleanProperty rightDoors;
    private DoubleProperty temperature;

    private IntegerProperty numCars;
    private IntegerProperty numPassengers;

    //Module Stubs
    private trackModel track;
    private trainController controller;

    public trainModelImpl(int trainID){
        this.authority = new SimpleIntegerProperty(0);
        this.commandSpeed = new SimpleDoubleProperty(0);
        this.speed = new SimpleDoubleProperty(0);
        this.acceleration = new SimpleDoubleProperty(0);
        this.power = new SimpleDoubleProperty(0);
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);

        this.brakeFailure = new SimpleBooleanProperty(false);
        this.powerFailure = new SimpleBooleanProperty(false);
        this.signalFailure = new SimpleBooleanProperty(false);

        this.temperature = new SimpleDoubleProperty(0);
        this.extLights = new SimpleBooleanProperty(false);
        this.intLights = new SimpleBooleanProperty(false);
        this.leftDoors = new SimpleBooleanProperty(false);
        this.rightDoors = new SimpleBooleanProperty(false);
        this.numCars = new SimpleIntegerProperty(0);
        this.numPassengers = new SimpleIntegerProperty(0);
    }

    //Vital Setters
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake.set(brake);
    }
    public void setServiceBrake(boolean brake) {
        this.serviceBrake.set(brake);
    }
    public void setPower(double power) {
        this.power.set(power);
    }

    //Murphy Setters
    public void setBrakeFailure(boolean failure) { this.brakeFailure.set(failure); }
    public void setPowerFailure(boolean failure) { this.powerFailure.set(failure); }
    public void setSignalFailure(boolean failure) { this.signalFailure.set(failure); }

    public void setTrainNumber(int number) {

    }

    //NonVital Setters
    public void setNumCars(int numCars) {
        this.numCars.set(numCars);
    }
    public void setNumPassengers(int numPassengers) {
        this.numPassengers.set(numPassengers);
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors.set(doors);
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors.set(doors);
    }
    public void setExtLights(boolean lights) {
        this.extLights.set(lights);
    }
    public void setIntLights(boolean lights) { this.intLights.set(lights); }
    public void setTemperature(double temp) {
        this.temperature.set(temp);
    }

    //Vital Getters
    public int getAuthority() {
        return this.authority.get();
    }

    public int getTrainNumber() {
        return 0;
    }

    public double getCommandSpeed() {
        return this.commandSpeed.get();
    }
    public double getSpeed() {
        return this.speed.get();
    }
    public double getAcceleration() {
        return this.acceleration.get();
    }
    public double getPower() {
        return this.power.get();
    }
    public boolean getServiceBrake() {
        return this.serviceBrake.get();
    }
    public boolean getEmergencyBrake() {
        return this.emergencyBrake.get();
    }

    public double getWeightKG() {
        return 0;
    }

    //Murphy Getters
    public boolean getBrakeFailure() { return this.brakeFailure.get(); }
    public boolean getPowerFailure() { return this.powerFailure.get(); }
    public boolean getSignalFailure() { return this.signalFailure.get(); }

    //NonVital Getters
    public double getTemperature() {
        return this.temperature.get();
    }
    public boolean getExtLights() {
        return this.extLights.get();
    }
    public boolean getIntLights() { return this.intLights.get(); }
    public boolean getLeftDoors() {
        return this.leftDoors.get();
    }
    public boolean getRightDoors() {
        return this.rightDoors.get();
    }

    public void calculateSpeed() {
        //Power = Force*velocity
    }

    public void calculateAcceleration() {
        //Force = mass*acceleration
    }

}
