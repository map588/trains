package CTCOffice.ScheduleInfo;

import Framework.Support.Notifier;

import java.util.List;
import java.util.Map;

import static CTCOffice.Properties.ScheduleProperties.*;
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
}
