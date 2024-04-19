package CTCOffice.ScheduleInfo;

import Framework.Support.Notifier;

import java.util.ArrayList;
import java.util.List;

import static CTCOffice.Properties.ScheduleProperties.*;

// A path in between two stations, a schedule is mostly just a list of these.
public class TrainStop implements Notifier { //SubRoute
    private int stationBlockID;
    private int arrivalTime;
    private int departureTime;
    private List<Integer> speedList;
    private List<Integer> routePath; //routePath
    private List<Integer> authorityList;
    private final TrainStopSubject subject;
    private int stopIndex;

    public TrainStop(int index, int stationBlockID, int arrivalTime, int departureTime, List<Integer> speedList, List<Integer> routePath, List<Integer> authorityList) {
        this.stopIndex = index;
        this.stationBlockID = stationBlockID;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.speedList = new ArrayList<>(speedList);
        this.routePath = new ArrayList<>(routePath);
        this.authorityList = new ArrayList<>(authorityList);
        subject = new TrainStopSubject(this);
    }

    public TrainStop(int index, int stationBlockID, int arrivalTime, int departureTime) {
        this.stopIndex = index;
        this.stationBlockID = stationBlockID;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.speedList = new ArrayList<>();
        this.routePath = new ArrayList<>();
        this.authorityList = new ArrayList<>();
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

    public int getStopIndex() {
        return stopIndex;
    }

    public void setStopIndex(int index) {
        this.stopIndex = index;
        notifyChange(STOP_INDEX_PROPERTY, index);
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
        notifyChange(DESTINATION_PROPERTY, stationBlockID);
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
        notifyChange(ARRIVAL_TIME_PROPERTY, arrivalTime);
    }

    public void setDepartureTime(int departureTime) {
        this.departureTime = departureTime;
        notifyChange(DEPARTURE_TIME_PROPERTY, departureTime);
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

    public void notifyChange(String property, Object newValue) {
        subject.setProperty(property, newValue);
    }
}
