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
import trainController.TrainControllerFactory;
import trainModel.Records.UpdatedTrainValues;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.concurrent.*;

import static Utilities.Constants.*;
import static Utilities.Conversion.accelerationUnit.FPS2;
import static Utilities.Conversion.accelerationUnit.MPS2;
import static Utilities.Conversion.convertVelocity;
import static Utilities.Conversion.distanceUnit.FEET;
import static Utilities.Conversion.distanceUnit.METERS;
import static Utilities.Conversion.massUnits.KILOGRAMS;
import static Utilities.Conversion.massUnits.TONS;
import static Utilities.Conversion.powerUnits.HORSEPOWER;
import static Utilities.Conversion.powerUnits.WATTS;
import static Utilities.Conversion.temperatureUnit.CELSIUS;
import static Utilities.Conversion.temperatureUnit.FAHRENHEIT;
import static Utilities.Conversion.velocityUnit.MPH;
import static Utilities.Conversion.velocityUnit.MPS;
import static trainModel.Properties.*;

//Actual, Real, what???

public class TrainModelImpl implements TrainModel, Notifier {


    private static final Logger logger = LoggerFactory.getLogger(TrainModelImpl.class);

    private final int trainID;
    private final double TIME_DELTA = TIME_STEP_S;
    private static final double COEFFICIENT_OF_FRICTION = 0.02;


    private final TrainModelSubject subject;
    private final TrainController controller;
    private final TrackLine track;
    private TrackBlock currentBlock;

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
    public volatile boolean deleted = false;



    //Failure Variables
    private boolean brakeFailure = false;
    private boolean powerFailure = false;
    private boolean signalFailure = false;

    //NonVital Variables
    private boolean extLights  = false;
    private boolean intLights  = false;
    private boolean rightDoors = false;
    private boolean leftDoors  = false;
    private double  realTemperature = 21;
    private double  setTemperature = 21;
    private double  newRealTemperature = 21;
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

    //Transition Variables


    private static final ExecutorService listeningExecutor = Executors.newSingleThreadExecutor();

    private final TrainControllerFactory controllerFactory = TrainControllerFactory.getInstance();

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

    // Constructor for TrainModelImpl when no parameters are provided
    public TrainModelImpl() throws NotBoundException, IOException {
        this.trainID = -1;
        this.track = new TrackLine();
        initializeValues();
        this.subject = new TrainModelSubject(this);
        this.controller = controllerFactory.createTrainController(this, -1);
    }

    // Constructor for TrainModelImpl when track and trainID are provided
    public TrainModelImpl(TrackLine track, int trainID){
        initializeValues();
        this.trainID = trainID;
        this.track = track;
        this.currentBlock = track.getBlock(0);
        this.subject = new TrainModelSubject(this);
        this.controller = controllerFactory.createTrainController(this, trainID);
    }

    // Method to delete the train
    public void delete() {
        this.subject.subjectDelete();
        this.deleted = true;
        controller.delete();
    }

    // Method to notify Subject and GUI of changes in the train model
    public void notifyChange(String property, Object newValue) {
        subject.notifyChange(property, newValue);
    }

    // Method to update the train model values for each time step
    public void trainModelTimeStep(Future<UpdatedTrainValues> updatedTrainValuesFuture) throws ExecutionException, InterruptedException {
        // Data Locked
        physicsUpdate();
        elapsedTime += TIME_DELTA;
        reconcileControllerValues(updatedTrainValuesFuture.get()); //Data unlocked
    }

    // Method to reconcile the train controller values with the train model values
     private void reconcileControllerValues(UpdatedTrainValues controllerValues) {

        this.setPower(controllerValues.power() * numCars);

        this.setExtLights(controllerValues.exteriorLights());
        this.setIntLights(controllerValues.interiorLights());
        this.setLeftDoors(controllerValues.leftDoors());
        this.setRightDoors(controllerValues.rightDoors());
        this.setSetTemperature(controllerValues.setTemperature());


        this.setAcceleration(acceleration);
        this.setActualSpeed(speed);
        this.setRealTemperature(newRealTemperature);


        controller.checkFailures(power);
    }

    //Called when not running System.
    public void trainModelPhysics(){
        physicsUpdate();

        this.setAcceleration(acceleration);
        this.setActualSpeed(speed);
        this.setRealTemperature(newRealTemperature);
    }

