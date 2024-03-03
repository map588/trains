package waysideController;

import java.util.HashMap;
import java.util.Map;

import static Utilities.Constants.*;

public class PLCProgram {

    private final Map<Integer, WaysideBlock> blockMap;
    private final PLCRunner controller;

    public PLCProgram(PLCRunner controller) {
        this.blockMap = controller.getBlockMap();

        this.controller = controller;
    }

    private void setSwitch(int blockID, boolean switchState) {
        blockMap.get(blockID).setSwitchState(switchState);
        controller.setSwitchPLC(blockID, switchState);
    }

    private void setLight(int blockID, boolean lightState) {
        blockMap.get(blockID).setLightState(lightState);
        controller.setTrafficLightPLC(blockID, lightState);
    }

    private void setCrossing(int blockID, boolean crossingState) {
        blockMap.get(blockID).setCrossingState(crossingState);
        controller.setCrossingPLC(blockID, crossingState);
    }

    private void setAuth(int blockID, boolean auth) {
        blockMap.get(blockID).setAuthority(auth);
        controller.setAuthorityPLC(blockID, auth);
    }

    public void runBlueLine() {

        // Process switch state requests
        System.out.println("State of switch is " + blockMap.get(5).getSwitchState());
        if(blockMap.get(5).getSwitchState() != blockMap.get(5).getSwitchRequest()) {
            if(!blockMap.get(5).isOccupied() && !blockMap.get(6).isOccupied() && !blockMap.get(11).isOccupied()) {
                setSwitch(5, blockMap.get(5).getSwitchRequest());
                System.out.println("Swapping switch state");
            }
        }

        // Set traffic lights
        if(!blockMap.get(1).isOccupied() && !blockMap.get(2).isOccupied() && !blockMap.get(3).isOccupied() && !blockMap.get(4).isOccupied() && !blockMap.get(5).isOccupied()) {
            if(blockMap.get(5).getSwitchState() == SWITCH_MAIN) {
                setLight(6, LIGHT_GREEN);
                setLight(11, LIGHT_RED);
            }
            else {
                setLight(6, LIGHT_RED);
                setLight(11, LIGHT_GREEN);
            }
        }
        else {
            setLight(6, LIGHT_RED);
            setLight(11, LIGHT_RED);
        }

        // Set Railroad Crossings
        if(blockMap.get(2).isOccupied() || blockMap.get(3).isOccupied() || blockMap.get(4).isOccupied()) {
            setCrossing(3, CROSSING_CLOSED);
        }
        else {
            setCrossing(3, CROSSING_OPEN);
        }

        setAuth(1, !blockMap.get(2).isOccupied() && !blockMap.get(3).isOccupied());
        setAuth(2, !blockMap.get(3).isOccupied() && !blockMap.get(4).isOccupied());
        setAuth(3, !blockMap.get(4).isOccupied() && !blockMap.get(5).isOccupied());
        if(blockMap.get(5).getSwitchState() == SWITCH_MAIN) {
            setAuth(4, !blockMap.get(5).isOccupied() && !blockMap.get(6).isOccupied());
            setAuth(5, !blockMap.get(6).isOccupied() && !blockMap.get(7).isOccupied());
        }
        else {
            setAuth(4, !blockMap.get(5).isOccupied() && !blockMap.get(11).isOccupied());
            setAuth(5, !blockMap.get(11).isOccupied() && !blockMap.get(12).isOccupied());
        }

        setAuth(6, !blockMap.get(7).isOccupied() && !blockMap.get(8).isOccupied());
        setAuth(7, !blockMap.get(8).isOccupied() && !blockMap.get(9).isOccupied());
        setAuth(8, !blockMap.get(9).isOccupied() && !blockMap.get(10).isOccupied());
        setAuth(9, !blockMap.get(10).isOccupied());
        setAuth(10, false);

        setAuth(11, !blockMap.get(12).isOccupied() && !blockMap.get(13).isOccupied());
        setAuth(12, !blockMap.get(13).isOccupied() && !blockMap.get(14).isOccupied());
        setAuth(13, !blockMap.get(14).isOccupied() && !blockMap.get(15).isOccupied());
        setAuth(14, !blockMap.get(15).isOccupied());
        setAuth(15, false);

        System.out.println("Switch = " + blockMap.get(5).getSwitchState());

        System.out.println("Light 6 = " + blockMap.get(6).getLightState());
        System.out.println("Light 1 = " + blockMap.get(11).getLightState());

        System.out.println("Crossing = " + blockMap.get(3).getLightState());
    }
}
