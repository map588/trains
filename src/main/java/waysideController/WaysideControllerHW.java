package waysideController;

import com.fazecast.jSerialComm.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class WaysideControllerHW implements PLCRunner {

    protected final BufferedReader inputStream;
    private final PrintStream outputStream;
    private PLCProgram plcProgram;
    public WaysideControllerHW(String comPort) {
        plcProgram = new PLCProgram(this);
//        try {
//            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(comPort);
//            SerialPort serialPort = (SerialPort) portId.open("WaysideController", 2000);
//            serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//            inputStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
//            outputStream = new PrintStream(serialPort.getOutputStream(), true);
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }

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
        outputStream.println("switchStateList="+blockID+":"+switchState);
    }

    /**
     * @param blockID
     * @param lightState
     */
    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        outputStream.println("trafficLightList="+blockID+":"+lightState);
    }

    /**
     * @param blockID
     * @param crossingState
     */
    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        outputStream.println("crossingList="+blockID+":"+crossingState);
    }

    protected void parseCOMMessage(String message) {
        System.out.println("Received: " + message);
        String[] values = message.split("=", 2);

        if (values[0].equals("occupancyList")) {
            String[] setValues = values[1].split(":");
            plcProgram.setOccupancy(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("switchStateList")) {
            String[] setValues = values[1].split(":");
            plcProgram.setSwitchState(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("switchRequestedStateList")) {
            String[] setValues = values[1].split(":");
            plcProgram.setSwitchRequest(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }

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
