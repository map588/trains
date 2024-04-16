package waysideController;

import Common.CTCOffice;
import Common.TrackModel;
import Common.WaysideController;
import Framework.Simulation.WaysideSystem;
import Framework.Support.Notifier;
import Utilities.Enums.Lines;
import Utilities.GlobalBasicBlockParser;
import Utilities.Records.BasicBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentSkipListMap;

import static Utilities.Constants.RESUME_TRAIN_SIGNAL;
import static Utilities.Constants.STOP_TRAIN_SIGNAL;
import static waysideController.Properties.PLCName_p;
import static waysideController.Properties.maintenanceMode_p;

public class WaysideControllerImpl implements WaysideController, PLCRunner, Notifier {

    private final Logger logger = LoggerFactory.getLogger(WaysideControllerImpl.class);

    // The ID of the wayside controller
    private final int id;

    // The name of the track line that the wayside controller is on
    private final Lines trackLine;

    // Whether the wayside controller is in maintenance mode
    private boolean maintenanceMode = false;

    // The map of blocks that the wayside controller controls
    protected final Map<Integer, WaysideBlock> blockMap = new HashMap<>();


    // The PLC program that the wayside controller is running
    private final PLCProgram[] plcPrograms;
    private final Stack<PLCChange>[] plcResults;
    private Stack<PLCChange> currentPLCResult;

    // The subject that the wayside controller is attached to for GUI updates
    private final WaysideControllerSubject subject;
    private final TrackModel trackModel;
    private final CTCOffice ctcOffice;


    /**
     * Constructor for the wayside controller
     * @param id The ID of the wayside controller (used mainly for internal identification)
     */
    public WaysideControllerImpl(int id, Lines trackLine, int[] blockIDList, TrackModel trackModel, CTCOffice ctcOffice) {
        this.trackModel = trackModel;
        this.ctcOffice = ctcOffice;
        this.id = id;
        this.trackLine = trackLine;

        subject = new WaysideControllerSubject(this);

        // Parse the CSV file to get the blocks that the wayside controls
        ConcurrentSkipListMap<Integer, BasicBlock> blockList = GlobalBasicBlockParser.getInstance().getBasicLine(trackLine);
        for(int blockID : blockIDList) {
            WaysideBlock block = new WaysideBlock(blockList.get(blockID));
            blockMap.put(blockID, block);
            subject.addBlock(new WaysideBlockSubject(block));
        }

        //TODO: Register this wayside with the track model and CTC office


        plcPrograms = new PLCProgram[2];
        plcResults = new Stack[2];
        plcPrograms[0] = new PLCProgram(this);
        plcPrograms[1] = new PLCProgram(this);
        plcResults[0] = new Stack<>();
        plcResults[1] = new Stack<>();
    }

    public WaysideControllerImpl(int id, Lines trackLine, int[] blockIDList, TrackModel trackModel, CTCOffice ctcOffice, String plcPath) {
        this(id, trackLine, blockIDList, trackModel, ctcOffice);
        File plcFile = new File(plcPath);
        this.loadPLC(plcFile);
    }


    /**
     * Load a PLC program into the wayside controller
     * @param PLC The PLC program to load
     */
    @Override
    public void loadPLC(File PLC) {
        logger.info("Loading PLC: {}", PLC.getName());

        for(PLCProgram plcProgram : plcPrograms) {
            plcProgram.loadPLC(PLC.getAbsolutePath());
        }
        notifyChange(PLCName_p, PLC.getName());
        subject.updateActivePLCProp();
    }

    /**
     * Run the PLC program
     */
    @Override
    public void runPLC() {
        if(!maintenanceMode) {

            for(int plcIndex = 0; plcIndex < plcPrograms.length; plcIndex++) {
                currentPLCResult = plcResults[plcIndex];
                plcPrograms[plcIndex].run();
//                System.out.println("PLC Results[" + plcIndex + "]: " + plcResults[plcIndex].size() + " " + currentPLCResult.size());
            }

            int changeSize = plcResults[0].size();
            for(int changeIndex = 0; changeIndex < changeSize; changeIndex++) {
                PLCChange change = plcResults[0].pop();

//                System.out.println("PLC Change: " + change.changeType + " " + change.blockID + " " + change.changeValue + " " + plcResults[0].size());

                for(int plcIndex = 1; plcIndex < plcResults.length; plcIndex++) {
                    PLCChange otherChange = plcResults[plcIndex].pop();

                    if(!change.changeType().equals(otherChange.changeType()) ||
                            change.changeValue() != otherChange.changeValue() ||
                            change.blockID() != otherChange.blockID()) {
                        throw new RuntimeException("PLC programs are not in sync");
                    }
                }

                switch(change.changeType()) {
                    case "switch" -> outputSwitchPLC(change.blockID(), change.changeValue());
                    case "light" -> outputTrafficLightPLC(change.blockID(), change.changeValue());
                    case "crossing" -> outputCrossingPLC(change.blockID(), change.changeValue());
                    case "auth" -> outputAuthorityPLC(change.blockID(), change.changeValue());
                    default -> throw new RuntimeException("Invalid PLC change type");
                }
            }
        }
    }

