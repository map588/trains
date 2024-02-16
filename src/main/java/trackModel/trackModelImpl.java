package trackModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;

import java.util.HashMap;


public class trackModelImpl {

    private HashMap<Integer,Integer> trainAuthorities;
    private HashMap<Integer,Integer> trainCommandSpeeds;
    private int line;
    private boolean blockOccupied;

    private IntegerProperty lightState;
    private BooleanProperty switchState;

    public trackModelImpl() {
        this.trainAuthorities = new HashMap<>();
        this.trainCommandSpeeds = new HashMap<>();
        this.line = 0;
        this.blockOccupied = false;
    }
    public int getAuthority(int trainID) {
        return this.trainAuthorities.get(trainID);
    }

    public int getTrainAuthority(int trainID) {
        return this.trainAuthorities.get(trainID);
    }

    public int getCommandedSpeed(int trainID) {
        return this.trainCommandSpeeds.get(trainID);
    }

    public boolean blockOccupied(int block) { return this.blockOccupied;}

    // Getters and Setters for Lights and Switches
    public int getLightState(int block) {return this.lightState.get();}

    public void setLightState(int state) {this.lightState.set(state);}

    public boolean getSwitchState(int block) {return this.switchState.get();}

    public void setSwitchState(boolean state) {this.switchState.set(state);}

    //public int setPassengersDisembarked(int trainID) {return 0;}
    //public int getPassengersDisembarked(int trainID) {return 0;}

    public void setTrainAuthority(int trainID, int authority) {
        if(this.trainAuthorities.containsKey(trainID)) {
            this.trainAuthorities.replace(trainID, authority);
        } else {
            this.trainAuthorities.put(trainID, authority);
        }
    }

    public void setCommandedSpeed(int trainID, int commandedSpeed) {
        if(this.trainCommandSpeeds.containsKey(trainID)) {
            this.trainCommandSpeeds.replace(trainID, commandedSpeed);
        } else {
            this.trainCommandSpeeds.put(trainID, commandedSpeed);
        }
    }
}

