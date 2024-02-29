package waysideController;

import Common.WaysideController;
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static waysideController.Properties.*;

public class WaysideControllerImpl implements WaysideController, PLCRunner {

    // The ID of the wayside controller
    private final int id;

    private final int trackLine;

    // Whether the wayside controller is in maintenance mode
    private boolean maintenanceMode = false;

    // List containing all the track blocks controlled by this instance of the wayside controller
    private final List<WaysideBlockSubject> trackList = new ArrayList<>();

    private final List<TrainSpeedAuth> speedAuthList = new ArrayList<>();

    // The PLC program that the wayside controller is running
    private File PLCFile = null;
    private PLCProgram program;

    private final WaysideControllerSubject subject;


    /**
     * Constructor for the wayside controller
     * @param id The ID of the wayside controller (used mainly for internal identification)
     */
    public WaysideControllerImpl(int id, int trackLine) {
        this.id = id;
        this.trackLine = trackLine;
        program = new PLCProgram(this);
        subject = new WaysideControllerSubject(this);

        addBlock((new WaysideBlockSubject(1, false, false, false)));
        addBlock((new WaysideBlockSubject(2, false, false, false)));
        addBlock((new WaysideBlockSubject(3, false, false, true)));
        addBlock((new WaysideBlockSubject(4, false, false, false)));
        addBlock((new WaysideBlockSubject(5, true, false, false, 6, 11)));
        addBlock((new WaysideBlockSubject(6, false, true, false)));
        addBlock((new WaysideBlockSubject(7, false, false, false)));
        addBlock((new WaysideBlockSubject(8, false, false, false)));
        addBlock((new WaysideBlockSubject(9, false, false, false)));
        addBlock((new WaysideBlockSubject(10, false, false, false)));
        addBlock((new WaysideBlockSubject(11, false, true, false)));
        addBlock((new WaysideBlockSubject(12, false, false, false)));
        addBlock((new WaysideBlockSubject(13, false, false, false)));
        addBlock((new WaysideBlockSubject(14, false, false, false)));
        addBlock((new WaysideBlockSubject(15, false, false, false)));

        CTCSetSpeedAuth(new TrainSpeedAuth(1));
        CTCSetSpeedAuth(new TrainSpeedAuth(2));
        CTCSetSpeedAuth(new TrainSpeedAuth(3));
    }

    @Override
    public File getPLCFile() {
        return this.PLCFile;
    }

    @Override
    public void loadPLC(File PLC) {
        this.PLCFile = PLC;
        notifyChange(PLCName_p, PLC.getName());
        updateActivePLCProp();
    }

    @Override
    public void runPLC() {
        if(!maintenanceMode)
            program.runBlueLine();
    }

    @Override
    public boolean isMaintenanceMode() {
        return this.maintenanceMode;
    }

    @Override
    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
//        subject.maintenanceModeProperty().set(maintenanceMode);
        updateActivePLCProp();
        runPLC();
    }

    @Override
    public void setMaintenanceModeNoUpdate(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
        updateActivePLCProp();
        runPLC();
    }

    @Override
    public List<WaysideBlockSubject> getBlockList() {
        return this.trackList;
    }

    @Override
    public void addBlock(WaysideBlockSubject block) {
        this.trackList.add(block);
        subject.addBlock(block);
    }

    @Override
    public void trackModelSetOccupancy(int blockID, boolean isOccupied) {
        trackList.get(blockID-1).occupationProperty().set(isOccupied);
        program.setOccupancy(blockID, isOccupied);
        runPLC();
    }

    @Override
    public void CTCSetSpeedAuth(TrainSpeedAuth speedAuth) {
        if(speedAuthList.contains(speedAuth)) {
            speedAuthList.set(speedAuthList.indexOf(speedAuth), speedAuth);
        }
        else {
            speedAuthList.add(speedAuth);
        }
        subject.setSpeedAuth(speedAuth);
        speedAuth.speedInProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.doubleValue() > 50) {
                speedAuth.speedOutProperty().set(50.0);
            }
            else {
                speedAuth.speedOutProperty().set(newValue.doubleValue());
            }
        });
        speedAuth.authorityInProperty().addListener((observable, oldValue, newValue) -> {
            speedAuth.authorityOutProperty().set(newValue.intValue());
        });
    }

    @Override
    public void CTCRequestSwitchState(int blockID, boolean switchState) {
        program.setSwitchRequest(blockID, switchState);
        runPLC();
    }

    @Override
    public void maintenanceSetSwitch(int blockID, boolean switchState) {
        program.setSwitchState(blockID, switchState);
    }

    @Override
    public void maintenanceSetAuthority(int blockID, boolean auth) {
        program.setAuthState(blockID, auth);
    }

    @Override
    public void maintenanceSetTrafficLight(int blockID, boolean lightState) {

    }

    @Override
    public void maintenanceSetCrossing(int blockID, boolean crossingState) {

    }

    @Override
    public int getID() {
        return this.id;
    }

    private void updateActivePLCProp() {
        if(!maintenanceMode && PLCFile != null)
            subject.getPaintProperty(activePLCColor_p).set(Color.BLUE);
        else
            subject.getPaintProperty(activePLCColor_p).set(Color.GRAY);
    }

    public WaysideControllerSubject getSubject() {
        return subject;
    }

    /**
     * This method is used to set the value of a property based on the property name.
     * It uses a switch statement to determine which property to set.
     * The method casts the newValue parameter to the appropriate type based on the property.
     * If the property name is not found, it prints an error message to the console.
     *
     * @param propertyName  The name of the property to be set.
     * @param newValue      The new value to be set for the property.
     */
    @Override
    public void setValue(String propertyName, Object newValue) {
        switch(propertyName) {
            case maintenanceMode_p -> setMaintenanceMode((boolean) newValue);
            default -> System.err.println("Property " + propertyName + " not found");
        }
    }

    /**
     * This method is used to notify the change in the value of a property.
     * It prints the property name and the new value to the console and then notifies the subject of the change.
     * The notification to the subject is only done if the GUI is not currently being updated.
     *
     * @param propertyName  The name of the property that has changed.
     * @param newValue      The new value of the property.
     */
    public void notifyChange(String propertyName, Object newValue) {
        System.out.println("Variable: " + propertyName + " changed to " + newValue);
        if(!subject.isGUIUpdate) {
            subject.notifyChange(propertyName, newValue);
        }
    }

    /**
     * @param blockID
     * @param switchState
     */
    @Override
    public void setSwitchPLC(int blockID, boolean switchState) {
        trackList.get(blockID-1).switchStateProperty().set(switchState);
    }

    /**
     * @param blockID
     * @param lightState
     */
    @Override
    public void setTrafficLightPLC(int blockID, boolean lightState) {
        trackList.get(blockID-1).setLightState(lightState);
    }

    /**
     * @param blockID
     * @param crossingState
     */
    @Override
    public void setCrossingPLC(int blockID, boolean crossingState) {
        trackList.get(blockID-1).setCrossingState(crossingState);
    }

    @Override
    public void setAuthorityPLC(int blockID, boolean auth) {
        trackList.get(blockID-1).authorityStateProperty().set(auth);
    }

    public String toString() {
        return "SW Wayside Controller #" + id;
    }
}
