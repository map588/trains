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
        for (int i = index; i < stops.size(); i++) {
            stops.get(i + 1).setStopIndex(i);
            stops.get(i + 1).setArrivalTime(stops.get(i).getArrivalTime());
            stops.get(i + 1).setDepartureTime(stops.get(i).getDepartureTime());
            stops.put(i, stops.get(i + 1));
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
        for(int i = 1; i <= stops.size(); i++) {
            System.out.println("stop " + i + ": index : " + stops.get(i).getStopIndex());
        }
        if(stopCurrentIndex == stopNewIndex) {
            return;
        }else if(stopCurrentIndex < stopNewIndex) {
            /*
             TrainStop temp = stops.get(stopCurrentIndex);
            for(int i = stopCurrentIndex; i < stopNewIndex; i++) {
                stops.put(i, stops.get(i + 1));
                stops.get(i).setStopIndex(i);
            }
            stops.put(stopNewIndex, temp);
            stops.get(stopNewIndex).setStopIndex(stopNewIndex);
             */
            int tempStationBlockID = stops.get(stopCurrentIndex).getStationBlockID();
            int tempArrivalTime = stops.get(stopNewIndex).getArrivalTime();
            int tempDepartureTime = stops.get(stopNewIndex).getDepartureTime();
            for(int i = stopNewIndex; i > stopCurrentIndex; i--) {
                stops.get(i).setArrivalTime(stops.get(i-1).getArrivalTime());
                stops.get(i).setDepartureTime(stops.get(i-1).getDepartureTime());
                stops.get(i).setStopIndex(i);
            }
            for(int i = stopCurrentIndex + 1; i < stopNewIndex; i++) {stops.put(i, stops.get(i + 1));}
            stops.get(stopNewIndex).setStationBlockID(tempStationBlockID);
            stops.get(stopNewIndex).setArrivalTime(tempArrivalTime);
            stops.get(stopNewIndex).setDepartureTime(tempDepartureTime);
        } else {
            /*
            TrainStop temp = stops.get(stopCurrentIndex);
            for (int i = stopCurrentIndex; i > stopNewIndex; i--) {
                stops.put(i, stops.get(i - 1));
                stops.get(i).setStopIndex(i);
            }
            stops.put(stopNewIndex, temp);
            stops.get(stopNewIndex).setStopIndex(stopNewIndex);
             */
        }
    }
}
