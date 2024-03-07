package Common;

public interface CTCOffice{
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
