package CTCOffice.ScheduleInfo;

import java.util.ArrayList;
import java.util.List;

// A path in between two stations, a schedule is mostly just a list of these.
public class TrainStop { //SubRoute
    private int stationBlockID;
    private int arrivalTime;
    private int departureTime;
    private List<Integer> speedList;
    private List<Integer> routePath; //routePath
    private List<Integer> authorityList;
    private final TrainStopSubject subject;

    public TrainStop(int stationBlockID, int arrivalTime, int departureTime, List<Integer> speedList, List<Integer> routePath, List<Integer> authorityList) {
        this.stationBlockID = stationBlockID;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.speedList = new ArrayList<>(speedList);
        this.routePath = new ArrayList<>(routePath);
        this.authorityList = new ArrayList<>(authorityList);
        subject = new TrainStopSubject(this);
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
        this.speedList = new ArrayList<>(speedList);
    }

    public void setRoutePath(List<Integer> routePath) {
        this.routePath = new ArrayList<>(routePath);
    }

    public void setAuthorityList(List<Integer> authorityList) {
        this.authorityList = new ArrayList<>(authorityList);
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

    public TrainStopSubject getSubject() {
        return subject;
    }

}
