package trainController;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Records.Beacon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainModel.Records.UpdatedTrainValues;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TrainControllerHW implements TrainController {

    private static final Logger logger = LoggerFactory.getLogger(TrainControllerHW.class);

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private final TrainModel train;
    private final int id;

    private Beacon beacon;

    private double power;
    private double ki;
    private double kp;
    private double overrideSpeed;
    private double speedLimit;
    private boolean serviceBrake;
    private boolean emergencyBrake;
    private boolean automaticMode;
    private boolean extLights;
    private boolean intLights;
    private boolean leftDoors;
    private boolean rightDoors;
    private double setTemperature;
    private double currentTemperature;
    private double commandSpeed;
    private int authority;
    private boolean announcements;
    private boolean signalFailure;
    private boolean brakeFailure;
    private boolean powerFailure;

    private final TrainControllerSubject subject;

    public TrainControllerHW(TrainModel m, int id) {
        this.train = m;
        this.id = id;
//        try {
//            socket = new Socket("raspberrypi.local", 1234); // Replace with the Raspberry Pi's IP address and port
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.subject = new TrainControllerSubject(this);
    }

    @Override
    public void setAuthority(int authority) {
        this.authority = authority;
//        sendCommand("setAuthority " + authority);
//        receiveResponse();
    }

    @Override
    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        sendCommand("setCommandSpeed " + speed);
        receiveResponse();
    }

    @Override
    public void setCurrentTemperature(double temp) {
        this.currentTemperature = temp;
//        sendCommand("setCurrentTemperature " + temp);
//        receiveResponse();
    }

    @Override
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
//        sendCommand("setEmergencyBrake " + brake);
//        receiveResponse();
    }

    @Override
    public void setPassengerEBrake() {
        sendCommand("setPassengerEBrake");
        receiveResponse();
    }

    @Override
    public TrainControllerSubject getSubject() {
        return subject;
    }

    @Override
    public int getID() {
//        sendCommand("getID");
//        return Integer.parseInt(Objects.requireNonNull(receiveResponse()));
        return id;
    }

    @Override
    public double getPower() {
        return power;
    }

    @Override
    public double getKi() {
        return ki;
    }

    @Override
    public double getKp() {
        return kp;
    }

    @Override
    public double getOverrideSpeed() {
        return overrideSpeed;
    }

    @Override
    public double getSpeedLimit() {
        return speedLimit;
    }

    @Override
    public boolean getServiceBrake() {
        return serviceBrake;
    }

    @Override
    public boolean getEmergencyBrake() {
        return emergencyBrake;
    }

    @Override
    public boolean getAutomaticMode() {
        return automaticMode;
    }

    @Override
    public boolean getExtLights() {
        return extLights;
    }

    @Override
    public boolean getIntLights() {
        return intLights;
    }

    @Override
    public boolean getLeftDoors() {
        return leftDoors;
    }

    @Override
    public boolean getRightDoors() {
        return rightDoors;
    }

    @Override
    public double getSetTemperature() {
        return setTemperature;
    }

    @Override
    public double getCurrentTemperature() {
        return currentTemperature;
    }

    @Override
    public double getCommandSpeed() {
        return commandSpeed;
    }

    @Override
    public int getAuthority() {
        return authority;
    }

    @Override
    public void setSetTemperature(double newTemperature) {

    }

    @Override
    public boolean getAnnouncements() {
        return announcements;
    }

    @Override
    public boolean getSignalFailure() {
        return signalFailure;
    }

    @Override
    public boolean getBrakeFailure() {
        return brakeFailure;
    }

    @Override
    public boolean getPowerFailure() {
        return powerFailure;
    }

    // Implement the remaining methods from the TrainController interface
    // These methods will send commands to the Raspberry Pi and receive responses

    private void sendCommand(String command) {
        logger.warn("Sending command: {}", command);
       // out.println(command);
    }

    private String receiveResponse() {
        logger.warn("Receiving response {}", "not implemented");
//        try {
//            return in.readLine();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
        return "unimplemented response";
    }

    @Override
    public void delete() {
//        try {
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public boolean isHW() {
        return true;
    }

    @Override
    public double calculatePower(double currentVelocity) {
        return 0;
    }

    @Override
    public double getGrade() {
        return 0;
    }

    @Override
    public void updateBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    @Override
    public UpdatedTrainValues sendUpdatedTrainValues() {
        return null;
    }

    @Override
    public TrainModel getTrain() {
        return train;
    }

    @Override
    public void checkFailures(double power) {

    }

    @Override
    public double getSpeed() {
        return 0;
    }


    @Override
    public Beacon getBeacon() {
        return beacon;
    }

    @Override
    public void onBlock() {

    }

    @Override
    public void setValue(Enum<?> propertyName, Object newValue) {
        switch ((ControllerProperty) propertyName) {
            case POWER -> power = (double) newValue;
            case KI -> ki = (double) newValue;
            case KP -> kp = (double) newValue;
            case OVERRIDE_SPEED -> overrideSpeed = (double) newValue;
            case SPEED_LIMIT -> speedLimit = (double) newValue;
            case SERVICE_BRAKE -> serviceBrake = (boolean) newValue;
            case EMERGENCY_BRAKE -> emergencyBrake = (boolean) newValue;
            case AUTOMATIC_MODE -> automaticMode = (boolean) newValue;
            case EXT_LIGHTS -> extLights = (boolean) newValue;
            case INT_LIGHTS -> intLights = (boolean) newValue;
            case LEFT_DOORS -> leftDoors = (boolean) newValue;
            case RIGHT_DOORS -> rightDoors = (boolean) newValue;
            case SET_TEMPERATURE -> setTemperature = (double) newValue;
            case CURRENT_TEMPERATURE -> currentTemperature = (double) newValue;
            case COMMAND_SPEED -> commandSpeed = (double) newValue;
            case AUTHORITY -> authority = (int) newValue;
            case ANNOUNCEMENTS -> announcements = (boolean) newValue;
            case SIGNAL_FAILURE -> signalFailure = (boolean) newValue;
            case BRAKE_FAILURE -> brakeFailure = (boolean) newValue;
            case POWER_FAILURE -> powerFailure = (boolean) newValue;
        }
    }
}