package trainController;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Records.BasicBlock;
import Utilities.Records.Beacon;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainController.ControllerBlocks.ControllerBlock;
import trainModel.NullTrain;
import trainModel.Records.UpdatedTrainValues;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;

import static Utilities.Constants.*;
import static Utilities.Conversion.*;
import static Utilities.Conversion.powerUnits.HORSEPOWER;
import static Utilities.Conversion.powerUnits.WATTS;
import static Utilities.Conversion.temperatureUnit.CELSIUS;
import static Utilities.Conversion.temperatureUnit.FAHRENHEIT;
import static Utilities.Conversion.velocityUnit.MPH;
import static Utilities.Conversion.velocityUnit.MPS;
import static Utilities.Enums.Lines.NULL;
import static trainController.ControllerProperty.*;

//TODO: The Subject is now entirely storing imperial units, and here we have only metric units. We convert them all here, and it is consistent.

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
public class TrainControllerImpl implements TrainController{
    private static final double TIME_STEP = TIME_STEP_S;
    private static final double DEAD_BAND = 0.05;

    //private final NullTrain nullTrain = new NullTrain();

    private ControllerBlock currentBlock;
    private Beacon currentBeacon = null;

    private static final Logger logger = LoggerFactory.getLogger(TrainControllerImpl.class);

    //Internal Metric
    private double commandSpeed = 0.0;
    private double currentSpeed = 0.0;
    private double overrideSpeed = 0.0;
    private double speedLimit = 0.0;
    private double Ki = 20;
    private double Kp = 100;
    private double power = 0.0;
    private double grade = 0.0;
    private double setTemperature = 0.0;
    private double currentTemperature = 0.0;
    private double rollingError = 0.0;

    private boolean waysideStop;


    private int authority = 0;

    private boolean serviceBrake = false, emergencyBrake = false, automaticMode = true,
            internalLights = false, externalLights = false, leftDoors = false,
            rightDoors = false, announcements = false, signalFailure = false,
            brakeFailure = false, powerFailure = false, leftPlatform = false,
            rightPlatform = false, inTunnel = false, passengerEngageEBrake = false;

    private boolean ascendingSection = false;

    private ArrayDeque<ControllerBlock> followingStations = new ArrayDeque<>();
    private String nextStationName;

    private final int trainID;
    private final TrainControllerSubject subject;
    private final TrainModel train;
    private ConcurrentHashMap<Integer, ControllerBlock> blockLookup;

    public TrainControllerImpl(TrainModel train, int trainID) {
        this.trainID = trainID;
        this.train = train;
        this.subject = new TrainControllerSubject(this);
        this.nextStationName = "Yard";
        populateTrainValues(train);
        if(train.getTrainNumber() != -1 && train.getLine() != NULL && train.getLine() != null){
            blockLookup = ControllerBlockLookups.getLookup(train.getLine());
            currentBlock = blockLookup.get(0);
        }else{
            currentBlock = new ControllerBlock(new BasicBlock());
        }

    }

