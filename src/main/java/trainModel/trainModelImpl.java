package trainModel;

//import Common.TrackModel;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.Notifications;
import Utilities.Constants;
import trainController.trainControllerImpl;

public class trainModelImpl implements TrainModel, Notifications {

    private trainModelSubject subject;

    //Passed Variables
    private int authority;
    private double commandSpeed;

    //Vital Variables
    private double speed, acceleration, power;
    private double mass, grade;
    private boolean serviceBrake, emergencyBrake;

    //physics variables (no setters or getters, only to be used within train model
    private double brakeForce, engineForce, gravityForce;
    private double netForce, currentAngle, previousAcceleration;
    private double TIME_DELTA;
    //Murphy Variables
    private boolean brakeFailure, powerFailure, signalFailure;

    //NonVital Variables
    private boolean extLights, intLights, rightDoors, leftDoors;
    private double temperature;
    private int numCars, numPassengers, crewCount;

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
        this.mass = (this.numCars + Constants.EMPTY_TRAIN_MASS) + (this.crewCount * Constants.PASSENGER_MASS);
        this.grade = 0;
        this.brakeFailure = false;
        this.powerFailure = false;
        this.signalFailure = false;
        this.TIME_DELTA = 10;
        this.temperature = 0;
        this.extLights = false;
        this.intLights = false;
        this.leftDoors = false;
        this.rightDoors = false;
        this.numCars = 1;
        this.numPassengers = 1;
        this.crewCount = 2;

        this.controller = new trainControllerImpl(trainID);
        controller.assignTrainModel(this);

