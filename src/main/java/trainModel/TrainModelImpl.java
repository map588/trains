package trainModel;

//import Common.TrackModel;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.Notifier;
import Utilities.Constants;
import Utilities.Conversion;
import Utilities.Enums.Direction;
import Utilities.Records.Beacon;
import Utilities.Records.UpdatedTrainValues;
import trackModel.TrackBlock;
import trackModel.TrackLine;
import trainController.TrainControllerImpl;

import java.util.concurrent.*;

import static Utilities.Constants.YARD_OUT_DIRECTION;
import static Utilities.Conversion.accelerationUnit.FPS2;
import static Utilities.Conversion.accelerationUnit.MPS2;
import static Utilities.Conversion.distanceUnit.FEET;
import static Utilities.Conversion.distanceUnit.METERS;
import static Utilities.Conversion.powerUnits.HORSEPOWER;
import static Utilities.Conversion.powerUnits.WATTS;
import static Utilities.Conversion.velocityUnit.MPH;
import static Utilities.Conversion.velocityUnit.MPS;
import static trainModel.Properties.*;


public class TrainModelImpl implements TrainModel, Notifier {

    private final TrainModelSubject subject;


    //Passed Variables
    private int authority = 0;
    private double commandSpeed = 0;
    private String announcement = "";

    private double relativeDistance = 0, currentBlockLength = 0;


    //Vital Variables
    private double speed = 0, acceleration = 0, power = 0;
    private double newSpeed = 0, newAcceleration = 0, newPower = 0;
    private double mass= 0, grade = 0;
    private boolean serviceBrake = false, emergencyBrake = false;
    private boolean newServiceBrake = false, newEmergencyBrake = false;
    private double distanceTraveled = 0;


    //physics variables (no setters or getters, only to be used within train model
    private double brakeForce = 0;
    private int TIME_DELTA = 10;
    //Murphy Variables
    private boolean brakeFailure = false, powerFailure = false, signalFailure = false;

    //NonVital Variables
    private boolean extLights = false, intLights = false, rightDoors = false, leftDoors = false;
    private boolean newExtLights = false, newIntLights = false, newRightDoors = false, newLeftDoors = false;
    private double realTemperature = 70, setTemperature = 70;
    private double newRealTemperature = 70, newSetTemperature = 70;
    private int numCars = 1, numPassengers = 0, crewCount = 2;
    private double length = Constants.TRAIN_LENGTH * numCars;


    //Module References
    private final TrainController controller;

    private TrackLine track;
    private Direction direction = Direction.NORTH;
    private TrackBlock currentBlock = new TrackBlock();

    //purely for temperature calculation
    private double elapsedTime = 0;

    private final ThreadLocalRandom r = ThreadLocalRandom.current();

    public TrainModelImpl() {
        initializeValues();
        this.controller = new TrainControllerImpl(this, -1);
        this.subject = new TrainModelSubject(this);
    }

    public TrainModelImpl(TrackLine track, int trainID) {
        initializeValues();
        this.track = track;
        this.controller = new TrainControllerImpl(this, trainID);
        this.subject = new TrainModelSubject(this);
    }

    public void delete() {
        controller.delete();
        this.subject.subjectDelete();

    }

    private void initializeValues() {
        this.authority = 10;
        this.commandSpeed = 10;
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
        this.direction = YARD_OUT_DIRECTION;
        this.mass = (Constants.EMPTY_TRAIN_MASS * numCars) + (Constants.PASSENGER_MASS * (crewCount + numPassengers));
        this.distanceTraveled = 0;
        this.announcement = "";
    }

    public void notifyChange(String property, Object newValue) {

       // System.out.println("Variable: " + property + " changed to " + newValue);

        // If the set is called by the GUI, it implies that the property is already changed
        // and we should not notify the subject of the change, because its already changed...
        if(!subject.isGUIUpdate) {
            subject.notifyChange(property, newValue);
        }
    }

    public void trainModelTimeStep(Future<UpdatedTrainValues> updatedTrainValuesFuture) throws ExecutionException, InterruptedException {
        // Data Locked
        physicsUpdate();

        reconcileControllerValues(updatedTrainValuesFuture.get()); //Data unlocked
    }

    public void reconcileControllerValues(UpdatedTrainValues controllerValues) {

        if (this.brakeFailure) {
            this.setServiceBrake(false);
            this.setEmergencyBrake(false);
        }
        else {
            this.setServiceBrake(controllerValues.serviceBrake());
            this.setEmergencyBrake(controllerValues.emergencyBrake());
        }

        if (this.powerFailure) {
            this.setPower(0);
        }
        else {
            this.setPower(controllerValues.power());
        }

        this.setExtLights(controllerValues.exteriorLights());
        this.setIntLights(controllerValues.interiorLights());
        this.setLeftDoors(controllerValues.leftDoors());
        this.setRightDoors(controllerValues.rightDoors());
        this.setSetTemperature(controllerValues.setTemperature());

        this.setAcceleration(newAcceleration);
        this.setActualSpeed(newSpeed);
        this.setRealTemperature(newRealTemperature);
    }

