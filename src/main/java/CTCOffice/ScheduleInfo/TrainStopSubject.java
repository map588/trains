package CTCOffice.ScheduleInfo;

import CTCOffice.ScheduleInfo.TrainStop;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;

import static CTCOffice.Properties.ScheduleProperties.*;

public class TrainStopSubject implements AbstractSubject {
    public final TrainStop stop;
    private final ObservableHashMap<String, Object> properties = new ObservableHashMap<>();

    TrainStopSubject(TrainStop stop) {
        this.stop = stop;
        properties.put(DESTINATION_PROPERTY, new SimpleIntegerProperty(this, DESTINATION_PROPERTY, stop.getStationBlockID()));
        properties.put(ARRIVAL_TIME_PROPERTY, new SimpleIntegerProperty(this, ARRIVAL_TIME_PROPERTY, stop.getArrivalTime()));
        properties.put(DEPARTURE_TIME_PROPERTY, new SimpleIntegerProperty(this, DEPARTURE_TIME_PROPERTY, stop.getDepartureTime()));
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

    public Integer getIntegerProperty(String propertyName) {
        return switch (propertyName) {
            case DESTINATION_PROPERTY -> (Integer) getProperty(DESTINATION_PROPERTY).getValue();
            case ARRIVAL_TIME_PROPERTY -> (Integer) getProperty(ARRIVAL_TIME_PROPERTY).getValue();
            case DEPARTURE_TIME_PROPERTY -> (Integer) getProperty(DEPARTURE_TIME_PROPERTY).getValue();
            default -> null;
        };
    }
}
