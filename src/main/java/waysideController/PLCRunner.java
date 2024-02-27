package waysideController;

public interface PLCRunner {

    public void setSwitchPLC(int blockID, boolean switchState);
    public void setTrafficLightPLC(int blockID, boolean lightState);
    public void setCrossingPLC(int blockID, boolean crossingState);
    public void setAuthority(int blockID, boolean auth);
}