    public void trainModelPhysics(){
        physicsUpdate();

        this.setAcceleration(newAcceleration);
        this.setActualSpeed(newSpeed);
        this.setRealTemperature(newRealTemperature);
    }

    private void physicsUpdate() {

        //MASS CALCULATION (some reduncancy here the empty train mass is final, and the crewcount is final)
        this.setMass((Constants.EMPTY_TRAIN_MASS * this.numCars) + (Constants.PASSENGER_MASS * (this.crewCount + this.numPassengers)));
        //How many loaded trains? I think you need to multiply by the number of cars
        if (this.mass >= (Constants.LOADED_TRAIN_MASS * this.numCars)) {
            this.setMass(Constants.LOADED_TRAIN_MASS * this.numCars);
        }

        //NEXT BLOCK NOTICE
        if(currentBlockLength - relativeDistance < 0) {
            enteredNextBlock();
        }

        //ACCELERATION PROGRESSION
        double previousAcceleration = this.acceleration;
//        System.out.println("Previous Acceleration: " + previousAcceleration);

        //BRAKE FORCES
        if (this.serviceBrake && !this.emergencyBrake) {
            this.brakeForce = Constants.SERVICE_BRAKE_FORCE;
        }
        if (this.emergencyBrake) {
            this.brakeForce = Constants.EMERGENCY_BRAKE_FORCE;
        }
        if (!this.serviceBrake && !this.emergencyBrake) {
            this.brakeForce = 0;
        }

        //ENGINE FORCE
        double engineForce;
        if (this.power > 0 && this.speed == 0) {
            this.speed = 0.1; //if train is not moving, division by 0 occurs, set small amount of speed so we can get ball rolling
            engineForce = this.power / 0.1;
        }
        else if(this.speed == 0 && this.emergencyBrake) {
            engineForce = 0;
        }
        else {
            engineForce = this.power / this.speed;
        }
//        System.out.println("Engine Force: " + engineForce);

        //SLOPE FORCE
        double currentAngle = Math.atan(this.grade / 100);
        double gravityForce = this.mass * Constants.GRAVITY * Math.sin(currentAngle);

        //NET FORCE
        double netForce = engineForce - gravityForce - this.brakeForce;
        if (netForce > Constants.MAX_ENGINE_FORCE){
            netForce = Constants.MAX_ENGINE_FORCE;
        }
//        System.out.println("Net Force: " + netForce);

        //ACCELERATION CALCULATION
        this.acceleration = (netForce / this.mass);
        this.newAcceleration = this.acceleration;
//        System.out.println("Acceleration: " + this.acceleration);

        //SPEED CALCULATION
        if (this.power <= Constants.MAX_POWER) {
            this.speed = (this.speed + ((double) this.TIME_DELTA / 2) * (this.acceleration + previousAcceleration));
        }

        if (this.speed < 0) { this.speed = 0; }
        if (this.speed > Constants.MAX_SPEED) { this.setActualSpeed(Constants.MAX_SPEED); }
        this.newSpeed = this.speed;
//        System.out.println("Speed: " + this.speed);

        //TEMPERATURE CALCULATION
        this.elapsedTime += this.TIME_DELTA;
        if(this.elapsedTime >= 1 && (this.realTemperature < this.setTemperature)) {
            this.realTemperature = this.realTemperature + 1;
            this.elapsedTime = 0;
        }
        else if(this.elapsedTime >= 1 && (this.realTemperature > this.setTemperature)) {
            this.realTemperature = this.realTemperature - 1;
            this.elapsedTime = 0;
        }
        this.newRealTemperature = this.realTemperature;
    }


    public void enteredNextBlock() {
        currentBlock = track.updateTrainLocation(this);
        currentBlockLength = currentBlock.getLength();
        //controller.onBlock();
        relativeDistance = 0;
    }

    public int updatePassengers(int passengersEmbarked) {
        int passengersDisembarked;
        if(this.numPassengers <= 0) {
            this.numPassengers = passengersEmbarked;
            passengersDisembarked = 0;
        }
        else {
            passengersDisembarked = ( r.nextInt(0, this.numPassengers) );
            if ((passengersEmbarked - passengersDisembarked) > Constants.MAX_PASSENGERS) {
                this.numPassengers = Constants.MAX_PASSENGERS;
            } else this.numPassengers += passengersEmbarked - passengersDisembarked;
        }
        return passengersDisembarked;
    }

