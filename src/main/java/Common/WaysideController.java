package Common;

import Utilities.BlockInfo;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Paint;
import waysideController.WaysideControllerSubject;

import java.io.File;
import java.util.List;


// This is the interface for the wayside controller, which is used to control the various track blocks
public interface WaysideController {

    // Accessor for PLC program file
    public File getPLC();

    // Loads a new PLC program into the wayside controller from a given file
    public void loadPLC(File PLC);

    // Returns whether the wayside controller is in manual mode
    public boolean isManualMode();

    // Sets whether the wayside controller is in manual mode
    public void setManualMode(boolean manualMode);

    // Sets whether the wayside controller is in manual mode without updating the property
    public void setManualModeNoUpdate(boolean manualMode);

    // Returns the list of block IDs under the wayside controller's control
    public List<BlockInfo> getBlockList();

    // Adds a new track block under the wayside controller's control
    public void addBlock(BlockInfo block);

    // Allows CTC to disable a specific block, which does not allow a train to occupy the block
    public void disableBlock(int blockID);

    // Allows CTC to enable a specific block that was previously disabled
    public void enableBlock(int blockID);

    // Allows CTC to enable all blocks that were previously disabled
    public void enableAllBlocks();

    // Returns the ID of the controller
    public int getID();


    public WaysideControllerSubject getSubject();
}

