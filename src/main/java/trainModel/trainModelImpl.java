package trainModel;

//import Common.TrackModel;
import Common.TrainController;
import Common.TrainModel;
import Framework.Support.Notifications;
import Framework.Support.PropertyChangeListener;
//import trackModel.stubTrackModel;
import trainController.stubTrainController;
import java.lang.Math;

import java.util.ArrayList;
import java.util.List;
import Utilities.Constants;

public class trainModelImpl implements TrainModel, Notifications {

    private trainModelSubject subject;

    //Passed Variables
    private int authority;
    private double commandSpeed;

    //Vital Variables
    private double speed;
    private double acceleration;
    private double power;
    private boolean serviceBrake;
    private boolean emergencyBrake;
    private double mass;
    private double grade;

    //physics variables (no setters or getters, only to be used within train model
    private double brakeForce;
    private double engineForce;
    private double slopeForce;
    private double netForce;
    private double currentAngle;
    private double previousAcceleration;

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
    private int crewCount;

    private final List<PropertyChangeListener> listeners = new ArrayList<>();

    //Module Stubs
  //  private final TrackModel track = new stubTrackModel();
    private TrainController controller;

    public trainModelImpl(int trainID){

        this.authority = 0;
        this.commandSpeed = 0;
        this.speed = 0;
        this.acceleration = 0;
        this.power = 0;
        this.serviceBrake = false;
        this.emergencyBrake = false;
        this.mass = Constants.EMPTY_TRAIN_MASS + (this.crewCount * Constants.PASSENGER_MASS);

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
        this.crewCount = 2;

        this.controller = new stubTrainController(trainID);
        controller.assignTrainModel(this);

        this.subject = new trainModelSubject(this);
    }

    public void addChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }

    protected void notifyChange(String property, Object newValue) {
        listeners.forEach(listener -> listener.onPropertyChange(property, newValue));
    }

    //Vital Setters
    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        notifyChange("commandSpeed", speed);
    }
    public void setActualSpeed(double speed) {
        this.speed = speed;
        notifyChange("actualSpeed", speed);
    }
    public void setAuthority(int authority) {
        this.authority = authority;
        notifyChange("authority", authority);
    }
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        notifyChange("emergencyBrake", brake);
    }
    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        notifyChange("serviceBrake", brake);
    }
    public void setPower(double power) {
        this.power = power;
        notifyChange("power", power);
    }
    public void setGrade(double grade) {
        this.grade = grade;
        notifyChange("grade", grade);
    }
    //Murphy Setters
    public void setBrakeFailure(boolean failure) {
        this.brakeFailure = failure;
        notifyChange("brakeFailure", failure);
    }
    public void setPowerFailure(boolean failure) {
        this.powerFailure = failure;
        notifyChange("powerFailure", failure);
    }
    public void setSignalFailure(boolean failure) {
        this.signalFailure = failure;
        notifyChange("signalFailure", failure);
    }
    //NonVital Setters
    public void setNumCars(int numCars) {
        this.numCars = numCars;
        notifyChange("numCars", numCars);
    }
    public void setNumPassengers(int numPassengers) {
        this.numPassengers = numPassengers;
        notifyChange("numPassengers", numPassengers);
    }
    public void setCrewCount(int crewCount) {
        this.crewCount = crewCount;
        notifyChange("crewCount", crewCount);
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        notifyChange("leftDoors", doors);
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        notifyChange("rightDoors", doors);
    }
    public void setExtLights(boolean lights) {
        this.extLights = lights;
        notifyChange("extLights", lights);
    }
    public void setIntLights(boolean lights) {
        this.intLights = lights;
        notifyChange("intLights", lights);
    }
    public void setTemperature(double temp) {
        this.temperature = temp;
        notifyChange("temperature", temp);
    }

    //Getters
    public trainModelSubject getSubject(){
        return this.subject;
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
    public double getGrade() { return grade; }

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

    public void trainModelPhysics() {
        //CALCULATE MASS
        this.mass = Constants.EMPTY_TRAIN_MASS + (Constants.PASSENGER_MASS * (this.crewCount + this.numPassengers));
        if (this.mass >= Constants.LOADED_TRAIN_MASS) {
            this.mass = Constants.LOADED_TRAIN_MASS;
        }

        //FAILURE STATES
        if (powerFailure) {
            this.power = 0;
        }
        if (brakeFailure) {
            this.serviceBrake = false;
        }

        //ACCELERATION PROGRESSION
        this.previousAcceleration = this.acceleration;

        //BRAKE FORCES
        if (this.serviceBrake && !this.emergencyBrake) {
            this.brakeForce = Constants.SERVICE_BRAKE_FORCE;
        }
        if (this.emergencyBrake) {
            this.brakeForce = Constants.EMERGENCY_BRAKE_FORCE;
            this.power = 0;
        }
        if (!this.serviceBrake && !this.emergencyBrake) {
            this.brakeForce = 0;
        }

        //ENGINE FORCE
        try {
            this.engineForce = this.power / this.speed;
        } catch (ArithmeticException e) {
            if (this.power > 0) {
                this.speed = 0.1; //if train is not moving, division by 0 occurs, set small amount of speed so we can get ball rolling
            }
        }

        //SLOPE FORCE
        this.currentAngle = Math.atan(this.grade / 100);
        this.slopeForce = this.mass * Constants.GRAVITY * Math.sin(this.currentAngle);

        //NET FORCE
        this.netForce = this.engineForce - this.slopeForce - this.brakeForce;
        if (this.netForce > Constants.MAX_ENGINE_FORCE){
            this.netForce = Constants.MAX_ENGINE_FORCE;
        }

        //ACCELERATION CALCULATION
        this.acceleration = this.netForce / this.mass;

        //SPEED CALCULATION
        if (this.power <= Constants.MAX_POWER) {
            //this.speed = this.speed + (TIME_DELTA * 0.001 / 2) * (this.acceleration + this.previousAcceleration);
        }

        if (this.speed < 0) { this.speed = 0; }
        if (this.speed > Constants.MAX_SPEED) { this.speed = Constants.MAX_SPEED; }
    }
}