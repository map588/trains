package trainController;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Records.Beacon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainModel.Records.UpdatedTrainValues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class RemoteTrainControllerImpl implements TrainController {

    private static final Logger logger = LoggerFactory.getLogger(RemoteTrainControllerImpl.class);

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public RemoteTrainControllerImpl() {
        try {
            socket = new Socket("raspberrypi.local", 1234); // Replace with the Raspberry Pi's IP address and port
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setAuthority(int authority) {
        sendCommand("setAuthority " + authority);
        receiveResponse();
    }

    @Override
    public void setCommandSpeed(double speed) {
        sendCommand("setCommandSpeed " + speed);
        receiveResponse();
    }

    @Override
    public void setCurrentTemperature(double temp) {
        sendCommand("setCurrentTemperature " + temp);
        receiveResponse();
    }

    @Override
    public void setEmergencyBrake(boolean brake) {
        sendCommand("setEmergencyBrake " + brake);
        receiveResponse();
    }

    @Override
    public TrainControllerSubject getSubject() {
        // Implement this method based on your requirements
        // You may need to modify the TrainControllerSubject class to support remote communication
        return null;
    }

    @Override
    public int getID() {
        sendCommand("getID");
        return Integer.parseInt(Objects.requireNonNull(receiveResponse()));
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

    // Implement the remaining methods from the TrainController interface
    // These methods will send commands to the Raspberry Pi and receive responses

    private void sendCommand(String command) {
        out.println(command);
    }

    private String receiveResponse() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    }

    @Override
    public UpdatedTrainValues sendUpdatedTrainValues() {
        return null;
    }

    @Override
    public TrainModel getTrain() {
        return null;
    }

    @Override
    public boolean getLeftPlatform() {
        return false;
    }

    @Override
    public String getNextStationName() {
        return "";
    }

    @Override
    public double getSpeed() {
        return 0;
    }

    @Override
    public boolean getRightPlatform() {
        return false;
    }

    @Override
    public boolean getInTunnel() {
        return false;
    }

    @Override
    public Beacon getBeacon() {
        return null;
    }

    @Override
    public void onBlock() {

    }

    @Override
    public void setValue(Enum<?> propertyName, Object newValue) {

    }
}