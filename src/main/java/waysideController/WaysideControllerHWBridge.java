package waysideController;

import Common.WaysideController;
import Utilities.BlockInfo;
import purejavacomm.*;
//import org.openmuc.jrxtx.Parity;
//import org.openmuc.jrxtx.SerialPort;
//import org.openmuc.jrxtx.SerialPortBuilder;

import java.io.*;
import java.util.List;
import java.util.StringTokenizer;

public class WaysideControllerHWBridge extends WaysideControllerImpl {

    private final SerialPort serialPort;
    private final BufferedReader inputStream;
    private final PrintStream outputStream;

    public WaysideControllerHWBridge(int id, int trackLine, String COMPort) {
        super(id, trackLine);

        try {
            CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(COMPort);
            serialPort = (SerialPort) portId.open("WaysideController", 2000);
            serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            inputStream = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            outputStream = new PrintStream(serialPort.getOutputStream(), true);

            SerialCheckerThread thread = new SerialCheckerThread();
            thread.start();

            System.out.println("Send: runPLC=true");
            outputStream.println("runPLC=true");
        }
        catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | IOException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public void setMaintenanceModeNoUpdate(boolean maintenanceMode) {
        super.setMaintenanceModeNoUpdate(maintenanceMode);
        System.out.println("Send: maintenanceMode="+maintenanceMode);
        outputStream.println("maintenanceMode="+maintenanceMode);
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
            super.setSwitchState(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("trafficLightList")) {
            String[] setValues = values[1].split(":");
            super.setTrafficLightState(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }
        else if (values[0].equals("crossingList")) {
            String[] setValues = values[1].split(":");
            super.setCrossingState(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
        }
    }

    protected BufferedReader getInputStream() {
        return inputStream;
    }

    private class SerialCheckerThread extends Thread {

        public SerialCheckerThread() {
            super("SerialCheckerThread");
        }
        @Override
        public void run() {
            try {
                while (true) {
                    if (inputStream.ready()) {
                        parseCOMMessage(inputStream.readLine());
                    }
                    Thread.sleep(100);
                }
            }
            catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
