package waysideController;

import java.util.Arrays;
import java.util.List;
import static Utilities.Constants.*;

public class PLCProgram {
    private final List<Boolean> occupancyList;
    private final List<Boolean> switchStateList;
    private final List<Boolean> switchRequestedStateList;
    private final List<Boolean> trafficLightList;
    private final List<Boolean> crossingList;

    public PLCProgram() {
        occupancyList = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false, false, false, false, false);
        switchStateList = Arrays.asList(SWITCH_MAIN);
        switchRequestedStateList = Arrays.asList(SWITCH_MAIN);
        trafficLightList = Arrays.asList(LIGHT_RED, LIGHT_RED);
        crossingList = Arrays.asList(CROSSING_CLOSED);
    }

    public void setOccupancy(int blockID, boolean occupied) {
        occupancyList.set(blockID, occupied);
    }

    public void runBlueLine() {

        // Process switch state requests
        if(switchStateList.get(0) != switchRequestedStateList.get(0)) {
            if(!occupancyList.get(5) && occupancyList.get(6) && occupancyList.get(11)) {
                switchStateList.set(0, switchRequestedStateList.get(0));
            }
        }

        // Set traffic lights
        if(!occupancyList.get(1) && !occupancyList.get(2) && !occupancyList.get(3) && !occupancyList.get(4) && !occupancyList.get(5)) {
            if(switchStateList.get(0) == SWITCH_MAIN) {
                trafficLightList.set(0, LIGHT_GREEN);
                trafficLightList.set(1, LIGHT_RED);
            }
            else {
                trafficLightList.set(0, LIGHT_RED);
                trafficLightList.set(1, LIGHT_GREEN);
            }
        }
        else {
            trafficLightList.set(0, LIGHT_RED);
            trafficLightList.set(1, LIGHT_RED);
        }

        // Set Railroad Crossings
        if(occupancyList.get(2) || occupancyList.get(3) || occupancyList.get(4)) {
            crossingList.set(0, CROSSING_CLOSED);
        }
        else {
            crossingList.set(1, CROSSING_OPEN);
        }

        for(int switchID = 0; switchID < switchStateList.size(); switchID++) {
            System.out.println("Switch " + switchID + " = " + switchStateList.get(switchID));
        }

        for(int lightID = 0; lightID < trafficLightList.size(); lightID++) {
            System.out.println("Light " + lightID + " = " + trafficLightList.get(lightID));
        }

        for(int crossingID = 0; crossingID < crossingList.size(); crossingID++) {
            System.out.println("Light " + crossingID + " = " + crossingList.get(crossingID));
        }
    }
}
