package CTCOffice;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private int trainID;
    private int dispatchTime;
    private int carCount;
    private List<SingleStop> stops  = new ArrayList<SingleStop>();

    Schedule(int trainID, int dispatchTime, int carCount , ArrayList<SingleStop> stops) {
        this.trainID = trainID;
        this.dispatchTime = dispatchTime;
        this.carCount = carCount;
        this.stops = stops;
        ScheduleSubjectFactory.getInstance().registerSubject(trainID, new ScheduleSubject(this));
    }

    public int getTrainID() {
        return trainID;
    }


    public int getDispatchTime() {
        return dispatchTime;
    }

    public int getCarCount() {
        return carCount;
    }

    public List<SingleStop> getStops() {
        return stops;
    }

    public void setTrainID(int trainID) {
        this.trainID = trainID;
    }

    public void setDispatchTime(int dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }

    public void setStops(List<SingleStop> stops) {
        this.stops = stops;
    }

    public void addStop(SingleStop stop) {
        stops.add(stop);
    }

    public void removeStop(int index) {
        stops.remove(index);
    }

    public void updateStop(int index, SingleStop stop) {
        stops.set(index, stop);
    }

    public SingleStop getStop(int index) {
        return stops.get(index);
    }

    public int getStopCount() {
        return stops.size();
    }

    public void clearStops() {
        stops.clear();
    }

    public void printStops() {
        for (SingleStop stop : stops) {
            System.out.println("Block ID: " + stop.getStationBlockID());
        }
    }

    public void printSchedule() {
        System.out.println("Train ID: " + trainID + " Departure Time: " + dispatchTime);
        printStops();
    }

    public void updateStopArrivalTime(int index, int arrivalTime) {
        stops.get(index).setArrivalTime(arrivalTime);
    }

    public void updateStopDepartureTime(int index, int departureTime) {
        stops.get(index).setDepartureTime(departureTime);
    }

    public void updateStopSpeedList(int index, List<Integer> speedList) {
        stops.get(index).setSpeedList(speedList);
    }

    public void updateStopBlockList(int index, List<Integer> blockList) {
        stops.get(index).setBlockList(blockList);
    }

    public void updateStopAuthorityList(int index, List<Integer> authorityList) {
        stops.get(index).setAuthorityList(authorityList);
    }

    public void updateStop(int index, int blockID, int arrivalTime, int departureTime, List<Integer> speedList, List<Integer> blockList, List<Integer> authorityList) {
        stops.get(index).setStationBlockID(blockID);
        stops.get(index).setArrivalTime(arrivalTime);
        stops.get(index).setDepartureTime(departureTime);
        stops.get(index).setSpeedList(speedList);
        stops.get(index).setBlockList(blockList);
        stops.get(index).setAuthorityList(authorityList);
    }

    public void addStop(int blockID, int arrivalTime, int departureTime, List<Integer> speedList, List<Integer> blockList, List<Integer> authorityList) {
        stops.add(new SingleStop(blockID, arrivalTime, departureTime, speedList, blockList, authorityList));
    }

    public void updateStopSpeed(int stopIndex, int speedIndex, int speed) {
        stops.get(stopIndex).updateSpeedList(speedIndex, speed);
    }

    public void updateStopStation(int stopIndex, int blockIndex, int block) {
        stops.get(stopIndex).updateBlockList(blockIndex, block);
    }

    public void updateStopAuthority(int stopIndex, int authorityIndex, int authority) {
        stops.get(stopIndex).updateAuthorityList(authorityIndex, authority);
    }

}
