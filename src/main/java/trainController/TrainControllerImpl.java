package trainController;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.GUIModifiable;
import Utilities.Constants;
import trainModel.stubTrainModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static Utilities.Constants.EMERGENCY_BRAKE_DECELERATION;
import static Utilities.Constants.SERVICE_BRAKE_DECELERATION;
import static Utilities.Conversion.convertPower;
import static Utilities.Conversion.convertVelocity;
import static Utilities.Conversion.powerUnits.HORSEPOWER;
import static Utilities.Conversion.powerUnits.WATTS;
import static Utilities.Conversion.velocityUnit.MPH;
import static Utilities.Conversion.velocityUnit.MPS;
import static trainController.Properties.*;

/**
 * This is the constructor for the trainControllerImpl class.
 * It initializes all the properties of the trainControllerImpl object.
 * It sets the trainID to the passed value and initializes all other properties to their default values.
 * It also creates a new trainControllerSubject object and assigns it to the subject property.
 * A stubTrainModel object is created and assigned to the train property.
 * The constructor also schedules the calculatePower method to be called at fixed rate intervals.
 * The rate is determined by the samplingPeriod property.
 *
 */
public class TrainControllerImpl implements TrainController, GUIModifiable {
    private final int trainID;
    private final TrainControllerSubject subject;
    private TrainModel train;

    private volatile int samplingPeriod = 10;

    private double commandSpeed = 0.0, currentSpeed = 0.0, overrideSpeed = 0.0,
            speedLimit = 0.0, Ki = 1.0, Kp = 1.0, power = 0.0, grade = 0.0,
            temperature = 0.0, rollingError = 0.0, prevError = 0.0, error = 0.0;

    private final int blocksToNextStation = 10;
    private int authority = 0;

    private boolean serviceBrake = false, emergencyBrake = false, automaticMode = false,
            internalLights = false, externalLights = false, leftDoors = false,
            rightDoors = false, announcements = false, signalFailure = false,
            brakeFailure = false, powerFailure = false, leftPlatform = false,
            rightPlatform = false, inTunnel = false;

    private String nextStationName;


    /**
     * This is the constructor for the trainControllerImpl class.
     * It initializes all the properties of the trainControllerImpl object.
     * It sets the trainID to the passed value and initializes all other properties to their default values.
     * It also creates a new trainControllerSubject object and assigns it to the subject property.
     * A stubTrainModel object is created and assigned to the train property.
     * The constructor also schedules the calculatePower method to be called at fixed rate intervals.
     * The rate is determined by the samplingPeriod property.
     *
     * @param trainID  The ID of the train to be controlled by this trainControllerImpl object.
     */
    public TrainControllerImpl(int trainID) {
        this.trainID = trainID;
        this.subject = new TrainControllerSubject(this);
        this.train = stubTrainModel.createstubTrainModel();
        this.nextStationName = "Yard";
    }

    /**
     * This method is used to assign a TrainModel object to the trainControllerImpl.
     * It first assigns the passed TrainModel object to the train variable of the trainControllerImpl.
     * Then it sets the service brake, emergency brake, internal lights, external lights, left doors, right doors, temperature, signal failure, brake failure, and power failure of the trainControllerImpl to the corresponding values of the passed TrainModel object.
     *
     * @param train  The TrainModel object to be assigned to the trainControllerImpl.
     */
    public void assignTrainModel(TrainModel train) {
        this.train = train;
        this.setServiceBrake(train.getServiceBrake());
        this.setEmergencyBrake(train.getEmergencyBrake());
        this.setIntLights(train.getIntLights());
        this.setExtLights(train.getExtLights());
        this.setLeftDoors(train.getLeftDoors());
        this.setRightDoors(train.getRightDoors());
        //this.setTemperature(train.getTemperature());
        this.setSignalFailure(train.getSignalFailure());
        this.setBrakeFailure(train.getBrakeFailure());
        this.setPowerFailure(train.getPowerFailure());
    }


