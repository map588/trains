package Common;

public interface trackModel {
    int getTrainAuthority(int trainID);
    int getCommandedSpeed(int trainID);
    boolean getLightState(int block);
    boolean getSwitchState(int block);
    void setLightState(boolean state);
    void setSwitchState(boolean state);
    void setTrainAuthority(int trainID, int authority);
    void setCommandedSpeed(int trainID, int commandedSpeed);
    boolean blockOccupied(int block);
}
