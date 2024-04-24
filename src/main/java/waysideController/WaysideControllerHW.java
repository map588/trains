package waysideController;

import Utilities.Enums.Lines;
import Utilities.BasicBlockParser;
import Utilities.Records.BasicBlock;
import com.fazecast.jSerialComm.SerialPort;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WaysideControllerHW implements PLCRunner {

    private boolean maintenanceMode = false;
    private String trackLine;
    private final Map<Integer, WaysideBlock> blockMap = new HashMap<>();
    protected final BufferedReader bufferedReader;
    private final PrintStream outputStream;
    private final File plcFile;
    private final PLCProgram[] plcPrograms;
    private final Stack<PLCChange>[] plcResults;
    private Stack<PLCChange> currentPLCResult;
    public WaysideControllerHW(String comPort) {
        plcPrograms = new PLCProgram[2];
        plcPrograms[0] = new PLCProgram(this);
        plcPrograms[1] = new PLCProgram(this);
        plcResults = new Stack[2];
        plcResults[0] = new Stack<>();
        plcResults[1] = new Stack<>();

        plcFile = new File("plcFile.plc");

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

        if(!block.inMaintenance() && block.getSwitchState() != switchState) {
            currentPLCResult.push(new PLCChange("switch", blockID, switchState));
//            block.setSwitchState(switchState);
//            System.out.println("Send: switchState="+blockID+":"+switchState);
//            outputStream.println("switchState="+blockID+":"+switchState);
        }
    }

    private void outputSwitchPLC(int blockID, boolean switchState) {
        blockMap.get(blockID).setSwitchState(switchState);
        System.out.println("Send: switchState="+blockID+":"+switchState);
        outputStream.println("switchState="+blockID+":"+switchState);
    }

    /**
     * @param blockID
     * @param lightState
     */
    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        WaysideBlock block = blockMap.get(blockID);

        if(!block.inMaintenance() && block.getLightState() != lightState) {
            currentPLCResult.push(new PLCChange("light", blockID, lightState));
//            block.setSwitchState(lightState);
//            System.out.println("Send: trafficLight=" + blockID + ":" + lightState);
//            outputStream.println("trafficLight=" + blockID + ":" + lightState);
        }
    }

    private void outputTrafficLightPLC(int blockID, boolean lightState) {
        blockMap.get(blockID).setLightState(lightState);
        System.out.println("Send: trafficLight=" + blockID + ":" + lightState);
        outputStream.println("trafficLight=" + blockID + ":" + lightState);
    }

    /**
     * @param blockID
     * @param crossingState
     */
    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        WaysideBlock block = blockMap.get(blockID);

        if(!block.inMaintenance() && block.getCrossingState() != crossingState) {
            currentPLCResult.push(new PLCChange("crossing", blockID, crossingState));
//            block.setSwitchState(crossingState);
//            System.out.println("Send: crossing=" + blockID + ":" + crossingState);
//            outputStream.println("crossing=" + blockID + ":" + crossingState);
        }
    }

    private void outputCrossingPLC(int blockID, boolean crossingState) {
        blockMap.get(blockID).setCrossingState(crossingState);
        System.out.println("Send: crossing=" + blockID + ":" + crossingState);
        outputStream.println("crossing=" + blockID + ":" + crossingState);
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
        WaysideBlock block = blockMap.get(blockID);

        if(!block.inMaintenance() && block.getBooleanAuth() != auth) {
            currentPLCResult.push(new PLCChange("auth", blockID, auth));
//            block.setSwitchState(auth);
//            System.out.println("Send: auth=" + blockID + ":" + auth);
//            outputStream.println("auth=" + blockID + ":" + auth);
        }
    }

    private void outputAuthorityPLC(int blockID, boolean auth) {
        blockMap.get(blockID).setBooleanAuth(auth);
        System.out.println("Send: auth=" + blockID + ":" + auth);
        outputStream.println("auth=" + blockID + ":" + auth);
    }

    // TODO: Fix this implementation
    @Override
    public boolean getOutsideOccupancy(int blockID) {
//        System.out.println("Send: outsideOccupancy=" + blockID);
//        outputStream.println("outsideOccupancy=" + blockID);
        return false;
    }

    @Override
    public boolean getOutsideSwitch(int blockID) {
        return false;
    }

    @Override
    public Map<Integer, WaysideBlock> getBlockMap() {
        return blockMap;
    }

    private void setupBlocks(int[] blockIDList, String trackLine) {
        blockMap.clear();
        // Parse the CSV file to get the blocks that the wayside controls
        ConcurrentSkipListMap<Integer, BasicBlock> blockList = BasicBlockParser.getInstance().getBasicLine(Lines.valueOf(trackLine));
        for(int blockID : blockIDList) {
            WaysideBlock block = new WaysideBlock(blockList.get(blockID));
            blockMap.put(blockID, block);
        }
    }

    protected void parseCOMMessage(String message) {
        if(!message.equals("runPLC"))
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
                for(PLCProgram program : plcPrograms) {
                    program.loadPLC(plcFile.getAbsolutePath());
                }
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
            case "auth" -> {
                String[] setValues = values[1].split(":");
                blockMap.get(Integer.parseInt(setValues[0])).setBooleanAuth(Boolean.parseBoolean(setValues[1]));
            }
            case "runPLC" -> {
                if(!maintenanceMode && !blockMap.isEmpty() && plcPrograms[0] != null) {
                    runPLC();
                }
            }
            case "ping" -> {
                outputStream.println("WaysideHW");
            }
        }
    }

    private void runPLC() {
        for(int plcIndex = 0; plcIndex < plcPrograms.length; plcIndex++) {
            currentPLCResult = plcResults[plcIndex];
            plcPrograms[plcIndex].run();
//            System.out.println("PLC Results[" + plcIndex + "]: " + plcResults[plcIndex].size() + " " + currentPLCResult.size());
        }

        int changeSize = plcResults[0].size();
        for(int changeIndex = 0; changeIndex < changeSize; changeIndex++) {
            PLCChange change = plcResults[0].pop();

//            System.out.println("PLC Change: " + change.changeType() + " " + change.blockID() + " " + change.changeValue() + " " + plcResults[0].size());

            for(int plcIndex = 1; plcIndex < plcResults.length; plcIndex++) {
                PLCChange otherChange = plcResults[plcIndex].pop();

                if(!change.changeType().equals(otherChange.changeType()) ||
                        change.changeValue() != otherChange.changeValue() ||
                        change.blockID() != otherChange.blockID())
                    throw new RuntimeException("PLC programs are not in sync");
            }

            switch(change.changeType()) {
                case "switch" -> outputSwitchPLC(change.blockID(), change.changeValue());
                case "light" -> outputTrafficLightPLC(change.blockID(), change.changeValue());
                case "crossing" -> outputCrossingPLC(change.blockID(), change.changeValue());
                case "auth" -> outputAuthorityPLC(change.blockID(), change.changeValue());
                default -> throw new RuntimeException("Invalid PLC change type");
            }
        }

        outputStream.println("ready");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Executor executor = Executors.newSingleThreadExecutor();
        System.out.println("Starting Wayside Controller");
        WaysideControllerHW controller = new WaysideControllerHW("/dev/ttyS0");

        executor.execute(() ->{
            while (true) {
                try {
                    if (controller.bufferedReader.ready()) {
                        controller.parseCOMMessage(controller.bufferedReader.readLine());
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
