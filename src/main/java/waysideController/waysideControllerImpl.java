package waysideController;

import Common.trackModel;
import Common.waysideController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class waysideControllerImpl implements waysideController {

    // The ID of the wayside controller
    private int id;

    // Whether the wayside controller is in manual mode
    private boolean manualMode = false;

    // Reference to the track model for pulling information on block states
    private trackModel trackModelReference = null;

    // List containing all the track blocks controlled by this instance of the wayside controller
    private List<Integer> trackList = new ArrayList<Integer>();

    // The PLC program that the wayside controller is running
    private File PLC = null;

    /**
     * Constructor for the wayside controller
     * @param id The ID of the wayside controller (used mainly for internal identification)
     */
    public waysideControllerImpl(int id) {
        this.id = id;
    }

    @Override
    public File getPLC() {
        return this.PLC;
    }

    @Override
    public void loadPLC(File PLC) {
        this.PLC = PLC;
    }

    @Override
    public boolean isManualMode() {
        return this.manualMode;
    }

    @Override
    public void setManualMode(boolean manualMode) {
        this.manualMode = manualMode;
    }

    public List<Integer> getBlockList() {
        return this.trackList;
    }

    @Override
    public void addBlock(int block) {
        this.trackList.add(block);
    }

    @Override
    public int getID() {
        return this.id;
    }
}
