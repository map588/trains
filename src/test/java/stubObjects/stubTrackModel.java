package stubObjects;

import Common.TrackModel;

import java.util.ArrayList;

public class stubTrackModel implements TrackModel{
    boolean lightState;
    boolean switchState;
    int trainAuthority;
    int commandedSpeed;
    int temp;
    ArrayList<String> line;
    ArrayList<Integer> blockOccupied, failures;

    public stubTrackModel(){
        this.lightState = false;
        this.switchState = false;
        this.trainAuthority = 0;
        this.commandedSpeed = 0;
        this.blockOccupied = new ArrayList<>();
        this.failures = new ArrayList<>();
        this.line = new ArrayList<>();
    }

    public void setBlockOccupied(int block, boolean state) {
        if(state){
            this.blockOccupied.add(block);
        }
        else{
            this.blockOccupied.remove(block);
        }
    }

    public boolean getBlockOccupied(int block) {
        return this.blockOccupied.contains(block);
    }

    public void setTrackHeaters(int temp){ this.temp = temp; }
    public void setTemperature(int temp){ this.temp = temp; }
    public int getTemperature(){ return this.temp; }

    public void setLine(String line) {
        this.line.add(line);
    }

    @Override
    public void setFailure(int block, String failure) {
        if(failure.equals("Fix Track Failure")){
            this.failures.remove(block);
        }
        else{
            this.failures.add(block);
        }
    }


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

}
