package Common;

public interface trackModel {
    // Called by the train model to pass on speed and authority
    int getTrainAuthority(int trainID);
    int getCommandedSpeed(int trainID);

    // Called by wayside to give track model speed and authority
    // as well as to see if a specific block is occupied
    boolean blockOccupied(int block);
    void setTrainAuthority(int trainID, int authority);
    void setCommandedSpeed(int trainID, int commandedSpeed);
    int getLightState(int block);
    void setLightState(int state);
    boolean getSwitchState(int block);
    void setSwitchState(boolean state);
}
