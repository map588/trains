package trackModel;

import Common.trackModel;

public class trackModelImpl implements  trackModel{

    public int getTrainAuthority(int trainID) {
        return 0;
    }

    public int getCommandedSpeed(int trainID) {
        return 0;
    }

    public boolean blockOccupied(int block) {
        return false;
    }

    public void setTrainAuthority(int trainID, int authority) {

    }
    public void setCommandedSpeed(int trainID, int commandedSpeed) {

    }
    public int getLightState(int block) {return 0;}

    public void setLightState(int state) {}

    public boolean getSwitchState(int block) {return false;}

    public void setSwitchState(boolean state) {}

}
