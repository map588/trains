package waysideController;

import Utilities.ParsedBasicBlocks;
import Utilities.Records.BasicBlock;
import Utilities.Enums.Lines;
import com.fazecast.jSerialComm.SerialPort;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class WaysideControllerHW implements PLCRunner {

    private boolean maintenanceMode = false;
    private String trackLine;
    private final Map<Integer, WaysideBlock> blockMap = new HashMap<>();
    protected final BufferedReader bufferedReader;
    private final PrintStream outputStream;
    private final File plcFile;
    private final PLCProgram[] plcPrograms;
    public WaysideControllerHW(String comPort) {
        plcPrograms = new PLCProgram[1];
        plcPrograms[0] = new PLCProgram(this);
        plcFile = new File("PLC.plc");

        SerialPort port = SerialPort.getCommPort(comPort);
        port.setComPortParameters(19200, 8, 1, 0);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); // block until bytes can be written
        port.openPort();
        bufferedReader = new BufferedReader(new InputStreamReader(port.getInputStream()));
        outputStream = new PrintStream(port.getOutputStream(), true);
    }

    /**
     * @param blockID
     * @param switchState
     */
    @Override
    public void setSwitchPLC(int blockID, boolean switchState) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen() && block.getSwitchState() != switchState) {
            block.setSwitchState(switchState);
            System.out.println("Send: switchState="+blockID+":"+switchState);
            outputStream.println("switchState="+blockID+":"+switchState);
        }
    }

    /**
     * @param blockID
     * @param lightState
     */
    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen() && block.getSwitchState() != lightState) {
            block.setSwitchState(lightState);
            System.out.println("Send: trafficLight=" + blockID + ":" + lightState);
            outputStream.println("trafficLight=" + blockID + ":" + lightState);
        }
    }

    /**
     * @param blockID
     * @param crossingState
     */
    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen() && block.getSwitchState() != crossingState) {
            block.setSwitchState(crossingState);
            System.out.println("Send: crossing=" + blockID + ":" + crossingState);
            outputStream.println("crossing=" + blockID + ":" + crossingState);
        }
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen() && block.getSwitchState() != auth) {
            block.setSwitchState(auth);
            System.out.println("Send: auth=" + blockID + ":" + auth);
            outputStream.println("auth=" + blockID + ":" + auth);
        }
    }

    // TODO: Fix this implementation
    @Override
    public boolean getOutsideOccupancy(int blockID) {
//        System.out.println("Send: outsideOccupancy=" + blockID);
//        outputStream.println("outsideOccupancy=" + blockID);
        return false;
    }

    @Override
    public Map<Integer, WaysideBlock> getBlockMap() {
        return blockMap;
    }

    private void setupBlocks(int[] blockIDList, String trackLine) {
        blockMap.clear();
        // Parse the CSV file to get the blocks that the wayside controls
        ConcurrentSkipListMap<Integer, BasicBlock> blockList = ParsedBasicBlocks.getInstance().getBasicLine(Lines.valueOf(trackLine));
        for(int blockID : blockIDList) {
            WaysideBlock block = new WaysideBlock(blockList.get(blockID));
            blockMap.put(blockID, block);
        }
    }

    protected void parseCOMMessage(String message) {
        System.out.println("Received: " + message);
        String[] values = message.split("=", 2);

        switch (values[0]) {
            case "uploadPLC" -> {
                try (OutputStream out = new FileOutputStream(plcFile)) {
                    String line;
                    while (!(line = bufferedReader.readLine()).equals("%EndOfFile%")) {
                        out.write((line + "\n").getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("PLC Uploaded");
                plcPrograms[0].loadPLC(plcFile.getAbsolutePath());
            }
            case "setLine" -> {
                trackLine = values[1];
            }
            case "blockList" -> {
                String[] blockListStrings = values[1].split(",");
                int[] blockList = new int[blockListStrings.length];
                for (int i = 0; i < blockListStrings.length; i++) {
                    blockList[i] = Integer.parseInt(blockListStrings[i]);
                }
                setupBlocks(blockList, trackLine);
            }
            case "maintenanceMode" -> {
                maintenanceMode = Boolean.parseBoolean(values[1]);
            }
            case "blockMaintenance" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setBlockMaintenanceState(Boolean.parseBoolean(setValues[1]));
            }
            case "occupancy" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setOccupied(Boolean.parseBoolean(setValues[1]));
            }
            case "switchState" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setSwitchState(Boolean.parseBoolean(setValues[1]));
            }
            case "crossing" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setCrossingState(Boolean.parseBoolean(setValues[1]));
            }
            case "trafficLight" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setLightState(Boolean.parseBoolean(setValues[1]));
            }
            case "speed" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setSpeed(Double.parseDouble(setValues[1]));
            }
            case "auth" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setBooleanAuth(Boolean.parseBoolean(setValues[1]));
            }
            case "runPLC" -> {
                if(!maintenanceMode && !blockMap.isEmpty() && plcPrograms[0] != null)
                    plcPrograms[0].run();
            }
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("Starting Wayside Controller");
        WaysideControllerHW controller = new WaysideControllerHW("/dev/ttyS0");

        while (true) {
            if (controller.bufferedReader.ready()) {
                controller.parseCOMMessage(controller.bufferedReader.readLine());
            }
        }
    }
}
