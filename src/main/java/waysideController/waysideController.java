package waysideController;

import Common.trackModel;


// This is the interface for the wayside controller, which is used to control the various track blocks
public interface waysideController {

    // Accessor for PLC program
    public String getPLC();

    // Loads a new PLC program into the wayside controller
    public void loadPLC(String PLC);

    // Adds a new track block under the wayside controller's control
    public void addBlock(trackModel block);
}

