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

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private boolean eBrakeGUI = false;


    private int authority = 0;

    private boolean serviceBrake = false, emergencyBrake = false, automaticMode = true,
            internalLights = false, externalLights = false, leftDoors = false,
            rightDoors = false, announcements = false, signalFailure = false,
            brakeFailure = false, powerFailure = false, leftPlatform = false,
            rightPlatform = false, inTunnel = false, passengerEngageEBrake = false;

    private boolean ascendingSection = false;

    private String nextStationName;

    private final int trainID;
    private final TrainControllerSubject subject;
    private final TrainModel train;
    private ConcurrentHashMap<Integer, ControllerBlock> blockLookup;
    private static final ExecutorService notificationExecutor = Executors.newSingleThreadExecutor();

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
            rollingError = 0;
            return 0;
        }
        else {

            double setSpeed = automaticMode? commandSpeed : overrideSpeed;
            double error = setSpeed - currentSpeed;
            double proportionalTerm = Kp * error;

            // Update the rolling error
            if(!powerFailure) {
                rollingError += error * TIME_STEP;
            }else{
                rollingError = 0;
            }

            // Introduce an integral term to reduce steady-state error
            double integralTerm = Ki * rollingError;

            // Calculate the control output
            double controlOutput = proportionalTerm + integralTerm;

            // Limit the control output to a reasonable range
            double pow = Math.max(-1, Math.min(MAX_POWER_W, controlOutput));

            // Apply a deadband to avoid oscillations around the setpoint
            if (Math.abs(error) < DEAD_BAND) {
                pow = 0.0;
            }

            // Apply brakes if the power is negative or if the train is overshooting
            if (pow < 0 || ((currentSpeed > setSpeed) && automaticMode)) {
                pow = 0;
                setServiceBrake(true);
            }
            // Cut off power if brakes are engaged or there's a failure
            if (emergencyBrake || serviceBrake) {
                pow = 0;
            }
            return pow;
        }
    }

    @Override
    public void checkFailures(double trainPower) {
        boolean badBrakes = this.serviceBrake ^ train.getServiceBrake();
        boolean badPower  =  this.power > 1 && trainPower == 0;

        setSignalFailure(commandSpeed == -1 || authority == -1);



        if(powerFailure){
            train.setPower(3);
            setPowerFailure(!(train.getPower() == 3));
        }else {
            setPowerFailure(badPower);
        }

        if(brakeFailure){
            train.setServiceBrake(true);
            setBrakeFailure(!train.getServiceBrake());
            train.setServiceBrake(false);
        }else {
            setBrakeFailure(badBrakes);
        }

        if(brakeFailure || powerFailure || signalFailure){
            setEmergencyBrake(true);
        }else{
            setEmergencyBrake(eBrakeGUI);
        }
    }

    // Function that calculates the stopping distance where the train needs to start stopping
    public double calculateStoppingDistance(double currentSpeed){
        logger.info("Stopping Distance is {}",Math.pow(currentSpeed,2) / (2*SERVICE_BRAKE_DECELERATION));

        return Math.pow(currentSpeed,2) / (2*SERVICE_BRAKE_DECELERATION);
    }

    /**
     *  onBlock()
     */
    @Override
    public void onBlock(){
        if(currentBeacon != null && blockLookup != null) {
            currentBlock = (ascendingSection) ? blockLookup.get(currentBeacon.blockIndices().pollFirst()) : blockLookup.get(currentBeacon.blockIndices().pollLast());

            if(currentBlock.isStation()){
                this.setNextStationName(currentBlock.stationName());
                onStation();
            }
            setSpeedLimit(currentBlock.speedLimit());
            setInTunnel(currentBlock.isUnderground());

            // Get Specific Block Info
            checkTunnel();
        }
        this.setAuthority(this.getAuthority()-(int)currentBlock.blockLength());

        if (this.authority <= this.calculateStoppingDistance(this.currentSpeed)){
            // Train starts slowing
            setServiceBrake(true);
        }

//        this.setAuthority(this.getAuthority()-1);
//        if (this.authority <= 4){
//            setServiceBrake(true);
//        }


    }

    /**
     * onStation()
     */
    public void onStation(){
        if (train.getSpeed() == 0){                             // Check if train is stopped

            // Hopefully won't affect make announcment implementation in manager
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




    //Functions called by the internal logic to notify of changes
    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        notificationExecutor.execute( ()->
            subject.notifyChange(AUTOMATIC_MODE, mode));
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
            notificationExecutor.execute(()->
	            subject.notifyChange(AUTHORITY, authority));
        }
    }
    public void setCommandSpeed(double speed) {
            this.commandSpeed = speed;
            notificationExecutor.execute( ()-> subject.notifyChange(COMMAND_SPEED, convertVelocity(speed, MPS, MPH)));
        //calculatePower());
    }
    public void setCurrentSpeed(double speed) {
        this.currentSpeed = speed;
        notificationExecutor.execute( ()-> subject.notifyChange(CURRENT_SPEED , convertVelocity(speed, MPS, MPH)));
    }
    private void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        notificationExecutor.execute( ()-> subject.notifyChange(SERVICE_BRAKE , brake));
    }
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        notificationExecutor.execute( ()-> subject.notifyChange(EMERGENCY_BRAKE , brake));
    }
    public void setKi(double Ki) {
        this.Ki = Ki;
        notificationExecutor.execute( ()-> subject.notifyChange(KI , Ki));
    }
    public void setKp(double Kp) {
        this.Kp = Kp;
        notificationExecutor.execute( ()-> subject.notifyChange(KP , Kp));
    }
    public void setPower(double power) {
        this.power = power;
        notificationExecutor.execute( ()-> subject.notifyChange(POWER , convertPower(power, WATTS, HORSEPOWER)));
    }
    public void setIntLights(boolean lights) {
        this.internalLights = lights;
        notificationExecutor.execute( ()-> subject.notifyChange(INT_LIGHTS , lights)); // This might've been the issue interiorLights -> intLights
    }
    public void setExtLights(boolean lights) {
        this.externalLights = lights;
        notificationExecutor.execute( ()-> subject.notifyChange(EXT_LIGHTS , lights));// This might've been the issue exteriorLights -> extLights
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        notificationExecutor.execute( ()-> subject.notifyChange(LEFT_DOORS , doors));
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        notificationExecutor.execute( ()-> subject.notifyChange(RIGHT_DOORS , doors));
    }
    public void setSetTemperature(double temp) {
        this.setTemperature = temp;
        notificationExecutor.execute( ()-> subject.notifyChange(SET_TEMPERATURE , convertTemperature(temp, CELSIUS, FAHRENHEIT)));
    }
    public void setCurrentTemperature(double temp){
        this.currentTemperature = temp;
        notificationExecutor.execute( ()-> subject.notifyChange(CURRENT_TEMPERATURE , convertTemperature(temp, CELSIUS, FAHRENHEIT)));
    }
    public void setAnnouncements(boolean announcements) {
        this.announcements = announcements;
        notificationExecutor.execute( ()-> subject.notifyChange(ANNOUNCEMENTS , announcements));
    }
    public void setSignalFailure(boolean signalFailure) {
        this.signalFailure = signalFailure;
        if(signalFailure){
            logger.warn("Signal Failure detected");
        }
        notificationExecutor.execute( ()-> {subject.notifyChange(SIGNAL_FAILURE , signalFailure);
        });
    }
    public void setBrakeFailure(boolean brakeFailure) {
        this.brakeFailure = brakeFailure;
        notificationExecutor.execute( ()-> {
            if(brakeFailure){
                logger.warn("Brake Failure detected");
            }
            subject.notifyChange(BRAKE_FAILURE , brakeFailure);
        });
    }
    public void setPowerFailure(boolean powerFailure) {
        this.powerFailure = powerFailure;
        notificationExecutor.execute( ()-> {
            if(powerFailure){
                logger.warn("Power Failure detected");
            }
            subject.notifyChange(POWER_FAILURE , powerFailure);
        });
    }
    public void setInTunnel(boolean tunnel){
        this.inTunnel = tunnel;
        notificationExecutor.execute( ()-> subject.notifyChange(IN_TUNNEL ,tunnel));
    }
    public void setLeftPlatform(boolean platform){
        this.leftPlatform = platform;
        notificationExecutor.execute( ()-> subject.notifyChange(LEFT_PLATFORM ,platform));
    }
    public void setRightPlatform(boolean platform){
        this.rightPlatform = platform;
        notificationExecutor.execute( ()-> subject.notifyChange(RIGHT_PLATFORM ,platform));
    }
    public void setSpeedLimit(double limit){
        this.speedLimit = limit;
        notificationExecutor.execute( ()-> subject.notifyChange(SPEED_LIMIT , convertVelocity(limit, MPS, MPH)));
    }
    public void setNextStationName(String name){
        this.nextStationName = name;
        notificationExecutor.execute( ()-> subject.notifyChange(NEXT_STATION ,name));
    }
    public void setGrade(double newValue) {
        this.grade = newValue;
        notificationExecutor.execute( ()-> subject.notifyChange(GRADE ,newValue));
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
            case EMERGENCY_BRAKE -> {this.emergencyBrake = (boolean) newValue; eBrakeGUI = (boolean) newValue;}
            case KI -> this.Ki = (double) newValue;
            case KP -> this.Kp = (double) newValue;
            case POWER -> this.power = convertPower((double) newValue, HORSEPOWER, WATTS);
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

    @Override
    public boolean isHW() {
        return false;
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

    private String findNextStationName(){
        ControllerBlock potentialStation;
        if(ascendingSection){
            for(int i = currentBeacon.startId(); i <= currentBeacon.endId(); i++){
                potentialStation = blockLookup.get(i);
                if(potentialStation.isStation()){
                    return potentialStation.stationName();
                }
            }
        }else{
            for(int i = currentBeacon.endId(); i >= currentBeacon.startId(); i--){
                potentialStation = blockLookup.get(i);
                if(potentialStation.isStation()){
                    return potentialStation.stationName();
                }
            }
        }
        return "Awaiting Beacon..";
    }

    @Override
    public void updateBeacon(Beacon beacon) {
        logger.info("Updating Beacon: {}", beacon);
        if (this.currentBeacon != null) {
            this.ascendingSection = (currentBlock.blockNumber() == beacon.startId());
            currentBlock = (ascendingSection) ? blockLookup.get(beacon.startId()) : blockLookup.get(beacon.endId());
        }else{
            currentBlock = blockLookup.get(beacon.startId());
        }
        this.currentBeacon = beacon;

        String currentStation = this.nextStationName;
        // Update the upcoming station array
        this.setNextStationName(findNextStationName());

        if(Objects.equals(currentStation, this.nextStationName)) {
            this.setNextStationName("Awaiting Beacon..");
        }
    }


    @Override
    public void setValue(Enum<?> propertyName, Object newValue) {
        setValue((ControllerProperty) propertyName, newValue);
    }
}