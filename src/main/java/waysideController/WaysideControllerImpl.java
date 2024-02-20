package waysideController;

import Common.WaysideController;
import Utilities.BlockInfo;
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
    private final List<BlockInfo> trackList = new ArrayList<>();

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
        program = new PLCProgram();
        subject = new WaysideControllerSubject(this);
    }

    @Override
    public File getPLCFile() {
        return this.PLCFile;
    }

    @Override
    public void loadPLC(File PLC) {
        this.PLCFile = PLC;
        subject.PLCNameProperty().set(PLC.getName());
        updateActivePLCProp();
    }

    @Override
    public boolean isMaintenanceMode() {
        return this.maintenanceMode;
    }

    @Override
    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
        subject.maintenanceModeProperty().set(maintenanceMode);
        updateActivePLCProp();
    }

    @Override
    public void setMaintenanceModeNoUpdate(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
        updateActivePLCProp();
    }

    @Override
    public List<BlockInfo> getBlockList() {
        return this.trackList;
    }

    @Override
    public void addBlock(BlockInfo block) {
        this.trackList.add(block);
    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean isOccupied) {
        trackList.get(blockID).setTrackCircuitState(isOccupied);
        program.setOccupancy(blockID, isOccupied);
        program.runBlueLine();
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
    public void CTCRequestSwitchState(int blockID, boolean switchState) {

    }

    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {

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

    private void updateActivePLCProp() {
        if(!maintenanceMode && PLCFile != null)
            subject.activePLCColorProperty().set(Color.BLUE);
        else
            subject.activePLCColorProperty().set(Color.GRAY);
    }

    public WaysideControllerSubject getSubject() {
        return subject;
    }

}
