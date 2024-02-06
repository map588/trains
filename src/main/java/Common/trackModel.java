package Common;

public interface trackModel {
    int getTrainAuthority(int trainID);
    int getCommandedSpeed(int trainID);

    boolean blockOccupied(int block);
    void setTrainAuthority(int trainID, int authority);
    void setCommandedSpeed(int trainID, int commandedSpeed);
}