    public void setCommandSpeed(double speed) {
        System.out.println("Command Speed: " + speed);

        if (signalFailure) {
            this.commandSpeed = -1;
        }
        else {
            this.commandSpeed = speed;
        }
        notifyChange(COMMANDSPEED_PROPERTY, Conversion.convertVelocity(speed, MPS, MPH));
        controller.setCommandSpeed(speed);
    }
    public void setActualSpeed(double speed) {this.speed = speed;notifyChange(ACTUALSPEED_PROPERTY, Conversion.convertVelocity(speed, MPS, MPH));}
    public void setAuthority(int authority) {
        System.out.println("Authority: " + authority);

        if (signalFailure) {
            this.authority = -1;
        }
        else {
            this.authority = authority;
        }
        notifyChange(AUTHORITY_PROPERTY, authority);
        controller.setAuthority(authority);
    }
    public void setEmergencyBrake(boolean brake) {
        if (this.brakeFailure) {
            this.emergencyBrake = false;
            notifyChange(EMERGENCYBRAKE_PROPERTY, false);
        } else {
            this.emergencyBrake = brake;
            notifyChange(EMERGENCYBRAKE_PROPERTY, brake);
        }
    }
    public void setServiceBrake(boolean brake) {
        if (this.brakeFailure) {
            this.serviceBrake = false;
            notifyChange(SERVICEBRAKE_PROPERTY, false);
        } else {
            this.serviceBrake = brake;
            notifyChange(SERVICEBRAKE_PROPERTY, brake);
        }
    }
    public void setPower(double power) { this.power = power; notifyChange(POWER_PROPERTY, Conversion.convertPower(power, WATTS, HORSEPOWER)); }
    public void setGrade(double grade) { this.grade = grade; notifyChange(GRADE_PROPERTY, grade); }
    public void setBrakeFailure(boolean failure) { this.brakeFailure = failure; notifyChange(BRAKEFAILURE_PROPERTY, failure); }
    public void setPowerFailure(boolean failure) { this.powerFailure = failure; notifyChange(POWERFAILURE_PROPERTY, failure); }
    public void setSignalFailure(boolean failure) { this.signalFailure = failure; notifyChange(SIGNALFAILURE_PROPERTY, failure); }
    public void setNumCars(int numCars) { this.numCars = numCars; notifyChange(NUMCARS_PROPERTY, numCars); }
    public void setNumPassengers(int numPassengers) { this.numPassengers = numPassengers; notifyChange(NUMPASSENGERS_PROPERTY, numPassengers); }
    public void setCrewCount(int crewCount) { this.crewCount = crewCount; notifyChange(CREWCOUNT_PROPERTY, crewCount); }
    public void setLeftDoors(boolean doors) { this.leftDoors = doors; notifyChange(LEFTDOORS_PROPERTY, doors); }
    public void setRightDoors(boolean doors) { this.rightDoors = doors; notifyChange(RIGHTDOORS_PROPERTY, doors); }
    public void setExtLights(boolean lights) { this.extLights = lights; notifyChange(EXTLIGHTS_PROPERTY, lights); }
    public void setIntLights(boolean lights) { this.intLights = lights; notifyChange(INTLIGHTS_PROPERTY, lights); }
    public void setSetTemperature(double temp) { this.setTemperature = temp; notifyChange(SETTEMPERATURE_PROPERTY, temp); }

    public void setRealTemperature(double temp) { this.realTemperature = temp; notifyChange(REALTEMPERATURE_PROPERTY, temp); }
    public void setAcceleration(double acceleration) { this.acceleration = acceleration; notifyChange(ACCELERATION_PROPERTY, Conversion.convertAcceleration(acceleration, MPS2, FPS2)); }
    public void setMass(double mass) { this.mass = mass; notifyChange(MASS_PROPERTY, mass); }
    public void setDistanceTraveled(double distance) { this.distanceTraveled = distance; notifyChange(DISTANCETRAVELED_PROPERTY, Conversion.convertDistance(distance, METERS, FEET)); }
    public void setLength(double length) { this.length = length; notifyChange(LENGTH_PROPERTY, Conversion.convertDistance(length, METERS, FEET)); }
    public void setAnnouncement(String announcement) {this.announcement = announcement;}

    public void setValue(String propertyName, Object newValue){
        if(newValue == null)
            return;
        switch(propertyName){
            case AUTHORITY_PROPERTY -> this.authority = (int)newValue;
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
            case Properties.LENGTH_PROPERTY -> this.length = (double)newValue;
        }
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

    @Override
    public double getMass() {
        return this.mass;
    }

    public double getDistanceTraveled() { return distanceTraveled; }
    public String getAnnouncement() {
        return this.announcement;
    }
    public double getLength() {
        return this.length;
    }

    @Override
    public TrainController getController() {
        return this.controller;
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
        return this.length;
    }

    @Override
    public int getPassengerCount() {
        return this.numPassengers;
    }

    @Override
    public Direction getDirection() {
        return this.direction;
    }

    @Override
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void changeDirection() {
        if (this.direction == Direction.NORTH) {
            this.direction = Direction.SOUTH;
        } else {
            this.direction = Direction.NORTH;
        }
    }

    @Override
    public double getGrade() {
        return this.grade;
    }

    @Override
    public int getNumCars() {
        return this.numCars;
    }

    @Override
    public int getCrewCount() {
        return this.crewCount;
    }

    @Override
    public void passBeacon(Beacon beacon) {
        controller.updateBeacon(beacon);
    }

    //TEMP TIME DELTA SETTER/GETTER
    public void changeTimeDelta(int timeDelta) { this.TIME_DELTA = timeDelta; notifyChange(TIMEDELTA_PROPERTY, timeDelta); }
    public int getTimeDelta() { return this.TIME_DELTA; }



}