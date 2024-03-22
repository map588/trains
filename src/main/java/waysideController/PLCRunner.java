package waysideController;

import java.util.Map;

public interface PLCRunner {

    void setSwitchPLC(int blockID, boolean switchState);
    void setTrafficLightPLC(int blockID, boolean lightState);
    void setCrossingPLC(int blockID, boolean crossingState);
    void setAuthorityPLC(int blockID, boolean auth);
    Map<Integer, WaysideBlock> getBlockMap();
}
