package CTCOffice;

import java.util.ArrayList;
import java.util.List;

// A path in between two stations, a schedule is mostly just a list of these.
public class SubRoute { //SubRoute
    private int stationBlockID;
    private int arrivalTime;
    private int departureTime;
    private List<Integer> speedList;
    private List<Integer> routePath; //routePath
    private List<Integer> authorityList;

    public SubRoute(int stationBlockID, int arrivalTime, int departureTime, List<Integer> speedList, List<Integer> routePath, List<Integer> authorityList) {
        this.stationBlockID = stationBlockID;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.speedList = new ArrayList<Integer>(speedList);
        this.routePath = new ArrayList<Integer>(routePath);
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

    public List<Integer> getRoutePath() {
        return routePath;
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

    public void setRoutePath(List<Integer> routePath) {
        this.routePath = new ArrayList<Integer>(routePath);
    }

    public void setAuthorityList(List<Integer> authorityList) {
        this.authorityList = new ArrayList<Integer>(authorityList);
    }

    public void updateSpeedList(int index, int speed) {
        speedList.set(index, speed);
    }

    public void updateBlockList(int index, int block) {
        routePath.set(index, block);
    }

    public void updateAuthorityList(int index, int authority) {
        authorityList.set(index, authority);
    }
}
