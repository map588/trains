package CTCOffice.ScheduleInfo;


import java.util.ArrayList;
import java.util.Map;

import static CTCOffice.CTCOfficeImpl.GreenTrackLayout;
import static Utilities.TimeConvert.*;

public class TrainSchedule {
    private final int trainID;
    private String line;
    private int dispatchTime;
    private int carCount;
    private final Map<Integer, TrainStop> stops;
    TrainScheduleSubject subject;

    public TrainSchedule(int trainID, String line, int dispatchTime, int carCount, Map<Integer, TrainStop> stops) {
        this.trainID = trainID;
        this.line = line;
        this.dispatchTime = dispatchTime;
        this.carCount = carCount;
        this.stops = stops;
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

    public Map<Integer, TrainStop> getStops() {
        return stops;
    }
    public TrainStop getStop(int index) {
        return stops.get(index);
    }
    public int getStopCount() {
        return stops.size();
    }

    public void addStop(int blockID,  int arrivalTime, int departureTime) {
        stops.put(stops.size()+1, new TrainStop(stops.size()+1, blockID, arrivalTime, departureTime));
    }

    public void removeStop(int index) {
        if(index < 1 || index > stops.size()) {
            return;
        }
        for (int i = stops.size(); i > index; i--) {
            stops.get(i).setArrivalTime(stops.get(i - 1).getArrivalTime());
            stops.get(i).setDepartureTime(stops.get(i - 1).getDepartureTime());
        }
        for (int i = index; i < stops.size(); i++) {
            stops.put(i, stops.get(i + 1));
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
        if(stopCurrentIndex == stopNewIndex) {
            return;
        }else if(stopCurrentIndex < stopNewIndex) {
            TrainStop temp = stops.get(stopCurrentIndex);
            int tempArrival = stops.get(stopNewIndex).getArrivalTime();
            int tempDeparture = stops.get(stopNewIndex).getDepartureTime();
           for (int i = stopNewIndex; i > stopCurrentIndex; i--) {
               stops.get(i).setArrivalTime(stops.get(i - 1).getArrivalTime());
                stops.get(i).setDepartureTime(stops.get(i - 1).getDepartureTime());
            }
           for (int i = stopCurrentIndex; i < stopNewIndex; i++) {
               stops.put(i, stops.get(i + 1));
               stops.get(i).setStopIndex(i);
           }
           stops.put(stopNewIndex, temp);
           stops.get(stopNewIndex).setStopIndex(stopNewIndex);
           stops.get(stopNewIndex).setArrivalTime(tempArrival);
           stops.get(stopNewIndex).setDepartureTime(tempDeparture);
        } else {
            TrainStop temp = stops.get(stopCurrentIndex);
            int tempArrival = stops.get(stopNewIndex).getArrivalTime();
            int tempDeparture = stops.get(stopNewIndex).getDepartureTime();
            for (int i = stopNewIndex; i < stopCurrentIndex; i++) {
                stops.get(i).setArrivalTime(stops.get(i + 1).getArrivalTime());
                stops.get(i).setDepartureTime(stops.get(i + 1).getDepartureTime());
            }
            for (int i = stopCurrentIndex; i > stopNewIndex; i--) {
                stops.put(i, stops.get(i - 1));
                stops.get(i).setStopIndex(i);
            }
            stops.put(stopNewIndex, temp);
            stops.get(stopNewIndex).setStopIndex(stopNewIndex);
            stops.get(stopNewIndex).setArrivalTime(tempArrival);
            stops.get(stopNewIndex).setDepartureTime(tempDeparture);
        }
    }
    public int checkSchedule() {
        int visited = 0;
        for (int i = 1; i <= stops.size(); i++) {
            // Check if arrival time is before departure time of previous stop
            if (i > 1) {
                if (stops.get(i).getArrivalTime() < stops.get(i - 1).getDepartureTime()) {
                    stops.get(i).setArrivalTime(stops.get(i - 1).getDepartureTime() + 2);
                }
            }
            // Check if arrival time is before departure time
            if (stops.get(i).getArrivalTime() > stops.get(i).getDepartureTime()) {
                stops.get(i).setDepartureTime(stops.get(i).getArrivalTime() + 1);
            }
            // Check if stop is at the same station as last stop
            if (i > 1) {
                if (stops.get(i).getStationBlockID() == stops.get(i - 1).getStationBlockID()) {
                    removeStop(i);
                    return -1;
                }
            }
            // Check if station is out of order
            if (i > 1) {
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
        return 0;
    }

    public void fixSchedule() {
        int fixed = -2;
        while(fixed != 0) {
            fixed = checkSchedule();
            if(fixed == 0) {
                break;
            }
            if(fixed > 0) {
                moveStop(fixed, fixed - 1);
            }
        }
    }
}
