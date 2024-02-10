package waysideController;

import Common.trackModel;

import java.util.ArrayList;
import java.util.List;

class waysideControllerImpl implements waysideController {

    // List containing all the track blocks controlled by this instance of the wayside controller
    List<trackModel> trackList = new ArrayList<trackModel>();

    // The PLC program that the wayside controller is running
    String PLC = "";

    @Override
    public String getPLC() {
        return this.PLC;
    }

    @Override
    public void loadPLC(String PLC) {
        this.PLC = PLC;
    }

    @Override
    public void addBlock(trackModel block) {
        this.trackList.add(block);
    }
}
