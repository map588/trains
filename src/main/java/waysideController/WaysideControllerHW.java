package waysideController;

import Utilities.CSVTokenizer;
import Utilities.TrueBlockInfo;
import com.fazecast.jSerialComm.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaysideControllerHW implements PLCRunner {

    private boolean maintenanceMode = false;
    private String trackLine;
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
        System.out.println("Send: switchState="+blockID+":"+switchState);
        outputStream.println("switchState="+blockID+":"+switchState);
    }

    /**
     * @param blockID
     * @param lightState
     */
    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        System.out.println("Send: trafficLight="+blockID+":"+lightState);
        outputStream.println("trafficLight="+blockID+":"+lightState);
    }

    /**
     * @param blockID
     * @param crossingState
     */
    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        System.out.println("Send: crossing="+blockID+":"+crossingState);
        outputStream.println("crossing="+blockID+":"+crossingState);
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
        System.out.println("Send: auth="+blockID+":"+auth);
        outputStream.println("auth="+blockID+":"+auth);
    }

    @Override
    public Map<Integer, WaysideBlock> getBlockMap() {
        return blockMap;
    }

    private void setupBlocks(int[] blockIDList) {
        blockMap.clear();
        List<TrueBlockInfo> fullBlockList = CSVTokenizer.blockList.get(trackLine);
        for(int blockID : blockIDList) {
            blockMap.put(blockID, new WaysideBlock(fullBlockList.get(blockID)));
        }
    }

    protected void parseCOMMessage(String message) {
        System.out.println("Received: " + message);
        String[] values = message.split("=", 2);

        switch (values[0]) {
            case "setLine" -> {
                trackLine = values[1];
            }
            case "blockList" -> {
                String[] blockListStrings = values[1].split(",");
                int[] blockList = new int[blockListStrings.length];
                for (int i = 0; i < blockListStrings.length; i++) {
                    blockList[i] = Integer.parseInt(blockListStrings[i]);
                }
                setupBlocks(blockList);
            }
            case "maintenanceMode" -> {
                maintenanceMode = Boolean.parseBoolean(values[1]);
            }
            case "occupancy" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setOccupied(Boolean.parseBoolean(setValues[1]));
            }
            case "switchState" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setSwitchState(Boolean.parseBoolean(setValues[1]));
            }
            case "switchRequestedState" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setSwitchRequest(Boolean.parseBoolean(setValues[1]));
            }
            case "auth" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setAuthority(Boolean.parseBoolean(setValues[1]));
            }
        }

        if(!maintenanceMode && blockMap.size() > 0)
            plcProgram.runBlueLine();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        CSVTokenizer csv = new CSVTokenizer();
        csv.setCSVFile("src/main/java/Utilities/BlueLine.csv");
        CSVTokenizer.parseCSVToTrueBlockInfo("BlueLine");

        System.out.println("Starting Wayside Controller");
        WaysideControllerHW controller = new WaysideControllerHW("/dev/ttyS0");

        while (true) {
            if (controller.inputStream.ready()) {
                controller.parseCOMMessage(controller.inputStream.readLine());
            }
        }
    }
}
