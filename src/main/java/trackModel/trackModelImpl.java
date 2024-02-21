package trackModel;


import Utilities.TrackLayoutInfo;
import Utilities.staticBlockInfo;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import Common.TrackModel;
import javafx.beans.property.BooleanProperty;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class trackModelImpl implements TrackModel {

    public trackModelImpl(){
        this.trainAuthorities = new HashMap<>();
        this.trainCommandSpeeds = new HashMap<>();
        this.line = new ArrayList<>();
        this.blockOccupied = false;
        this.lightState = false;
        this.switchState = false;
    }

    private HashMap<Integer,Integer> trainAuthorities;
    private HashMap<Integer,Integer> trainCommandSpeeds;
    private ArrayList<String> line = new ArrayList<>();
    private boolean blockOccupied;
    private boolean lightState;
    private boolean switchState;
    private ArrayList<Integer> failures = new ArrayList<Integer>();

    private final List<TrackLayoutInfo> trackInfo = new ArrayList<>();

    public trackModelImpl(ArrayList<String> csvData) {
        this.trainAuthorities = new HashMap<>();
        this.trainCommandSpeeds = new HashMap<>();
        this.line = null;
        this.blockOccupied = false;
        this.failures = new ArrayList<Integer>();
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
    public void setLine(String line) { this.line.add(line); }
    public ArrayList<String> getLines() {return this.line;}
    public String getLine(int lineNumber) { return this.line.get(lineNumber); }
    public void setFailure(int block, boolean failure) {

        for(int i = 0; i <= failures.size(); i++){
            failures.add(i);
        }


    }
    public void setFailure(int block, String failure) {
        switch (failure) {
            case "Broken Rail" -> failures.add(block);
            case "Track Circuit Failure" -> failures.add(block);
            case "Power Failure" -> failures.add(block);
            case "Fix Track Failure" -> failures.add(block);

        }
    }




    public List<TrackLayoutInfo> getTrackInfo() {

        for(int i = 0; i <= 15; i++){
            TrackLayoutInfo block = new TrackLayoutInfo();
            if (failures.contains(i)){
                block.setHasFailure(true);
            }
            else {
                block.setHasFailure(false);
            }
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
            block.setIsStation(i == 10 || i == 15);
            block.setIsBeacon(i == 9 || i == 14);
            this.trackInfo.add(block);
        }

        return this.trackInfo;
    }
}
