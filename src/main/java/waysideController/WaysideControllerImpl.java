package waysideController;

import Common.WaysideController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static waysideController.Properties.*;

public class WaysideControllerImpl implements WaysideController, PLCRunner {

    // The ID of the wayside controller
    private final int id;

    private final int trackLine;

    // Whether the wayside controller is in maintenance mode
    private boolean maintenanceMode = false;

    private final List<TrainSpeedAuth> speedAuthList = new ArrayList<>();

    private final Map<Integer, WaysideBlock> blockMap = new HashMap<>();

    // The PLC program that the wayside controller is running
    private File PLCFile = null;
    private PLCProgram program;

    private final WaysideControllerSubject subject;


    /**
     * Constructor for the wayside controller
     * @param id The ID of the wayside controller (used mainly for internal identification)
     */
    public WaysideControllerImpl(int id, int trackLine) {
        this.id = id;
        this.trackLine = trackLine;
        program = new PLCProgram(this);
        subject = new WaysideControllerSubject(this);

        addBlock((new WaysideBlock(1, false, false, false)));
        addBlock((new WaysideBlock(2, false, false, false)));
        addBlock((new WaysideBlock(3, false, false, true)));
        addBlock((new WaysideBlock(4, false, false, false)));
        addBlock((new WaysideBlock(5, true, false, false, 6, 11)));
        addBlock((new WaysideBlock(6, false, true, false)));
        addBlock((new WaysideBlock(7, false, false, false)));
        addBlock((new WaysideBlock(8, false, false, false)));
        addBlock((new WaysideBlock(9, false, false, false)));
        addBlock((new WaysideBlock(10, false, false, false)));
        addBlock((new WaysideBlock(11, false, true, false)));
        addBlock((new WaysideBlock(12, false, false, false)));
        addBlock((new WaysideBlock(13, false, false, false)));
        addBlock((new WaysideBlock(14, false, false, false)));
        addBlock((new WaysideBlock(15, false, false, false)));

        CTCSetSpeedAuth(new TrainSpeedAuth(1));
        CTCSetSpeedAuth(new TrainSpeedAuth(2));
        CTCSetSpeedAuth(new TrainSpeedAuth(3));
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

    @Override
    public void addBlock(WaysideBlock block) {
        blockMap.put(block.getBlockID(), block);
        subject.addBlock(new WaysideBlockSubject(block));
    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean isOccupied) {
        blockMap.get(blockID).setOccupied(isOccupied);
        subject.blockListProperty().get(blockID-1).setOccupation(isOccupied);
        runPLC();
    }

    // TODO Remove this!
    @Override
    public void CTCSetSpeedAuth(TrainSpeedAuth speedAuth) {
        if(speedAuthList.contains(speedAuth)) {
            speedAuthList.set(speedAuthList.indexOf(speedAuth), speedAuth);
        }
        else {
            speedAuthList.add(speedAuth);
        }
        subject.setSpeedAuth(speedAuth);
        speedAuth.speedInProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() > 50) {
                speedAuth.speedOutProperty().set(50.0);
            }
            else {
                speedAuth.speedOutProperty().set(newValue.doubleValue());
            }
        });
        speedAuth.authorityInProperty().addListener((observable, oldValue, newValue) -> {
            speedAuth.authorityOutProperty().set(newValue.intValue());
        });
    }

    @Override
    public void CTCRequestSwitchState(int blockID, boolean switchState) {
        blockMap.get(blockID).setSwitchRequest(switchState);
        subject.blockListProperty().get(blockID-1).setSwitchRequestedState(switchState);
        runPLC();
    }

    @Override
    public void CTCDisableBlock(int blockID) {

    }

    @Override
    public void CTCEnableBlock(int blockID) {

    }

    @Override
    public void CTCEnableAllBlocks() {

    }

    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        blockMap.get(blockID).setSwitchState(switchState);
//        subject.blockListProperty().get(blockID-1).setSwitchState(switchState);
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
        subject.blockListProperty().get(blockID-1).setSwitchState(switchState);
    }

    /**
     * @param blockID
     * @param lightState
     */
    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        subject.blockListProperty().get(blockID-1).setLightState(lightState);
    }

    /**
     * @param blockID
     * @param crossingState
     */
    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        subject.blockListProperty().get(blockID-1).setCrossingState(crossingState);
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
        subject.blockListProperty().get(blockID-1).setAuthority(auth);
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
