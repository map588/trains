package CTCOffice;

import static CTCOffice.Properties.ScheduleProperties.*;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.*;


import java.util.ArrayList;

public class SingleTrainScheduleSubject implements AbstractSubject {
    public final SingleTrainSchedule schedule;
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();

    SingleTrainScheduleSubject(SingleTrainSchedule schedule) {
       properties.put(TRAIN_ID_PROPERTY, new SimpleIntegerProperty(this, TRAIN_ID_PROPERTY, schedule.getTrainID()));
        properties.put(TRAIN_LINE_PROPERTY, new SimpleStringProperty(this, TRAIN_LINE_PROPERTY, schedule.getLine()));
        properties.put(DISPATCH_TIME_PROPERTY, new SimpleIntegerProperty(this, DISPATCH_TIME_PROPERTY, schedule.getDispatchTime()));
        properties.put(CAR_COUNT_PROPERTY, new SimpleIntegerProperty(this, CAR_COUNT_PROPERTY, schedule.getCarCount()));
        for (int i = 0; i < schedule.getStops().size(); i++) {
            properties.put(DESTINATION_PROPERTY + i, new SimpleIntegerProperty(this, DESTINATION_PROPERTY + i, schedule.getStops().get(i).getStationBlockID()));
            properties.put(ARRIVAL_TIME_PROPERTY + i, new SimpleIntegerProperty(this, ARRIVAL_TIME_PROPERTY + i, schedule.getStops().get(i).getArrivalTime()));
            properties.put(DEPARTURE_TIME_PROPERTY + i, new SimpleIntegerProperty(this, DEPARTURE_TIME_PROPERTY + i, schedule.getStops().get(i).getDepartureTime()));
        }
        this.schedule = schedule;
       // ScheduleLibrary.getInstance().registerSubject(schedule.

    }

    public void setProperty(String propertyName, Object newValue) {
        if(newValue == null){
            System.err.println("Null value for property " + propertyName);
            return;
        }
        switch (propertyName) {
            case TRAIN_ID_PROPERTY -> updateProperty(getProperty(TRAIN_ID_PROPERTY), newValue);
            case TRAIN_LINE_PROPERTY -> updateProperty(getProperty(TRAIN_LINE_PROPERTY), newValue);
            case DISPATCH_TIME_PROPERTY -> updateProperty(getProperty(DISPATCH_TIME_PROPERTY), newValue);
            case CAR_COUNT_PROPERTY -> updateProperty(getProperty(CAR_COUNT_PROPERTY), newValue);
            default -> System.err.println("Unknown property " + propertyName);
            }
        }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public StringProperty getStringProperty(String propertyName) {
        return switch (propertyName) {
            case TRAIN_LINE_PROPERTY -> (StringProperty) getProperty(TRAIN_LINE_PROPERTY);
            default -> null;
        };
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return switch (propertyName) {
            case TRAIN_ID_PROPERTY -> (IntegerProperty) getProperty(TRAIN_ID_PROPERTY);
            case DISPATCH_TIME_PROPERTY -> (IntegerProperty) getProperty(DISPATCH_TIME_PROPERTY);
            case CAR_COUNT_PROPERTY -> (IntegerProperty) getProperty(CAR_COUNT_PROPERTY);

            default -> null;
        };
    }

    public IntegerProperty getIntegerProperty(String propertyName, int index) {
        if (index < 0 || index >= schedule.getStops().size()) {
            System.err.println("Index out of bounds for property " + propertyName);
            return new SimpleIntegerProperty();
        }
        return switch (propertyName) {
            case DESTINATION_PROPERTY -> (IntegerProperty) getProperty(DESTINATION_PROPERTY + index);
            case ARRIVAL_TIME_PROPERTY -> (IntegerProperty) getProperty(ARRIVAL_TIME_PROPERTY + index);
            case DEPARTURE_TIME_PROPERTY -> (IntegerProperty) getProperty(DEPARTURE_TIME_PROPERTY + index);
            default -> new SimpleIntegerProperty();//may get ridof
        };
    }

    public void setIntegerProperty(String propertyName, int index, Object newValue) {
        if (index < 0 || index >= schedule.getStops().size()) {
            System.err.println("Index out of bounds for property " + propertyName);
            return;
        }
        if (newValue == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        switch (propertyName) {
            case DESTINATION_PROPERTY -> updateProperty(getProperty(DESTINATION_PROPERTY + index), newValue);
            case ARRIVAL_TIME_PROPERTY -> updateProperty(getProperty(ARRIVAL_TIME_PROPERTY + index), newValue);
            case DEPARTURE_TIME_PROPERTY -> updateProperty(getProperty(DEPARTURE_TIME_PROPERTY + index), newValue);
            default -> System.err.println("Unknown property " + propertyName);
        }
    }

}