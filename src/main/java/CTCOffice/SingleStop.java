package CTCOffice;

import java.util.ArrayList;
import java.util.List;

public class SingleStop {
    private int stationBlockID;
    private int arrivalTime;
    private int departureTime;
    private List<Integer> speedList;
    private List<Integer> blockList;
    private List<Integer> authorityList;

    public SingleStop(int stationBlockID, int arrivalTime, int departureTime, List<Integer> speedList, List<Integer> blockList, List<Integer> authorityList) {
        this.stationBlockID = stationBlockID;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.speedList = new ArrayList<Integer>(speedList);
        this.blockList = new ArrayList<Integer>(blockList);
        this.authorityList = new ArrayList<Integer>(authorityList);
    }

    public int getStationBlockID() {
        return stationBlockID;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getDepartureTime() {
        return departureTime;
    }

    public List<Integer> getSpeedList() {
        return speedList;
    }

    public List<Integer> getBlockList() {
        return blockList;
    }

    public List<Integer> getAuthorityList() {
        return authorityList;
    }

    public void setStationBlockID(int stationBlockID) {
        this.stationBlockID = stationBlockID;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setDepartureTime(int departureTime) {
        this.departureTime = departureTime;
    }

    public void setSpeedList(List<Integer> speedList) {
        this.speedList = new ArrayList<Integer>(speedList);
    }

    public void setBlockList(List<Integer> blockList) {
        this.blockList = new ArrayList<Integer>(blockList);
    }

    public void setAuthorityList(List<Integer> authorityList) {
        this.authorityList = new ArrayList<Integer>(authorityList);
    }

    public void updateSpeedList(int index, int speed) {
        speedList.set(index, speed);
    }

    public void updateBlockList(int index, int block) {
        blockList.set(index, block);
    }

    public void updateAuthorityList(int index, int authority) {
        authorityList.set(index, authority);
    }
}
