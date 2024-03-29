package Common;

import waysideController.WaysideBlock;
import waysideController.WaysideControllerSubject;

import java.io.File;
import java.util.Map;


// This is the interface for the wayside controller, which is used to control the various track blocks
public interface WaysideController {

    // Loads a new PLC program into the wayside controller from a given file
    void loadPLC(File PLC);

    // Runs the PLC program
    void runPLC();

    // Returns whether the wayside controller is in maintenance mode
    boolean isMaintenanceMode();

    // Sets whether the wayside controller is in maintenance mode
    void setMaintenanceMode(boolean maintenanceMode);

    // Allows Track Model to set train occupancy for a specific block
    void trackModelSetOccupancy(int blockID, boolean isOccupied);

    void CTCSendSpeed(int blockID, double speed);

    // Allows CTC to enable or disable a block (works with automatic mode)
    // Combined the two methods into one for simplicity
    void CTCChangeBlockMaintenanceState(int blockID, boolean maintenanceState);

    // Allows CTC to enable all blocks (works with automatic mode)
    void CTCEnableAllBlocks();

    // Manually sets a switch state in maintenance mode
    void maintenanceSetSwitch(int blockID, boolean switchState);

    // Manually sets a block authority in maintenance mode
    void maintenanceSetAuthority(int blockID, boolean auth);

    // Manually sets a traffic light state in maintenance mode
    void maintenanceSetTrafficLight(int blockID, boolean lightState);

    // Manually sets a railroad crossing state in maintenance mode
    void maintenanceSetCrossing(int blockID, boolean crossingState);

    // Returns the ID of the controller
    int getID();

    Map<Integer, WaysideBlock> getBlockMap();

    // Returns the subject attached to this controller
    WaysideControllerSubject getSubject();

    // Allows a subject to set local variables by name and value
    void setValue(String propertyName, Object newValue);


    /**
     * Sends the authority for a train to the Wayside.
     * The authority is simply the block ID that the train is allowed to travel to.
     * This allows the Wayside to stop a train at a specific block or station.
     * @param blockID The ID of the block the train is located in
     * @param blockCount The number of blocks the train is allowed to travel
     */
    void CTCSendAuthority(int blockID, int blockCount);
}

