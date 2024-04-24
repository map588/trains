package waysideController;

import Common.CTCOffice;
import Common.TrackModel;
import Common.WaysideController;
import Framework.Support.Notifier;
import Utilities.BasicBlockParser;
import Utilities.Records.BasicBlock;
import Utilities.Enums.Lines;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import static Utilities.Constants.RESUME_TRAIN_SIGNAL;
import static Utilities.Constants.STOP_TRAIN_SIGNAL;
import static waysideController.Properties.PLCName_p;
import static waysideController.Properties.maintenanceMode_p;

public class WaysideControllerHWBridge implements WaysideController, Notifier {

    private final Logger logger = LoggerFactory.getLogger(WaysideControllerHWBridge.class);

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
    private final TrackModel trackModel;
    private final CTCOffice ctcOffice;

    private boolean isReady = true;

    private final SerialPort port;
    private final PrintStream printStream;

    public WaysideControllerHWBridge(int id, Lines trackLine, int[] blockIDList, int[] outsideOccupancyBlockList, String comPort, TrackModel trackModel, CTCOffice ctcOffice) {
        this.trackModel = trackModel;
        this.ctcOffice = ctcOffice;
        this.id = id;
        this.trackLine = trackLine;

        subject = new WaysideControllerSubject(this);

        // Parse the CSV file to get the blocks that the wayside controls
        ConcurrentSkipListMap<Integer, BasicBlock> blockList = BasicBlockParser.getInstance().getBasicLine(trackLine).toConcurrentSkipListMap();
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
        for(int i = 0; i < blockIDList.length; i++) {
            printStream.print(blockIDList[i] + ",");
        }
        for(int i = 0; i < outsideOccupancyBlockList.length-1; i++) {
            printStream.print(outsideOccupancyBlockList[i]);
            if(i < outsideOccupancyBlockList.length - 1) {
                printStream.print(",");
            }
        }
        printStream.println(outsideOccupancyBlockList[outsideOccupancyBlockList.length-1]);

    }

    public WaysideControllerHWBridge(int id, Lines trackLine, int[] blockIDList, int[] outsideOccupancyBlockList, String comPort, TrackModel trackModel, CTCOffice ctcOffice, String plcPath) {
        this(id, trackLine, blockIDList, outsideOccupancyBlockList, comPort, trackModel, ctcOffice);
        loadPLC(new File(plcPath));
    }

    @Override
    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
        notifyChange(maintenanceMode_p, maintenanceMode);
        subject.updateActivePLCProp();

