package trackModel;


import Common.TrackModel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TrackModelImpl implements TrackModel {
    public TrackModelImpl(){
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
    private ArrayList<Integer> beacons = new ArrayList<Integer>();

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
    public void setBlockOccupied(int block, boolean state) { if(state){ this.blockOccupied.add(block); } }
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
    @Override
    public void setTrackHeaters(int temp){
        System.out.println("Setting Track Heaters: " + temp);
        for(TrackLayoutInfo trackProperties : trackInfo) {
            if(temp < 32){
                trackProperties.trackHeaterProperty().set("STATUS - ON");
            }
            else{
                trackProperties.trackHeaterProperty().set("STATUS - OFF");
            }
        }
    }



    public List<TrackLayoutInfo> getTrackInfo() {

        trackInfo.clear();

        for(int i = 0; i <= 15; i++){
            TrackLayoutInfo block = new TrackLayoutInfo();
            block.setHasFailure(failures.contains(i));
            block.setIsOccupied(blockOccupied.contains(i));
            block.setBlockNumber("" + i);

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

            if(i == 3){
                block.setIsCrossing(true);
                block.setCrossingState("TRUE");
            }

            block.setIsSignal(i == 6 || i == 11);
            block.setIsSwitch(i == 5 || i == 6 || i == 11);
            block.setIsUnderground(false);
            block.setIsStation(i == 10 || i == 15);
            block.setIsBeacon(i == 9 || i == 14);
            if(i == 5){
                block.setSwitchMain("6");
                block.setSwitchAlt("11");
                block.setSwitchBlockID("5");
            }
            
            if(i == 9){
                beacons.add(9);
            }

            if (i == 14){
                beacons.add(14);
            }

            if(i == 6){
                block.setSignalID("6");
            }

            if(i == 11){
                block.setSignalID("11");
            }

            if(i == 10) {
                block.setNameOfStation("Station B");
            }
            if(i == 15) {
                block.setNameOfStation("Station C");
            }



            this.trackInfo.add(block);
        }

        return this.trackInfo;
    }
}
