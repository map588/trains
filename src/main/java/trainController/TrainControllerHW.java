package trainController;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Enums.Lines;
import Utilities.Records.Beacon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainController.ControllerBlocks.ControllerBlock;
import trainController.HWUtils.TrainControllerRemote;
import trainModel.Records.UpdatedTrainValues;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ConcurrentHashMap;

import static Utilities.Constants.EMERGENCY_BRAKE_DECELERATION;
import static Utilities.Constants.TIME_STEP_S;

public class TrainControllerHW implements TrainController {

    private static final Logger logger = LoggerFactory.getLogger(TrainControllerHW.class);

    private ControllerBlock currentBlock;

    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;


    private static final double TIME_STEP = TIME_STEP_S;
    private static final double DEAD_BAND = 0.05;
    private final int trainID;

    private double  authority;
    private double  internalAuthority = 0.0;
    private double  commandSpeed = 0.0;
    private double  currentSpeed = 0.0;
    private double  overrideSpeed = 0.0;
    private double  power = 0.0;

    private boolean serviceBrake = false;
    private boolean emergencyBrake = false;
    private boolean automaticMode = true;

    private boolean brakeFailure = false;
    private boolean powerFailure = false;
    private boolean signalFailure = false;
    private boolean passengerEngageEBrake = false;


    private ConcurrentHashMap<Integer, ControllerBlock> blockLookup;
    private TrainControllerSubject subject;
    private Beacon currentBeacon;
    private Lines line;


    private double  speedLimit = 0.0;
    private double  setTemperature = 0.0;
    private double  currentTemperature = 0.0;
    private boolean eBrakeGUI = false;
    private boolean sBrakeGUI = false;
    private boolean waysideStop;
    private boolean passengerEBrake;


    String nextStationName;

    double eStoppingDistance = 100;
    double sStoppingDistance = 200;

    private final TrainModel train;

    private TrainControllerRemote remoteInstance;



    public TrainControllerHW(TrainModel train, int trainID) {
        Registry registry = null;

        try {
            registry = LocateRegistry.getRegistry("raspberrypi", 1099);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        try {
            remoteInstance = (TrainControllerRemote) registry.lookup("TrainController");
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

        this.line = train.getLine();

        this.blockLookup = ControllerBlockLookups.getLookup(line);
        this.train = train;
        this.trainID = trainID;
        this.subject = new TrainControllerSubject(this);
    }


    @Override
    public void onBlock() {

    }



    @Override
    public UpdatedTrainValues sendUpdatedTrainValues() {
        return null;
    }


    @Override
    public double calculatePower(double currentVelocity) {
        return 0;
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
        if (this.internalAuthority <= eStoppingDistance) {
            setEmergencyBrake(true);
        } else if (this.internalAuthority <= sStoppingDistance) { //same for sbrake
            setServiceBrake(true);
        }

    }

    private void calculateStoppingDistance(double currentSpeed) {

        this.sStoppingDistance = Math.pow(currentSpeed, 2) / (2 * EMERGENCY_BRAKE_DECELERATION);
        this.eStoppingDistance = Math.pow(currentSpeed, 2) / (2 * EMERGENCY_BRAKE_DECELERATION);
    }


    private void setServiceBrake(boolean b) {
        this.serviceBrake = b;
    }



    private void setBrakeFailure(boolean b) {
        this.brakeFailure = b;
    }

    private void setPowerFailure(boolean b) {
        this.powerFailure = b;
    }

    private void setSignalFailure(boolean b) {
        this.signalFailure = b;
    }

    @Override
    public void setAuthority(int authority) {
        this.authority = authority;
        this.internalAuthority = authority;
        try {
            outputStream.writeObject("setAuthority");
            outputStream.writeObject(authority);
            outputStream.flush();
            // Wait for response if needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCommandSpeed(double speed) {
        try {
            outputStream.writeObject("setCommandSpeed");
            outputStream.writeObject(speed);
            outputStream.flush();
            // Wait for response if needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCurrentTemperature(double temp) {
        try {
            outputStream.writeObject("setCurrentTemperature");
            outputStream.writeObject(temp);
            outputStream.flush();
            // Wait for response if needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBeacon(Beacon beacon) {
        if (currentBeacon != null) {
            boolean isEnteringJunction = currentBeacon.endId().equals(beacon.sourceId());
            boolean isExitingJunction = currentBeacon.sourceId().equals(beacon.sourceId());

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

    void setNextStationName(String stationName) {
        this.nextStationName = stationName;
    }

    private ControllerBlock getBlock(int blockId) {
        return blockLookup.get(blockId);
    }

    private String findNextStationName() {
        ControllerBlock nextBlock = getBlock(currentBeacon.endId());
        if(currentBeacon != null && nextBlock != null && nextBlock.isStation()){
            return nextBlock.stationName();
        }
        return "Awaiting Beacon..";
    }



    @Override
    public void setEmergencyBrake(boolean brake) {
        try {
            outputStream.writeObject("setEmergencyBrake");
            outputStream.writeObject(brake);
            outputStream.flush();
            // Wait for response if needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPassengerEBrake() {
        this.passengerEBrake = true;
        this.emergencyBrake = true;
        try {
            outputStream.writeObject("setPassengerEBrake");
            outputStream.flush();
            // Wait for response if needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public int getID() {
        return 0;
    }

    @Override
    public double getPower() {
        return 0;
    }

    @Override
    public double getKi() {
        return 0;
    }

    @Override
    public double getKp() {
        return 0;
    }

    @Override
    public double getOverrideSpeed() {
        return 0;
    }

    @Override
    public double getSpeedLimit() {
        return 0;
    }

    @Override
    public boolean getServiceBrake() {
        return false;
    }

    @Override
    public boolean getEmergencyBrake() {
        return false;
    }

    @Override
    public boolean getAutomaticMode() {
        return false;
    }

    @Override
    public boolean getExtLights() {
        return false;
    }

    @Override
    public boolean getIntLights() {
        return false;
    }

    @Override
    public boolean getLeftDoors() {
        return false;
    }

    @Override
    public boolean getRightDoors() {
        return false;
    }

    @Override
    public double getSetTemperature() {
        return 0;
    }

    @Override
    public double getCurrentTemperature() {
        return 0;
    }

    @Override
    public double getCommandSpeed() {
        return 0;
    }

    @Override
    public int getAuthority() {
        return 0;
    }

    @Override
    public void setSetTemperature(double newTemperature) {

    }

    @Override
    public boolean getAnnouncements() {
        return false;
    }

    @Override
    public boolean getSignalFailure() {
        return false;
    }

    @Override
    public boolean getBrakeFailure() {
        return false;
    }

    @Override
    public boolean getPowerFailure() {
        return false;
    }

    @Override
    public void delete() {

    }

    // Implement other methods similarly

    @Override
    public boolean isHW() {
        return true; // Indicates that this is a hardware implementation
    }



    @Override
    public double getGrade() {
        return 0;
    }



    @Override
    public TrainModel getTrain() {
        return null;
    }



    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    public Beacon getBeacon() {
        return null;
    }




    //Here only for the sake of the interface
    //GUI on the main program is inactive and cannot mutate this object
    @Override
    public void setValue(Enum<?> propertyName, Object newValue) {

    }

    //Will just hold a greyed out subject
    @Override
    public TrainControllerSubject getSubject() {
        return subject;
    }
}