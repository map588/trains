package Common;

import waysideController.TrainSpeedAuth;
import waysideController.WaysideBlock;
import waysideController.WaysideBlockSubject;
import waysideController.WaysideControllerSubject;

import java.io.File;
import java.util.List;


// This is the interface for the wayside controller, which is used to control the various track blocks
public interface WaysideController {

    // Accessor for PLC program file
    public File getPLCFile();

    // Loads a new PLC program into the wayside controller from a given file
    public void loadPLC(File PLC);

    public void runPLC();

    // Returns whether the wayside controller is in maintenance mode
    public boolean isMaintenanceMode();

    // Sets whether the wayside controller is in maintenance mode
    public void setMaintenanceMode(boolean maintenanceMode);

    // Sets whether the wayside controller is in maintenance mode without updating the property
//    public void setMaintenanceModeNoUpdate(boolean maintenanceMode);

    // Returns the list of block IDs under the wayside controller's control
//    public List<WaysideBlockSubject> getBlockList();

    // Adds a new track block under the wayside controller's control
    public void addBlock(WaysideBlock block);

    // Allows Track Model to set train occupancy for a specific block
    public void trackModelSetOccupancy(int blockID, boolean isOccupied);

    public void CTCSetSpeedAuth(TrainSpeedAuth speedAuth);

    // Allows CTC to request a switch change (works with automatic mode)
    public void CTCRequestSwitchState(int blockID, boolean switchState);

    // Allows CTC to disable a block (works with automatic mode)
    public void CTCDisableBlock(int blockID);

    // Allows CTC to enable a block (works with automatic mode)
    public void CTCEnableBlock(int blockID);

    // Allows CTC to enable all blocks (works with automatic mode)
    public void CTCEnableAllBlocks();

    // Manually sets a switch state in maintenance mode
    public void maintenanceSetSwitch(int blockID, boolean switchState);

    public void maintenanceSetAuthority(int blockID, boolean auth);

    // Manually sets a traffic light state in maintenance mode
    public void maintenanceSetTrafficLight(int blockID, boolean lightState);

    // Manually sets a railroad crossing state in maintenance mode
    public void maintenanceSetCrossing(int blockID, boolean crossingState);

    // Returns the ID of the controller
    public int getID();

    // Returns the subject attached to this controller
    public WaysideControllerSubject getSubject();

    void setValue(String propertyName, Object newValue);
}

