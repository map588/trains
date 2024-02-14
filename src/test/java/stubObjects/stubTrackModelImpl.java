package stubObjects;

import Common.trackModel;

import java.util.Map;

public class stubTrackModelImpl implements trackModel{
    private Map<Integer,Integer> trainAuthorities;
    private Map<Integer,Integer> trainCommandSpeeds;

    private int line;
    private boolean blockOccupied;



    public int getTrainAuthority(int trainID) {
        int auth = trainAuthorities.get(trainID);
        System.out.print("Get Train Authority: "+ auth);
        return auth;
    }

    public int getCommandedSpeed(int trainID) {
        int speed = trainCommandSpeeds.get(trainID);
        System.out.print("Get Commanded Speed: "+ speed);
        return speed;
    }

    public boolean blockOccupied(int block) {
        System.out.print("Get Block Occupied: "+ blockOccupied);
        return this.blockOccupied;
    }

    public void setTrainAuthority(int trainID, int authority) {
        if(this.trainAuthorities.containsKey(trainID)) {
            this.trainAuthorities.replace(trainID, authority);
        } else {
            this.trainAuthorities.put(trainID, authority);
        }
        System.out.print("Set Train Authority: "+ authority);
    }

    public void setCommandedSpeed(int trainID, int commandedSpeed) {
        if(this.trainCommandSpeeds.containsKey(trainID)) {
            this.trainCommandSpeeds.replace(trainID, commandedSpeed);
        } else {
            this.trainCommandSpeeds.put(trainID, commandedSpeed);
        }
        System.out.print("Set Commanded Speed: "+ commandedSpeed);
    }

    public int getLightState(int block) { return 0; }
    public void setLightState(int state) {}
    public boolean getSwitchState(int block) {return false;}
    public void setSwitchState(boolean state) {}
}
