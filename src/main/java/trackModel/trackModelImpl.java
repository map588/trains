package trackModel;


import Utilities.TrackLayoutInfo;
import Common.TrackModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class trackModelImpl implements TrackModel {
    public trackModelImpl(){
        this.trainAuthorities = new HashMap<>();
        this.trainCommandSpeeds = new HashMap<>();
        this.line = new ArrayList<>();
        this.lightState = false;
        this.switchState = false;
        this.blockOccupied = new ArrayList<Integer>();
        this.failures = new ArrayList<Integer>();
    }

    private HashMap<Integer,Integer> trainAuthorities;
    private HashMap<Integer,Integer> trainCommandSpeeds;
    private ArrayList<String> line = new ArrayList<>();
    private ArrayList<Integer> blockOccupied = new ArrayList<>();
    private boolean lightState;
    private boolean switchState;
    private int temperature;
    private ArrayList<Integer> failures = new ArrayList<Integer>();

    private final List<TrackLayoutInfo> trackInfo = new ArrayList<>();



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

    public boolean getBlockOccupied(int block) {
        return this.blockOccupied.contains(block);
    }
    public void setBlockOccupied(int block, boolean state) {
        if(state){
            this.blockOccupied.add(block);
        }
        else{
            this.blockOccupied.remove(block);
        }
    }
    public void setLine(String line) { this.line.add(line); }
    public String getLine(int lineNumber) { return this.line.get(lineNumber); }

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

    public void setFailure(int block, String failure) {
        if(failure.equals("Broken Rail") || failure.equals("Track Circuit Failure") || failure.equals("Power Failure")) {
            failures.add(block);
            this.trackInfo.get(block).setHasFailure(true);
        }
        if(failure.equals("Fix Track Failure")){
            failures.remove(block);
            this.trackInfo.get(block).setHasFailure(false);
        }
    }

    public List<TrackLayoutInfo> getTrackInfo() {

        for(int i = 0; i <= 15; i++){
            TrackLayoutInfo block = new TrackLayoutInfo();
            block.setHasFailure(failures.contains(i));
            block.setIsOccupied(blockOccupied.contains(i));
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
