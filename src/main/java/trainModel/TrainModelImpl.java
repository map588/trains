package trainModel;

//import Common.TrackModel;

import Common.TrackModel;
import Common.TrainController;
import Common.TrainModel;
import Framework.Support.Notifier;
import Utilities.Constants;
import trackModel.TrackModelImpl;
import trackModel.TrackPseudoCode;
import trainController.TrainControllerImpl;
import trainController.stubTrainController;

import javax.swing.text.Utilities;


public class TrainModelImpl implements TrainModel, Notifier {

    private TrainModelSubject subject;

    //Passed Variables
    private int authority;
    private double commandSpeed;
    private String beacon, announcement;

    private double relativeDistance, currentBlockLength;


    //Vital Variables
    private double speed, acceleration, power;
    private double mass, grade;
    private boolean serviceBrake, emergencyBrake;
    private double distanceTraveled;


    //physics variables (no setters or getters, only to be used within train model
    private double brakeForce, engineForce, gravityForce;
    private double netForce, currentAngle, previousAcceleration;
    private int TIME_DELTA;
    //Murphy Variables
    private boolean brakeFailure, powerFailure, signalFailure;

    //NonVital Variables
    private boolean extLights, intLights, rightDoors, leftDoors;
    private double realTemperature, setTemperature;
    private int numCars, numPassengers, crewCount;
    private double length = Constants.TRAIN_LENGTH * numCars;

    //Module References
    private final TrainController controller;

