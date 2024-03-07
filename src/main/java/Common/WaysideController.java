package Common;

import waysideController.WaysideControllerSubject;

import java.io.File;


// This is the interface for the wayside controller, which is used to control the various track blocks
public interface WaysideController {

    // Loads a new PLC program into the wayside controller from a given file
    public void loadPLC(File PLC);

    // Runs the PLC program
    public void runPLC();

    // Returns whether the wayside controller is in maintenance mode
    public boolean isMaintenanceMode();

    // Sets whether the wayside controller is in maintenance mode
    public void setMaintenanceMode(boolean maintenanceMode);

    // Allows Track Model to set train occupancy for a specific block
    public void trackModelSetOccupancy(int blockID, boolean isOccupied);

    // Allows CTC to request a switch change (works with automatic mode)
    public void CTCRequestSwitchState(int blockID, boolean switchState);

    public void CTCSendSpeedAuth(int blockID, double speed, int authority);

    // Allows CTC to enable or disable a block (works with automatic mode)
    // Combined the two methods into one for simplicity
    public void CTCChangeBlockMaintenanceState(int blockID, boolean maintenanceState);

    // Allows CTC to enable all blocks (works with automatic mode)
    public void CTCEnableAllBlocks();

    // Manually sets a switch state in maintenance mode
    public void maintenanceSetSwitch(int blockID, boolean switchState);

    // Manually sets a block authority in maintenance mode
    public void maintenanceSetAuthority(int blockID, boolean auth);

    // Manually sets a traffic light state in maintenance mode
    public void maintenanceSetTrafficLight(int blockID, boolean lightState);

    // Manually sets a railroad crossing state in maintenance mode
    public void maintenanceSetCrossing(int blockID, boolean crossingState);

    // Returns the ID of the controller
    public int getID();

    // Returns the subject attached to this controller
    public WaysideControllerSubject getSubject();

    // Allows a subject to set local variables by name and value
    void setValue(String propertyName, Object newValue);
}

