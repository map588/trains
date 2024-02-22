package CTCOffice;

import Framework.Support.AbstractSubject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.ArrayList;
import java.util.List;

public class ScheduleSubject implements AbstractSubject {
    private final IntegerProperty trainID;
    private final IntegerProperty dispatchTime;
    private final IntegerProperty carCount;
    private List<IntegerProperty> stationBlockID = new ArrayList<>();
    private List<IntegerProperty> arrivalTime = new ArrayList<>();
    private List<IntegerProperty> departureTime = new ArrayList<>();

    ScheduleSubject(Schedule schedule) {
        this.trainID = new SimpleIntegerProperty(this, "trainID", schedule.getTrainID());
        this.dispatchTime = new SimpleIntegerProperty(this, "dispatchTime", schedule.getDispatchTime());
        this.carCount = new SimpleIntegerProperty(this, "carCount", schedule.getCarCount());
        for (int i = 0; i < schedule.getStopCount(); i++) {
            this.stationBlockID.add(new SimpleIntegerProperty(this, "blockID", schedule.getStop(i).getStationBlockID()));
            this.arrivalTime.add(new SimpleIntegerProperty(this, "arrivalTime", schedule.getStop(i).getArrivalTime()));
            this.departureTime.add(new SimpleIntegerProperty(this, "departureTime", schedule.getStop(i).getDepartureTime()));
        }
    }

    ScheduleSubject(int trainID, int dispatchTime, int carCount, List<Integer> stationBlockID, List<Integer> arrivalTime, List<Integer> departureTime) {
        this.trainID = new SimpleIntegerProperty(this, "trainID", trainID);
        this.dispatchTime = new SimpleIntegerProperty(this, "dispatchTime", dispatchTime);
        this.carCount = new SimpleIntegerProperty(this, "carCount", carCount);
        for (int i = 0; i < stationBlockID.size(); i++) {
        System.out.println("making schedule" + stationBlockID.get(i));
            this.stationBlockID.add(new SimpleIntegerProperty(this, "blockID", stationBlockID.get(i)));
            this.arrivalTime.add(new SimpleIntegerProperty(this, "arrivalTime", arrivalTime.get(i)));
            this.departureTime.add(new SimpleIntegerProperty(this, "departureTime", departureTime.get(i)));
        }
    }

    public void setProperty(String propertyName, Object newValue) {
        switch (propertyName) {
            case "trainID" -> trainID.set((Integer) newValue);
            case "dispatchTime" -> dispatchTime.set((Integer) newValue);
            case "carCount" -> carCount.set((Integer) newValue);
            default -> System.err.println("Unknown property " + propertyName);
        };
    }

    public void setProperty(String propertyName, Object newValue, int index) {
        switch (propertyName) {
            case "blockID" -> stationBlockID.get(index).set((Integer) newValue);
            case "arrivalTime" -> arrivalTime.get(index).set((Integer) newValue);
            case "departureTime" -> departureTime.get(index).set((Integer) newValue);
            default -> System.err.println("Unknown property " + propertyName);
        };
    }

    public Property<?> getProperty(String propertyName) {
        return switch (propertyName) {
            case "trainID" -> trainID;
            case "dispatchTime" -> dispatchTime;
            case "carCount" -> carCount;
            default -> null;
        };
    }

    public Property<?> getProperty(String propertyName, int index) {
        return switch (propertyName) {
            case "blockID" -> stationBlockID.get(index);
            case "arrivalTime" -> arrivalTime.get(index);
            case "departureTime" -> departureTime.get(index);
            default -> null;
        };
    }

    public void updateProperties(Schedule currentSchedule) {
        trainID.set(currentSchedule.getTrainID());
        dispatchTime.set(currentSchedule.getDispatchTime());
        carCount.set(currentSchedule.getCarCount());
        for (int i = 0; i < currentSchedule.getStopCount(); i++) {
            stationBlockID.get(i).set(currentSchedule.getStop(i).getStationBlockID());
            arrivalTime.get(i).set(currentSchedule.getStop(i).getArrivalTime());
            departureTime.get(i).set(currentSchedule.getStop(i).getDepartureTime());
        }

    }
}
