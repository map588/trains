package waysideController;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import java.io.*;

public class WaysideControllerHWBridge extends WaysideControllerImpl {

    private final SerialPort port;
    private final BufferedReader inputStream;
    private final PrintStream outputStream;

    public WaysideControllerHWBridge(int id, int trackLine, String comPort) {
        super(id, trackLine);

        port = SerialPort.getCommPort(comPort);
        port.setComPortParameters(19200, 8, 1, 0);
//        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); // block until bytes can be written
        port.openPort();
        inputStream = new BufferedReader(new InputStreamReader(port.getInputStream()));
        outputStream = new PrintStream(port.getOutputStream(), true);

        port.addDataListener(new SerialPortMessageListener() {
            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
            @Override
            public byte[] getMessageDelimiter() { return new byte[] { (byte) '\n' }; }
            @Override
            public boolean delimiterIndicatesEndOfMessage() {
                return true;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                byte[] message = event.getReceivedData();
                parseCOMMessage(new String(message).trim());
            }
        });

        System.out.println("Send: runPLC=true");
        outputStream.println("runPLC=true");
    }

    @Override
    public void loadPLC(File PLC) {
        super.loadPLC(PLC);
    }

    @Override
    public void setMaintenanceMode(boolean maintenanceMode) {
        super.setMaintenanceMode(maintenanceMode);
        System.out.println("Send: maintenanceMode="+maintenanceMode);
        outputStream.println("maintenanceMode="+maintenanceMode);
    }

//    @Override
//    public void setMaintenanceModeNoUpdate(boolean maintenanceMode) {
//        super.setMaintenanceModeNoUpdate(maintenanceMode);
//        System.out.println("Send: maintenanceMode="+maintenanceMode);
//        outputStream.println("maintenanceMode="+maintenanceMode);
//    }

    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        System.out.println("Send: switchStateList="+blockID+":"+switchState);
        outputStream.println("switchStateList="+blockID+":"+switchState);
    }

    @Override
    public void maintenanceSetAuthority(int blockID, boolean auth) {
        System.out.println("Send: authList="+blockID+":"+auth);
        outputStream.println("authList="+blockID+":"+auth);
    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean occupied) {
        super.trackModelSetOccupancy(blockID, occupied);
        System.out.println("Send: occupancyList="+blockID+":"+occupied);
        outputStream.println("occupancyList="+blockID+":"+occupied);
    }

    @Override
    public void CTCRequestSwitchState(int blockID, boolean occupied) {
        super.CTCRequestSwitchState(blockID, occupied);
        System.out.println("Send: switchRequestedStateList="+blockID+":"+occupied);
        outputStream.println("switchRequestedStateList="+blockID+":"+occupied);
    }

    @Override
    public void runPLC() {

    }

    private void parseCOMMessage(String message) {
        System.out.println("Received: " + message);
        String[] values = message.split("=", 2);

        if (values[0].equals("maintenanceMode")) {
            super.setMaintenanceMode(Boolean.parseBoolean(values[1]));
        }
        else if (values[0].equals("switchStateList")) {
            String[] setValues = values[1].split(":");
            super.setSwitchPLC(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("trafficLightList")) {
            String[] setValues = values[1].split(":");
            super.setTrafficLightPLC(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("crossingList")) {
            String[] setValues = values[1].split(":");
            super.setCrossingPLC(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("authList")) {
            String[] setValues = values[1].split(":");
            super.setAuthorityPLC(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }
    }

    public String getPort() {
        return port.getSystemPortName();
    }

    public String toString() {
        return "HW Wayside Controller #" + getID();
    }
}