    public TrainModelImpl(int trainID) {
        this.authority = 0;
        this.commandSpeed = 0;
        this.speed = 0;
        this.acceleration = 0;
        this.power = 0;
        this.serviceBrake = false;
        this.emergencyBrake = false;
        this.grade = 0;
        this.brakeFailure = false;
        this.powerFailure = false;
        this.signalFailure = false;
        this.TIME_DELTA = 10;
        this.realTemperature = 0;
        this.setTemperature = 0;
        this.extLights = false;
        this.intLights = false;
        this.leftDoors = false;
        this.rightDoors = false;
        this.numCars = 1;
        this.numPassengers = 1;
        this.crewCount = 2;
        this.mass = (Constants.EMPTY_TRAIN_MASS * numCars) + (Constants.PASSENGER_MASS * (crewCount + numPassengers));
        this.distanceTraveled = 0;
        this.beacon = "";
        this.announcement = "";
        this.controller = new TrainControllerImpl(trainID);
        controller.assignTrainModel(this);
        this.subject = new TrainModelSubject(this);
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
    public void setSetTemperature(double temp) { this.setTemperature = temp; notifyChange("SetTemperature", temp); }
    public void setRealTemperature(double temp) { this.realTemperature = temp; notifyChange("realTemperature", temp); }
    public void setAcceleration(double acceleration) { this.acceleration = acceleration; notifyChange("acceleration", acceleration); }
    public void setMass(double mass) { this.mass = mass; notifyChange("mass", mass); }
    public void setDistanceTraveled(double distance) { this.distanceTraveled = distance; notifyChange("distanceTraveled", distance); }
    public void setLength(double length) { this.length = length; notifyChange("length", length); }
    public void setBeacon(String beacon) { this.beacon = beacon; notifyChange("beacon", beacon); }
    public void setAnnouncement(String announcement) {this.announcement = announcement;}

    public void setValue(String propertyName, Object newValue){
        if(newValue == null)
            return;
        switch(propertyName){
            case Properties.AUTHORITY_PROPERTY -> this.authority = (int)newValue;
            case Properties.COMMANDSPEED_PROPERTY -> this.commandSpeed = (double)newValue;
            case Properties.ACTUALSPEED_PROPERTY -> this.speed = (double)newValue;
            case Properties.ACCELERATION_PROPERTY -> this.acceleration = (double)newValue;
            case Properties.POWER_PROPERTY -> this.power = (double)newValue;
            case Properties.GRADE_PROPERTY -> this.grade = (double)newValue;
            case Properties.SERVICEBRAKE_PROPERTY -> this.serviceBrake = (boolean)newValue;
            case Properties.EMERGENCYBRAKE_PROPERTY -> this.emergencyBrake = (boolean)newValue;
            case Properties.BRAKEFAILURE_PROPERTY -> this.brakeFailure = (boolean)newValue;
            case Properties.POWERFAILURE_PROPERTY -> this.powerFailure = (boolean)newValue;
            case Properties.SIGNALFAILURE_PROPERTY -> this.signalFailure = (boolean)newValue;
            case Properties.SETTEMPERATURE_PROPERTY -> this.setTemperature = (double)newValue;
            case Properties.REALTEMPERATURE_PROPERTY -> this.realTemperature = (double)newValue;
            case Properties.EXTLIGHTS_PROPERTY -> this.extLights = (boolean)newValue;
            case Properties.INTLIGHTS_PROPERTY -> this.intLights = (boolean)newValue;
            case Properties.LEFTDOORS_PROPERTY -> this.leftDoors = (boolean)newValue;
            case Properties.RIGHTDOORS_PROPERTY -> this.rightDoors = (boolean)newValue;
            case Properties.NUMCARS_PROPERTY -> this.numCars = (int)newValue;
            case Properties.NUMPASSENGERS_PROPERTY -> this.numPassengers = (int)newValue;
            case Properties.CREWCOUNT_PROPERTY -> this.crewCount = (int)newValue;
            case Properties.TIMEDELTA_PROPERTY -> this.TIME_DELTA = (int)newValue;
            case Properties.MASS_PROPERTY -> this.mass = (double)newValue;
            case Properties.DISTANCETRAVELED_PROPERTY -> this.distanceTraveled = (double)newValue;
            case Properties.BEACON_PROPERTY -> this.beacon = (String)newValue;
            case Properties.LENGTH_PROPERTY -> this.length = (double)newValue;
        }
    }
    //Getters
    public TrainModelSubject getSubject(){
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
    public double getDistanceTraveled() { return distanceTraveled; }
    public String getAnnouncement() {
        return this.announcement;
    }

    public double getLength() {
        return this.length;
    }

    @Override
    public int getCrewCount() {
        return this.crewCount;
    }

    @Override
    public int getNumPassengers() {
        return this.numPassengers;
    }

    @Override
    public int getNumCars() {
        return this.numCars;
    }

    @Override
    public double getMass() {
        return this.mass;
    }


    //Murphy Getters
    public boolean getBrakeFailure() { return this.brakeFailure; }
    public boolean getPowerFailure() { return this.powerFailure; }
    public boolean getSignalFailure() { return this.signalFailure; }

    //NonVital Getters
    public double getSetTemperature() {
        return this.setTemperature;
    }
    public double getRealTemperature() {
        return this.realTemperature;
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

    @Override
    public double getlength() {
        return 0;
    }

    public String getBeacon() {
        return this.beacon;
    }

    //TEMP TIME DELTA SETTER/GETTER
    public void setTimeDelta(int timeDelta) { this.TIME_DELTA = timeDelta; notifyChange("timeDelta", timeDelta); }
    public int getTimeDelta() { return this.TIME_DELTA; }

//    public void enteredNextBlock() {
//       currentBlockLength = (double)track.updateTrainLocation(this);
//       relativeDistance = 0;
//    }


    public void trainModelPhysics() {
        //CALCULATE MASS
        this.setMass((Constants.EMPTY_TRAIN_MASS * this.numCars) + (Constants.PASSENGER_MASS * (this.crewCount + this.numPassengers)));
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
        if (signalFailure) {
            this.setServiceBrake(true);
        }

//        if(currentBlockLength - relativeDistance < 0) {
//            enteredNextBlock();
//        }

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
        else if(this.speed == 0 && this.emergencyBrake) {
            this.engineForce = 0;
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