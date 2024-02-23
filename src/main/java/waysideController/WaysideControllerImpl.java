package waysideController;

import Common.WaysideController;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WaysideControllerImpl implements WaysideController {

    // The ID of the wayside controller
    private final int id;

    private final int trackLine;

    // Whether the wayside controller is in maintenance mode
    private boolean maintenanceMode = false;

    // List containing all the track blocks controlled by this instance of the wayside controller
    private final List<WaysideBlockInfo> trackList = new ArrayList<>();

    private final List<TrainSpeedAuth> speedAuthList = new ArrayList<>();

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

        addBlock((new WaysideBlockInfo(1, false, false, false)));
        addBlock((new WaysideBlockInfo(2, false, false, false)));
        addBlock((new WaysideBlockInfo(3, false, false, true)));
        addBlock((new WaysideBlockInfo(4, false, false, false)));
        addBlock((new WaysideBlockInfo(5, true, false, false, 6, 11)));
        addBlock((new WaysideBlockInfo(6, false, true, false)));
        addBlock((new WaysideBlockInfo(7, false, false, false)));
        addBlock((new WaysideBlockInfo(8, false, false, false)));
        addBlock((new WaysideBlockInfo(9, false, false, false)));
        addBlock((new WaysideBlockInfo(10, false, false, false)));
        addBlock((new WaysideBlockInfo(11, false, true, false)));
        addBlock((new WaysideBlockInfo(12, false, false, false)));
        addBlock((new WaysideBlockInfo(13, false, false, false)));
        addBlock((new WaysideBlockInfo(14, false, false, false)));
        addBlock((new WaysideBlockInfo(15, false, false, false)));

        CTCSetSpeedAuth(new TrainSpeedAuth(1));
        CTCSetSpeedAuth(new TrainSpeedAuth(2));
        CTCSetSpeedAuth(new TrainSpeedAuth(3));
    }

    /**
     * Get the PLC program that the wayside controller is running
     * @return The PLC program that the wayside controller is running
     */
    @Override
    public File getPLCFile() {
        return this.PLCFile;
    }


    /**
     * Load a PLC program into the wayside controller
     * @param PLC The PLC program to load
     */
    @Override
    public void loadPLC(File PLC) {
        this.PLCFile = PLC;
        subject.PLCNameProperty().set(PLC.getName());
        updateActivePLCProp();
    }

    /**
     * Run the PLC program
     */
    @Override
    public void runPLC() {
        if(!maintenanceMode)
            program.runBlueLine();
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
        subject.maintenanceModeProperty().set(maintenanceMode);
        updateActivePLCProp();
        runPLC();
    }

    /**
     * Set the maintenance mode of the wayside controller without updating the subject
     * @param maintenanceMode The new maintenance mode of the wayside controller
     */
    @Override
    public void setMaintenanceModeNoUpdate(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
        updateActivePLCProp();
        runPLC();
    }

    /**
     * Get the track line that the wayside controller is controlling
     * @return The track line that the wayside controller is controlling
     */
    @Override
    public List<WaysideBlockInfo> getBlockList() {
        return this.trackList;
    }

    /**
     * Add a block to the wayside controller's list of controlled blocks
     * @param block The block to add
     */
    @Override
    public void addBlock(WaysideBlockInfo block) {
        this.trackList.add(block);
        subject.addBlock(block);
    }

    /**
     * Sets a block as occupied or unoccupied
     * @param blockID The ID of the block to set
     * @param isOccupied Whether the block is occupied or not
     */
    @Override
    public void trackModelSetOccupancy(int blockID, boolean isOccupied) {
        trackList.get(blockID-1).occupationProperty().set(isOccupied);
        program.setOccupancy(blockID, isOccupied);
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

    /**
     * Set the speed and authority of a train
     * @param speedAuth The speed and authority of the train
     */
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

    /**
     * Set the switch state of a block
     * @param blockID The ID of the block to set
     * @param switchState The state to set the switch to
     */
    @Override
    public void CTCRequestSwitchState(int blockID, boolean switchState) {
        program.setSwitchRequest(blockID, switchState);
        runPLC();
    }

    /**
     * Set the switch state of a block
     * @param blockID The ID of the block to set
     * @param switchState The state to set the switch to
     */
    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        program.setSwitch(blockID, switchState);
    }

    @Override
    public void maintenanceSetTrafficLight(int blockID, boolean lightState) {

    }

    @Override
    public void maintenanceSetCrossing(int blockID, boolean crossingState) {

    }

    @Override
    public int getID() {
        return this.id;
    }

    /**
     * Update the active PLC property of the wayside controller
     */
    private void updateActivePLCProp() {
        if(!maintenanceMode && PLCFile != null)
            subject.activePLCColorProperty().set(Color.BLUE);
        else
            subject.activePLCColorProperty().set(Color.GRAY);
    }

    /**
     * Set the switch state of a block
     * @param blockID The ID of the block to set
     * @param switchState The state to set the switch to
     */
    protected void setSwitchState(int blockID, boolean switchState) {
        trackList.get(blockID-1).switchStateProperty().set(switchState);
    }

    /**
     * Set the traffic light state of a block
     * @param blockID The ID of the block to set
     * @param lightState The state to set the traffic light to
     */
    protected void setTrafficLightState(int blockID, boolean lightState) {
        trackList.get(blockID-1).setLightState(lightState);
    }

    /**
     * Set the crossing state of a block
     * @param blockID The ID of the block to set
     * @param crossingState The state to set the crossing to
     */
    protected void setCrossingState(int blockID, boolean crossingState) {
        trackList.get(blockID-1).setCrossingState(crossingState);
    }

    /**
     * Get the wayside controller's subject
     * @return The wayside controller's subject
     */
    public WaysideControllerSubject getSubject() {
        return subject;
    }

}