        this.subject = new trainModelSubject(this);
    }


    public void notifyChange(String property, Object newValue) {

        System.out.println("Variable: " + property + " changed to " + newValue);

        // If the set is called by the GUI, it implies that the property is already changed
        // and we should not notify the subject of the change, because its already changed...
        if(!subject.isGUIUpdate) {
            subject.notifyChange(property, newValue);
        }
    }

    public void setCommandSpeed(double speed) { this.commandSpeed = speed; notifyChange("commandSpeed", speed); }
    public void setActualSpeed(double speed) { this.speed = speed; notifyChange("actualSpeed", speed); }
    public void setAuthority(int authority) { this.authority = authority; notifyChange("authority", authority); }
    public void setEmergencyBrake(boolean brake) { this.emergencyBrake = brake; notifyChange("emergencyBrake", brake); }
    public void setServiceBrake(boolean brake) { this.serviceBrake = brake; notifyChange("serviceBrake", brake); }
    public void setPower(double power) { this.power = power; notifyChange("power", power); }
    public void setGrade(double grade) { this.grade = grade; notifyChange("grade", grade); }
    public void setBrakeFailure(boolean failure) { this.brakeFailure = failure; notifyChange("brakeFailure", failure); }
    public void setPowerFailure(boolean failure) { this.powerFailure = failure; notifyChange("powerFailure", failure); }
    public void setSignalFailure(boolean failure) { this.signalFailure = failure; notifyChange("signalFailure", failure); }
    public void setNumCars(int numCars) { this.numCars = numCars; notifyChange("numCars", numCars); }
    public void setNumPassengers(int numPassengers) { this.numPassengers = numPassengers; notifyChange("numPassengers", numPassengers); }
    public void setCrewCount(int crewCount) { this.crewCount = crewCount; notifyChange("crewCount", crewCount); }
    public void setLeftDoors(boolean doors) { this.leftDoors = doors; notifyChange("leftDoors", doors); }
    public void setRightDoors(boolean doors) { this.rightDoors = doors; notifyChange("rightDoors", doors); }
    public void setExtLights(boolean lights) { this.extLights = lights; notifyChange("extLights", lights); }
    public void setIntLights(boolean lights) { this.intLights = lights; notifyChange("intLights", lights); }
    public void setTemperature(double temp) { this.temperature = temp; notifyChange("temperature", temp); }
    public void setAcceleration(double acceleration) { this.acceleration = acceleration; notifyChange("acceleration", acceleration); }
    public void setMass(double mass) { this.mass = mass; notifyChange("mass", mass); }

    public void setValue(String propertyName, Object newValue){
        if(newValue == null)
            return;
        switch(propertyName){
            case "authority" -> setAuthority((int)newValue);
            case "commandSpeed" -> setCommandSpeed((double)newValue);
            case "actualSpeed" -> setActualSpeed((double)newValue);
            case "acceleration" -> setAcceleration((double)newValue);
            case "power" -> setPower((double)newValue);
            case "grade" -> setGrade((double)newValue);
            case "serviceBrake" -> setServiceBrake((boolean)newValue);
            case "emergencyBrake" -> setEmergencyBrake((boolean)newValue);
            case "brakeFailure" -> setBrakeFailure((boolean)newValue);
            case "powerFailure" -> setPowerFailure((boolean)newValue);
            case "signalFailure" -> setSignalFailure((boolean)newValue);
            case "temperature" -> setTemperature((double)newValue);
            case "extLights" -> setExtLights((boolean)newValue);
            case "intLights" -> setIntLights((boolean)newValue);
            case "leftDoors" -> setLeftDoors((boolean)newValue);
            case "rightDoors" -> setRightDoors((boolean)newValue);
            case "numCars" -> setNumCars((int)newValue);
            case "numPassengers" -> setNumPassengers((int)newValue);
            case "crewCount" -> setCrewCount((int)newValue);
            case "timeDelta" -> setTimeDelta((double)newValue);
            case "mass" -> setMass((double)newValue);
        }
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

    //TEMP TIME DELTA SETTER/GETTER
    public void setTimeDelta(double timeDelta) { this.TIME_DELTA = timeDelta; notifyChange("timeDelta", timeDelta); }
    public double getTimeDelta() { return this.TIME_DELTA; }

    public void trainModelPhysics() {
        //CALCULATE MASS
        this.setMass(Constants.EMPTY_TRAIN_MASS + (Constants.PASSENGER_MASS * (this.crewCount + this.numPassengers)));
        if (this.mass >= Constants.LOADED_TRAIN_MASS) {
            this.setMass(Constants.LOADED_TRAIN_MASS);
        }

        //FAILURE STATES
        if (powerFailure) {
            this.setPower(0);
        }
        if (brakeFailure) {
            this.setServiceBrake(false);
        }

        //ACCELERATION PROGRESSION
        this.previousAcceleration = this.acceleration;
        System.out.println("Previous Acceleration: " + this.previousAcceleration);

        //BRAKE FORCES
        if (this.serviceBrake && !this.emergencyBrake) {
            this.brakeForce = Constants.SERVICE_BRAKE_FORCE;
        }
        if (this.emergencyBrake) {
            this.brakeForce = Constants.EMERGENCY_BRAKE_FORCE;
            this.setPower(0);
        }
        if (!this.serviceBrake && !this.emergencyBrake) {
            this.brakeForce = 0;
        }

        //ENGINE FORCE
        if (this.power > 0 && this.speed == 0) {
            this.setActualSpeed(0.1); //if train is not moving, division by 0 occurs, set small amount of speed so we can get ball rolling
            this.engineForce = this.power / 0.1;
        }
        else {
            this.engineForce = this.power / this.speed;
        }
        System.out.println("Engine Force: " + this.engineForce);

        //SLOPE FORCE
        this.currentAngle = Math.atan(this.grade / 100);
        this.gravityForce = this.mass * Constants.GRAVITY * Math.sin(this.currentAngle);

        //NET FORCE
        this.netForce = this.engineForce - this.gravityForce - this.brakeForce;
        if (this.netForce > Constants.MAX_ENGINE_FORCE){
            this.netForce = Constants.MAX_ENGINE_FORCE;
        }
        System.out.println("Net Force: " + this.netForce);

        //ACCELERATION CALCULATION
        this.setAcceleration(this.netForce / this.mass);
        System.out.println("Acceleration: " + this.acceleration);

        //SPEED CALCULATION
        if (this.power <= Constants.MAX_POWER) {
            this.setActualSpeed(this.speed + (this.TIME_DELTA / 2) * (this.acceleration + this.previousAcceleration));
        }

        if (this.speed < 0) { this.setActualSpeed(0); }
        if (this.speed > Constants.MAX_SPEED) { this.setActualSpeed(Constants.MAX_SPEED); }
        System.out.println("Speed: " + this.speed);
    }

}