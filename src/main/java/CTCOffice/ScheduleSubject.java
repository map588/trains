package CTCOffice;

import static CTCOffice.Properties.ScheduleProperties.*;
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
    private final List<IntegerProperty> stationBlockID = new ArrayList<>();
    private final List<IntegerProperty> arrivalTime = new ArrayList<>();
    private final List<IntegerProperty> departureTime = new ArrayList<>();

    ScheduleSubject(Schedule schedule) {
        this.trainID = new SimpleIntegerProperty(this, TRAIN_ID_PROPERTY, schedule.getTrainID());
        this.dispatchTime = new SimpleIntegerProperty(this, DISPATCH_TIME_PROPERTY, schedule.getDispatchTime());
        this.carCount = new SimpleIntegerProperty(this, CAR_COUNT_PROPERTY, schedule.getCarCount());
        for (int i = 0; i < schedule.getStopCount(); i++) {
            this.stationBlockID.add(new SimpleIntegerProperty(this, BLOCK_ID_PROPERTY, schedule.getStop(i).getStationBlockID()));
            this.arrivalTime.add(new SimpleIntegerProperty(this, ARRIVAL_TIME_PROPERTY, schedule.getStop(i).getArrivalTime()));
            this.departureTime.add(new SimpleIntegerProperty(this, DEPARTURE_TIME_PROPERTY, schedule.getStop(i).getDepartureTime()));
        }
    }

    ScheduleSubject(int trainID, int dispatchTime, int carCount, List<Integer> stationBlockID, List<Integer> arrivalTime, List<Integer> departureTime) {
        this.trainID = new SimpleIntegerProperty(this, TRAIN_ID_PROPERTY, trainID);
        this.dispatchTime = new SimpleIntegerProperty(this, DISPATCH_TIME_PROPERTY, dispatchTime);
        this.carCount = new SimpleIntegerProperty(this, CAR_COUNT_PROPERTY, carCount);
        for (int i = 0; i < stationBlockID.size(); i++) {
        System.out.println("making schedule" + stationBlockID.get(i));
            this.stationBlockID.add(new SimpleIntegerProperty(this, BLOCK_ID_PROPERTY, stationBlockID.get(i)));
            this.arrivalTime.add(new SimpleIntegerProperty(this, ARRIVAL_TIME_PROPERTY, arrivalTime.get(i)));
            this.departureTime.add(new SimpleIntegerProperty(this, DEPARTURE_TIME_PROPERTY, departureTime.get(i)));
        }
    }

    public void setProperty(String propertyName, Object newValue) {
        switch (propertyName) {
            case TRAIN_ID_PROPERTY -> trainID.set((Integer) newValue);
            case DISPATCH_TIME_PROPERTY -> dispatchTime.set((Integer) newValue);
            case CAR_COUNT_PROPERTY -> carCount.set((Integer) newValue);
            default -> System.err.println("Unknown property " + propertyName);
        };
    }

    public void setProperty(String propertyName, Object newValue, int index) {
        switch (propertyName) {
            case BLOCK_ID_PROPERTY -> stationBlockID.get(index).set((Integer) newValue);
            case ARRIVAL_TIME_PROPERTY -> arrivalTime.get(index).set((Integer) newValue);
            case DEPARTURE_TIME_PROPERTY -> departureTime.get(index).set((Integer) newValue);
            default -> System.err.println("Unknown property " + propertyName);
        };
    }

    public Property<?> getProperty(String propertyName) {
        return switch (propertyName) {
            case TRAIN_ID_PROPERTY -> trainID;
            case DISPATCH_TIME_PROPERTY -> dispatchTime;
            case CAR_COUNT_PROPERTY -> carCount;
            default -> null;
        };
    }

    public Property<?> getProperty(String propertyName, int index) {
        return switch (propertyName) {
            case BLOCK_ID_PROPERTY -> stationBlockID.get(index);
            case ARRIVAL_TIME_PROPERTY -> arrivalTime.get(index);
            case DEPARTURE_TIME_PROPERTY -> departureTime.get(index);
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
