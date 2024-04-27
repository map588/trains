package CTCOffice;

import static CTCOffice.Properties.ScheduleProperties.*;
import static CTCOffice.TimeConvert.*;

import Framework.Support.Notifier;
import java.util.ArrayList;
import java.util.List;

// A path in between two stations, a schedule is mostly just a list of these.
public class TrainStop implements Notifier { // SubRoute
  private int stationBlockID;
  private double arrivalTime;
  private double departureTime;
  private final List<Double> speedList;
  private final List<Integer> routePath; // routePath
  private final List<Double> authorityList;
  private final TrainStopSubject subject;
  private int stopIndex;

  private int passedBlocks = 0;

  public TrainStop(
      int index,
      int stationBlockID,
      double arrivalTime,
      double departureTime,
      List<Double> speedList,
      List<Integer> routePath,
      List<Double> authorityList) {
    this.stopIndex = index;
    this.stationBlockID = stationBlockID;
    this.arrivalTime = arrivalTime;
    this.departureTime = departureTime;
    this.speedList = new ArrayList<>(speedList);
    this.routePath = new ArrayList<>(routePath);
    this.authorityList = new ArrayList<>(authorityList);
    subject = new TrainStopSubject(this);
  }

  public TrainStop(CompressedStop compressedStop) {
    this.stationBlockID = compressedStop.stopBlockID();
    this.arrivalTime = compressedStop.arrivalTime();
    this.departureTime = compressedStop.departureTime();
    this.speedList = new ArrayList<>(compressedStop.speedList());
    this.routePath = new ArrayList<>(compressedStop.routePath());
    this.authorityList = new ArrayList<>(compressedStop.authorityList());
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

  // <editor-fold desc="Getters and Setters">
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

  public boolean incrementPassedBlocks() {
    if (passedBlocks >= (routePath.size() - 1)) return true;

    passedBlocks++;
    return passedBlocks == routePath.size() - 1;
  }

  public int getPassedBlocks() {
    return passedBlocks;
  }

  public TrainStopSubject getSubject() {
    return subject;
  }

  // </editor-fold>
  public void notifyChange(String property, Object newValue) {
    subject.setProperty(property, newValue);
  }
}
