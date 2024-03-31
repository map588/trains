package waysideController;

import Common.CTCOffice;
import Common.TrackModel;
import Common.WaysideController;
import Framework.Support.Notifier;
import Utilities.BasicBlock;
import Utilities.BlockParser;
import Utilities.Enums.Lines;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

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
        ConcurrentSkipListMap<Integer, BasicBlock> blockList = BlockParser.parseCSV().get(Lines.GREEN);
        for(int blockID : blockIDList) {
            WaysideBlock block = new WaysideBlock(blockList.get(blockID));
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

        WaysideBlock block = blockMap.get(blockID);
        if(block.isOpen() && block.isOccupied() != occupied) {
            block.setOccupied(occupied);

            if(ctcOffice != null)
                ctcOffice.setBlockOccupancy(trackLine==Lines.GREEN, blockID, occupied);

            runPLC();
        }
    }

    @Override
    public void CTCSendSpeed(int blockID, double speed) {
        speed = Math.min(speed, blockMap.get(blockID).getSpeed());
        blockMap.get(blockID).setSpeed(speed);

        if(trackModel != null) {
            if (blockMap.get(blockID).getBooleanAuth()) {
                //trackModel.setCommandedSpeed(blockID, speed);
            } else {
                //trackModel.setCommandedSpeed(blockID, 0);
            }
        }
    }

    // Sets the block access state for a block, updates simulated occupancy, and runs the PLC
    @Override
    public void CTCChangeBlockMaintenanceState(int blockID, boolean maintenanceState) {
        WaysideBlock block = blockMap.get(blockID);
        boolean currentState = block.isOpen();

        if(currentState != maintenanceState) {
            block.setBlockMaintenanceState(maintenanceState);
            block.setOccupied(!maintenanceState);
            if(ctcOffice != null)
                ctcOffice.setBlockOccupancy(trackLine==Lines.GREEN, blockID, !maintenanceState);

            runPLC();
        }
    }

    // This method is a convenience method not reflected in the diagrams
    @Override
    public void CTCEnableAllBlocks() {
        for(WaysideBlock block : blockMap.values()) {
            if(!block.isOpen()) {
                block.setBlockMaintenanceState(true);
                block.setOccupied(false);
                if(ctcOffice != null)
                    ctcOffice.setBlockOccupancy(trackLine==Lines.GREEN, block.getBlockID(), false);
            }
        }
        runPLC();
    }


    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        if(maintenanceMode) {
            blockMap.get(blockID).setSwitchState(switchState);
            if(trackModel != null)
                trackModel.setSwitchState(blockID, switchState);
            if(ctcOffice != null)
                ctcOffice.setSwitchState(trackLine==Lines.GREEN, blockID, switchState);
            System.out.println("maintenanceSetSwitch: " + blockID + " " + switchState);
        }
    }

    @Override
    public void maintenanceSetAuthority(int blockID, boolean auth) {
        if(maintenanceMode) {
            blockMap.get(blockID).setBooleanAuth(auth);
//            trackModel.setTrainAuthority(blockID, auth);
            System.out.println("maintenanceSetAuthority: " + blockID + " " + auth);
        }
    }

    @Override
    public void maintenanceSetTrafficLight(int blockID, boolean lightState) {
        if(maintenanceMode) {
            blockMap.get(blockID).setLightState(lightState);
            if(trackModel != null)
                trackModel.setLightState(blockID, lightState);
            if(ctcOffice != null)
                ctcOffice.setLightState(trackLine==Lines.GREEN, blockID, lightState);
            System.out.println("maintenanceSetTrafficLight: " + blockID + " " + lightState);
        }
    }

    @Override
    public void maintenanceSetCrossing(int blockID, boolean crossingState) {
        if(maintenanceMode) {
            blockMap.get(blockID).setCrossingState(crossingState);
            if(trackModel != null)
                trackModel.setCrossing(blockID, crossingState);
            if(ctcOffice != null)
                ctcOffice.setCrossingState(trackLine==Lines.GREEN, blockID, crossingState);
            System.out.println("maintenanceSetCrossing: " + blockID + " " + crossingState);
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

        if(block.isOpen() && block.getSwitchState() != switchState) {
            block.setSwitchState(switchState);
            if(trackModel != null)
                trackModel.setSwitchState(blockID, switchState);
            if(ctcOffice != null)
                ctcOffice.setSwitchState(trackLine==Lines.GREEN, blockID, switchState);
        }
    }

    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen() && block.getLightState() != lightState) {
            block.setLightState(lightState);
            if(trackModel != null)
                trackModel.setLightState(blockID, lightState);
            if(ctcOffice != null)
                ctcOffice.setLightState(trackLine==Lines.GREEN, blockID, lightState);
        }
    }

    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen() && block.getCrossingState() != crossingState) {
            block.setCrossingState(crossingState);
            if(trackModel != null)
                trackModel.setCrossing(blockID, crossingState);
            if(ctcOffice != null)
                ctcOffice.setCrossingState(trackLine==Lines.GREEN, blockID, crossingState);
        }
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
        WaysideBlock block = blockMap.get(blockID);

        if(block.isOpen() && block.getBooleanAuth() != auth) {
            block.setBooleanAuth(auth);
//              trackModel.setTrainAuthority(blockID, auth);

            if(trackModel != null) {
                if (blockMap.get(blockID).getBooleanAuth()) {
                    //trackModel.setCommandedSpeed(blockID, block.getSpeed());
                } else {
                    //trackModel.setCommandedSpeed(blockID, 0);
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
    public void CTCSendAuthority(int blockID, int blockCount) {

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
