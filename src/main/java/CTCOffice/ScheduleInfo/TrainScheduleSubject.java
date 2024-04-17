package CTCOffice.ScheduleInfo;

import CTCOffice.ScheduleInfo.TrainSchedule;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.*;
import Utilities.TimeConvert;

import static CTCOffice.Properties.ScheduleProperties.*;
import static Utilities.TimeConvert.*;

public class TrainScheduleSubject implements AbstractSubject {
    public final TrainSchedule schedule;
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();

    TrainScheduleSubject(TrainSchedule schedule) {
        properties.put(TRAIN_ID_PROPERTY, new SimpleIntegerProperty(this, TRAIN_ID_PROPERTY, schedule.getTrainID()));
        properties.put(TRAIN_LINE_PROPERTY, new SimpleStringProperty(this, TRAIN_LINE_PROPERTY, schedule.getLine()));
        properties.put(DISPATCH_TIME_PROPERTY, new SimpleStringProperty(this, DISPATCH_TIME_PROPERTY, convertDoubleToClockTime((double)schedule.getDispatchTime())));
        properties.put(CAR_COUNT_PROPERTY, new SimpleIntegerProperty(this, CAR_COUNT_PROPERTY, schedule.getCarCount()));

        properties.get(TRAIN_LINE_PROPERTY).addListener((observable, oldValue, newValue) -> schedule.setLine((String) newValue));
        properties.get(DISPATCH_TIME_PROPERTY).addListener((observable, oldValue, newValue) -> schedule.setDispatchTime((int)convertClockTimeToDouble((String) newValue)));
        properties.get(CAR_COUNT_PROPERTY).addListener((observable, oldValue, newValue) -> schedule.setCarCount((int) newValue));
        this.schedule = schedule;
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
            case DISPATCH_TIME_PROPERTY -> (StringProperty) getProperty(DISPATCH_TIME_PROPERTY);
            default -> null;
        };
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        System.out.println("Getting integer property " + propertyName);
        return switch (propertyName) {
            case TRAIN_ID_PROPERTY -> (IntegerProperty) getProperty(TRAIN_ID_PROPERTY);
            case CAR_COUNT_PROPERTY -> (IntegerProperty) getProperty(CAR_COUNT_PROPERTY);
            default -> null;
        };
    }


    public TrainSchedule getSchedule(){
        return schedule;
    }

}