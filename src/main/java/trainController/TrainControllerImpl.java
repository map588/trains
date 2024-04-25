package trainController;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Records.BasicBlock;
import Utilities.Records.Beacon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainController.ControllerBlocks.ControllerBlock;
import trainModel.NullTrain;
import trainModel.Records.UpdatedTrainValues;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Utilities.Constants.*;
import static Utilities.Conversion.*;
import static Utilities.Conversion.powerUnits.HORSEPOWER;
import static Utilities.Conversion.powerUnits.WATTS;
import static Utilities.Conversion.temperatureUnit.CELSIUS;
import static Utilities.Conversion.temperatureUnit.FAHRENHEIT;
import static Utilities.Conversion.velocityUnit.*;
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
public class TrainControllerImpl implements TrainController {
    private static final double TIME_STEP = TIME_STEP_S;
    private static final double DEAD_BAND = 0.5;

    //private final NullTrain nullTrain = new NullTrain();
    private ControllerBlock currentBlock;

    private Beacon currentBeacon = null;

    private static final Logger logger = LoggerFactory.getLogger(TrainControllerImpl.class);

    //Internal Metric
    private double commandSpeed = 0.0;
    private double currentSpeed = 0.0;
    private double overrideSpeed = 0.0;
    private double speedLimit = 0.0;
    private double setTemperature = 0.0;
    private double currentTemperature = 0.0;
    private boolean eBrakeGUI = false;
    private boolean sBrakeGUI = false;


    private boolean waysideStop = false;
    private final intWrapper stopTime = new intWrapper(0);
    private double emergencyDistance;
    private double serviceDistance;

    private double Ki = 50;
    private double Kp = 200;
    private double power = 0.0;
    private double grade = 0.0;
    private double rollingError = 0.0;


    private int authority = 0;

    private double internalAuthority = 0;

    private boolean serviceBrake = false, emergencyBrake = false, automaticMode = true,
            internalLights = false, externalLights = false, leftDoors = false,
            rightDoors = false, announcements = false, signalFailure = false,
            brakeFailure = false, powerFailure = false, leftPlatform = false,
            rightPlatform = false, inTunnel = false, passengerEngageEBrake = false;


