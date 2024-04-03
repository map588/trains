package waysideController;

import Common.WaysideController;
import Framework.Support.Notifier;
import Utilities.ParsedBasicBlocks;
import Utilities.Records.BasicBlock;
import Utilities.Enums.Lines;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import static waysideController.Properties.PLCName_p;
import static waysideController.Properties.maintenanceMode_p;

public class WaysideControllerHWBridge implements WaysideController, Notifier {

    // The ID of the wayside controller
    private final int id;

    // The name of the track line that the wayside controller is on
    private final Lines trackLine;

    // Whether the wayside controller is in maintenance mode
    private boolean maintenanceMode = false;

    // The map of blocks that the wayside controller controls
    protected final Map<Integer, WaysideBlock> blockMap = new HashMap<>();

    // The subject that the wayside controller is attached to for GUI updates
    private final WaysideControllerSubject subject;

    private final SerialPort port;
    private final PrintStream printStream;

    public WaysideControllerHWBridge(int id, Lines trackLine, int[] blockIDList, String comPort) {
        this.id = id;
        this.trackLine = trackLine;

        subject = new WaysideControllerSubject(this);

        // Parse the CSV file to get the blocks that the wayside controls
        ConcurrentSkipListMap<Integer, BasicBlock> blockList = ParsedBasicBlocks.getInstance().getBasicLine(trackLine).toConcurrentSkipListMap();
        for(int blockID : blockIDList) {
            WaysideBlock block = new WaysideBlock(blockList.get(blockID));
            blockMap.put(blockID, block);
            subject.addBlock(new WaysideBlockSubject(block));
        }

        port = SerialPort.getCommPort(comPort);
        port.setComPortParameters(19200, 8, 1, 0);
        port.openPort();
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

        System.out.println("Send: runPLC");
        printStream.println("runPLC");
    }

    public WaysideControllerHWBridge(int id, Lines trackLine, int[] blockIDList, String comPort, String plcPath) {
        this(id, trackLine, blockIDList, comPort);
        loadPLC(new File(plcPath));
    }

    @Override
    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
        notifyChange(maintenanceMode_p, maintenanceMode);
        subject.updateActivePLCProp();

        System.out.println("Send: maintenanceMode="+maintenanceMode);
        printStream.println("maintenanceMode="+maintenanceMode);
    }

    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        blockMap.get(blockID).setSwitchState(switchState);

        System.out.println("Send: switchState="+blockID+":"+switchState);
        printStream.println("switchState="+blockID+":"+switchState);
    }

    @Override
    public void maintenanceSetAuthority(int blockID, boolean auth) {
        blockMap.get(blockID).setBooleanAuth(auth);

        System.out.println("Send: auth="+blockID+":"+auth);
        printStream.println("auth="+blockID+":"+auth);
    }

    @Override
    public void maintenanceSetTrafficLight(int blockID, boolean lightState) {
        blockMap.get(blockID).setLightState(lightState);

        System.out.println("Send: trafficLight="+blockID+":"+lightState);
        printStream.println("trafficLight="+blockID+":"+lightState);
    }

    @Override
    public void maintenanceSetCrossing(int blockID, boolean crossingState) {
        blockMap.get(blockID).setCrossingState(crossingState);

        System.out.println("Send: crossing="+blockID+":"+crossingState);
        printStream.println("crossing="+blockID+":"+crossingState);
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public Map<Integer, WaysideBlock> getBlockMap() {
        return blockMap;
    }

    @Override
    public WaysideControllerSubject getSubject() {
        return subject;
    }

    @Override
    public void setValue(String propertyName, Object newValue) {
        switch(propertyName) {
            case maintenanceMode_p -> setMaintenanceMode((boolean) newValue);
            default -> System.err.println("Property " + propertyName + " not found");
        }
    }

    // TODO: implement these functions

    @Override
    public void CTCSendAuthority(int blockID, int blockCount) {

    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean occupied) {
        blockMap.get(blockID).setOccupied(occupied);

        System.out.println("Send: occupancy="+blockID+":"+occupied);
        printStream.println("occupancy="+blockID+":"+occupied);
    }

    @Override
    public void CTCSendSpeed(int blockID, double speed) {
        blockMap.get(blockID).setSpeed(speed);

        System.out.println("Send: speed="+blockID+":"+speed);
        printStream.println("speed="+blockID+":"+speed);
    }

    @Override
    public void CTCChangeBlockMaintenanceState(int blockID, boolean maintenanceState) {
        WaysideBlock block = blockMap.get(blockID);
        boolean currentState = block.isOpen();

        if(currentState != maintenanceState) {
            block.setBlockMaintenanceState(maintenanceState);
            trackModelSetOccupancy(blockID, !maintenanceState);

            System.out.println("Send: blockMaintenance="+blockID+":"+maintenanceState);
            printStream.println("blockMaintenance="+blockID+":"+maintenanceState);
        }
    }

    @Override
    public void CTCEnableAllBlocks() {
        for(WaysideBlock block : blockMap.values()) {
            if(!block.isOpen()) {
                block.setBlockMaintenanceState(true);
                trackModelSetOccupancy(block.getBlockID(), false);
            }
        }
    }

    @Override
    public void runPLC() {
        System.out.println("Send: runPLC");
        printStream.println("runPLC");
    }

    @Override
    public boolean isMaintenanceMode() {
        return maintenanceMode;
    }

    @Override
    public void loadPLC(File PLC) {
        notifyChange(PLCName_p, PLC.getName());
        subject.updateActivePLCProp();

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
            case "switchState" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setSwitchState(Boolean.parseBoolean(setValues[1]));
            }
            case "trafficLight" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setLightState(Boolean.parseBoolean(setValues[1]));
            }
            case "crossing" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setCrossingState(Boolean.parseBoolean(setValues[1]));
            }
            case "auth" -> {
                String[] setValues = values[1].split(":");
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

    public void notifyChange(String propertyName, Object newValue) {
        System.out.println("Variable: " + propertyName + " changed to " + newValue);
        if(!subject.isGUIUpdate) {
            subject.notifyChange(propertyName, newValue);
        }
    }
}
