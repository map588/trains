package waysideController;

import Common.trackModel;

import java.io.File;


// This is the interface for the wayside controller, which is used to control the various track blocks
public interface waysideController {

    // Accessor for PLC program file
    public File getPLC();

    // Loads a new PLC program into the wayside controller from a given file
    public void loadPLC(File PLC);

    // Adds a new track block under the wayside controller's control
    public void addBlock(trackModel block);

    // Returns the ID of the controller
    public int getID();
}

