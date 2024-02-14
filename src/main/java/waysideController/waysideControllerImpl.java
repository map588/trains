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

    // List containing all the track blocks controlled by this instance of the wayside controller
    private List<trackModel> trackList = new ArrayList<trackModel>();

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

    @Override
    public void addBlock(trackModel block) {
        this.trackList.add(block);
    }

    @Override
    public int getID() {
        return this.id;
    }
}
