package trainController;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.GUIModifiableEnum;
import Utilities.Constants;
import Utilities.Records.Beacon;
import Utilities.Records.UpdatedTrainValues;
import javafx.scene.control.Alert;
import trainModel.TrainModelImpl;

import static Utilities.Constants.EMERGENCY_BRAKE_DECELERATION;
import static Utilities.Constants.SERVICE_BRAKE_DECELERATION;
import static Utilities.Conversion.convertPower;
import static Utilities.Conversion.convertVelocity;
import static Utilities.Conversion.powerUnits.HORSEPOWER;
import static Utilities.Conversion.powerUnits.WATTS;
import static Utilities.Conversion.velocityUnit.MPH;
import static Utilities.Conversion.velocityUnit.MPS;
import static trainController.Controller_Property.*;

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
public class TrainControllerImpl implements TrainController, GUIModifiableEnum<Controller_Property> {
    private final int trainID;
    private final TrainControllerSubject subject;
    private final TrainModel train;
    private Beacon beacon;

    private volatile int samplingPeriod = 10;

    private double commandSpeed = 0.0, currentSpeed = 0.0, overrideSpeed = 0.0,
            speedLimit = 0.0, Ki = 100.0, Kp = 100.0, power = 0.0, grade = 0.0,
            setTemperature = 0.0, currentTemperature = 0.0, rollingError = 0.0, prevError = 0.0, error = 0.0;

    private int authority = 0;

    private boolean serviceBrake = false, emergencyBrake = false, automaticMode = false,
            internalLights = false, externalLights = false, leftDoors = false,
            rightDoors = false, announcements = false, signalFailure = false,
            brakeFailure = false, powerFailure = false, leftPlatform = false,
            rightPlatform = false, inTunnel = false, passengerEngageEBrake = false;

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
     * @param train  The train to be controlled by this trainControllerImpl object.
     */
    public TrainControllerImpl(TrainModel train, int trainID) {
        this.trainID = trainID;
        this.train = train;
        this.subject = new TrainControllerSubject(this);
        populateTrainValues(train);
        this.nextStationName = "Yard";
    }

    public TrainControllerImpl() {
        this.trainID = -1;
        this.train = new TrainModelImpl();
        this.subject = new TrainControllerSubject(this);
        this.nextStationName = "Yard";
    }

    /**
     * This method is used to assign a TrainModel object to the trainControllerImpl.
     * It first assigns the passed TrainModel object to the train variable of the trainControllerImpl.
     * Then it sets the service brake, emergency brake, internal lights, external lights, left doors, right doors, temperature, signal failure, brake failure, and power failure of the trainControllerImpl to the corresponding values of the passed TrainModel object.
     *
     * @param train  The TrainModel object to be assigned to the trainControllerImpl.
     */
    void populateTrainValues(TrainModel train) {
        this.setServiceBrake(train.getServiceBrake());
        this.setEmergencyBrake(train.getEmergencyBrake());
        this.setIntLights(train.getIntLights());
        this.setExtLights(train.getExtLights());
        this.setLeftDoors(train.getLeftDoors());
        this.setRightDoors(train.getRightDoors());
        this.setSignalFailure(train.getSignalFailure());
        this.setBrakeFailure(train.getBrakeFailure());
        this.setPowerFailure(train.getPowerFailure());
        this.setCurrentTemperature((train.getRealTemperature()));
    }


