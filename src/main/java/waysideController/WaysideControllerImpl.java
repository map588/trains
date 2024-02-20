package waysideController;

import Common.WaysideController;
import Utilities.BlockInfo;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WaysideControllerImpl implements WaysideController {

    // The ID of the wayside controller
    private final int id;

    private final int trackLine;

    // Whether the wayside controller is in manual mode
    private boolean manualMode = false;

    // List containing all the track blocks controlled by this instance of the wayside controller
    private final List<BlockInfo> trackList = new ArrayList<>();

    // The PLC program that the wayside controller is running
    private File PLC = null;

    private final WaysideControllerSubject subject;


    /**
     * Constructor for the wayside controller
     * @param id The ID of the wayside controller (used mainly for internal identification)
     */
    public WaysideControllerImpl(int id, int trackLine) {
        this.id = id;
        this.trackLine = trackLine;
        subject = new WaysideControllerSubject(this);
    }

    @Override
    public File getPLC() {
        return this.PLC;
    }

    @Override
    public void loadPLC(File PLC) {
        this.PLC = PLC;
        subject.PLCNameProperty().set(PLC.getName());
        updateActivePLCProp();
    }

    @Override
    public boolean isManualMode() {
        return this.manualMode;
    }

    @Override
    public void setManualMode(boolean manualMode) {
        this.manualMode = manualMode;
        subject.manualModeProperty().set(manualMode);
        updateActivePLCProp();
    }

    @Override
    public void setManualModeNoUpdate(boolean manualMode) {
        this.manualMode = manualMode;
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
    public void disableBlock(int blockID) {

    }

    @Override
    public void enableBlock(int blockID) {

    }

    @Override
    public void enableAllBlocks() {

    }

    @Override
    public int getID() {
        return this.id;
    }

    private void updateActivePLCProp() {
        if(!manualMode && PLC != null)
            subject.activePLCColorProperty().set(Color.BLUE);
        else
            subject.activePLCColorProperty().set(Color.GRAY);
    }

    public WaysideControllerSubject getSubject() {
        return subject;
    }

}
