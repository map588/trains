package trainModel;

import Common.TrackModel;
import Common.TrainController;
import Common.TrainModel;
import Framework.Notifications;
import Framework.PropertyChangeListener;
import javafx.beans.property.*;
import trainController.stubTrainController;

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
    private int numPassengers;

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
        this.numCars = 1;
        this.numPassengers = 1;

        this.controller = new stubTrainController(trainID);
        controller.assignTrainModel(this);


    }

    public void addChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }

    protected void notifyChange(String property, Object oldValue, Object newValue) {
        listeners.forEach(listener -> listener.onPropertyChange(property, newValue);
    }

    //Vital Setters
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
    }
    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
    }
    public void setPower(double power) {
        this.power=power;
    }

    //Murphy Setters
    public void setBrakeFailure(boolean failure) { this.brakeFailure = failure; }
    public void setPowerFailure(boolean failure) { this.powerFailure = failure; }
    public void setSignalFailure(boolean failure) { this.signalFailure = failure; }



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
        this.rightDoors = doors;
    }
    public void setExtLights(boolean lights) {
        this.extLights = lights;
    }
    public void setIntLights(boolean lights) { this.intLights = lights; }
    public void setTemperature(double temp) {
        this.temperature = temp;
    }

    //Vital Getters
    public int getAuthority() {
        return this.authority;
    }

    public int getTrainNumber() {
        return controller.getID();
    }

    public double getCommandSpeed() {
        return this.commandSpeed;
    }
    public double getSpeed() {
        return this.speed;
    }
    public double getAcceleration() {
        return this.acceleration;
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

    public double getWeightKG() {
        return 0;
    }

    //Murphy Getters
    public boolean getBrakeFailure() { return this.brakeFailure; }
    public boolean getPowerFailure() { return this.powerFailure; }
    public boolean getSignalFailure() { return this.signalFailure; }

    //NonVital Getters
    public double getTemperature() {
        return this.temperature;
    }
    public boolean getExtLights() {
        return this.extLights;
    }
    public boolean getIntLights() { return this.intLights; }
    public boolean getLeftDoors() {
        return this.leftDoors;
    }
    public boolean getRightDoors() {
        return this.rightDoors;
    }

    public void calculateSpeed() {
        //Power = Force*velocity
    }

    public void calculateAcceleration() {
        //Force = mass*acceleration
    }

}