    //Functions called by the internal logic to notify of changes
    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        subject.notifyChange(AUTOMATIC_MODE_PROPERTY, mode);
    }
    public void setAuthority(int authority) {
        this.authority = authority;
        subject.notifyChange(AUTHORITY_PROPERTY, authority);
    }
    public void setOverrideSpeed(double speed) {
        this.overrideSpeed = speed;
        subject.notifyChange(OVERRIDE_SPEED_PROPERTY, speed);
        //calculatePower();
    }
    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        subject.notifyChange(COMMAND_SPEED_PROPERTY, speed);
        //calculatePower();
    }
    public void setSpeed(double speed) {
        this.currentSpeed = speed;
        subject.notifyChange(CURRENT_SPEED_PROPERTY, speed);
    }
    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        subject.notifyChange(SERVICE_BRAKE_PROPERTY, brake);
    }
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        subject.notifyChange(EMERGENCY_BRAKE_PROPERTY, brake);
    }
    public void setKi(double Ki) {
        this.Ki = Ki;
        subject.notifyChange(KI_PROPERTY, Ki);
    }
    public void setKp(double Kp) {
        this.Kp = Kp;
        subject.notifyChange(KP_PROPERTY, Kp);
    }
    public void setPower(double power) {
        this.power = power;
        subject.notifyChange(POWER_PROPERTY, power);
    }
    public void setIntLights(boolean lights) {
        this.internalLights = lights;
        subject.notifyChange(INT_LIGHTS_PROPERTY, lights); // This might've been the issue interiorLights -> intLights
    }
    public void setExtLights(boolean lights) {
        this.externalLights = lights;

        subject.notifyChange(EXT_LIGHTS_PROPERTY, lights); // This might've been the issue exteriorLights -> extLights
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        subject.notifyChange(LEFT_DOORS_PROPERTY, doors);
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        subject.notifyChange(RIGHT_DOORS_PROPERTY, doors);
    }
    public void setTemperature(double temp) {
        this.temperature = temp;
        subject.notifyChange(TEMPERATURE_PROPERTY, temp);
    }
    public void setAnnouncements(boolean announcements) {
        this.announcements = announcements;
        subject.notifyChange(ANNOUNCEMENTS_PROPERTY, announcements);
    }
    public void setSignalFailure(boolean signalFailure) {
        this.signalFailure = signalFailure;
        subject.notifyChange(SIGNAL_FAILURE_PROPERTY, signalFailure);
    }
    public void setBrakeFailure(boolean brakeFailure) {
        this.brakeFailure = brakeFailure;
        subject.notifyChange(BRAKE_FAILURE_PROPERTY, brakeFailure);
    }
    public void setPowerFailure(boolean powerFailure) {
        this.powerFailure = powerFailure;
        subject.notifyChange(POWER_FAILURE_PROPERTY, powerFailure);
    }
    public void setInTunnel(boolean tunnel){
        this.inTunnel = tunnel;
        subject.notifyChange(IN_TUNNEL_PROPERTY,tunnel);
    }
    public void setLeftPlatform(boolean platform){
        this.leftPlatform = platform;
        subject.notifyChange(LEFT_PLATFORM_PROPERTY,platform);
    }
    public void setRightPlatform(boolean platform){
        this.rightPlatform = platform;
        subject.notifyChange(RIGHT_PLATFORM_PROPERTY,platform);
    }
    public void setSamplingPeriod(int period){
        this.samplingPeriod = period;
        subject.notifyChange(SAMPLING_PERIOD_PROPERTY,period);
    }
    public void setSpeedLimit(double speedLimit){
        this.speedLimit = speedLimit;
        subject.notifyChange(SPEED_LIMIT_PROPERTY,speedLimit);
    }
    public void setNextStationName(String name){
        this.nextStationName = name;
        subject.notifyChange(NEXT_STATION_PROPERTY,name);
    }
    public void setGrade(double newValue) {
        this.grade = newValue;
        subject.notifyChange(GRADE_PROPERTY,newValue);
    }

    /**
     * This method is used to set the value of a property based on the property name.
     * It uses a switch statement to determine which property to set.
     * The method casts the newValue parameter to the appropriate type based on the property.
     * If the property name is not found, it prints an error message to the console.
     * After setting the property, it does not calculate power.
     *
     * @param propertyName  The name of the property to be set.
     * @param newValue      The new value to be set for the property.
     */
    public void setValue(String propertyName, Object newValue) {
        switch (propertyName) {
            case Properties.AUTOMATIC_MODE_PROPERTY -> this.automaticMode = (boolean) newValue;
            case AUTHORITY_PROPERTY -> this.authority = (int) newValue;
            case OVERRIDE_SPEED_PROPERTY -> this.overrideSpeed = (double) newValue;
            case COMMAND_SPEED_PROPERTY -> this.commandSpeed = (double) newValue;
            case CURRENT_SPEED_PROPERTY -> this.currentSpeed = (double) newValue;
            case SERVICE_BRAKE_PROPERTY -> this.serviceBrake = (boolean) newValue;
            case EMERGENCY_BRAKE_PROPERTY -> this.emergencyBrake = (boolean) newValue;
            case KI_PROPERTY -> this.Ki = (double) newValue;
            case KP_PROPERTY -> this.Kp = (double) newValue;
            case POWER_PROPERTY -> this.power = (double) newValue;
            case INT_LIGHTS_PROPERTY -> this.internalLights = (boolean) newValue;
            case EXT_LIGHTS_PROPERTY -> this.externalLights = (boolean) newValue;
            case LEFT_DOORS_PROPERTY -> this.leftDoors = (boolean) newValue;
            case RIGHT_DOORS_PROPERTY -> this.rightDoors = (boolean) newValue;
            case TEMPERATURE_PROPERTY -> this.temperature = (double) newValue;
            case ANNOUNCEMENTS_PROPERTY -> this.announcements = (boolean) newValue;
            case SIGNAL_FAILURE_PROPERTY -> this.signalFailure = (boolean) newValue;
            case BRAKE_FAILURE_PROPERTY -> this.brakeFailure = (boolean) newValue;
            case POWER_FAILURE_PROPERTY -> this.powerFailure = (boolean) newValue;
            case IN_TUNNEL_PROPERTY -> this.inTunnel = (boolean) newValue;
            case LEFT_PLATFORM_PROPERTY -> this.leftPlatform = (boolean) newValue;
            case RIGHT_PLATFORM_PROPERTY -> this.rightPlatform = (boolean) newValue;
            case SAMPLING_PERIOD_PROPERTY -> this.samplingPeriod = (int) newValue;
            case SPEED_LIMIT_PROPERTY -> this.speedLimit = (double) newValue;
            case NEXT_STATION_PROPERTY -> this.nextStationName = (String) newValue;
            case GRADE_PROPERTY -> this.grade = (double) newValue;
            default -> System.err.println("Property " + propertyName + " not found");
        }
        //calculatePower();
    }


    //-----------------Getters-----------------


    public int  getID() {
        return this.trainID;
    }
    public double  getSpeed() {
        return this.currentSpeed;
    }
    public double  getAcceleration() {
        return this.train.getAcceleration();
    }
    public int  getSamplingPeriod(){return this.samplingPeriod;}
    public double  getPower() {
        return this.power;
    }
    public boolean getServiceBrake() {
        return this.serviceBrake;
    }
    public boolean getEmergencyBrake() {
        return this.emergencyBrake;
    }
    public double  getCommandSpeed() {
        return this.commandSpeed;
    }
    public int     getAuthority() {
        return this.authority;
    }
    public double  getKi() {
        return this.Ki;
    }
    public double  getKp() {
        return this.Kp;
    }
    public double  getOverrideSpeed() {
        return this.overrideSpeed;
    }
    public boolean getAutomaticMode() {
        return this.automaticMode;
    }
    public TrainControllerSubject getSubject() {
        return this.subject;
    }
    public boolean getExtLights() {
        return this.externalLights;
    }
    public boolean getIntLights() {
        return this.internalLights;
    }
    public boolean getAnnouncements() {
        return this.announcements;
    }
    public boolean getSignalFailure() {
        return this.signalFailure;
    }
    public boolean getBrakeFailure() {
        return this.brakeFailure;
    }
    public boolean getPowerFailure() {
        return this.powerFailure;
    }
    public double  getSpeedLimit() {
        return this.speedLimit;
    }
    public boolean getLeftDoors() {
        return this.leftDoors;
    }
    public boolean getRightDoors() {
        return this.rightDoors;
    }
    public double  getTemperature() {
        return this.temperature;
    }
    public String  getStationName(){
        return this.nextStationName;
    }
    public boolean getLeftPlatform(){return this.leftPlatform;}
    public boolean getRightPlatform(){return this.rightPlatform;}
    public boolean getInTunnel(){return this.inTunnel;}
    public double  getGrade(){return this.grade;}

    /**
     * Profetta Notes:
     * The train controller is meant to stop if a command speed is not sent.  The wayside is not able to send a corrected speed,
     * rather it just chooses not to send a speed at all, and if the train does not receive a speed, it should stop.
     */
    /**
     * This method is used to calculate the power needed for the train.
     * It first determines the set speed based on whether the train is in automatic mode or not.
     * Then it calculates the current speed and initializes acceleration to 0.
     * It calculates the error between the set speed and the current speed, and updates the rolling error.
     * The power is then calculated using the proportional-integral (PI) controller formula.
     * If the calculated power exceeds the maximum power, it is set to the maximum power.
     * If the train is in automatic mode and the calculated power is less than 0, the power is set to 0 and the service brake is activated.
     * If the service brake is active and the calculated power is greater than 0, the service brake is deactivated.
     * If the emergency brake or service brake is active, or there is a power failure, or the calculated power is less than 0, the power is set to 0.
     * If the emergency brake is active, the acceleration is set to the emergency brake deceleration.
     * If the service brake is active, the acceleration is set to the service brake deceleration.
     * Otherwise, the acceleration is calculated by dividing the power by the weight of the train.
     * The current speed is then updated based on the calculated acceleration.
     * If the current speed is less than 0, it is set to 0.
     * Finally, the speed and power of the train are updated.
     */
    public double calculatePower(double currentSpeed){
        // Convert Units
        double setSpeed, currSpeed, pow, accel;

        if (automaticMode){
            setSpeed = convertVelocity(commandSpeed, MPH, MPS);
        }
        else{
            setSpeed = convertVelocity(overrideSpeed, MPH, MPS);
        }

        currSpeed = convertVelocity(currentSpeed, MPH, MPS);
        accel = 0;


        error = setSpeed - currSpeed;
        rollingError += (float)samplingPeriod/2000 * (error + prevError);
        prevError = error;

        pow = Kp * error + Ki * rollingError;
        if(pow > Constants.MAX_POWER) {
            pow = convertPower(Constants.MAX_POWER,HORSEPOWER, WATTS);
        }

        if(automaticMode && (pow < 0)){
            pow = 0;
            setServiceBrake(true);
        }else if(serviceBrake && (pow > 0)){
            setServiceBrake(false);
        }

        if(emergencyBrake || serviceBrake || powerFailure || (pow < 0)) {
            pow = 0;
            if(emergencyBrake){
                accel = -1 * EMERGENCY_BRAKE_DECELERATION;
            }else if(serviceBrake) {
                accel = -1 * SERVICE_BRAKE_DECELERATION;
            }
        } else{
            double mass = train.getWeightKG();
            if(mass <= 0){
                mass = 1;
            }
            accel = pow / mass;
        }

        currSpeed += accel * (float)samplingPeriod/1000;
        if(currSpeed < 0){
            currSpeed = 0;
        }

        this.setSpeed(convertVelocity(currSpeed, MPS, MPH));
        this.setPower(convertPower(pow, WATTS, HORSEPOWER));
    }

}