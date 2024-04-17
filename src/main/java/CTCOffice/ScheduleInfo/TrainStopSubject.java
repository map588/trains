package CTCOffice.ScheduleInfo;

import CTCOffice.ScheduleInfo.TrainStop;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;

import static CTCOffice.Properties.ScheduleProperties.*;

public class TrainStopSubject implements AbstractSubject {
    public final TrainStop stop;
    private final ObservableHashMap<String, Object> properties = new ObservableHashMap<>();

    TrainStopSubject(TrainStop stop) {
        this.stop = stop;
        properties.put(DESTINATION_PROPERTY, new SimpleIntegerProperty(this, DESTINATION_PROPERTY));
        properties.put(ARRIVAL_TIME_PROPERTY, new SimpleIntegerProperty(this, ARRIVAL_TIME_PROPERTY));
        properties.put(DEPARTURE_TIME_PROPERTY, new SimpleIntegerProperty(this, DEPARTURE_TIME_PROPERTY));
    }


    public void setProperty(String propertyName, Object newValue) {
        if(newValue == null){
            System.err.println("Null value for property " + propertyName);
            return;
        }
        switch (propertyName) {
            case DESTINATION_PROPERTY -> updateProperty(getProperty(DESTINATION_PROPERTY), newValue);
            case ARRIVAL_TIME_PROPERTY -> updateProperty(getProperty(ARRIVAL_TIME_PROPERTY), newValue);
            case DEPARTURE_TIME_PROPERTY -> updateProperty(getProperty(DEPARTURE_TIME_PROPERTY), newValue);
            default -> System.err.println("Unknown property " + propertyName);
        }
    }

    public Property<?> getProperty(String propertyName) {
        return (Property<?>) properties.get(propertyName);
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return switch (propertyName) {
            case DESTINATION_PROPERTY -> (IntegerProperty) getProperty(DESTINATION_PROPERTY);
            case ARRIVAL_TIME_PROPERTY -> (IntegerProperty) getProperty(ARRIVAL_TIME_PROPERTY);
            case DEPARTURE_TIME_PROPERTY -> (IntegerProperty) getProperty(DEPARTURE_TIME_PROPERTY);
            default -> null;
        };
    }
}
