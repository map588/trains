package Common;

public interface TrackModel {
    int getTrainAuthority(int trainID);
    int getCommandedSpeed(int trainID);
    boolean getLightState(int block);
    boolean getSwitchState(int block);
    void setLightState(boolean state);
    void setSwitchState(boolean state);
    void setTrainAuthority(int trainID, int authority);
    void setCommandedSpeed(int trainID, int commandedSpeed);
    void setBlockOccupied(int block, boolean state);
    boolean getBlockOccupied(int block);
    void setTrackHeaters(int temp);
    void setLine(String line);
    void setFailure(int block, String failure);
}
