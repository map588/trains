package trackModel;

import Common.TrackModel;
public class stubTrackModel implements TrackModel{
    boolean lightState;
    boolean switchState;
    int trainAuthority;
    int commandedSpeed;
    boolean blockOccupied;

    public int getTrainAuthority(int trainID) {
        return 0;
    }

    public int getCommandedSpeed(int trainID) {
        return 0;
    }

    public boolean getLightState(int block) {
        return false;
    }

    public boolean getSwitchState(int block) {
        return false;
    }

    public void setLightState(boolean state) {
        this.lightState = state;
    }

    public void setSwitchState(boolean state) {
        this.switchState = state;
    }

    public void setTrainAuthority(int trainID, int authority) {
        this.trainAuthority = authority;
    }

    public void setCommandedSpeed(int trainID, int commandedSpeed) {
        this.commandedSpeed = commandedSpeed;
    }

    public boolean blockOccupied(int block) {
        return false;
    }
}
