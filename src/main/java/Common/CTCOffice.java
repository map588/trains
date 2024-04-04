package Common;

public interface CTCOffice{
    //Todo: talk to track, and wayside on functions that are needed for the requirements of the CTC Office

    boolean getBlockOccupancy(boolean line, int blockID);
    boolean getSwitchState(boolean line, int switchID);
    boolean getLightState(boolean line, int blockID);
    boolean getCrossingState(boolean line, int blockID);
    boolean getUnderMaintenance(boolean line, int blockID);

    void setBlockOccupancy(boolean line, int blockID, boolean occupied);
    void setLightState(boolean line, int blockID, boolean lightState);
    void setSwitchState(boolean line, int blockID, boolean switchState);
    void setCrossingState(boolean line, int blockID, boolean crossingState);
}
