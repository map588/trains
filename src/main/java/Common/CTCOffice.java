package Common;

import Utilities.Enums.Lines;

public interface CTCOffice{
    //Todo: talk to track, and wayside on functions that are needed for the requirements of the CTC Office

    void setBlockOccupancy(Lines line, int blockID, boolean occupied);
    void setLightState(Lines line, int blockID, boolean lightState);
    void setSwitchState(Lines line, int blockID, boolean switchState);
    void setCrossingState(Lines line, int blockID, boolean crossingState);
    void setBlockMaintenance(Lines line, int blockID, boolean underMaintenance);
    void notifyTrainReturn(int trainID);
}