    private String nextStationName;
    private String arrivalStation;

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
        if (train.getTrainNumber() != -1 && train.getLine() != NULL && train.getLine() != null) {
            blockLookup = ControllerBlockLookups.getLookup(train.getLine());
            currentBlock = blockLookup.get(0);
        } else {
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
     * @param train The TrainModel object to be assigned to the trainControllerImpl.
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
    public UpdatedTrainValues sendUpdatedTrainValues() {

        double trainSpeed = train.getSpeed();

        this.setCurrentTemperature(train.getRealTemperature());
        this.setCurrentSpeed(trainSpeed);


        this.setPower(calculatePower(trainSpeed));


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

    //  Calculate power -> (train does shit) ->  check failures -> repeat
    public double calculatePower(double currentSpeed) {

        if (waysideStop) {
            setServiceBrake(true);
            rollingError = 0;
            return 0;
        } else {

            internalAuthority -= currentSpeed * TIME_STEP;

            double setSpeed = automaticMode ? commandSpeed : overrideSpeed;

            setSpeed = Math.min(setSpeed, speedLimit);

            //If the train is coming to a stop and its intentional
            if (power == 0 && currentSpeed > 0 && currentSpeed <= 2) {
                setAuthority((int) internalAuthority);
                if (internalAuthority > 15 && !waysideStop) {
                    setSpeed = 5;
                    setServiceBrake(sBrakeGUI);
                }
                rollingError = 0;
            } else if (currentBlock != null && currentBlock.isStation() && power == 0 && currentSpeed == 0) {
                String platformValues = currentBlock.Doorside();

                setLeftPlatform(platformValues.contains("LEFT"));
                setRightPlatform(platformValues.contains("RIGHT"));
                onStation();
            }

            double error = setSpeed - currentSpeed;
            double proportionalTerm = Kp * error;

            // Update the rolling error
            if (!powerFailure) {
                rollingError += error * TIME_STEP;
            } else {
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


    int brakeCount = 0;

    @Override
    public void checkFailures(double trainPower) {
        boolean badBrakes = this.serviceBrake ^ train.getServiceBrake();
        boolean badPower = this.power > 0 && trainPower == 0;

        if (badBrakes) {
            badBrakes = ++brakeCount > 2;
        } else {
            brakeCount = 0;
        }

        if (powerFailure) {
            train.setPower(3);
            setPowerFailure((train.getPower() < 3));
        } else {
            setPowerFailure(badPower);
            setEmergencyBrake(powerFailure || this.eBrakeGUI || this.passengerEngageEBrake);
        }

        if (brakeFailure) {
            // setServiceBrake(true);
            setBrakeFailure(!train.getServiceBrake());
            // setServiceBrake(this.sBrakeGUI);
        } else {
            setServiceBrake(this.sBrakeGUI);
            setBrakeFailure(badBrakes);
            if (badBrakes) {
                logger.warn("Brake Failure detected {}", brakeCount);
            }
        }

        if (signalFailure) {
            if (train.getCommandSpeed() != -1 && train.getAuthority() != -1) {
                setCommandSpeed(train.getCommandSpeed());
                setAuthority(train.getAuthority());
            }
        }

        //if any failure is detected, set the emergency brake
        if (brakeFailure || powerFailure || signalFailure) {
            setEmergencyBrake(true);
        } else {  //else set the emergency brake to the GUI value, or the passenger emergency brake
            setEmergencyBrake(this.eBrakeGUI || this.passengerEngageEBrake);
        }

        //How far would we travel if we slammed the brakes right now?
        calculateStoppingDistance(this.currentSpeed);

        //If ebrake stopping distance is farther than authority, set the emergency brake
        if (this.internalAuthority <= emergencyDistance) {
            setEmergencyBrake(true);
        } else if (this.internalAuthority <= serviceDistance) { //same for sbrake
            setServiceBrake(true);
        }

    }



    // Function that calculates the stopping distance where the train needs to start stopping
    void calculateStoppingDistance(double currentSpeed) {

        //logger.info("Stopping Distance is {}", stoppingDistance);
        emergencyDistance = Math.pow(currentSpeed, 2) / (2 * EMERGENCY_BRAKE_DECELERATION);
        serviceDistance = Math.pow(currentSpeed, 2) / (2 * SERVICE_BRAKE_DECELERATION);
    }


    //Function is called by the train when it detects that it enters a new block
    @Override
    public void onBlock() {
        if (currentBeacon != null) {

            Integer blockID = currentBeacon.blockIndices().pollFirst();

            if (blockID != null) {
                currentBlock = blockLookup.get(blockID);
                if (currentBeacon.blockIndices().peekFirst() == null) {
                    logger.info("Runnin low on Beacon..");
                }
            } else {
                currentBlock = blockLookup.get(currentBeacon.endId());
                logger.warn("FRESH OUT OF BEACON AT {}!", currentBlock.blockNumber());
            }

            logger.info("Controller thinks its on block {}", currentBlock.blockNumber());


            setAuthority((int) internalAuthority);
            setSpeedLimit(currentBlock.speedLimit());
            setInTunnel(currentBlock.isUnderground());

            // Get Specific Block Info
            checkTunnel();
        } else {
            logger.warn("Controller entered a block blind");
        }
        setAuthority((int) internalAuthority);
    }

    /**
     * onStation()
     */
    public void onStation() {
        // Check if train is stopped
        if (train.getSpeed() == 0) {

            stopTime.increment();
            if(stopTime.getValue() == 0) {
                this.setLeftPlatform(currentBlock.Doorside().contains("LEFT"));
                this.setRightPlatform(currentBlock.Doorside().contains("RIGHT"));
                if (this.leftPlatform) this.setLeftDoors(true);     // Open left doors
                if (this.rightPlatform) this.setRightDoors(true);   // Open right doors
                logger.warn("Train stopping at station {}", nextStationName);
                train.updatePassengers();
                setArrivalStation(currentBlock.stationName());
            }

            if (stopTime.getValue() < (60 * 8)) {
                waysideStop = true;
            } else {
                logger.info("Train stopped at station {} for {} seconds", nextStationName, (float) stopTime.getValue()/8);
                waysideStop = false;
                stopTime.setValue(0);
            }



            if(!waysideStop) {
                this.setLeftDoors(false);
                this.setRightDoors(false);
                this.setLeftPlatform(false);
                this.setRightPlatform(false);
            }
        }
    }

    // Implement Crossing tunnel
    public void checkTunnel() {
        // Get block information somehow

        if (inTunnel) {
            logger.info("Train is in a tunnel");
            setIntLights(true);
            setExtLights(true);

        } else {
            setIntLights(false);
            setExtLights(false);
        }
    }


    //Functions called by the internal logic to notify of changes
    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        notificationExecutor.execute(() -> subject.notifyChange(AUTOMATIC_MODE, mode));
    }


    @Override
    public void setPassengerEBrake() {
        this.passengerEngageEBrake = true;
        this.emergencyBrake = true;
        notificationExecutor.execute(() -> subject.notifyChange(EMERGENCY_BRAKE, true));
    }

    private void clearPassengerEBrake() {
        this.passengerEngageEBrake = false;
    }

    public void setAuthority(int auth) {

        switch (auth) {
            case -1:
                setSignalFailure(true);
                break;
            case STOP_TRAIN_SIGNAL:
                if (!waysideStop) {
                    logger.info("Wayside Stop: Train {}", trainID);
                }

                waysideStop = true;
                setServiceBrake(true);
                break;
            case RESUME_TRAIN_SIGNAL:
                waysideStop = false;
                setServiceBrake(this.sBrakeGUI);
                //logger.info("Wayside Resume: T{}", trainID);
                break;
            default:
                if (signalFailure) {
                    setSignalFailure(false);
                }
                this.authority = auth;
                this.internalAuthority = authority;
                notificationExecutor.execute(() -> subject.notifyChange(AUTHORITY, convertDistance(authority,distanceUnit.METERS,distanceUnit.FEET) ));
                break;
        }
    }


    @Override
    public void updateBeacon(Beacon beacon) {
        logger.warn("Updating Beacon: {}", beacon);
        if (currentBeacon != null) {
            boolean isEnteringJunction = currentBeacon.endId().equals(beacon.sourceId());
            boolean isExitingJunction = currentBeacon.sourceId().equals(beacon.sourceId());

            //Note: We could deal with this case by just setting the "head" of the beacon equal to the end
            //and traversing it backwards in the case of large bi-directional sections
            boolean backWardsBeacon = currentBeacon.sourceId().equals(beacon.endId());


            ControllerBlock block =  getBlock(beacon.sourceId());

            if (isEnteringJunction && !backWardsBeacon) {
                if(block.isStation()) {
                    logger.warn("Arriving at station: {}", block.stationName());
                }else{
                    logger.warn("ENTERING SWITCH: {}", block.blockNumber());
                }
                currentBeacon = beacon;
                this.setNextStationName(findNextStationName());
                currentBeacon.blockIndices().pollFirst();
            } else if (isExitingJunction) {
                if(block.isStation()) {
                    logger.warn("Departing from {}", block.stationName());
                }else{
                    logger.warn("EXITING SWITCH: {}", block.blockNumber());
                }
                beacon.blockIndices().pollFirst();
                this.setNextStationName(findNextStationName());
                currentBeacon = beacon;

            } else if (backWardsBeacon) {

                currentBlock = getBlock(currentBeacon.sourceId());

                logger.warn("Wrong beacon direction, rejecting beacon: {}", beacon);
            }else {
                logger.warn("Beacon is not related to current block: {}", beacon);
                currentBeacon = beacon;
                currentBeacon.blockIndices().pollFirst();
            }
        }else{
            currentBeacon = beacon;
            currentBeacon.blockIndices().pollFirst();
            this.setNextStationName(findNextStationName());
            logger.warn("First Beacon: {}", currentBeacon);
        }
    }


    public void setCommandSpeed(double speed) {
        if (speed == -1) {
            setSignalFailure(true);
        } else if (signalFailure) {
            setSignalFailure(false);
        }
        this.commandSpeed = speed;
        notificationExecutor.execute(() -> subject.notifyChange(COMMAND_SPEED, convertVelocity(speed, MPS, MPH)));
        //calculatePower());
    }

    public void setCurrentSpeed(double speed) {
        this.currentSpeed = speed;
        notificationExecutor.execute(() -> subject.notifyChange(CURRENT_SPEED, convertVelocity(speed, MPS, MPH)));
    }

    private void setServiceBrake(boolean brake) {
        this.train.setServiceBrake(brake);
        this.serviceBrake = brake;
        notificationExecutor.execute(() -> subject.notifyChange(SERVICE_BRAKE, brake));
    }

    public void setEmergencyBrake(boolean brake) {
        this.train.setEmergencyBrake(brake);
        this.emergencyBrake = brake;
        notificationExecutor.execute(() -> subject.notifyChange(EMERGENCY_BRAKE, brake));
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
        notificationExecutor.execute(() -> subject.notifyChange(ARRIVAL_STATION, arrivalStation));
    }

    public void setKi(double Ki) {
        this.Ki = Ki;
        notificationExecutor.execute(() -> subject.notifyChange(KI, Ki));
    }

    public void setKp(double Kp) {
        this.Kp = Kp;
        notificationExecutor.execute(() -> subject.notifyChange(KP, Kp));
    }

    public void setPower(double power) {
        this.power = power;
        notificationExecutor.execute(() -> subject.notifyChange(POWER, convertPower(power, WATTS, HORSEPOWER)));
    }

    public void setIntLights(boolean lights) {
        this.internalLights = lights;
        notificationExecutor.execute(() -> subject.notifyChange(INT_LIGHTS, lights)); // This might've been the issue interiorLights -> intLights
    }

    public void setExtLights(boolean lights) {
        this.externalLights = lights;
        notificationExecutor.execute(() -> subject.notifyChange(EXT_LIGHTS, lights));// This might've been the issue exteriorLights -> extLights
    }

    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        notificationExecutor.execute(() -> subject.notifyChange(LEFT_DOORS, doors));
    }

    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        notificationExecutor.execute(() -> subject.notifyChange(RIGHT_DOORS, doors));
    }

    public void setSetTemperature(double temp) {
        this.setTemperature = temp;
        notificationExecutor.execute(() -> subject.notifyChange(SET_TEMPERATURE, convertTemperature(temp, CELSIUS, FAHRENHEIT)));
    }

    public void setCurrentTemperature(double temp) {
        this.currentTemperature = temp;
        notificationExecutor.execute(() -> subject.notifyChange(CURRENT_TEMPERATURE, convertTemperature(temp, CELSIUS, FAHRENHEIT)));
    }

    public void setAnnouncements(boolean announcements) {
        this.announcements = announcements;
        notificationExecutor.execute(() -> subject.notifyChange(ANNOUNCEMENTS, announcements));
    }

    public void setSignalFailure(boolean signalFailure) {
        this.signalFailure = signalFailure;
        if (signalFailure) {
            logger.info("Signal Failure detected");
        }
        notificationExecutor.execute(() -> {
            subject.notifyChange(SIGNAL_FAILURE, signalFailure);
        });
    }

    public void setBrakeFailure(boolean brakeFailure) {
        this.brakeFailure = brakeFailure;
        notificationExecutor.execute(() -> {
            if (brakeFailure) {
                logger.info("Brake Failure detected");
            }
            subject.notifyChange(BRAKE_FAILURE, brakeFailure);
        });
    }

    public void setPowerFailure(boolean powerFailure) {
        this.powerFailure = powerFailure;
        notificationExecutor.execute(() -> {
            if (powerFailure) {
                logger.info("Power Failure detected");
            }
            subject.notifyChange(POWER_FAILURE, powerFailure);
        });
    }

    public void setInTunnel(boolean tunnel) {
        this.inTunnel = tunnel;
        notificationExecutor.execute(() -> subject.notifyChange(IN_TUNNEL, tunnel));
    }

    public void setLeftPlatform(boolean platform) {
        this.leftPlatform = platform;
        notificationExecutor.execute(() -> subject.notifyChange(LEFT_PLATFORM, platform));
    }

    public void setRightPlatform(boolean platform) {
        this.rightPlatform = platform;
        notificationExecutor.execute(() -> subject.notifyChange(RIGHT_PLATFORM, platform));
    }

    public void setSpeedLimit(double limit) {
        this.speedLimit = limit;
        notificationExecutor.execute(() -> subject.notifyChange(SPEED_LIMIT, convertVelocity(limit, KPH, MPH)));
    }

    public void setNextStationName(String name) {
        this.nextStationName = name;
        notificationExecutor.execute(() -> subject.notifyChange(NEXT_STATION, name));
    }

    public void setGrade(double newValue) {
        this.grade = newValue;
        notificationExecutor.execute(() -> subject.notifyChange(GRADE, newValue));
    }

    /**
     * This method is used to set the value of a property based on the property name.
     * It uses a switch statement to determine which property to set.
     * The method casts the newValue parameter to the appropriate type based on the property.
     * If the property name is not found, it prints an error message to the console.
     * After setting the property, it does not calculate power.
     *
     * @param propertyName The name of the property to be set.
     * @param newValue     The new value to be set for the property.
     */

    public void setValue(ControllerProperty propertyName, Object newValue) {
        switch (propertyName) {
            case AUTOMATIC_MODE -> this.automaticMode = (boolean) newValue;
            case AUTHORITY -> this.authority = (int) newValue;
            case OVERRIDE_SPEED -> this.overrideSpeed = convertVelocity((double) newValue, MPH, MPS);
            case COMMAND_SPEED -> this.commandSpeed = convertVelocity((double) newValue, MPH, MPS);
            case CURRENT_SPEED -> this.currentSpeed = convertVelocity((double) newValue, MPH, MPS);
            case SERVICE_BRAKE ->  {this.serviceBrake = (boolean) newValue;sBrakeGUI = (boolean) newValue;}
            case EMERGENCY_BRAKE -> {this.emergencyBrake = (boolean) newValue; eBrakeGUI = (boolean) newValue; clearPassengerEBrake();}
            case KI -> this.Ki = (double) newValue;
            case KP -> this.Kp = (double) newValue;
            case POWER -> this.power = convertPower((double) newValue, HORSEPOWER, WATTS);
            case INT_LIGHTS -> this.internalLights = (boolean) newValue;
            case EXT_LIGHTS -> this.externalLights = (boolean) newValue;
            case LEFT_DOORS -> this.leftDoors = (boolean) newValue;
            case RIGHT_DOORS -> this.rightDoors = (boolean) newValue;
            case SET_TEMPERATURE -> this.setTemperature = convertTemperature((double) newValue, FAHRENHEIT, CELSIUS);
            case CURRENT_TEMPERATURE -> this.currentTemperature = convertTemperature((double) newValue, FAHRENHEIT, CELSIUS);
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
        logger.info("TrainController Value {} set to {} from GUI.", propertyName, newValue);
    }



    //-----------------Getters-----------------

    @Override
    public TrainModel getTrain() {
        return train;
    }
    public int getID() {
        return this.trainID;
    }

    public double getSpeed() {
        return this.currentSpeed;
    }

    public double getAcceleration() {
        return this.train.getAcceleration();
    }

    public double getTimeInterval() {
        return TIME_STEP;
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

    public double getCommandSpeed() {
        return this.commandSpeed;
    }

    public int getAuthority() {
        return this.authority;
    }

    public double getKi() {
        return this.Ki;
    }

    public double getKp() {
        return this.Kp;
    }

    public double getOverrideSpeed() {
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
        if(!this.train.isDeleted()){
            this.train.delete();
        }
        this.subject.delete();
    }

    @Override
    public boolean isHW() {
        return false;
    }

    public double getSpeedLimit() {
        return this.speedLimit;
    }

    public boolean getLeftDoors() {
        return this.leftDoors;
    }

    public boolean getRightDoors() {
        return this.rightDoors;
    }

    public double getSetTemperature() {
        return this.setTemperature;
    }

    public double getCurrentTemperature() {
        return this.currentTemperature;
    }

    public String getNextStationName() {
        return this.nextStationName;
    }

    public boolean getLeftPlatform() {
        return this.leftPlatform;
    }

    public boolean getRightPlatform() {
        return this.rightPlatform;
    }

    public boolean getInTunnel() {
        return this.inTunnel;
    }

    @Override
    public Beacon getBeacon() {
        return this.currentBeacon;
    }

    public double getGrade() {
        return this.grade;
    }

    private String findNextStationName() {
        ControllerBlock nextBlock = getBlock(currentBeacon.endId());
        if(currentBeacon != null && nextBlock != null && nextBlock.isStation()){
            return nextBlock.stationName();
        }
        return "Awaiting Beacon..";
    }




    private Beacon previousBeacon;

//    @Override
//    public void updateBeacon(Beacon beacon) {
//        logger.warn("Updating Beacon: {}", beacon);
//        if (currentBeacon == null) {
//            currentBeacon = beacon;
//        } else {
//            ControllerBlock currentSourceBlock = getBlock(currentBeacon.sourceId());
//            ControllerBlock updatedSourceBlock = getBlock(beacon.sourceId());
//
//            if (previousBeacon != null) {
//                ControllerBlock previousSourceBlock = getBlock(previousBeacon.sourceId());
//
//                if (previousSourceBlock.blockNumber() == updatedSourceBlock.blockNumber()) {
//                    // Entering a switch or station
//                    if (updatedSourceBlock.isStation()) {
//                        logger.info("Arriving at station: {}", updatedSourceBlock.stationName());
//                    } else {
//                        logger.info("Entering switch: {}", updatedSourceBlock.blockNumber());
//                    }
//                } else if (currentSourceBlock.blockNumber() == previousSourceBlock.blockNumber()) {
//                    // Exiting a switch or station
//                    if (previousSourceBlock.isStation()) {
//                        logger.info("Leaving station: {}", previousSourceBlock.stationName());
//                    } else {
//                        logger.info("Exiting switch: {}", previousSourceBlock.blockNumber());
//                    }
//                }
//            }
//
//            this.previousBeacon = currentBeacon;
//            this.currentBeacon = beacon;
//            this.setNextStationName(findNextStationName());
//        }
//        currentBeacon.blockIndices().pollFirst();
//    }




    private ControllerBlock getBlock(Integer i) {
        return blockLookup.get(i);
    }
    @Override
    public void setValue(Enum<?> propertyName, Object newValue) {
        setValue((ControllerProperty) propertyName, newValue);
    }


    private static class intWrapper{

        private int value;

        public intWrapper(int value){
            this.value = value;
        }
        public int getValue(){
            return value;
        }
        public void setValue(int value){
            this.value = value;
        }

        public void increment(){
            value++;
        }
    }

}