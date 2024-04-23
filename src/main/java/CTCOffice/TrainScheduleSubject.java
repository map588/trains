package CTCOffice;

import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.*;

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
        if (newValue == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        updateProperty(getProperty(propertyName), newValue);
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public StringProperty getStringProperty(String propertyName) {
        return (StringProperty) getProperty(propertyName);
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return (IntegerProperty) getProperty(propertyName);
    }

    public TrainSchedule getSchedule(){
        return schedule;
    }

}