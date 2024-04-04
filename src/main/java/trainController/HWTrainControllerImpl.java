package trainController;

import Common.TrainController;
import Common.TrainModel;
import Utilities.Records.Beacon;
import Utilities.Records.UpdatedTrainValues;
import com.fazecast.jSerialComm.SerialPort;

public class HWTrainControllerImpl implements TrainController {
    private SerialPort raspberryPiPort;


    public HWTrainControllerImpl() {
        // Find the Raspberry Pi serial port
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getDescriptivePortName().contains("Raspberry Pi")) {
                raspberryPiPort = port;
                break;
            }
        }

        if (raspberryPiPort != null) {
            raspberryPiPort.openPort();
            raspberryPiPort.setComPortParameters(115200, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        } else {
            System.out.println("Raspberry Pi not found.");
        }
    }

    private void sendCommand(String command) {
        if (raspberryPiPort != null && raspberryPiPort.isOpen()) {
            raspberryPiPort.writeBytes((command + "\n").getBytes(), command.length() + 1);
        }
    }

    //Non-vital variables can be held by the software controller
    //Vital calculations will be done by the hardware controller
    int ID;
    boolean intLights;
    boolean extLights;
    boolean leftDoors;
    boolean rightDoors;

    boolean serviceBrake;
    boolean emergencyBrake;

    boolean automaticMode;

    double powerOutput;


    @Override
    public void setAuthority(int authority) {
        sendCommand("setAuthority:" + authority);
    }

    @Override
    public void setCommandSpeed(double speed) {
        sendCommand("setCommandSpeed:" + speed);
    }

    @Override
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        sendCommand("setEmergencyBrake:" + brake);
    }

    @Override
    public int getID() {
        return this.ID;
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
    public double getCurrentTemperature() {
        return 0;
    }

    @Override
    public double getSetTemperature() {
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
    public void setCurrentTemperature(double newTemperature) {

    }

    @Override
    public TrainControllerSubject getSubject() {
        return null;
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

    @Override
    public double calculatePower(double currentVelocity) {
        return 0;
    }

    @Override
    public double getGrade() {
        return 0;
    }

    // Implement the remaining methods of the TrainController interface
    // ...

    @Override
    public void updateBeacon(Beacon beacon) {
        // Implement the logic to update the beacon information
        // ...
    }
    @Override
    public UpdatedTrainValues sendUpdatedTrainValues(){ return null;}

    @Override
    public TrainModel getTrain() {
        return null;
    }

    @Override
    public void setValue(String propertyName, Object newValue) {

    }
}