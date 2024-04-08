package trainModel;

//import Common.TrackModel;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.Notifier;
import Utilities.Constants;
import Utilities.Conversion;
import Utilities.Enums.Direction;
import Utilities.Enums.Lines;
import Utilities.Records.Beacon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trackModel.TrackBlock;
import trackModel.TrackLine;
import trainController.TrainControllerImpl;
import trainModel.Records.UpdatedTrainValues;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import static Utilities.Constants.*;
import static Utilities.Conversion.accelerationUnit.*;
import static Utilities.Conversion.*;
import static Utilities.Conversion.distanceUnit.*;
import static Utilities.Conversion.powerUnits.*;
import static Utilities.Conversion.temperatureUnit.*;
import static Utilities.Conversion.velocityUnit.*;
import static trainModel.Properties.*;

//Actual, Real, what???

public class TrainModelImpl implements TrainModel, Notifier {

    //TODO: John, please separate your methods into what is done to the train, within the train, and what the train does to other modules


    private static final Logger logger = LoggerFactory.getLogger(TrainModelImpl.class);

    private final int trainID;
    private final double TIME_DELTA = (double)TIME_STEP_MS/1000;


    private final TrainModelSubject subject;
    private final TrainController controller;
    private final TrackLine track;

    //Passed Variables
    private int authority = 0;
    private double commandSpeed = 0;
    private String announcement = "";


    //Vital Variables
    private double  power = 0;
    private boolean serviceBrake = false;
    private boolean emergencyBrake = false;

    //Train Update Variables
    private double relativeDistance = 0;
    private double currentBlockLength = 0;



    //Failure Variables
    private boolean brakeFailure = false;
    private boolean powerFailure = false;
    private boolean signalFailure = false;

    //NonVital Variables
    private boolean extLights  = false;
    private boolean intLights  = false;
    private boolean rightDoors = false;
    private boolean leftDoors  = false;
    private double  realTemperature = 70;
    private double  setTemperature = 70;
    private double  newRealTemperature = 70;
    private int     numCars = 1;
    private int     numPassengers = 1;
    private int     crewCount = 2;

    //Physics Variables
    private double  grade = 0;
    private double  mass = 0;
    private double  speed = 0;
    private double  acceleration = 0;
    private double  distanceTraveled = 0;
    private double  length = Constants.TRAIN_LENGTH * numCars;
    private double  brakeForce = 0;

    //Trasition Variables
    private double newSpeed = 0;



    private void initializeValues() {
        this.direction = YARD_OUT_DIRECTION;
        this.mass = (Constants.EMPTY_TRAIN_MASS * numCars) + (Constants.PASSENGER_MASS * (crewCount + numPassengers));
        this.distanceTraveled = 0;
        this.announcement = "";
    }

    //Module References

    private Direction direction = Direction.NORTH;

    //purely for temperature calculation
    private double elapsedTime = 0;

    private final ThreadLocalRandom r = ThreadLocalRandom.current();

    public TrainModelImpl() {
        this.trainID = -1;
        this.track = new TrackLine();
        this.controller = new TrainControllerImpl(this, -1);
        initializeValues();
        this.subject = new TrainModelSubject(this);
    }

    public TrainModelImpl(TrackLine track, int trainID) {
        initializeValues();
        this.trainID = trainID;
        this.track = track;
        this.controller = new TrainControllerImpl(this, trainID);
        this.subject = new TrainModelSubject(this);
    }

    public void delete() {
        controller.delete();
        this.subject.subjectDelete();

    }

    public void notifyChange(String property, Object newValue) {
        subject.notifyChange(property, newValue);
    }

    public void trainModelTimeStep(Future<UpdatedTrainValues> updatedTrainValuesFuture) throws ExecutionException, InterruptedException {
        // Data Locked
        physicsUpdate();

        reconcileControllerValues(updatedTrainValuesFuture.get()); //Data unlocked
    }

