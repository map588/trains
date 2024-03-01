package waysideController;

import com.fazecast.jSerialComm.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class WaysideControllerHW implements PLCRunner {

    private boolean maintenanceMode = false;
    private final Map<Integer, WaysideBlock> blockMap = new HashMap<>();
    protected final BufferedReader inputStream;
    private final PrintStream outputStream;
    private PLCProgram plcProgram;
    public WaysideControllerHW(String comPort) {
        plcProgram = new PLCProgram(this);

        SerialPort port = SerialPort.getCommPort(comPort);
        port.setComPortParameters(19200, 8, 1, 0);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); // block until bytes can be written
        port.openPort();
        inputStream = new BufferedReader(new InputStreamReader(port.getInputStream()));
        outputStream = new PrintStream(port.getOutputStream(), true);
    }

    /**
     * @param blockID
     * @param switchState
     */
    @Override
    public void setSwitchPLC(int blockID, boolean switchState) {
        System.out.println("Send: switchStateList="+blockID+":"+switchState);
        outputStream.println("switchStateList="+blockID+":"+switchState);
    }

    /**
     * @param blockID
     * @param lightState
     */
    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        System.out.println("Send: trafficLightList="+blockID+":"+lightState);
        outputStream.println("trafficLightList="+blockID+":"+lightState);
    }

    /**
     * @param blockID
     * @param crossingState
     */
    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        System.out.println("Send: crossingList="+blockID+":"+crossingState);
        outputStream.println("crossingList="+blockID+":"+crossingState);
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
        System.out.println("Send: authList="+blockID+":"+auth);
        outputStream.println("authList="+blockID+":"+auth);
    }

    @Override
    public Map<Integer, WaysideBlock> getBlockMap() {
        return blockMap;
    }

    protected void parseCOMMessage(String message) {
        System.out.println("Received: " + message);
        String[] values = message.split("=", 2);

        if (values[0].equals("maintenanceMode")) {
            maintenanceMode = Boolean.parseBoolean(values[1]);
        }
        else if (values[0].equals("occupancyList")) {
            String[] setValues = values[1].split(":");
//            plcProgram.setOccupancy(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
            blockMap.get(Integer.parseInt(setValues[0])).setOccupied(Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("switchStateList")) {
            String[] setValues = values[1].split(":");
//            plcProgram.setSwitchState(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
            blockMap.get(Integer.parseInt(setValues[0])).setSwitchState(Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("switchRequestedStateList")) {
            String[] setValues = values[1].split(":");
//            plcProgram.setSwitchRequest(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
            blockMap.get(Integer.parseInt(setValues[0])).setSwitchRequest(Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("authList")) {
            String[] setValues = values[1].split(":");
//            plcProgram.setAuthState(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
            blockMap.get(Integer.parseInt(setValues[0])).setAuthority(Boolean.parseBoolean(setValues[1]));
        }

        if(!maintenanceMode)
            plcProgram.runBlueLine();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Starting Wayside Controller");
        WaysideControllerHW controller = new WaysideControllerHW("/dev/ttyS0");

        while (true) {
            if (controller.inputStream.ready()) {
                controller.parseCOMMessage(controller.inputStream.readLine());
            }
        }
    }
}