    /**
     * Get the current state of the wayside controller
     * @return The current state of the wayside controller
     */
    @Override
    public boolean isMaintenanceMode() {
        return this.maintenanceMode;
    }

    /**
     * Set the maintenance mode of the wayside controller
     * @param maintenanceMode The new maintenance mode of the wayside controller
     */
    @Override
    public void setMaintenanceMode(boolean maintenanceMode) {
        logger.info("Setting maintenance mode to {}", maintenanceMode);
        this.maintenanceMode = maintenanceMode;
        notifyChange(maintenanceMode_p, maintenanceMode);
        subject.updateActivePLCProp();
    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean occupied) {
        logger.info("Setting occupancy for block {} to {}", blockID, occupied);
        WaysideBlock block = blockMap.get(blockID);
        if(!block.inMaintenance() && block.isOccupied() != occupied) {
            block.setOccupied(occupied);

            if(ctcOffice != null)
                ctcOffice.setBlockOccupancy(trackLine, blockID, occupied);

            if(occupied && !blockMap.get(blockID).getBooleanAuth()) {
                trackModel.setCommandedSpeed(blockID, STOP_TRAIN_SIGNAL);
            }
        }
    }

    @Override
    public void CTCSendSpeed(int blockID, double speed) {
        logger.info("Sending speed {} to block {}", speed, blockID);

        if(trackModel != null) {
            trackModel.setCommandedSpeed(blockID, speed);
        }
    }

    // Sets the block access state for a block, updates simulated occupancy, and runs the PLC
    @Override
    public void CTCChangeBlockMaintenanceState(int blockID, boolean maintenanceState) {
        logger.info("Setting block {} maintenance state to {}", blockID, maintenanceState);
        WaysideBlock block = blockMap.get(blockID);
        boolean currentState = block.inMaintenance();

        if(currentState != maintenanceState) {
            block.setBlockMaintenanceState(maintenanceState);
            block.setOccupied(maintenanceState);
//            if(ctcOffice != null)
//                ctcOffice.setBlockOccupancy(Lines.GREEN, blockID, maintenanceState);
        }
    }

