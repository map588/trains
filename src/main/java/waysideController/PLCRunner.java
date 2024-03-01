package waysideController;

import java.util.Map;

public interface PLCRunner {

    public void setSwitchPLC(int blockID, boolean switchState);
    public void setTrafficLightPLC(int blockID, boolean lightState);
    public void setCrossingPLC(int blockID, boolean crossingState);
    public void setAuthorityPLC(int blockID, boolean auth);
    public Map<Integer, WaysideBlock> getBlockMap();
}
