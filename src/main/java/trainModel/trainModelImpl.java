package trainModel;

import Common.TrackModel;
import Common.TrainController;
import Common.TrainModel;
import Framework.Notifications;
import Framework.PropertyChangeListener;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

public class trainModelImpl implements TrainModel, Notifications {
    //Passed Variables
    private int authority;
    private double commandSpeed;

    //Vital Variables
    private double speed;
    private double acceleration;
    private double power;
    private boolean serviceBrake;
    private boolean emergencyBrake;

    //Murphy Variables
    private boolean brakeFailure;
    private boolean powerFailure;
    private boolean signalFailure;

    //NonVital Variables
    private boolean extLights;
    private boolean intLights;
    private boolean leftDoors;
    private boolean rightDoors;
    private double temperature;

    private int numCars;
    private boolean numPassengers;

    private final List<PropertyChangeListener> listeners = new ArrayList<>();

    //Module Stubs
    private TrackModel track;
    private TrainController controller;

    public trainModelImpl(int trainID){
        this.authority = 0;
        this.commandSpeed = 0;
        this.speed = 0;
        this.acceleration = 0;
        this.power = 0;
        this.serviceBrake = false;
        this.emergencyBrake = false;

        this.brakeFailure = false;
        this.powerFailure = false;
        this.signalFailure = false;

        this.temperature = 0;
        this.extLights = false;
        this.intLights = false;
        this.leftDoors = false;
        this.rightDoors = false;
        this.numCars = false;
        this.numPassengers = false;
    }

    //Vital Setters
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake =brake;
    }
    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
    }
    public void setPower(double power) {
        this.power=power;
    }

    //Murphy Setters
    public void setBrakeFailure(boolean failure) { this.brakeFailure=failure; }
    public void setPowerFailure(boolean failure) { this.powerFailure=failure; }
    public void setSignalFailure(boolean failure) { this.signalFailure=failure; }

    public void setTrainNumber(int number) {

    }

    //NonVital Setters
    public void setNumCars(int numCars) {
        this.numCars = numCars;
    }
    public void setNumPassengers(int numPassengers) {
        this.numPassengers = numPassengers;
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
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

    public void addChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }
}
