package CTCOffice;


import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.*;
import static CTCOffice.Properties.ScheduleProperties.*;

public class ScheduleFileSubject implements AbstractSubject {

    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private ScheduleFile schedule;

    public ScheduleFileSubject(ScheduleFile schedule) {
        properties.put(SCHEDULE_FILE_NAME_PROPERTY, new SimpleStringProperty(this, SCHEDULE_FILE_NAME_PROPERTY, schedule.getScheduleFileName()));
        properties.put(LAST_MODIFIED_PROPERTY, new SimpleStringProperty(this, LAST_MODIFIED_PROPERTY, schedule.getLastModified()));
        this.schedule = schedule;
        ScheduleLibrary.getInstance().registerSubject(schedule.getScheduleFileName(), this);
    }

    public void setProperty(String propertyName, Object newValue) {

    }

    public StringProperty getStringProperty(String propertyName) {
        return (StringProperty) properties.get(propertyName);
    }

    public Property<?> getProperty(String propertyName) {
        return null;
    }

    public ScheduleFile getSchedule() {
        return schedule;
    }

}