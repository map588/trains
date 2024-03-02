package stubObjects;

import Common.TrackModel;

import java.util.ArrayList;

public class stubTrackModel implements TrackModel{
    public void setLightState(int block, boolean state){}
    public void setSwitchState(int block, boolean state){}
    public void setCrossing(int block, boolean state){}
    public void setBeacon(int block, String beacon){}
    public void setBlockOccupied(int block, boolean state){}
    public void setTrainAuthority(int trainID, int authority){}
    public void setCommandedSpeed(int trainID, int commandedSpeed){}
    public void setTemperature(int temp){}
    public void addLine(String lines){}
    public void setFailure(int block, String failure){}

    public int getTrainAuthority(int trainID){return 0;}
    public int getCommandedSpeed(int trainID){return 0;}
    public boolean getLightState(int block){return false;}
    public boolean getSwitchState(int block){return false;}
    public boolean getCrossingState(int block){return false;}
    public String getBeacon(int block){return "";}
    public boolean getBlockOccupied(int block){return false;}
    public String getFailures(int block){return "";}
    public int getTemperature(){return 0;}
    public String getStation(int block){return "";}
    public String getLine(int block){return "";}
    public int ticketSales(){return 0;}
    public int passengers(int disembarked){return 0;}

}