    //Functions called by the internal logic to notify of changes
    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        subject.notifyChange(AUTOMATIC_MODE.getPropertyName(), mode);
    }
    public void setAuthority(int authority) {
        this.authority = authority;
        subject.notifyChange(AUTHORITY.getPropertyName(), authority);
    }
    public void setOverrideSpeed(double speed) {
        this.overrideSpeed = speed;
        subject.notifyChange(OVERRIDE_SPEED.getPropertyName(), speed);
        //calculatePower();
    }
    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        subject.notifyChange(COMMAND_SPEED.getPropertyName(), speed);
        //calculatePower();
    }
    public void setSpeed(double speed) {
        this.currentSpeed = speed;
        subject.notifyChange(CURRENT_SPEED.getPropertyName(), speed);
    }
    private void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        subject.notifyChange(SERVICE_BRAKE.getPropertyName(), brake);
    }
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        subject.notifyChange(EMERGENCY_BRAKE.getPropertyName(), brake);
    }
    public void setKi(double Ki) {
        this.Ki = Ki;
        subject.notifyChange(KI.getPropertyName(), Ki);
    }
    public void setKp(double Kp) {
        this.Kp = Kp;
        subject.notifyChange(KP.getPropertyName(), Kp);
    }
    public void setPower(double power) {
        this.power = power;
        subject.notifyChange(POWER.getPropertyName(), power);
    }
    public void setIntLights(boolean lights) {
        this.internalLights = lights;
        subject.notifyChange(INT_LIGHTS.getPropertyName(), lights); // This might've been the issue interiorLights -> intLights
    }
    public void setExtLights(boolean lights) {
        this.externalLights = lights;
        subject.notifyChange(EXT_LIGHTS.getPropertyName(), lights); // This might've been the issue exteriorLights -> extLights
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        subject.notifyChange(LEFT_DOORS.getPropertyName(), doors);
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        subject.notifyChange(RIGHT_DOORS.getPropertyName(), doors);
    }
    public void setSetTemperature(double temp) {
        this.setTemperature = temp;
        subject.notifyChange(SET_TEMPERATURE.getPropertyName(), temp);
    }
    public void setCurrentTemperature(double temp){
        this.currentTemperature = temp;
        subject.notifyChange(CURRENT_TEMPERATURE.getPropertyName(),temp);
    }
    public void setAnnouncements(boolean announcements) {
        this.announcements = announcements;
        subject.notifyChange(ANNOUNCEMENTS.getPropertyName(), announcements);
    }
    public void setSignalFailure(boolean signalFailure) {
        this.signalFailure = signalFailure;
        subject.notifyChange(SIGNAL_FAILURE.getPropertyName(), signalFailure);
    }
    public void setBrakeFailure(boolean brakeFailure) {
        this.brakeFailure = brakeFailure;
        subject.notifyChange(BRAKE_FAILURE.getPropertyName(), brakeFailure);
    }
    public void setPowerFailure(boolean powerFailure) {
        this.powerFailure = powerFailure;
        subject.notifyChange(POWER_FAILURE.getPropertyName(), powerFailure);
    }
    public void setInTunnel(boolean tunnel){
        this.inTunnel = tunnel;
        subject.notifyChange(IN_TUNNEL.getPropertyName(),tunnel);
    }
    public void setLeftPlatform(boolean platform){
        this.leftPlatform = platform;
        subject.notifyChange(LEFT_PLATFORM.getPropertyName(),platform);
    }
    public void setRightPlatform(boolean platform){
        this.rightPlatform = platform;
        subject.notifyChange(RIGHT_PLATFORM.getPropertyName(),platform);
    }
    public void setSamplingPeriod(int period){
        this.samplingPeriod = period;
        subject.notifyChange(SAMPLING_PERIOD.getPropertyName(),period);
    }
    public void setSpeedLimit(double speedLimit){
        this.speedLimit = speedLimit;
        subject.notifyChange(SPEED_LIMIT.getPropertyName(),speedLimit);
    }
    public void setNextStationName(String name){
        this.nextStationName = name;
        subject.notifyChange(NEXT_STATION.getPropertyName(),name);
    }
    public void setGrade(double newValue) {
        this.grade = newValue;
        subject.notifyChange(GRADE.getPropertyName(),newValue);
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

    public void setValue(Controller_Property propertyName, Object newValue) {
        System.out.println("Value " + propertyName + " set to " + newValue);
        switch (propertyName) {
            case AUTOMATIC_MODE -> this.automaticMode = (boolean) newValue;
            case AUTHORITY -> this.authority = (int) newValue;
            case OVERRIDE_SPEED -> this.overrideSpeed = (double) newValue;
            case COMMAND_SPEED -> this.commandSpeed = (double) newValue;
            case CURRENT_SPEED -> this.currentSpeed = (double) newValue;
            case SERVICE_BRAKE -> this.serviceBrake = (boolean) newValue;
            case EMERGENCY_BRAKE -> this.emergencyBrake = (boolean) newValue;
            case KI -> this.Ki = (double) newValue;
            case KP -> this.Kp = (double) newValue;
            case POWER -> this.power = (double) newValue;
            case INT_LIGHTS -> this.internalLights = (boolean) newValue;
            case EXT_LIGHTS -> this.externalLights = (boolean) newValue;
            case LEFT_DOORS -> this.leftDoors = (boolean) newValue;
            case RIGHT_DOORS -> this.rightDoors = (boolean) newValue;
            case SET_TEMPERATURE -> this.setTemperature = (double) newValue;
            case CURRENT_TEMPERATURE ->  this.currentTemperature = (double) newValue;
            case ANNOUNCEMENTS -> this.announcements = (boolean) newValue;
            case SIGNAL_FAILURE -> this.signalFailure = (boolean) newValue;
            case BRAKE_FAILURE -> this.brakeFailure = (boolean) newValue;
            case POWER_FAILURE -> this.powerFailure = (boolean) newValue;
            case IN_TUNNEL -> this.inTunnel = (boolean) newValue;
            case LEFT_PLATFORM -> this.leftPlatform = (boolean) newValue;
            case RIGHT_PLATFORM -> this.rightPlatform = (boolean) newValue;
            case SAMPLING_PERIOD -> this.samplingPeriod = (int) newValue;
            case SPEED_LIMIT -> this.speedLimit = (double) newValue;
            case NEXT_STATION -> this.nextStationName = (String) newValue;
            case GRADE -> this.grade = (double) newValue;
            case TRAIN_ID -> System.out.println("Train ID is a read-only property");
            case ERROR -> System.out.println("Error is a read-only property");
            default -> System.err.println("Property " + propertyName + " not found");
        }
    }


    //-----------------Getters-----------------

    @Override
    public TrainModel getTrain() {
        return train;
    }
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

    @Override
    public void delete() {
        this.subject.delete();
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
    public double  getSetTemperature() {
        return this.setTemperature;
    }
    public double  getCurrentTemperature() {
        return this.currentTemperature;
    }
    public String  getStationName(){
        return this.nextStationName;
    }
    public boolean getLeftPlatform(){return this.leftPlatform;}
    public boolean getRightPlatform(){return this.rightPlatform;}
    public boolean getInTunnel(){return this.inTunnel;}
    public double  getGrade(){return this.grade;}

    //TODO: Implement this method
    @Override
    public void updateBeacon(Beacon beacon) {

    }



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
        rollingError += (double)samplingPeriod/2000 * (error + prevError);
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
            double mass = train.getMass();
            if(mass <= 0){
                mass = 1;
            }
            accel = pow / mass;
        }

        currSpeed += accel * (double)samplingPeriod/1000;
        if(currSpeed < 0){
            currSpeed = 0;
        }

        this.setSpeed(convertVelocity(currSpeed, MPS, MPH));
        this.setPower(convertPower(pow, WATTS, HORSEPOWER));
        return setSpeed;
    }


    /**
     *  onBlock()
     */
    public void onBlock(){
        //TODO:

        // Update Block by Block

        // Get Specific Block Info
        checkTunnel();

    }
    /**
     * onStation()
     */
    public void onStation(){

        // Get Block info


        //TODO: Stopping train in middle of a block


        //This will eventually go inside a check to see if we are stopped at a station.
        if (train.getSpeed() == 0){                             // Check if train is stopped

            // Hopefully wont affect make announcment implementation in manager
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Arrival");
            alert.setHeaderText(null);
            alert.setContentText("Arriving at " + nextStationName); // Note that nextStationName will be updated at some point
            alert.showAndWait();

            if (this.leftPlatform) this.setLeftDoors(true);     // Open left doors
            if (this.rightPlatform) this.setRightDoors(true);   // Open right doors

            //wait(60000);

            this.setLeftDoors(false);
            this.setRightDoors(false);
        }
    }


    // Implement Crossing tunnel
    public void checkTunnel(){
        // Get block information somehow

        if(inTunnel) {
            setIntLights(true);
            setExtLights(true);

        }
        else{
            setIntLights(false);
            setExtLights(false);

        }
    }

    // Failure Management with Steven He
    public boolean checkBrakeFailure(){

        // Failures occur when the brake states in the train controller do not match with brake states in the train model
        if (this.serviceBrake && !train.getServiceBrake()) this.setBrakeFailure(true);
        if (this.emergencyBrake && !train.getEmergencyBrake()) this.setBrakeFailure(true);

        // If true, pick a god and pray

        return brakeFailure;
    }
    public boolean checkSignalFailure(){
        // Failure occur when the commanded speed or commanded authority is -1
        this.setCommandSpeed(train.getCommandSpeed());
        this.setAuthority(train.getAuthority());

        if (commandSpeed == -1 && authority == -1) this.setSignalFailure(true);

        //If true, activate emergency brake
        if (signalFailure) this.setEmergencyBrake(true);

        return signalFailure;
    }
    public boolean checkPowerFailure(){
        // Failure occurs when train model's set power equals 0 but we are outputting power

        if (this.power > 0 && train.getPower() == 0) this.setPowerFailure(true);

        // If true, activate emergency brake
        if (this.powerFailure) this.setEmergencyBrake(true);

        return this.powerFailure;
    }

    @Override
    public void setValue(String propertyName, Object newValue) {
        setValue(Controller_Property.valueOf(propertyName.toUpperCase()), newValue);
    }
    @Override
    public UpdatedTrainValues sendUpdatedTrainValues(){

        //This is a bandaged solution
        this.setCurrentTemperature(train.getRealTemperature());
        if (train.getEmergencyBrake() && !passengerEngageEBrake){
            passengerEngageEBrake = true;
            this.setEmergencyBrake((passengerEngageEBrake));
        }
        else if (!emergencyBrake){
            passengerEngageEBrake = false;
            this.setEmergencyBrake(false);
        }

        calculatePower(this.currentSpeed);

        return new UpdatedTrainValues(
                this.power,
                this.serviceBrake,
                this.emergencyBrake,
                this.setTemperature,
                this.internalLights,
                this.externalLights,
                this.leftDoors,
                this.rightDoors
        );
    }


}