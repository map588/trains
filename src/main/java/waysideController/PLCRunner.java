package waysideController;

import java.util.Map;

public interface PLCRunner {

    void setSwitchPLC(int blockID, boolean switchState);
    void setTrafficLightPLC(int blockID, boolean lightState);
    void setCrossingPLC(int blockID, boolean crossingState);
    void setAuthorityPLC(int blockID, boolean auth);
    boolean getOutsideOccupancy(int blockID);
    boolean getOutsideSwitch(int blockID);
    Map<Integer, WaysideBlock> getBlockMap();
}
