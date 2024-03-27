package waysideController;

import Common.CTCOffice;
import Common.TrackModel;
import Common.WaysideController;
import Framework.Support.Notifier;
import Utilities.BasicBlock;
import Utilities.BlockParser;
import Utilities.Enums.Lines;

import java.io.File;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static waysideController.Properties.PLCName_p;
import static waysideController.Properties.maintenanceMode_p;

public class WaysideControllerImpl implements WaysideController, PLCRunner, Notifier {

    // The ID of the wayside controller
    private final int id;

    // The name of the track line that the wayside controller is on
    private final Lines trackLine;

    // Whether the wayside controller is in maintenance mode
    private boolean maintenanceMode = false;

    // The map of blocks that the wayside controller controls
    protected final Map<Integer, WaysideBlock> blockMap = new HashMap<>();

    private final Map<Integer, List<Integer>> trainMap = new HashMap<>();

    // The PLC program that the wayside controller is running
    private final PLCProgram[] plcPrograms;

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
        ArrayDeque<BasicBlock> blockDeque = BlockParser.parseCSV().get(Lines.GREEN);
        BasicBlock[] blockArray = blockDeque.toArray(new BasicBlock[0]);
        for(int blockID : blockIDList) {
            WaysideBlock block = new WaysideBlock(blockArray[blockID]);
            blockMap.put(blockID, block);
            subject.addBlock(new WaysideBlockSubject(block));
        }

        //TODO: Register this wayside with the track model and CTC office


        plcPrograms = new PLCProgram[1];
        plcPrograms[0] = new PLCProgram(this);
    }


    /**
     * Load a PLC program into the wayside controller
     * @param PLC The PLC program to load
     */
    @Override
    public void loadPLC(File PLC) {
        plcPrograms[0].loadPLC(PLC.getAbsolutePath());
        notifyChange(PLCName_p, PLC.getName());
        subject.updateActivePLCProp();
    }

    /**
     * Run the PLC program
     */
    @Override
    public void runPLC() {
        if(!maintenanceMode)
            plcPrograms[0].run();
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
        this.maintenanceMode = maintenanceMode;
        notifyChange(maintenanceMode_p, maintenanceMode);
        subject.updateActivePLCProp();
        runPLC();
    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean occupied) {

        if(blockMap.get(blockID).isOpen() && blockMap.get(blockID).isOccupied() != occupied) {
            blockMap.get(blockID).setOccupied(occupied);
//             ctcOffice.setBlockOccupancy(trackLine, blockID, occupied);

            // Update train ID if the block is occupied
            if (occupied) {
                if (blockMap.get(blockID - 1).hasTrain()) {
                    blockMap.get(blockID).setTrainID(blockMap.get(blockID - 1).getTrainID());
                } else if (blockMap.get(blockID + 1).hasTrain()) {
                    blockMap.get(blockID).setTrainID(blockMap.get(blockID + 1).getTrainID());
                }
            }
            // Clear train ID if the block is not occupied
            else {
                blockMap.get(blockID).removeTrainID();
            }
            runPLC();
        }
    }

    @Override
    public void CTCSendSpeed(int blockID, double speed) {
        blockMap.get(blockID).setSpeed(speed);

        if(blockMap.get(blockID).getBooleanAuth()) {
            trackModel.setCommandedSpeed(blockID, speed);
        }
        else {
            trackModel.setCommandedSpeed(blockID, 0);
        }
    }

    // Sets the block access state for a block, updates simulated occupancy, and runs the PLC
    @Override
    public void CTCChangeBlockMaintenanceState(int blockID, boolean maintenanceState) {
        WaysideBlock block = blockMap.get(blockID);
        boolean currentState = block.isOpen();

        if(currentState != maintenanceState) {
            block.setBlockmaintenanceStateState(maintenanceState);
            block.setOccupied(!maintenanceState);
//            ctcOffice.setBlockOccupancy(trackLine, blockID, !maintenanceState);

            runPLC();
        }
    }

    // This method is a convenience method not reflected in the diagrams
    @Override
    public void CTCEnableAllBlocks() {
        for(WaysideBlock block : blockMap.values()) {
            if(!block.isOpen()) {
                block.setBlockmaintenanceStateState(true);
                block.setOccupied(false);
//                ctcOffice.setBlockOccupancy(trackLine, block.getBlockID(), false);
            }
        }
        runPLC();
    }


    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        blockMap.get(blockID).setSwitchState(switchState);
//        trackModel.setSwitchState(blockID, switchState);
//        ctcOffice.setSwitchState(trackLine, blockID, switchState);
        System.out.println("maintenanceSetSwitch: " + blockID + " " + switchState);
    }

    @Override
    public void maintenanceSetAuthority(int blockID, boolean auth) {
        blockMap.get(blockID).setBooleanAuth(auth);
//        trackModel.setTrainAuthority(blockID, auth);
        System.out.println("maintenanceSetAuthority: " + blockID + " " + auth);
    }

    @Override
    public void maintenanceSetTrafficLight(int blockID, boolean lightState) {
        blockMap.get(blockID).setLightState(lightState);
//        trackModel.setLightState(blockID, lightState);
//        ctcOffice.setLightState(trackLine, blockID, lightState);
        System.out.println("maintenanceSetTrafficLight: " + blockID + " " + lightState);
    }

    @Override
    public void maintenanceSetCrossing(int blockID, boolean crossingState) {
        blockMap.get(blockID).setCrossingState(crossingState);
//        trackModel.setCrossing(blockID, crossingState);
//        ctcOffice.setCrossingState(trackLine, blockID, crossingState);
        System.out.println("maintenanceSetCrossing: " + blockID + " " + crossingState);
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

        if(block.isOpen()) {
            block.setSwitchState(switchState);
//            trackModel.setSwitchState(blockID, switchState);
//            ctcOffice.setSwitchState(trackLine, blockID, switchState);
        }
    }

    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen()) {
            block.setLightState(lightState);
//            trackModel.setLightState(blockID, lightState);
//            ctcOffice.setLightState(trackLine, blockID, lightState);
        }
    }

    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen()) {
            block.setCrossingState(crossingState);
            trackModel.setCrossing(blockID, crossingState);
//            ctcOffice.setCrossingState(trackLine, blockID, crossingState);
        }
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen()) {
            if(block.getBooleanAuth() != auth) {
                block.setBooleanAuth(auth);
//              trackModel.setTrainAuthority(blockID, auth);

                if(blockMap.get(blockID).getBooleanAuth()) {
                    trackModel.setCommandedSpeed(blockID, block.getSpeed());
                }
                else {
                    trackModel.setCommandedSpeed(blockID, 0);
                }
            }
        }
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
    public void CTCDispatchTrain(int trainID) {
        blockMap.get(0).setTrainID(trainID);
    }

    @Override
    public void CTCSendSchedule(int trainID, int[] schedule) {

    }

    @Override
    public void waysideIncomingTrain(int trainID, int blockID) {
        blockMap.get(blockID).setTrainID(trainID);
    }

    @Override
    public boolean waysideRequestDirection(int blockID, boolean direction) {
        if(blockMap.get(blockID).isDir_assigned())
            return false;
        else {
            blockMap.get(blockID).setDir_assigned(true);
            blockMap.get(blockID).setDirection(direction);
            return true;
        }
    }

    @Override
    public boolean waysideReleaseDirection(int blockID) {
        blockMap.get(blockID).setDir_assigned(false);
        return true;

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
        System.out.println("Variable: " + propertyName + " changed to " + newValue);
        if(!subject.isGUIUpdate) {
            subject.notifyChange(propertyName, newValue);
        }
    }
}
