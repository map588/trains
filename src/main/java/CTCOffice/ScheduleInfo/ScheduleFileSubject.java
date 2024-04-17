package CTCOffice.ScheduleInfo;


import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.*;

import static CTCOffice.Properties.ScheduleProperties.*;

public class ScheduleFileSubject implements AbstractSubject {

    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private final ScheduleFile schedule;

    public ScheduleFileSubject(ScheduleFile schedule) {
        properties.put(SCHEDULE_FILE_NAME_PROPERTY, new SimpleStringProperty(this, SCHEDULE_FILE_NAME_PROPERTY, schedule.getScheduleFileName()));
        properties.put(LAST_MODIFIED_PROPERTY, new SimpleStringProperty(this, LAST_MODIFIED_PROPERTY, schedule.getLastModified()));
        properties.put(NUM_TRAINS_PROPERTY, new SimpleIntegerProperty(this, NUM_TRAINS_PROPERTY, schedule.getNumTrains()));
        this.schedule = schedule;
        ScheduleLibrary.getInstance().registerSubject(schedule.getScheduleFileName(), this);
    }

    public void setProperty(String propertyName, Object newValue) {
        if (newValue == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        updateProperty(getProperty(propertyName), newValue);
    }

    public StringProperty getStringProperty(String propertyName) {
        return (StringProperty) getProperty(propertyName);
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public ScheduleFile getSchedule() {
        return schedule;
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return (IntegerProperty) getProperty(propertyName);
    }

}
