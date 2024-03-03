package waysideController;

import Common.WaysideController;
import Framework.Support.Notifications;
import Utilities.CSVTokenizer;
import Utilities.TrueBlockInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static waysideController.Properties.*;

public class WaysideControllerImpl implements WaysideController, PLCRunner, Notifications {

    // The ID of the wayside controller
    private final int id;

    private final String trackLine;

    // Whether the wayside controller is in maintenance mode
    private boolean maintenanceMode = false;

    protected final Map<Integer, WaysideBlock> blockMap = new HashMap<>();

    // The PLC program that the wayside controller is running
    private File PLCFile = null;
    private PLCProgram program;

    private final WaysideControllerSubject subject;


    /**
     * Constructor for the wayside controller
     * @param id The ID of the wayside controller (used mainly for internal identification)
     */
    public WaysideControllerImpl(int id, String lineName, List<Integer> blockIDList) {
        this.id = id;
        this.trackLine = lineName;

        program = new PLCProgram(this);
        subject = new WaysideControllerSubject(this);

        List<TrueBlockInfo> fullBlockList = CSVTokenizer.blockList.get(trackLine);
        for(int blockID : blockIDList) {
            addBlock(new WaysideBlock(fullBlockList.get(blockID)));
        }
    }

    @Override
    public File getPLCFile() {
        return this.PLCFile;
    }

    @Override
    public void loadPLC(File PLC) {
        this.PLCFile = PLC;
        notifyChange(PLCName_p, PLC.getName());
        subject.updateActivePLCProp();
    }

    @Override
    public void runPLC() {
        if(!maintenanceMode)
            program.runBlueLine();
    }

    @Override
    public boolean isMaintenanceMode() {
        return this.maintenanceMode;
    }

    @Override
    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
        notifyChange(maintenanceMode_p, maintenanceMode);
        subject.updateActivePLCProp();
        runPLC();
    }

    private void addBlock(WaysideBlock block) {
        blockMap.put(block.getBlockID(), block);
        subject.addBlock(new WaysideBlockSubject(block));
    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean isOccupied) {
        blockMap.get(blockID).setOccupied(isOccupied);
        runPLC();
    }

    @Override
    public void CTCRequestSwitchState(int blockID, boolean switchState) {
        blockMap.get(blockID).setSwitchRequest(switchState);
        runPLC();
    }

    // TODO: Update PLC program to account for block access state
    @Override
    public void CTCDisableBlock(int blockID) {
        blockMap.get(blockID).setBlockAccessState(false);
        runPLC();
    }

    @Override
    public void CTCEnableBlock(int blockID) {
        blockMap.get(blockID).setBlockAccessState(true);
        runPLC();
    }

    // This method is a convenience method not reflected in the diagrams
    @Override
    public void CTCEnableAllBlocks() {
        for(WaysideBlock block : blockMap.values()) {
            block.setBlockAccessState(true);
        }
        runPLC();
    }

    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        blockMap.get(blockID).setSwitchState(switchState);
        System.out.println("maintenanceSetSwitch: " + blockID + " " + switchState);
    }

    @Override
    public void maintenanceSetAuthority(int blockID, boolean auth) {
        blockMap.get(blockID).setAuthority(auth);
        System.out.println("maintenanceSetAuthority: " + blockID + " " + auth);
    }

    @Override
    public void maintenanceSetTrafficLight(int blockID, boolean lightState) {
        blockMap.get(blockID).setLightState(lightState);
        System.out.println("maintenanceSetTrafficLight: " + blockID + " " + lightState);
    }

    @Override
    public void maintenanceSetCrossing(int blockID, boolean crossingState) {
        blockMap.get(blockID).setCrossingState(crossingState);
        System.out.println("maintenanceSetCrossing: " + blockID + " " + crossingState);
    }

    @Override
    public int getID() {
        return this.id;
    }

    public WaysideControllerSubject getSubject() {
        return subject;
    }

    /**
     * @param blockID
     * @param switchState
     */
    @Override
    public void setSwitchPLC(int blockID, boolean switchState) {
//        subject.blockListProperty().get(blockID-1).setSwitchState(switchState);
    }

    /**
     * @param blockID
     * @param lightState
     */
    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
//        subject.blockListProperty().get(blockID-1).setLightState(lightState);
    }

    /**
     * @param blockID
     * @param crossingState
     */
    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
//        subject.blockListProperty().get(blockID-1).setCrossingState(crossingState);
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
//        subject.blockListProperty().get(blockID-1).setAuthority(auth);
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
