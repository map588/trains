package waysideController;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import java.io.*;

public class WaysideControllerHWBridge_Old extends WaysideControllerImpl {

    private final SerialPort port;
    private final BufferedReader inputStream;
    private final PrintStream printStream;

    public WaysideControllerHWBridge_Old(int id, String trackLine, int[] blockIDList, String comPort) {
        super(id, trackLine, blockIDList);

        port = SerialPort.getCommPort(comPort);
        port.setComPortParameters(19200, 8, 1, 0);
//        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); // block until bytes can be written
        port.openPort();
        inputStream = new BufferedReader(new InputStreamReader(port.getInputStream()));
        printStream = new PrintStream(port.getOutputStream(), true);

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

        printStream.println("setLine="+trackLine);
        printStream.print("blockList=");
        for(int i = 0; i < blockIDList.length-1; i++) {
            printStream.print(blockIDList[i]);
            if(i < blockIDList.length - 1) {
                printStream.print(",");
            }
        }
        printStream.println(blockIDList[blockIDList.length-1]);

        System.out.println("Send: runPLC=true");
        printStream.println("runPLC=true");
    }

    @Override
    public void setMaintenanceMode(boolean maintenanceMode) {
        super.setMaintenanceMode(maintenanceMode);
        System.out.println("Send: maintenanceMode="+maintenanceMode);
        printStream.println("maintenanceMode="+maintenanceMode);
    }

    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        System.out.println("Send: switchState="+blockID+":"+switchState);
        printStream.println("switchState="+blockID+":"+switchState);
    }

    @Override
    public void maintenanceSetAuthority(int blockID, boolean auth) {
        System.out.println("Send: auth="+blockID+":"+auth);
        printStream.println("auth="+blockID+":"+auth);
    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean occupied) {
        super.trackModelSetOccupancy(blockID, occupied);
        System.out.println("Send: occupancy="+blockID+":"+occupied);
        printStream.println("occupancy="+blockID+":"+occupied);
    }

    @Override
    public void CTCRequestSwitchState(int blockID, boolean occupied) {
        super.CTCRequestSwitchState(blockID, occupied);
        System.out.println("Send: switchRequestedState="+blockID+":"+occupied);
        printStream.println("switchRequestedState="+blockID+":"+occupied);
    }

    @Override
    public void runPLC() {

    }

    @Override
    public void loadPLC(File PLC) {
        printStream.println("uploadPLC");
        try (InputStream in = new FileInputStream(PLC)) {
            in.transferTo(printStream);
            printStream.println("\n%EndOfFile%");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseCOMMessage(String message) {
        System.out.println("Received: " + message);
        String[] values = message.split("=", 2);

        switch (values[0]) {
            case "maintenanceMode" -> {
                super.setMaintenanceMode(Boolean.parseBoolean(values[1]));
            }
            case "switchState" -> {
                String[] setValues = values[1].split(":");
//                super.setSwitchPLC(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
                blockMap.get(Integer.parseInt(setValues[0])).setSwitchState(Boolean.parseBoolean(setValues[1]));
            }
            case "trafficLight" -> {
                String[] setValues = values[1].split(":");
//                super.setTrafficLightPLC(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
                blockMap.get(Integer.parseInt(setValues[0])).setLightState(Boolean.parseBoolean(setValues[1]));
            }
            case "crossing" -> {
                String[] setValues = values[1].split(":");
//                super.setCrossingPLC(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
                blockMap.get(Integer.parseInt(setValues[0])).setCrossingState(Boolean.parseBoolean(setValues[1]));
            }
            case "auth" -> {
                String[] setValues = values[1].split(":");
//                super.setAuthorityPLC(Integer.parseInt(setValues[0]), Boolean.parseBoolean(setValues[1]));
                blockMap.get(Integer.parseInt(setValues[0])).setBooleanAuth(Boolean.parseBoolean(setValues[1]));
            }
        }
    }

    public String getPort() {
        return port.getSystemPortName();
    }

    public String toString() {
        return "HW Wayside Controller #" + getID();
    }
}
