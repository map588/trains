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

    // Whether the wayside controller is in manual mode
    protected boolean manualMode = false;

    // List containing all the track blocks controlled by this instance of the wayside controller
    private final List<BlockInfo> trackList = new ArrayList<>();

    // The PLC program that the wayside controller is running
    protected File PLC = null;

    private final WaysideControllerSubject subject;


    /**
     * Constructor for the wayside controller
     * @param id The ID of the wayside controller (used mainly for internal identification)
     */
    public WaysideControllerImpl(int id) {
        this.id = id;
        subject = new WaysideControllerSubject(this);

        subject.manualModeProperty().addListener((observableValue, oldValue, newVal) -> {
            manualMode = newVal;
            updateActivePLCProp();
            System.out.println("Setting manual mode to " + newVal);
        });
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
    public List<BlockInfo> getBlockList() {
        return this.trackList;
    }

    @Override
    public void addBlock(BlockInfo block) {
        this.trackList.add(block);
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
