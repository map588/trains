package CTCOffice.ScheduleInfo;

import CTCOffice.ScheduleInfo.TrainStop;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

import static CTCOffice.Properties.ScheduleProperties.*;

public class TrainStopSubject implements AbstractSubject {
    public final TrainStop stop;
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();

    TrainStopSubject(TrainStop stop) {
        this.stop = stop;
        properties.put(DESTINATION_PROPERTY, new SimpleIntegerProperty(this, DESTINATION_PROPERTY));
        properties.put(ARRIVAL_TIME_PROPERTY, new SimpleIntegerProperty(this, ARRIVAL_TIME_PROPERTY));
        properties.put(DEPARTURE_TIME_PROPERTY, new SimpleIntegerProperty(this, DEPARTURE_TIME_PROPERTY));
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

    public IntegerProperty getIntegerProperty(String propertyName) {
        return (IntegerProperty) getProperty(propertyName);
    }
}