        logger.info("Send: maintenanceMode="+maintenanceMode);
        printStream.println("maintenanceMode="+maintenanceMode);
    }

    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        if(maintenanceMode || blockMap.get(blockID).inMaintenance()) {
            blockMap.get(blockID).setSwitchState(switchState);

            logger.info("Send: switchState=" + blockID + ":" + switchState);
            printStream.println("switchState=" + blockID + ":" + switchState);

            if (trackModel != null)
                trackModel.setSwitchState(blockID, switchState);
            if (ctcOffice != null)
                ctcOffice.setSwitchState(trackLine, blockID, switchState);
        }
    }

    @Override
    public void maintenanceSetAuthority(int blockID, boolean auth) {
        WaysideBlock block = blockMap.get(blockID);
        if(maintenanceMode || block.inMaintenance()) {
            block.setBooleanAuth(auth);

            logger.info("Send: auth=" + blockID + ":" + auth);
            printStream.println("auth=" + blockID + ":" + auth);

            if(trackModel != null && block.isOccupied()) {
                if (!auth) {
                    logger.info("Stoppping train");
                    trackModel.setTrainAuthority(blockID, STOP_TRAIN_SIGNAL);
                }
                else {
                    logger.info("Resuming train");
                    trackModel.setTrainAuthority(blockID, RESUME_TRAIN_SIGNAL);
                }
            }
        }
    }

    @Override
    public void maintenanceSetTrafficLight(int blockID, boolean lightState) {
        if(maintenanceMode || blockMap.get(blockID).inMaintenance()) {
            blockMap.get(blockID).setLightState(lightState);

            logger.info("Send: trafficLight=" + blockID + ":" + lightState);
            printStream.println("trafficLight=" + blockID + ":" + lightState);

            if (trackModel != null)
                trackModel.setLightState(blockID, lightState);
            if (ctcOffice != null)
                ctcOffice.setLightState(trackLine, blockID, lightState);
        }
    }

    @Override
    public void maintenanceSetCrossing(int blockID, boolean crossingState) {
        if(maintenanceMode || blockMap.get(blockID).inMaintenance()) {
            blockMap.get(blockID).setCrossingState(crossingState);

            logger.info("Send: crossing=" + blockID + ":" + crossingState);
            printStream.println("crossing=" + blockID + ":" + crossingState);

            if (trackModel != null)
                trackModel.setCrossing(blockID, crossingState);
            if (ctcOffice != null)
                ctcOffice.setCrossingState(trackLine, blockID, crossingState);
        }
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
        logger.info("CTCSendAuthority: " + blockID + " " + blockCount);

        if(blockMap.get(blockID).isOccupied() && trackModel != null) {
            trackModel.setTrainAuthority(blockID, blockCount);
        }
    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean occupied) {
        WaysideBlock block = blockMap.get(blockID);
        if(!block.inMaintenance() && block.isOccupied() != occupied) {
            blockMap.get(blockID).setOccupied(occupied);

            logger.info("Send: occupancy=" + blockID + ":" + occupied);
            printStream.println("occupancy=" + blockID + ":" + occupied);

            if (ctcOffice != null)
                ctcOffice.setBlockOccupancy(trackLine, blockID, occupied);

            if (occupied && !blockMap.get(blockID).getBooleanAuth()) {
                trackModel.setTrainAuthority(blockID, STOP_TRAIN_SIGNAL);
            }
        }
    }

    @Override
    public void CTCSendSpeed(int blockID, double speed) {
        logger.info("CTCSendSpeed: " + blockID + " " + speed);

        if(blockMap.get(blockID).isOccupied() && trackModel != null) {
            trackModel.setCommandedSpeed(blockID, speed);
        }
    }

    @Override
    public void CTCChangeBlockMaintenanceState(int blockID, boolean maintenanceState) {
        WaysideBlock block = blockMap.get(blockID);
        boolean currentState = block.inMaintenance();

        if(currentState != maintenanceState && (!maintenanceState || !block.isOccupied())) {
            block.setBlockMaintenanceState(maintenanceState);
            block.setOccupied(maintenanceState);
            printStream.println("occupancy=" + blockID + ":" + maintenanceState);
            if(ctcOffice != null)
                ctcOffice.setBlockMaintenance(trackLine, blockID, maintenanceState);
//                ctcOffice.setBlockOccupancy(Lines.GREEN, blockID, maintenanceState);
        }
    }

    @Override
    public void CTCEnableAllBlocks() {
        for(WaysideBlock block : blockMap.values()) {
            if(!block.inMaintenance()) {
                block.setBlockMaintenanceState(false);
                block.setOccupied(false);
                printStream.println("occupancy=" + block.getBlockID() + ":" + false);
                if(ctcOffice != null)
                    ctcOffice.setBlockMaintenance(trackLine, block.getBlockID(), false);
//                    ctcOffice.setBlockOccupancy(Lines.GREEN, block.getBlockID(), false);
            }
        }
    }

    @Override
    public void runPLC() {
        if(isReady) {
            isReady = false;
            printStream.println("runPLC");
        }
//        else {
//            logger.info("PLC not ready");
//        }
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

    public void sendExternalOccupancy(int blockID, boolean occupied) {
        logger.info("Send: occupancy="+blockID+":"+occupied);
        printStream.println("occupancy="+blockID+":"+occupied);
    }

    private void parseCOMMessage(String message) {
        if(message.equals("ready")) {
            isReady = true;
            return;
        }

        logger.info("Received: " + message);

        String[] values = message.split("=", 2);
        String[] setValues = values[1].split(":");
        int blockID = Integer.parseInt(setValues[0]);

        switch (values[0]) {
            case "switchState" -> {
                boolean boolVal = Boolean.parseBoolean(setValues[1]);
                blockMap.get(blockID).setSwitchState(boolVal);
                if(trackModel != null)
                    trackModel.setSwitchState(blockID, boolVal);
                if(ctcOffice != null)
                    ctcOffice.setSwitchState(trackLine, blockID, boolVal);
            }
            case "trafficLight" -> {
                boolean boolVal = Boolean.parseBoolean(setValues[1]);
                blockMap.get(blockID).setLightState(boolVal);
                if(trackModel != null)
                    trackModel.setLightState(blockID, boolVal);
                if(ctcOffice != null)
                    ctcOffice.setLightState(trackLine, blockID, boolVal);
            }
            case "crossing" -> {
                boolean boolVal = Boolean.parseBoolean(setValues[1]);
                blockMap.get(blockID).setCrossingState(boolVal);
                if(trackModel != null)
                    trackModel.setCrossing(blockID, boolVal);
                if(ctcOffice != null)
                    ctcOffice.setCrossingState(trackLine, blockID, boolVal);
            }
            case "auth" -> {
                boolean boolVal = Boolean.parseBoolean(setValues[1]);
                WaysideBlock block = blockMap.get(blockID);
                block.setBooleanAuth(boolVal);
                if(trackModel != null && block.isOccupied()) {
                    if (!boolVal) {
                        trackModel.setTrainAuthority(blockID, STOP_TRAIN_SIGNAL);
                    }
                    else {
                        trackModel.setTrainAuthority(blockID, RESUME_TRAIN_SIGNAL);
                    }
                }
            }
        }
    }

    public String getPort() {
        return port.getSystemPortName();
    }

    public String toString() {
        return trackLine.toString() + " line HW Wayside Controller #" + id;
    }

    public void notifyChange(String propertyName, Object newValue) {
        logger.info("Variable: " + propertyName + " changed to " + newValue);
        if(!subject.isGUIUpdate) {
            subject.notifyChange(propertyName, newValue);
        }
    }
}