    // This method is a convenience method not reflected in the diagrams
    @Override
    public void CTCEnableAllBlocks() {
        logger.info("Put all blocks out of maintenance mode");
        for(WaysideBlock block : blockMap.values()) {
            if(!block.inMaintenance()) {
                block.setBlockMaintenanceState(true);
                block.setOccupied(false);
//                if(ctcOffice != null)
//                    ctcOffice.setBlockOccupancy(Lines.GREEN, block.getBlockID(), false);
            }
        }
    }


    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        if(maintenanceMode || blockMap.get(blockID).inMaintenance()) {
            logger.info("Setting switch {} to {}", blockID, switchState);
            blockMap.get(blockID).setSwitchState(switchState);
            if(trackModel != null)
                trackModel.setSwitchState(blockID, switchState);
            if(ctcOffice != null)
                ctcOffice.setSwitchState(trackLine, blockID, switchState);
        }
    }

    @Override
    public void maintenanceSetAuthority(int blockID, boolean auth) {
        logger.info("Setting authority for block {} to {}", blockID, auth);
        if(maintenanceMode || blockMap.get(blockID).inMaintenance()) {
            blockMap.get(blockID).setBooleanAuth(auth);
//            trackModel.setTrainAuthority(blockID, auth);
//            System.out.println("maintenanceSetAuthority: " + blockID + " " + auth);
        }
    }

    @Override
    public void maintenanceSetTrafficLight(int blockID, boolean lightState) {
        logger.info("Setting traffic light for block {} to {}", blockID, lightState);
        if(maintenanceMode || blockMap.get(blockID).inMaintenance()) {
            blockMap.get(blockID).setLightState(lightState);
            if(trackModel != null)
                trackModel.setLightState(blockID, lightState);
            if(ctcOffice != null)
                ctcOffice.setLightState(trackLine, blockID, lightState);
//            System.out.println("maintenanceSetTrafficLight: " + blockID + " " + lightState);
        }
    }

    @Override
    public void maintenanceSetCrossing(int blockID, boolean crossingState) {
        logger.info("Setting crossing for block {} to {}", blockID, crossingState);
        if(maintenanceMode || blockMap.get(blockID).inMaintenance()) {
            blockMap.get(blockID).setCrossingState(crossingState);
            if(trackModel != null)
                trackModel.setCrossing(blockID, crossingState);
            if(ctcOffice != null)
                ctcOffice.setCrossingState(trackLine, blockID, crossingState);
//            System.out.println("maintenanceSetCrossing: " + blockID + " " + crossingState);
        }
    }

    @Override
    public int getID() {
        return this.id;
    }

    public WaysideControllerSubject getSubject() {
        return subject;
    }

    @Override
    public void setSwitchPLC(int blockID, boolean switchState) {
        WaysideBlock block = blockMap.get(blockID);

        if(!block.inMaintenance() && block.getSwitchState() != switchState) {
//            System.out.println("setSwitchPLC: " + blockID + " " + switchState);
            currentPLCResult.push(new PLCChange("switch", blockID, switchState));
        }
    }

    private void outputSwitchPLC(int blockID, boolean switchState) {
        logger.info("Setting switch {} to {}", blockID, switchState);
        WaysideBlock block = blockMap.get(blockID);
        block.setSwitchState(switchState);
        if(trackModel != null)
            trackModel.setSwitchState(blockID, switchState);
        if(ctcOffice != null)
            ctcOffice.setSwitchState(trackLine, blockID, switchState);
    }

    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        WaysideBlock block = blockMap.get(blockID);

        if(!block.inMaintenance() && block.getLightState() != lightState) {
//            System.out.println("setTrafficLightPLC: " + blockID + " " + lightState);
            currentPLCResult.push(new PLCChange("light", blockID, lightState));
        }
    }

    private void outputTrafficLightPLC(int blockID, boolean lightState) {
        logger.info("Setting traffic light for block {} to {}", blockID, lightState);
        WaysideBlock block = blockMap.get(blockID);
        block.setLightState(lightState);
        if(trackModel != null)
            trackModel.setLightState(blockID, lightState);
        if(ctcOffice != null)
            ctcOffice.setLightState(trackLine, blockID, lightState);
    }

    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        WaysideBlock block = blockMap.get(blockID);

        if(!block.inMaintenance() && block.getCrossingState() != crossingState) {
//            System.out.println("setCrossingPLC: " + blockID + " " + crossingState);
            currentPLCResult.push(new PLCChange("crossing", blockID, crossingState));
        }
    }

    private void outputCrossingPLC(int blockID, boolean crossingState) {
        logger.info("Setting crossing for block {} to {}", blockID, crossingState);
        WaysideBlock block = blockMap.get(blockID);
        block.setCrossingState(crossingState);
        if(trackModel != null)
            trackModel.setCrossing(blockID, crossingState);
        if(ctcOffice != null)
            ctcOffice.setCrossingState(trackLine, blockID, crossingState);
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
        WaysideBlock block = blockMap.get(blockID);

        if(!block.inMaintenance() && block.getBooleanAuth() != auth) {
//            System.out.println("setAuthorityPLC: " + blockID + " " + auth);
            currentPLCResult.push(new PLCChange("auth", blockID, auth));
        }
    }

    private void outputAuthorityPLC(int blockID, boolean auth) {
        logger.info("Setting authority for block {} to {}", blockID, auth);
        WaysideBlock block = blockMap.get(blockID);
        block.setBooleanAuth(auth);

        if(trackModel != null && block.isOccupied()) {
            if (!auth) {
                trackModel.setCommandedSpeed(blockID, STOP_TRAIN_SIGNAL);
            }
            else {
                trackModel.setCommandedSpeed(blockID, RESUME_TRAIN_SIGNAL);
            }
        }
    }

    @Override
    public boolean getOutsideOccupancy(int blockID) {
        WaysideController controller = WaysideSystem.getController(trackLine, blockID);
        return controller.getBlockMap().get(blockID).isOccupied();
    }

    @Override
    public Map<Integer, WaysideBlock> getBlockMap() {
        return blockMap;
    }

    public String toString() {
        return "SW Wayside Controller #" + id;
    }

    /**
     * This method is used to set the value of a property based on the property name.
     * It uses a switch statement to determine which property to set.
     * The method casts the newValue parameter to the appropriate type based on the property.
     * If the property name is not found, it prints an error message to the console.
     *
     * @param propertyName  The name of the property to be set.
     * @param newValue      The new value to be set for the property.
     */
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
        logger.info("Sending authority for block {} to {}", blockID, blockCount);

        if(blockMap.get(blockID).inMaintenance() && blockMap.get(blockID).isOccupied() && trackModel != null) {
            trackModel.setTrainAuthority(blockID, blockCount);
        }
    }

    /**
     * This method is used to notify the change in the value of a property.
     * It prints the property name and the new value to the console and then notifies the subject of the change.
     * The notification to the subject is only done if the GUI is not currently being updated.
     *
     * @param propertyName  The name of the property that has changed.
     * @param newValue      The new value of the property.
     */
    public void notifyChange(String propertyName, Object newValue) {
//        System.out.println("Variable: " + propertyName + " changed to " + newValue);
        if(!subject.isGUIUpdate) {
            subject.notifyChange(propertyName, newValue);
        }
    }
}
