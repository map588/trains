package waysideController;

import Common.WaysideController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Utilities.Constants.*;

public class PLCProgram {
    private final List<Boolean> occupancyList;
    private final Map<Integer, Boolean> switchStateList;
    private final Map<Integer, Boolean> switchRequestedStateList;
    private final Map<Integer, Boolean> trafficLightList;
    private final Map<Integer, Boolean> crossingList;

    private final WaysideController controller;

    public PLCProgram(WaysideController controller) {
        occupancyList = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false);
        switchStateList = new HashMap<>();
        switchRequestedStateList = new HashMap<>();
        trafficLightList = new HashMap<>();
        crossingList = new HashMap<>();

        switchStateList.put(5, SWITCH_MAIN);
        switchRequestedStateList.put(5, SWITCH_MAIN);
        trafficLightList.put(6, LIGHT_RED);
        trafficLightList.put(11, LIGHT_RED);
        crossingList.put(3, CROSSING_OPEN);

        this.controller = controller;
    }

    public void setOccupancy(int blockID, boolean occupied) {
        occupancyList.set(blockID, occupied);
    }

    public void setSwitchRequest(int blockID, boolean switchState) {
        switchRequestedStateList.put(5, switchState);
    }

    public void runBlueLine() {

        // Process switch state requests
        if(switchStateList.get(5) != switchRequestedStateList.get(5)) {
            if(!occupancyList.get(5) && !occupancyList.get(6) && !occupancyList.get(11)) {
                switchStateList.put(5, switchRequestedStateList.get(5));
                controller.getBlockList().get(4).switchStateProperty().set(switchStateList.get(5));
            }
        }

        // Set traffic lights
        if(!occupancyList.get(1) && !occupancyList.get(2) && !occupancyList.get(3) && !occupancyList.get(4) && !occupancyList.get(5)) {
            if(switchStateList.get(5) == SWITCH_MAIN) {
                trafficLightList.put(6, LIGHT_GREEN);
                trafficLightList.put(11, LIGHT_RED);
            }
            else {
                trafficLightList.put(6, LIGHT_RED);
                trafficLightList.put(11, LIGHT_GREEN);
            }
        }
        else {
            trafficLightList.put(6, LIGHT_RED);
            trafficLightList.put(11, LIGHT_RED);
        }

        controller.getBlockList().get(5).setLightState(trafficLightList.get(6));
        controller.getBlockList().get(10).setLightState(trafficLightList.get(11));

        // Set Railroad Crossings
        if(occupancyList.get(2) || occupancyList.get(3) || occupancyList.get(4)) {
            crossingList.put(3, CROSSING_CLOSED);
        }
        else {
            crossingList.put(3, CROSSING_OPEN);
        }

        for(int switchID = 0; switchID < switchStateList.size(); switchID++) {
            System.out.println("Switch " + switchID + " = " + switchStateList.get(switchID));
        }

        for(int lightID = 0; lightID < trafficLightList.size(); lightID++) {
            System.out.println("Light " + lightID + " = " + trafficLightList.get(lightID));
        }

        for(int crossingID = 0; crossingID < crossingList.size(); crossingID++) {
            System.out.println("Crossing " + crossingID + " = " + crossingList.get(crossingID));
        }
    }
}
