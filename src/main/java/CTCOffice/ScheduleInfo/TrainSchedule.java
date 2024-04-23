package CTCOffice.ScheduleInfo;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

import static CTCOffice.CTCOfficeImpl.GreenTrackLayout;

public class TrainSchedule {
    private final int trainID;
    private String line;
    private int dispatchTime;
    private int carCount;
    private final ArrayList<TrainStop> stops;
    public final ObservableList<Integer> stopIndices = FXCollections.observableArrayList();
    public final ObservableList<TrainStopSubject> stopList;
    TrainScheduleSubject subject;

    public TrainSchedule(int trainID, String line, int dispatchTime, int carCount, ArrayList<TrainStop> stops) {
        this.trainID = trainID;
        this.line = line;
        this.dispatchTime = dispatchTime;
        this.carCount = carCount;
        this.stops = stops;
        stopList = FXCollections.observableArrayList();
        for (TrainStop stop : stops) {
            stopList.add(stop.getSubject());
            stopIndices.add(stop.getStopIndex());
        }
        subject = new TrainScheduleSubject(this);
    }

    public int getTrainID() {
        return trainID;
    }

    public void setDispatchTime(int dispatchTime) {
        this.dispatchTime = dispatchTime;
    }
    public int getDispatchTime() {
        return dispatchTime;
    }

    public void setCarCount(int carCount) {
        this.carCount = carCount;
    }
    public int getCarCount() {
        return carCount;
    }

    public ArrayList<TrainStop> getStops() {
        return stops;
    }
    public TrainStop getStop(int index) {
        return stops.get(index);
    }
    public int getStopCount() {
        return stops.size();
    }

    public void addStop(int blockID,  int arrivalTime, int departureTime) {
        stops.add( new TrainStop(stops.size(), blockID, arrivalTime, departureTime));
        stopList.add(stops.get(stops.size() - 1).getSubject());
        stopIndices.add(stops.size());
    }

    public void removeStop(int index) {
        if(index < 0 || index >= stops.size()) {
            return;
        }
        for (int i = stops.size() - 1; i > index; i--) {
            stops.get(i).setArrivalTime(stops.get(i - 1).getArrivalTime());
            stops.get(i).setDepartureTime(stops.get(i - 1).getDepartureTime());
        }
        stopList.remove(index);
        stops.remove(index);
        stopIndices.remove(stops.size());
        for (int i = index; i < stops.size(); i++) {
            stops.get(i).setStopIndex(i);
        }
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public TrainScheduleSubject getSubject() {
       return subject;
    }

    public void moveStop(int stopCurrentIndex, int stopNewIndex) {
        TrainStop temp = stops.get(stopCurrentIndex);
        int tempArrival = stops.get(stopNewIndex).getArrivalTime();
        int tempDeparture = stops.get(stopNewIndex).getDepartureTime();
        if(stopCurrentIndex == stopNewIndex) {
            return;
        }else if(stopCurrentIndex < stopNewIndex) {
           for (int i = stopNewIndex; i > stopCurrentIndex; i--) {
               stops.get(i).setArrivalTime(stops.get(i - 1).getArrivalTime());
                stops.get(i).setDepartureTime(stops.get(i - 1).getDepartureTime());
            }
        } else {
            for (int i = stopNewIndex; i < stopCurrentIndex; i++) {
                stops.get(i).setArrivalTime(stops.get(i + 1).getArrivalTime());
                stops.get(i).setDepartureTime(stops.get(i + 1).getDepartureTime());
            }
        }
        stopList.remove(stopCurrentIndex);
        stops.remove(stopCurrentIndex);
        stops.add(stopNewIndex, temp);
        stops.get(stopNewIndex).setStopIndex(stopNewIndex);
        stops.get(stopNewIndex).setArrivalTime(tempArrival);
        stops.get(stopNewIndex).setDepartureTime(tempDeparture);
        stopList.add(stopNewIndex, stops.get(stopNewIndex).getSubject());
        for(int i = 0; i < stops.size(); i++) {
            stops.get(i).setStopIndex(i);
        }
    }
    public int checkSchedule() {
        int visited = 0;
        for (int i = 0; i < stops.size(); i++) {
            // Check if arrival time is before departure time of previous stop
            if(!GreenTrackLayout.contains(stops.get(i).getStationBlockID())) {
                removeStop(i);
                return -1;
            }
            if (i >= 1) {
                if (stops.get(i).getArrivalTime() < stops.get(i - 1).getDepartureTime()) {
                    stops.get(i).setArrivalTime(stops.get(i - 1).getDepartureTime() + 2);
                }
            }
            // Check if arrival time is before departure time
            if (stops.get(i).getArrivalTime() > stops.get(i).getDepartureTime()) {
                stops.get(i).setDepartureTime(stops.get(i).getArrivalTime() + 1);
            }
            // Check if stop is at the same station as last stop
            if (i >= 1) {
                if (stops.get(i).getStationBlockID() == stops.get(i - 1).getStationBlockID()) {
                    System.out.println("Removing stop : " + i);
                    removeStop(i);
                    System.out.println("Removed stop : " + i);
                    return -1;
                }
            }
            // Check if station is out of order
            if (i >= 1) {
                if (GreenTrackLayout.indexOf(stops.get(i).getStationBlockID()) > visited) {
                    visited = GreenTrackLayout.indexOf(stops.get(i).getStationBlockID());
                }else if (GreenTrackLayout.lastIndexOf(stops.get(i).getStationBlockID()) > visited) {
                    visited = GreenTrackLayout.lastIndexOf(stops.get(i).getStationBlockID());
                }else{
                    return i;
                }
            }
            else{
                visited = GreenTrackLayout.indexOf(stops.get(i).getStationBlockID());
            }
        }
        return -2;
    }

    public void fixSchedule() {
        int fixed;
        while(true) {
            fixed = checkSchedule();
            System.out.println(fixed + " : " + stops.size());
            if(fixed == -2) {
                break;
            }
            if(fixed >= 0) {
                System.out.println("moving stop : " + fixed + " to " + (fixed - 1));
                moveStop(fixed, fixed - 1);
            }
        }
        System.out.println(fixed);
    }
}
