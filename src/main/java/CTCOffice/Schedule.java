package CTCOffice;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private int trainID;
    private int dispatchTime;
    private int carCount;
    private List<SubRoute> stops  = new ArrayList<SubRoute>();
    private final ScheduleSubjectFactory factory = ScheduleSubjectFactory.getInstance();

    Schedule(int trainID, int dispatchTime, int carCount , ArrayList<SubRoute> stops) {
        this.trainID = trainID;
        this.dispatchTime = dispatchTime;
        this.carCount = carCount;
        this.stops = stops;
        factory.registerSubject(trainID, new ScheduleSubject(this));
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

    public List<SubRoute> getStops() {
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

    public void setStops(List<SubRoute> stops) {
        this.stops = stops;
    }

    public void addStop(SubRoute stop) {
        stops.add(stop);
    }

    public void removeStop(int index) {
        stops.remove(index);
    }

    public void updateStop(int index, SubRoute stop) {
        stops.set(index, stop);
    }

    public SubRoute getStop(int index) {
        return stops.get(index);
    }

    public int getStopCount() {
        return stops.size();
    }

    public void clearStops() {
        stops.clear();
    }

    public void printStops() {
        for (SubRoute stop : stops) {
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
        stops.get(index).setRoutePath(blockList);
    }

    public void updateStopAuthorityList(int index, List<Integer> authorityList) {
        stops.get(index).setAuthorityList(authorityList);
    }

    public void updateStop(int index, int blockID, int arrivalTime, int departureTime, List<Integer> speedList, List<Integer> blockList, List<Integer> authorityList) {
        stops.get(index).setStationBlockID(blockID);
        stops.get(index).setArrivalTime(arrivalTime);
        stops.get(index).setDepartureTime(departureTime);
        stops.get(index).setSpeedList(speedList);
        stops.get(index).setRoutePath(blockList);
        stops.get(index).setAuthorityList(authorityList);
    }

    public void addStop(int blockID, int arrivalTime, int departureTime, List<Integer> speedList, List<Integer> blockList, List<Integer> authorityList) {
        stops.add(new SubRoute(blockID, arrivalTime, departureTime, speedList, blockList, authorityList));
    }

    public void setSpeed(int subRouteIndex, int blockID, int speed) {
        stops.get(subRouteIndex).updateSpeedList(blockID, speed);
    }

    public void setAuthority(int subRouteIndex, int blockID, int authority) {
        stops.get(subRouteIndex).updateAuthorityList(blockID, authority);
    }

    public void setDestination(int subRouteIndex, int blockID) {
        stops.get(subRouteIndex).setStationBlockID(blockID);
    }

}