    @Override
    public Lines getLine() {
        return track.getLine();
    }

    // Method to update all physics of the train model, including mass, acceleration, speed, distance, and temperature
    private void physicsUpdate() {
        //MASS CALCULATION (some redundancy here, the empty train mass is final, and the crew count is final)


        //Check if the train is fully loaded
        if (this.mass >= (Constants.LOADED_TRAIN_MASS * this.numCars)) {
            this.setMass (Constants.LOADED_TRAIN_MASS * this.numCars);
        }

        //NEXT BLOCK NOTICE
        if(currentBlockLength - relativeDistance <= 0) {
            enteredNextBlock();
        }

        //TRANSITION STEP
        double previousAcceleration = this.acceleration;


        //BRAKE FORCES
        brakeForce = 0;
        if (this.emergencyBrake) {
            this.brakeForce = EMERGENCY_BRAKE_FORCE;
        } else if (this.serviceBrake) {
            this.brakeForce = SERVICE_BRAKE_FORCE;
        }


        //ENGINE FORCE
        double engineForce;
        if (this.power > 0.0001 && this.speed < 0.0001) {
            engineForce = this.power / 0.1;
        }
        else {
            engineForce = this.power / this.speed;
        }

        if(engineForce > MAX_ENGINE_FORCE * numCars) {
            engineForce = MAX_ENGINE_FORCE * numCars;
        }else if(Double.isNaN(engineForce)){
            engineForce = 0;
        }


        // SLOPE FORCE
        double currentAngle = Math.atan(this.grade / 100);
        double gravityForce = this.mass * Constants.GRAVITY * Math.sin(currentAngle);

        // FRICTION FORCE
        double normalForce = this.mass * Constants.GRAVITY * Math.cos(currentAngle);
        double frictionForce = COEFFICIENT_OF_FRICTION * normalForce;

        // NET FORCE
        double netForce = engineForce - gravityForce - this.brakeForce - frictionForce;
        if (netForce > MAX_ENGINE_FORCE * numCars) {
            netForce = MAX_ENGINE_FORCE * numCars;
        }


        //ACCELERATION CALCULATION
        this.acceleration = (netForce / this.mass);

        //SPEED CALCULATION
        if (this.power <= MAX_POWER_W * numCars) {
            this.speed = (this.speed + (this.TIME_DELTA / 2) * (this.acceleration + previousAcceleration));
        }

        if (this.speed < 0) {
            this.speed = 0;
        }

        if(this.speed == 0 && this.acceleration < 0) {
            this.acceleration = 0;
        }

        //TODO: If we can go faster than the "maximum speed" in simulation, we are doing something wrong
//        if (this.speed > Constants.MAX_SPEED) {
//            this.speed = Constants.MAX_SPEED;
//        }

        //DISTANCE CALCULATION
        this.relativeDistance += this.speed * this.TIME_DELTA;

        //TEMPERATURE CALCULATION
        double k = 0.1; // heat transfer coefficient

        double dT = k * (this.setTemperature - this.realTemperature) * this.TIME_DELTA;
        this.newRealTemperature = this.realTemperature + dT;
    }

    // Method to check if the train model is deleted
    public boolean isDeleted() {
        return deleted;
    }

    // Method to handle when the train enters the next block
    public void enteredNextBlock() {
        listeningExecutor.submit(controller::onBlock);
        double previousElevation = currentBlock.getElevation();
        currentBlock = track.updateTrainLocation(this);
        if(previousElevation < currentBlock.getElevation()) {
            this.setGrade(currentBlock.getGrade());
        }
        else {
            this.setGrade(-currentBlock.getGrade());
        }
        relativeDistance = 0;
        currentBlockLength = currentBlock.getLength();

    }

    // Method to set the commanded of the train model and pass the value to the train controller
    public void setCommandSpeed(double speed) {
        controller.setCommandSpeed((signalFailure) ? -1 : speed);
        this.commandSpeed =  speed;

        listeningExecutor.execute(() -> {
        notifyChange(COMMANDSPEED_PROPERTY, Conversion.convertVelocity(commandSpeed,MPS,MPH));
        });

      //  logger.info("Train {} <= Command Speed: {}",this.trainID, speed);
    }

