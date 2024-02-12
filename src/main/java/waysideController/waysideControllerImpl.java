package waysideController;

import Common.trackModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class waysideControllerImpl implements waysideController {

    // List containing all the track blocks controlled by this instance of the wayside controller
    private List<trackModel> trackList = new ArrayList<trackModel>();

    // The PLC program that the wayside controller is running
    private File PLC = null;

    @Override
    public File getPLC() {
        return this.PLC;
    }

    @Override
    public void loadPLC(File PLC) {
        this.PLC = PLC;
    }

    @Override
    public void addBlock(trackModel block) {
        this.trackList.add(block);
    }
}
