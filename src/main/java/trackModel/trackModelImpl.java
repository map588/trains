package trackModel;


import Utilities.TrackLayoutInfo;
import Utilities.staticBlockInfo;
import javafx.collections.ObservableList;
import Common.TrackModel;
import javafx.beans.property.BooleanProperty;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class trackModelImpl implements TrackModel {
    private HashMap<Integer,Integer> trainAuthorities;
    private HashMap<Integer,Integer> trainCommandSpeeds;
    private int line;
    private boolean blockOccupied;
    private boolean lightState;
    private boolean switchState;

    private ObservableList<TrackLayoutInfo> trackInfo;

    public trackModelImpl(ArrayList<String> csvData) {
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

    public boolean getLightState(int block){ return this.lightState; }
    public boolean getSwitchState(int block){ return this.switchState; }
    public void setSwitchState(boolean state) { switchState = state; }
    public void setLightState(boolean state) { lightState = state; }

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
    public boolean blockOccupied(int block) {
        return this.blockOccupied;
    }
    public void setBlockOccupied(boolean state) { this.blockOccupied = state; }
    public void setLine(int line) { this.line = line; }
    public int getLine() { return this.line; }



    public ObservableList<TrackLayoutInfo> getTrackInfo() {

        for(int i = 0; i < 15; i++){


            TrackLayoutInfo block = new TrackLayoutInfo();

            block.setBlockNumber(i);

            if(i < 6){
                block.setSection("A");
            }
            else if(i < 11){
                block.setSection("B");
            }
            else{
                block.setSection("C");
            }

            block.setBlockLength(50);
            block.setBlockGrade(0);
            block.setSpeedLimit(50);
            block.setIsCrossing(i == 3);
            block.setIsSignal(i == 6 || i == 11);
            block.setIsSwitch(i == 5 || i == 6 || i == 11);
            block.setIsUnderground(false);
            block.setIsStation(false);
            block.setIsBeacon(i == 9 || i == 14);
            this.trackInfo.add(block);
        }

        return this.trackInfo;
    }
}