    // Method to set the authority of the train model and pass the value to the train controller
    public void setAuthority(int authority) {

        controller.setAuthority((signalFailure) ?  -1 : authority);
        this.authority = authority;

        listeningExecutor.execute(() -> {
         //   if(authority == -3)
           //     logger.debug("Train {} <=     Authority: {}",this.trainID, authority);
         //   else
           //     logger.info("Train {} <=     Authority: {}",this.trainID, authority);
            notifyChange(AUTHORITY_PROPERTY, authority);
        });


    }

    // Method to set the value of the emergency brake
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;

        listeningExecutor.execute(() -> {
            notifyChange(EMERGENCYBRAKE_PROPERTY, brake);
        });

    }

    // Method to set the value of the service brake
    public void setServiceBrake(boolean brake) {

        this.serviceBrake = (!brakeFailure && brake);
        listeningExecutor.execute(() -> {
            notifyChange(SERVICEBRAKE_PROPERTY, !brakeFailure && brake);
        });
    }

    // Method to set the value of the engine power
    public void setPower(double power) {
        this.power = (powerFailure) ? 0 : power;
        listeningExecutor.execute(() -> {
            notifyChange(POWER_PROPERTY, Conversion.convertPower(this.power, WATTS, HORSEPOWER));
        });
    }

    // Method to set the actual speed of the train model
    public void setActualSpeed(double speed) {
        this.speed = speed;

        listeningExecutor.execute(() -> {
             notifyChange(ACTUALSPEED_PROPERTY, convertVelocity(this.speed, MPS, MPH));
        });
    }

    // Traditional Setters
    public void setBrakeFailure(boolean failure) { this.brakeFailure = failure;
        notifyChange(BRAKEFAILURE_PROPERTY, this.brakeFailure);
    }
    public void setPowerFailure(boolean failure) {
        this.powerFailure = failure;
        notifyChange(POWERFAILURE_PROPERTY, this.powerFailure);
    }
    public void setSignalFailure(boolean failure) {
        this.signalFailure = failure;
        notifyChange(SIGNALFAILURE_PROPERTY, this.signalFailure);
    }
    public void setGrade(double grade) {
        this.grade = grade;
        notifyChange(GRADE_PROPERTY, grade);
    }
    public void setNumCars(int numCars) {
        this.numCars = numCars;
        notifyChange(NUMCARS_PROPERTY, this.numCars);
    }
    public void setNumPassengers(int numPassengers) {
        this.numPassengers = numPassengers;
        notifyChange(NUMPASSENGERS_PROPERTY, this.numPassengers);
    }
    public void setCrewCount(int crewCount) {
        this.crewCount = crewCount;
        notifyChange(CREWCOUNT_PROPERTY, this.crewCount);
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        notifyChange(LEFTDOORS_PROPERTY, this.leftDoors);
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        notifyChange(RIGHTDOORS_PROPERTY, this.rightDoors);
    }
    public void setExtLights(boolean lights) {
        this.extLights = lights;
        listeningExecutor.execute(() -> {
            notifyChange(EXTLIGHTS_PROPERTY, this.extLights);
        });
    }
    public void setIntLights(boolean lights) {
        this.intLights = lights;
        notifyChange(INTLIGHTS_PROPERTY, this.intLights);
    }
    public void setSetTemperature(double temp) {
        this.setTemperature = temp;
        notifyChange(SETTEMPERATURE_PROPERTY, Conversion.convertTemperature(this.setTemperature, CELSIUS, FAHRENHEIT));
    }
    public void setRealTemperature(double temp) {
        this.realTemperature = temp;
        notifyChange(REALTEMPERATURE_PROPERTY, Conversion.convertTemperature(this.realTemperature, CELSIUS, FAHRENHEIT));
    }
    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
        notifyChange(ACCELERATION_PROPERTY, Conversion.convertAcceleration(this.acceleration, MPS2, FPS2));
    }
    public void setMass(double mass) {
        this.mass = mass;
        notifyChange(MASS_PROPERTY, Conversion.convertMass(this.mass, KILOGRAMS, TONS));
    }
    public void setDistanceTraveled(double distance) {
        this.distanceTraveled = distance;
        notifyChange(DISTANCETRAVELED_PROPERTY, Conversion.convertDistance(this.distanceTraveled, METERS, FEET));
    }
    public void setLength(double length) {
        this.length = length;
        notifyChange(LENGTH_PROPERTY, Conversion.convertDistance(this.length, METERS, FEET));
    }
    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }

    @Override
    public void updatePassengers() {
        int passengersEmbarked = track.embarkPassengers(this);
        int passengersDisembarked;
        if(this.numPassengers <= 0) {
            this.numPassengers = passengersEmbarked;
            passengersDisembarked = 0;
        }
        else {
            passengersDisembarked = ( r.nextInt(0, this.numPassengers) );
            if ((passengersEmbarked - passengersDisembarked) > Constants.MAX_PASSENGERS) {
                this.setNumPassengers(Constants.MAX_PASSENGERS);
            } else this.setNumPassengers(passengersEmbarked - passengersDisembarked);
        }
        track.disembarkPassengers(this, passengersDisembarked);
        this.setMass((Constants.EMPTY_TRAIN_MASS * this.numCars) + (Constants.PASSENGER_MASS * (this.crewCount + this.numPassengers)));
    }

    public void setValue(String propertyName, Object newValue){
        if(newValue == null)
            return;
        switch(propertyName){
            //case AUTHORITY_PROPERTY -> this.authority = (int)newValue;
            //case Properties.COMMANDSPEED_PROPERTY -> this.commandSpeed = convertVelocity((double)newValue, MPH, MPS);
            //case Properties.ACTUALSPEED_PROPERTY -> this.speed = convertVelocity((double)newValue, MPH, MPS);
            //case Properties.ACCELERATION_PROPERTY -> this.acceleration = convertAcceleration((double)newValue, FPS2, MPS2);
            //case Properties.POWER_PROPERTY -> this.power = convertPower((double)newValue, HORSEPOWER, WATTS);
            //case Properties.GRADE_PROPERTY -> this.grade = (double)newValue;
            //case Properties.SERVICEBRAKE_PROPERTY -> this.serviceBrake = (boolean)newValue;
            case Properties.EMERGENCYBRAKE_PROPERTY -> {this.emergencyBrake = (boolean)newValue; this.controller.setPassengerEBrake();}
            case Properties.BRAKEFAILURE_PROPERTY -> this.brakeFailure = (boolean)newValue;
            case Properties.POWERFAILURE_PROPERTY -> this.powerFailure = (boolean)newValue;
            case Properties.SIGNALFAILURE_PROPERTY -> this.signalFailure = (boolean)newValue;
            //case Properties.SETTEMPERATURE_PROPERTY -> this.setTemperature = convertTemperature((double)newValue, FAHRENHEIT, CELSIUS);
            //case Properties.REALTEMPERATURE_PROPERTY -> this.realTemperature = convertTemperature((double)newValue, FAHRENHEIT, CELSIUS);
            //case Properties.EXTLIGHTS_PROPERTY -> this.extLights = (boolean)newValue;
            //case Properties.INTLIGHTS_PROPERTY -> this.intLights = (boolean)newValue;
            //case Properties.LEFTDOORS_PROPERTY -> this.leftDoors = (boolean)newValue;
            //case Properties.RIGHTDOORS_PROPERTY -> this.rightDoors = (boolean)newValue;
            //case Properties.NUMCARS_PROPERTY -> this.numCars = (int)newValue;
            //case Properties.NUMPASSENGERS_PROPERTY -> this.numPassengers = (int)newValue;
            //case Properties.CREWCOUNT_PROPERTY -> this.crewCount = (int)newValue;
            //case Properties.MASS_PROPERTY -> this.mass = (double)newValue;
            //case Properties.DISTANCETRAVELED_PROPERTY -> this.distanceTraveled = convertDistance((double)newValue, FEET, METERS);
            //case Properties.LENGTH_PROPERTY -> this.length = convertDistance((double)newValue, FEET, METERS);
        }
    }

    @Override
    public void changeTimeDelta(int v) {
        logger.info("Cannot change time delta at runtime");
    }

    //Vital Getters
    public int getAuthority() {
        return (signalFailure)? -1 : this.authority;
    }
    public int getTrainNumber() {
        return this.trainID;
    }
    public double getCommandSpeed() {
        return (signalFailure)? -1 : this.commandSpeed;
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
        logger.warn("Train {} <=> Direction: {}", this.trainID, this.direction);
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