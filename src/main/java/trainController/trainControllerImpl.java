package trainController;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.Notifications;
import Utilities.Constants;
import trainModel.stubTrainModel;
import trainModel.trainModelImpl;
import trainModel.trainSubjectFactory;

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
public class trainControllerImpl implements TrainController, Notifications {
    private int authority;
    private int blocksToNextStation;
    private double commandSpeed;
    private double currentSpeed;
    private double overrideSpeed;
    private double speedLimit;

    private double Ki;
    private double Kp;

    //These are actually private, property mirror not necessary
    private double rollingError;
    private int samplingPeriod;
    private double prevError;
    private double error;

    private double power;
    private double grade;

    private boolean serviceBrake;
    private boolean emergencyBrake;
    private boolean automaticMode;

    private boolean internalLights;
    private boolean externalLights;
    private boolean leftDoors;
    private boolean rightDoors;
    private boolean announcements;
    private boolean signalFailure;
    private boolean brakeFailure;
    private boolean powerFailure;

    private boolean leftPlatform;
    private boolean rightPlatform;
    private boolean inTunnel;

    private double temperature;

    private String nextStationName;
    private final int trainID;
    private TrainModel train;

    private final trainControllerSubject subject;

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
    public trainControllerImpl(int trainID) {
        this.trainID = trainID;
        this.authority = 0;
        this.commandSpeed = 0.0;
        this.currentSpeed = 0.0;
        this.overrideSpeed = 0.0;
        this.speedLimit = 0.0;
        this.Ki = 1.0;
        this.Kp = 1.0;
        this.power = 0.0;
        this.serviceBrake = false;
        this.emergencyBrake = false;
        this.automaticMode = false;
        this.internalLights = false;
        this.externalLights = false;
        this.leftDoors = false;
        this.rightDoors = false;
        this.temperature = 0.0;
        this.inTunnel = false;
        this.rightPlatform = false;
        this.leftPlatform = false;
        this.subject = new trainControllerSubject(this);
        this.train = stubTrainModel.createstubTrainModel();
        this.samplingPeriod = 10;
        this.nextStationName = "Yard";
        this.grade = 0;
        this.rollingError = 0;
        this.prevError = 0;
        this.error = 0;
        this.power = 0;

        /**
         * An executor service that allows for scheduling and executing tasks in a single thread.
         */
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(this::calculatePower, samplingPeriod, samplingPeriod, TimeUnit.MILLISECONDS);
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
        this.setTemperature(train.getTemperature());
        this.setSignalFailure(train.getSignalFailure());
        this.setBrakeFailure(train.getBrakeFailure());
        this.setPowerFailure(train.getPowerFailure());
    }

    /**
     * This method is used to notify the change in the value of a property.
     * It prints the property name and the new value to the console and then notifies the subject of the change.
     * The notification to the subject is only done if the GUI is not currently being updated.
     *
     * @param propertyName  The name of the property that has changed.
     * @param newValue      The new value of the property.
     */
    public void notifyChange(String propertyName, Object newValue) {
        System.out.println("Variable: " + propertyName + " changed to " + newValue);
        if(!subject.isGUIUpdate) {
            subject.notifyChange(propertyName, newValue);
        }
    }

    /**
     * This method is similar to the above, but it includes an additional parameter to suppress console output.
     * If suppressOutput is true, the method will not print the property name and new value to the console.
     * Regardless of the value of suppressOutput, the method will still notify the subject of the change if the GUI is not currently being updated.
     *
     * @param propertyName  The name of the property that has changed.
     * @param newValue      The new value of the property.
     * @param suppressOutput If true, suppresses console output.
     */
    public void notifyChange(String propertyName, Object newValue, boolean suppressOutput) {
        if(!suppressOutput) {
            System.out.println("Variable: " + propertyName + " changed to " + newValue);
        }
        if(!subject.isGUIUpdate) {
            subject.notifyChange(propertyName, newValue);
        }
    }

