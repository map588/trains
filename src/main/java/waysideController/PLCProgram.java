package waysideController;

import java.util.HashMap;
import java.util.Map;

import static Utilities.Constants.*;

public class PLCProgram {
    private final Map<Integer, Boolean> occupancyList;
    private final Map<Integer, Boolean> switchStateList;
    private final Map<Integer, Boolean> switchRequestedStateList;
    private final Map<Integer, Boolean> trafficLightList;
    private final Map<Integer, Boolean> crossingList;
    private final Map<Integer, Boolean> authList;
    private final Map<Integer, Double> speedList;

    private final PLCRunner controller;

    public PLCProgram(PLCRunner controller) {
        occupancyList = new HashMap<>();
        switchStateList = new HashMap<>();
        switchRequestedStateList = new HashMap<>();
        trafficLightList = new HashMap<>();
        crossingList = new HashMap<>();
        authList = new HashMap<>();
        speedList = new HashMap<>();

        for(int i = 1; i <= 15; i++) {
            occupancyList.put(i, false);
        }

        switchStateList.put(5, SWITCH_MAIN);
        switchRequestedStateList.put(5, SWITCH_MAIN);
        trafficLightList.put(6, LIGHT_RED);
        trafficLightList.put(11, LIGHT_RED);
        crossingList.put(3, CROSSING_OPEN);

        this.controller = controller;
    }

    public void setOccupancy(int blockID, boolean occupied) {
        occupancyList.put(blockID, occupied);
    }

    public void setSwitchRequest(int blockID, boolean switchState) {
        switchRequestedStateList.put(5, switchState);
    }

    public void setSwitchState(int blockID, boolean switchState) {
        switchStateList.put(5, switchState);
    }

    public void setAuthState(int blockID, boolean auth) {
        authList.put(blockID, auth);
    }

    private void setSwitch(int blockID, boolean switchState) {
        switchStateList.put(blockID, switchState);
        controller.setSwitchPLC(blockID, switchState);
    }

    private void setLight(int blockID, boolean lightState) {
        trafficLightList.put(blockID, lightState);
        controller.setTrafficLightPLC(blockID, lightState);
    }

    private void setCrossing(int blockID, boolean crossingState) {
        crossingList.put(blockID, crossingState);
        controller.setCrossingPLC(blockID, crossingState);
    }

    private void setAuth(int blockID, boolean auth) {
        authList.put(blockID, auth);
        controller.setAuthorityPLC(blockID, auth);
    }

    public void runBlueLine() {

        // Process switch state requests
        System.out.println("State of switch is " + switchStateList.get(5));
        if(switchStateList.get(5) != switchRequestedStateList.get(5)) {
            if(!occupancyList.get(5) && !occupancyList.get(6) && !occupancyList.get(11)) {
                setSwitch(5, switchRequestedStateList.get(5));
                System.out.println("Swapping switch state");
            }
        }

        // Set traffic lights
        if(!occupancyList.get(1) && !occupancyList.get(2) && !occupancyList.get(3) && !occupancyList.get(4) && !occupancyList.get(5)) {
            if(switchStateList.get(5) == SWITCH_MAIN) {
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
        if(occupancyList.get(2) || occupancyList.get(3) || occupancyList.get(4)) {
            setCrossing(3, CROSSING_CLOSED);
        }
        else {
            setCrossing(3, CROSSING_OPEN);
        }

        setAuth(1, !occupancyList.get(2) && !occupancyList.get(3));
        setAuth(2, !occupancyList.get(3) && !occupancyList.get(4));
        setAuth(3, !occupancyList.get(4) && !occupancyList.get(5));
        if(switchStateList.get(5) == SWITCH_MAIN) {
            setAuth(4, !occupancyList.get(5) && !occupancyList.get(6));
            setAuth(5, !occupancyList.get(6) && !occupancyList.get(7));
        }
        else {
            setAuth(4, !occupancyList.get(5) && !occupancyList.get(11));
            setAuth(5, !occupancyList.get(11) && !occupancyList.get(12));
        }

        setAuth(6, !occupancyList.get(7) && !occupancyList.get(8));
        setAuth(7, !occupancyList.get(8) && !occupancyList.get(9));
        setAuth(8, !occupancyList.get(9) && !occupancyList.get(10));
        setAuth(9, !occupancyList.get(10));
        setAuth(10, false);

        setAuth(11, !occupancyList.get(12) && !occupancyList.get(13));
        setAuth(12, !occupancyList.get(13) && !occupancyList.get(14));
        setAuth(13, !occupancyList.get(14) && !occupancyList.get(15));
        setAuth(14, !occupancyList.get(15));
        setAuth(15, false);

        System.out.println("Switch = " + switchStateList.get(5));

        System.out.println("Light 6 = " + trafficLightList.get(6));
        System.out.println("Light 1 = " + trafficLightList.get(11));

        System.out.println("Crossing = " + crossingList.get(3));
    }
}
