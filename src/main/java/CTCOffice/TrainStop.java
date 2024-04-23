package CTCOffice;

import Framework.Support.Notifier;

import java.util.ArrayList;
import java.util.List;

import static CTCOffice.Properties.ScheduleProperties.*;
import static Utilities.TimeConvert.*;

// A path in between two stations, a schedule is mostly just a list of these.
public class TrainStop implements Notifier { //SubRoute
    private int stationBlockID;
    private double arrivalTime;
    private double departureTime;
    private List<Double> speedList;
    private List<Integer> routePath; //routePath
    private List<Double> authorityList;
    private final TrainStopSubject subject;
    private int stopIndex;

    public TrainStop(int index, int stationBlockID, double arrivalTime, double departureTime, List<Double> speedList, List<Integer> routePath, List<Double> authorityList) {
        this.stopIndex = index;
        this.stationBlockID = stationBlockID;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.speedList = new ArrayList<>(speedList);
        this.routePath = new ArrayList<>(routePath);
        this.authorityList = new ArrayList<>(authorityList);
        subject = new TrainStopSubject(this);
    }

    public TrainStop(int index, int stationBlockID, double arrivalTime, double departureTime) {
        this.stopIndex = index;
        this.stationBlockID = stationBlockID;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.speedList = new ArrayList<>();
        this.routePath = new ArrayList<>();
        this.authorityList = new ArrayList<>();
        subject = new TrainStopSubject(this);
    }
    //<editor-fold desc="Getters and Setters">
    public int getStationBlockID() {
        return stationBlockID;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getDepartureTime() {
        return departureTime;
    }

    public int getStopIndex() {
        return stopIndex;
    }

    public void setStopIndex(int index) {
        this.stopIndex = index;
        notifyChange(STOP_INDEX_PROPERTY, index);
    }

    public List<Double> getSpeedList() {
        return speedList;
    }

    public List<Integer> getRoutePath() {
        return routePath;
    }

    public List<Double> getAuthorityList() {
        return authorityList;
    }

    public void setStationBlockID(int stationBlockID) {
        this.stationBlockID = stationBlockID;
        notifyChange(DESTINATION_PROPERTY, stationBlockID);
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
        notifyChange(ARRIVAL_TIME_PROPERTY, convertDoubleToClockTime(arrivalTime));
    }

    public void setDepartureTime(double departureTime) {
        this.departureTime = departureTime;
        notifyChange(DEPARTURE_TIME_PROPERTY, convertDoubleToClockTime(departureTime));
    }

    public void setSpeedList(List<Double> speedList) {
        this.speedList = new ArrayList<>(speedList);
    }

    public void setRoutePath(List<Integer> routePath) {
        this.routePath = new ArrayList<>(routePath);
    }

    public void setAuthorityList(List<Double> authorityList) {
        this.authorityList = new ArrayList<>(authorityList);
    }

    public void updateSpeedList(int index, double speed) {
        speedList.set(index, speed);
    }

    public void updateBlockList(int index, int block) {
        routePath.set(index, block);
    }

    public void updateAuthorityList(int index, double authority) {
        authorityList.set(index, authority);
    }

    public TrainStopSubject getSubject() {
        return subject;
    }
    //</editor-fold>
    public void notifyChange(String property, Object newValue) {
        subject.setProperty(property, newValue);
    }
}