    public TrainControllerImpl() {
        this.trainID = -1;
        this.train = new NullTrain();
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

    @Override
    public UpdatedTrainValues sendUpdatedTrainValues(){

        //This is a bandaged solution
        this.setCurrentTemperature(train.getRealTemperature());
        if (train.getEmergencyBrake() && !passengerEngageEBrake){
            passengerEngageEBrake = true;
            this.setEmergencyBrake(true);
        }
        else if (!emergencyBrake){
            passengerEngageEBrake = false;
            this.setEmergencyBrake(false);
        }
        this.setCurrentSpeed(train.getSpeed());
        this.setPower(calculatePower(this.currentSpeed));

        //TODO: this.authorityCheck(this.power, this.currentSpeed);  will change power if it does not align with authority

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

    public double calculatePower(double currentSpeed) {

        if (waysideStop){
            setServiceBrake(true);
            rollingError = 0.0;
            return 0;
        }
        else {

            double setSpeed, pow;
            boolean serviceBrake = false;

            if (automaticMode) {
                setSpeed = commandSpeed;
            } else {
                setSpeed = overrideSpeed;
            }

            double error = setSpeed - currentSpeed;
            double proportionalTerm = Kp * error;

            // Update the rolling error
            rollingError += error * TIME_STEP;

            // Introduce an integral term to reduce steady-state error
            double integralTerm = Ki * rollingError;

            // Calculate the control output
            double controlOutput = proportionalTerm + integralTerm;

            // Limit the control output to a reasonable range
            pow = Math.max(-1, Math.min(MAX_POWER_W, controlOutput));

            // Apply a deadband to avoid oscillations around the setpoint
            if (Math.abs(error) < DEAD_BAND) {
                pow = 0.0;
            }

            // Apply brakes if the power is negative or if the train is overshooting
            if (pow < 0 || (currentSpeed > setSpeed && automaticMode)) {
                pow = 0;
                serviceBrake = true;
            }

            // Cut off power if brakes are engaged or there's a failure
            if (emergencyBrake || serviceBrake || powerFailure) {
                pow = 0;
            }

            setServiceBrake(serviceBrake);
            return pow;
        }
    }


    /**
     *  onBlock()
     */
    @Override
    public void onBlock(){
        if(currentBeacon != null && blockLookup != null) {
            currentBlock = (ascendingSection) ? blockLookup.get(currentBeacon.blockIndices().pollFirst()) : blockLookup.get(currentBeacon.blockIndices().pollLast());

            //setNextStationName(currentBlock.stationName());
            setSpeedLimit(currentBlock.speedLimit());
            setInTunnel(currentBlock.isUnderground());
            //.... proof of concept

            // Update Block by Block

            // Get Specific Block Info
            checkTunnel();
        }

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
            logger.info("Train is in a tunnel");
            setIntLights(true);
            setExtLights(true);

        }
        else{
            logger.info("Train is not in a tunnel");
            setIntLights(false);
            setExtLights(false);

        }
    }

//    // Failure Management with Steven He
//    public boolean checkBrakeFailure(){
//
//        // Failures occur when the brake states in the train controller do not match with brake states in the train model
//        if (this.serviceBrake && !train.getServiceBrake()) this.setBrakeFailure(true);
//        if (this.emergencyBrake && !train.getEmergencyBrake()) this.setBrakeFailure(true);
//
//        // If true, pick a god and pray
//
//        return brakeFailure;
//    }
//    public boolean checkSignalFailure(){
//        // Failure occur when the commanded speed or commanded authority is -1
//        this.setCommandSpeed(train.getCommandSpeed());
//        this.setAuthority(train.getAuthority());
//
//        if (commandSpeed == -1 && authority == -1) this.setSignalFailure(true);
//
//        //If true, activate emergency brake
//        if (signalFailure) this.setEmergencyBrake(true);
//
//        return signalFailure;
//    }
//    public boolean checkPowerFailure(){
//        // Failure occurs when train model's set power equals 0 but we are outputting power
//
//        if (this.power > 0 && train.getPower() == 0) this.setPowerFailure(true);
//
//        // If true, activate emergency brake
//        if (this.powerFailure) this.setEmergencyBrake(true);
//
//        return this.powerFailure;
//    }


    //Functions called by the internal logic to notify of changes
    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        subject.notifyChange(AUTOMATIC_MODE, mode);
    }
    public void setAuthority(int authority) {

        if (authority == STOP_TRAIN_SIGNAL) {
            waysideStop = true;
            // Make a message on the logger
            logger.info("Train {} has been stopped by wayside", trainID);
        }
        else if (authority == RESUME_TRAIN_SIGNAL){
            waysideStop = false;
            // Make a message on the logger
            logger.info("Train {} has been resumed by wayside", trainID);
        }
        else {
            this.authority = authority;
            subject.notifyChange(AUTHORITY, authority);
        }
    }
    public void setCommandSpeed(double speed) {
        this.commandSpeed = convertVelocity(speed, MPS, MPH);
        subject.notifyChange(COMMAND_SPEED , speed);
        //calculatePower();
    }
    public void setCurrentSpeed(double speed) {
        this.currentSpeed = speed;
        subject.notifyChange(CURRENT_SPEED , convertVelocity(speed, MPS, MPH));
    }
    private void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        subject.notifyChange(SERVICE_BRAKE , brake);
    }
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        subject.notifyChange(EMERGENCY_BRAKE , brake);
    }
    public void setKi(double Ki) {
        this.Ki = Ki;
        subject.notifyChange(KI , Ki);
    }
    public void setKp(double Kp) {
        this.Kp = Kp;
        subject.notifyChange(KP , Kp);
    }
    public void setPower(double power) {
        this.power = power;
        subject.notifyChange(POWER , convertPower(power, WATTS, HORSEPOWER));
    }
    public void setIntLights(boolean lights) {
        this.internalLights = lights;
        subject.notifyChange(INT_LIGHTS , lights); // This might've been the issue interiorLights -> intLights
    }
    public void setExtLights(boolean lights) {
        this.externalLights = lights;
        subject.notifyChange(EXT_LIGHTS , lights); // This might've been the issue exteriorLights -> extLights
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        subject.notifyChange(LEFT_DOORS , doors);
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        subject.notifyChange(RIGHT_DOORS , doors);
    }
    public void setSetTemperature(double temp) {
        this.setTemperature = temp;
        subject.notifyChange(SET_TEMPERATURE , convertTemperature(temp, CELSIUS, FAHRENHEIT));
    }
    public void setCurrentTemperature(double temp){
        this.currentTemperature = temp;
        subject.notifyChange(CURRENT_TEMPERATURE , convertTemperature(temp, CELSIUS, FAHRENHEIT));
    }
    public void setAnnouncements(boolean announcements) {
        this.announcements = announcements;
        subject.notifyChange(ANNOUNCEMENTS , announcements);
    }
    public void setSignalFailure(boolean signalFailure) {
        this.signalFailure = signalFailure;
        subject.notifyChange(SIGNAL_FAILURE , signalFailure);
    }
    public void setBrakeFailure(boolean brakeFailure) {
        this.brakeFailure = brakeFailure;
        subject.notifyChange(BRAKE_FAILURE , brakeFailure);
    }
    public void setPowerFailure(boolean powerFailure) {
        this.powerFailure = powerFailure;
        subject.notifyChange(POWER_FAILURE , powerFailure);
    }
    public void setInTunnel(boolean tunnel){
        this.inTunnel = tunnel;
        subject.notifyChange(IN_TUNNEL ,tunnel);
    }
    public void setLeftPlatform(boolean platform){
        this.leftPlatform = platform;
        subject.notifyChange(LEFT_PLATFORM ,platform);
    }
    public void setRightPlatform(boolean platform){
        this.rightPlatform = platform;
        subject.notifyChange(RIGHT_PLATFORM ,platform);
    }
    public void setSpeedLimit(double limit){
        this.speedLimit = convertVelocity(limit, MPH, MPS);
        subject.notifyChange(SPEED_LIMIT , limit);
    }
    public void setNextStationName(String name){
        this.nextStationName = name;
        subject.notifyChange(NEXT_STATION ,name);
    }
    public void setGrade(double newValue) {
        this.grade = newValue;
        subject.notifyChange(GRADE ,newValue);
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

    public void setValue(ControllerProperty propertyName, Object newValue) {
        switch (propertyName) {
            case AUTOMATIC_MODE -> this.automaticMode = (boolean) newValue;
            case AUTHORITY -> this.authority = (int) newValue;
            case OVERRIDE_SPEED -> this.overrideSpeed = convertVelocity((double) newValue, MPH, MPS);
            case COMMAND_SPEED -> this.commandSpeed = convertVelocity((double) newValue, MPH, MPS);
            case CURRENT_SPEED -> this.currentSpeed = convertVelocity((double) newValue, MPH, MPS);
            case SERVICE_BRAKE -> this.serviceBrake = (boolean) newValue;
            case EMERGENCY_BRAKE -> this.emergencyBrake = (boolean) newValue;
            case KI -> this.Ki = (double) newValue;
            case KP -> this.Kp = (double) newValue;
            case POWER -> this.power = convertPower((double) newValue, WATTS, HORSEPOWER);
            case INT_LIGHTS -> this.internalLights = (boolean) newValue;
            case EXT_LIGHTS -> this.externalLights = (boolean) newValue;
            case LEFT_DOORS -> this.leftDoors = (boolean) newValue;
            case RIGHT_DOORS -> this.rightDoors = (boolean) newValue;
            case SET_TEMPERATURE -> this.setTemperature = convertTemperature((double) newValue, FAHRENHEIT, CELSIUS);
            case CURRENT_TEMPERATURE ->  this.currentTemperature = convertTemperature((double) newValue, FAHRENHEIT, CELSIUS);
            case ANNOUNCEMENTS -> this.announcements = (boolean) newValue;
            case SIGNAL_FAILURE -> this.signalFailure = (boolean) newValue;
            case BRAKE_FAILURE -> this.brakeFailure = (boolean) newValue;
            case POWER_FAILURE -> this.powerFailure = (boolean) newValue;
            case IN_TUNNEL -> this.inTunnel = (boolean) newValue;
            case LEFT_PLATFORM -> this.leftPlatform = (boolean) newValue;
            case RIGHT_PLATFORM -> this.rightPlatform = (boolean) newValue;
            case SPEED_LIMIT -> this.speedLimit = convertVelocity((double) newValue, MPH, MPS);
            case NEXT_STATION -> this.nextStationName = (String) newValue;
            case GRADE -> this.grade = (double) newValue;
            case TRAIN_ID -> System.out.println("Train ID is a read-only property");
            case ERROR -> System.out.println("Error is a read-only property");
            default -> System.err.println("Property " + propertyName + " not found");
        }
        logger.info("TrainController Value {} set to {} from GUI.",propertyName,newValue);
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
    public double getTimeInterval(){
        return TIME_STEP;
    }
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
    public String getNextStationName(){
        return this.nextStationName;
    }
    public boolean getLeftPlatform(){
        return this.leftPlatform;
    }
    public boolean getRightPlatform(){
        return this.rightPlatform;
    }
    public boolean getInTunnel(){
        return this.inTunnel;
    }

    @Override
    public Beacon getBeacon() {
        return this.currentBeacon;
    }

    public double getGrade(){
        return this.grade;
    }

    @Override
    public void updateBeacon(Beacon beacon) {
        if (this.currentBeacon != null) {
            this.ascendingSection = (currentBlock.blockNumber() == beacon.startId());
            currentBlock = (ascendingSection) ? blockLookup.get(beacon.startId()) : blockLookup.get(beacon.endId());
        }else{
            currentBlock = blockLookup.get(beacon.startId());
        }
        this.currentBeacon = beacon;

        // Update the upcoming station array
        ControllerBlock potentialStation;
        if(ascendingSection){
            for(int i = beacon.startId(); i <= beacon.endId(); i++){
                potentialStation = blockLookup.get(i);
                if(potentialStation.isStation()){
                    followingStations.addFirst(potentialStation);
                }
            }
        }else{
            for(int i = beacon.endId(); i >= beacon.startId(); i--){
                potentialStation = blockLookup.get(i);
                if(potentialStation.isStation()){
                    followingStations.addFirst(potentialStation);
                }
            }
        }

        // Im 90% sure this code can't really set the next station name cause based off of followingStation bc the if only runs when following station is empty
        if(followingStations.isEmpty()) {
            this.setNextStationName("N/A");
            this.setNextStationName(followingStations.pollFirst().stationName());
        }
    }


    @Override
    public void setValue(Enum<?> propertyName, Object newValue) {
        setValue((ControllerProperty) propertyName, newValue);
    }
}