    //Functions called by the internal logic to notify of changes
    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        notifyChange(automaticMode_p, mode);
    }
    public void setAuthority(int authority) {
        this.authority = authority;
        notifyChange(authority_p, authority);
    }
    public void setOverrideSpeed(double speed) {
        this.overrideSpeed = speed;
        notifyChange(overrideSpeed_p, speed);
        //calculatePower();
    }
    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        notifyChange(commandSpeed_p, speed);
        //calculatePower();
    }
    public void setSpeed(double speed) {
        this.currentSpeed = speed;
        notifyChange(currentSpeed_p, speed, true);
    }
    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        notifyChange(serviceBrake_p, brake);
    }
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        notifyChange(emergencyBrake_p, brake);
    }
    public void setKi(double Ki) {
        this.Ki = Ki;
        notifyChange(Ki_p, Ki);
    }
    public void setKp(double Kp) {
        this.Kp = Kp;
        notifyChange(Kp_p, Kp);
    }
    public void setPower(double power) {
        this.power = power;
        subject.updateFromLogic(() -> {
            notifyChange(power_p, power, true);
        });
    }
    public void setIntLights(boolean lights) {
        this.internalLights = lights;
        notifyChange(intLights_p, lights); // This might've been the issue interiorLights -> intLights
    }
    public void setExtLights(boolean lights) {
        this.externalLights = lights;

        notifyChange(extLights_p, lights); // This might've been the issue exteriorLights -> extLights
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        notifyChange(leftDoors_p, doors);
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        notifyChange(rightDoors_p, doors);
    }
    public void setTemperature(double temp) {
        this.temperature = temp;
        notifyChange(temperature_p, temp);
    }
    public void setAnnouncements(boolean announcements) {
        this.announcements = announcements;
        notifyChange(announcements_p, announcements);
    }
    public void setSignalFailure(boolean signalFailure) {
        this.signalFailure = signalFailure;
        notifyChange(signalFailure_p, signalFailure);
    }
    public void setBrakeFailure(boolean brakeFailure) {
        this.brakeFailure = brakeFailure;
        notifyChange(brakeFailure_p, brakeFailure);
    }
    public void setPowerFailure(boolean powerFailure) {
        this.powerFailure = powerFailure;
        notifyChange(powerFailure_p, powerFailure);
    }
    public void setInTunnel(boolean tunnel){
        this.inTunnel = tunnel;
        notifyChange(inTunnel_p,tunnel);
    }
    public void setLeftPlatform(boolean platform){
        this.leftPlatform = platform;
        notifyChange(leftPlatform_p,platform);
    }
    public void setRightPlatform(boolean platform){
        this.rightPlatform = platform;
        notifyChange(rightPlatform_p,platform);
    }
    public void setSamplingPeriod(int period){
        this.samplingPeriod = period;
        notifyChange(samplingPeriod_p,period);
    }
    public void setSpeedLimit(double speedLimit){
        this.speedLimit = speedLimit;
        notifyChange(speedLimit_p,speedLimit);
    }
    public void setNextStationName(String name){
        this.nextStationName = name;
        notifyChange(nextStationName_p,name);
    }
    public void setGrade(double newValue) {
        this.grade = newValue;
        notifyChange(grade_p,newValue);
    }

    /**
     * Why wouldn't we just put the setter in here?  Why have both?  Not sure.
     */
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
            case Properties.automaticMode_p -> setAutomaticMode((boolean) newValue);
            case authority_p -> setAuthority((int) newValue);
            case overrideSpeed_p -> setOverrideSpeed((double) newValue);
            case commandSpeed_p -> setCommandSpeed((double) newValue);
            case currentSpeed_p -> setSpeed((double) newValue);
            case serviceBrake_p -> setServiceBrake((boolean) newValue);
            case emergencyBrake_p -> setEmergencyBrake((boolean) newValue);
            case Ki_p -> setKi((double) newValue);
            case Kp_p -> setKp((double) newValue);
            case power_p -> setPower((double) newValue);
            case intLights_p -> setIntLights((boolean) newValue);
            case extLights_p -> setExtLights((boolean) newValue);
            case leftDoors_p -> setLeftDoors((boolean) newValue);
            case rightDoors_p -> setRightDoors((boolean) newValue);
            case temperature_p -> setTemperature((double) newValue);
            case announcements_p -> setAnnouncements((boolean) newValue);
            case signalFailure_p -> setSignalFailure((boolean) newValue);
            case brakeFailure_p -> setBrakeFailure((boolean) newValue);
            case powerFailure_p -> setPowerFailure((boolean) newValue);
            case inTunnel_p -> setInTunnel((boolean) newValue);
            case leftPlatform_p -> setLeftPlatform((boolean) newValue);
            case rightPlatform_p -> setRightPlatform((boolean) newValue);
            case samplingPeriod_p -> setSamplingPeriod((int) newValue);
            case speedLimit_p -> setSpeedLimit((double) newValue);
            case nextStationName_p -> setNextStationName((String) newValue);
            case grade_p -> setGrade((double) newValue);
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
    public trainControllerSubject getSubject() {
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
    public void calculatePower(){
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
        } else {
            accel = pow / train.getWeightKG();
        }

        currSpeed += accel * (float)samplingPeriod/1000;
        if(currSpeed < 0){
            currSpeed = 0;
        }

        this.setSpeed(convertVelocity(currSpeed, MPS, MPH));
        this.setPower(convertPower(pow, WATTS, HORSEPOWER));
    }

}