    public void reconcileControllerValues(UpdatedTrainValues controllerValues) {

        if (this.brakeFailure) {
            this.setServiceBrake(false);
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

        this.setAcceleration(acceleration);
        this.setActualSpeed(newSpeed);
        this.setRealTemperature(newRealTemperature);
    }

    //Called when not running System.
    public void trainModelPhysics(){
        physicsUpdate();

        this.acceleration = acceleration;
        this.setActualSpeed(newSpeed);
        this.setRealTemperature(newRealTemperature);
    }

    @Override
    public Lines getLine() {
        return track.getLine();
    }

    private void physicsUpdate() {
        //MASS CALCULATION (some redundancy here, the empty train mass is final, and the crew count is final)
        this.setMass((Constants.EMPTY_TRAIN_MASS * this.numCars) + (Constants.PASSENGER_MASS * (this.crewCount + this.numPassengers)));

        //Check if the train is fully loaded
        if (this.mass >= (Constants.LOADED_TRAIN_MASS * this.numCars)) {
            this.setMass(Constants.LOADED_TRAIN_MASS * this.numCars);
        }

        //NEXT BLOCK NOTICE
//        if(currentBlockLength - relativeDistance <= 0) {
//            enteredNextBlock();
//        }

        //TRANSITION STEP
        double previousAcceleration = this.acceleration;


        //BRAKE FORCES
        if (this.serviceBrake && !this.emergencyBrake) {
            this.brakeForce = SERVICE_BRAKE_FORCE;
        }
        if (this.emergencyBrake) {
            this.brakeForce = EMERGENCY_BRAKE_FORCE;
        }
        if (!this.serviceBrake && !this.emergencyBrake) {
            this.brakeForce = 0;
        }

        //ENGINE FORCE
        double engineForce;
        if (this.power > 0.0001 && this.speed < 0.0001) {
            this.newSpeed = 0.1; //if train is not moving, division by 0 occurs, set small amount of speed so we can get ball rolling
            engineForce = this.power / 0.1;
        }
        else {
            engineForce = this.power / this.newSpeed;
        }

        if(engineForce > MAX_ENGINE_FORCE) {
            engineForce = MAX_ENGINE_FORCE;
        }else if(Double.isNaN(engineForce)){
            engineForce = 0;
        }


        //SLOPE FORCE
        double currentAngle = Math.atan(this.grade / 100);
        double gravityForce = this.mass * Constants.GRAVITY * Math.sin(currentAngle);
        //System.out.println("Gravity Force: " + gravityForce);

        //NET FORCE
        double netForce = engineForce - gravityForce - this.brakeForce;
        if (netForce > MAX_ENGINE_FORCE){
            netForce = MAX_ENGINE_FORCE;
        }


        //ACCELERATION CALCULATION
        this.acceleration = (netForce / this.mass);

        //SPEED CALCULATION
        if (this.power <= MAX_POWER_W) {
            this.newSpeed = (this.speed + (this.TIME_DELTA / 2) * (this.acceleration + previousAcceleration));
        }

        if (this.newSpeed < 0) { this.newSpeed = 0; }
        if (this.newSpeed > Constants.MAX_SPEED) { this.newSpeed = Constants.MAX_SPEED; }

        //DISTANCE CALCULATION
        this.relativeDistance += this.newSpeed * this.TIME_DELTA;

        //TEMPERATURE CALCULATION
        this.elapsedTime += this.TIME_DELTA;
        if(this.elapsedTime >= 1 && (this.realTemperature < this.setTemperature)) {
            this.newRealTemperature = this.realTemperature + 1;
            this.elapsedTime = 0;
        }
        else if(this.elapsedTime >= 1 && (this.realTemperature > this.setTemperature)) {
            this.newRealTemperature = this.realTemperature - 1;
            this.elapsedTime = 0;
        }

        this.speed = this.newSpeed;

    }



    public void enteredNextBlock() {
        //System.out.println("Train Entered Next Block");
        TrackBlock currentBlock = track.updateTrainLocation(this);
        currentBlockLength = currentBlock.getLength();
        //controller.onBlock();
        relativeDistance = 0;
    }

    public void setCommandSpeed(double speed) {
        this.commandSpeed = (signalFailure) ? -1 : convertVelocity(speed, MPH, MPS);
        notifyChange(COMMANDSPEED_PROPERTY, speed);
        controller.setCommandSpeed(speed);
    }
    public void setAuthority(int authority) {
        this.authority = (signalFailure) ? -1 : authority;
        controller.setAuthority(this.authority);
        logger.info("Train {} received Authority: {}",this.trainID, this.authority);
        notifyChange(AUTHORITY_PROPERTY, this.authority);
    }

    public void setEmergencyBrake(boolean brake) {
            this.emergencyBrake = brake;
            notifyChange(EMERGENCYBRAKE_PROPERTY, this.emergencyBrake);
    }
    public void setServiceBrake(boolean brake) {
        this.serviceBrake = (!brakeFailure && brake);
        notifyChange(SERVICEBRAKE_PROPERTY, this.serviceBrake);
    }

    public void setPower(double power) {
        if(power < 0) power = 0;
        //if(power > Constants.MAX_POWER_W) power = Constants.MAX_POWER_W;
        this.power = power;
        notifyChange(POWER_PROPERTY,
        Conversion.convertPower(this.power, WATTS, HORSEPOWER));
    }

    public void setActualSpeed(double speed) {
        this.speed = speed;
        notifyChange(ACTUALSPEED_PROPERTY, convertVelocity(this.speed, MPS, MPH));
    }

    public void setBrakeFailure(boolean failure) { this.brakeFailure = failure; notifyChange(BRAKEFAILURE_PROPERTY, this.brakeFailure); }
    public void setPowerFailure(boolean failure) { this.powerFailure = failure; notifyChange(POWERFAILURE_PROPERTY, this.powerFailure); }
    public void setSignalFailure(boolean failure) { this.signalFailure = failure; notifyChange(SIGNALFAILURE_PROPERTY, this.signalFailure); }

    public void setGrade(double grade) { this.grade = grade; notifyChange(GRADE_PROPERTY, grade); }
    public void setNumCars(int numCars) { this.numCars = numCars; notifyChange(NUMCARS_PROPERTY, this.numCars); }
    public void setNumPassengers(int numPassengers) { this.numPassengers = numPassengers; notifyChange(NUMPASSENGERS_PROPERTY, this.numPassengers); }
    public void setCrewCount(int crewCount) { this.crewCount = crewCount; notifyChange(CREWCOUNT_PROPERTY, this.crewCount); }
    public void setLeftDoors(boolean doors) { this.leftDoors = doors; notifyChange(LEFTDOORS_PROPERTY, this.leftDoors); }
    public void setRightDoors(boolean doors) { this.rightDoors = doors; notifyChange(RIGHTDOORS_PROPERTY, this.rightDoors); }
    public void setExtLights(boolean lights) { this.extLights = lights; notifyChange(EXTLIGHTS_PROPERTY, this.extLights); }
    public void setIntLights(boolean lights) { this.intLights = lights; notifyChange(INTLIGHTS_PROPERTY, this.intLights); }
    public void setSetTemperature(double temp) { this.setTemperature = temp; notifyChange(SETTEMPERATURE_PROPERTY, this.setTemperature); }
    public void setRealTemperature(double temp) { this.realTemperature = temp; notifyChange(REALTEMPERATURE_PROPERTY, this.realTemperature); }
    public void setAcceleration(double acceleration) { this.acceleration = acceleration; notifyChange(ACCELERATION_PROPERTY, Conversion.convertAcceleration(this.acceleration, MPS2, FPS2)); }
    public void setMass(double mass) { this.mass = mass; notifyChange(MASS_PROPERTY, mass); }
    public void setDistanceTraveled(double distance) { this.distanceTraveled = distance; notifyChange(DISTANCETRAVELED_PROPERTY, Conversion.convertDistance(this.distanceTraveled, METERS, FEET)); }
    public void setLength(double length) { this.length = length; notifyChange(LENGTH_PROPERTY, Conversion.convertDistance(this.length, METERS, FEET)); }
    public void setAnnouncement(String announcement) {this.announcement = announcement;}


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


    public void setValue(String propertyName, Object newValue){
        if(newValue == null)
            return;
        switch(propertyName){
            case AUTHORITY_PROPERTY -> this.authority = (int)newValue;
            case Properties.COMMANDSPEED_PROPERTY -> this.commandSpeed = convertVelocity((double)newValue, MPH, MPS);
            case Properties.ACTUALSPEED_PROPERTY -> this.speed = convertVelocity((double)newValue, MPH, MPS);
            case Properties.ACCELERATION_PROPERTY -> this.acceleration = convertAcceleration((double)newValue, FPS2, MPS2);
            case Properties.POWER_PROPERTY -> this.power = convertPower((double)newValue, HORSEPOWER, WATTS);
            case Properties.GRADE_PROPERTY -> this.grade = (double)newValue;
            case Properties.SERVICEBRAKE_PROPERTY -> this.serviceBrake = (boolean)newValue;
            case Properties.EMERGENCYBRAKE_PROPERTY -> this.emergencyBrake = (boolean)newValue;
            case Properties.BRAKEFAILURE_PROPERTY -> this.brakeFailure = (boolean)newValue;
            case Properties.POWERFAILURE_PROPERTY -> this.powerFailure = (boolean)newValue;
            case Properties.SIGNALFAILURE_PROPERTY -> this.signalFailure = (boolean)newValue;
            case Properties.SETTEMPERATURE_PROPERTY -> this.setTemperature = convertTemperature((double)newValue, FAHRENHEIT, CELSIUS);
            case Properties.REALTEMPERATURE_PROPERTY -> this.realTemperature = convertTemperature((double)newValue, FAHRENHEIT, CELSIUS);
            case Properties.EXTLIGHTS_PROPERTY -> this.extLights = (boolean)newValue;
            case Properties.INTLIGHTS_PROPERTY -> this.intLights = (boolean)newValue;
            case Properties.LEFTDOORS_PROPERTY -> this.leftDoors = (boolean)newValue;
            case Properties.RIGHTDOORS_PROPERTY -> this.rightDoors = (boolean)newValue;
            case Properties.NUMCARS_PROPERTY -> this.numCars = (int)newValue;
            case Properties.NUMPASSENGERS_PROPERTY -> this.numPassengers = (int)newValue;
            case Properties.CREWCOUNT_PROPERTY -> this.crewCount = (int)newValue;
            case Properties.MASS_PROPERTY -> this.mass = (double)newValue;
            case Properties.DISTANCETRAVELED_PROPERTY -> this.distanceTraveled = convertDistance((double)newValue, FEET, METERS);
            case Properties.LENGTH_PROPERTY -> this.length = convertDistance((double)newValue, FEET, METERS);
        }
    }

    @Override
    public void changeTimeDelta(int v) {
        logger.info("Cannot change time delta at runtime");
    }

    //Vital Getters
    public int getAuthority() {
        return this.authority;
    }
    public int getTrainNumber() {
        return this.trainID;
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

}