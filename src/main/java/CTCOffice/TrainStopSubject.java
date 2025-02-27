package CTCOffice;

import static CTCOffice.Properties.ScheduleProperties.*;
import static CTCOffice.TimeConvert.*;

import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.*;

public class TrainStopSubject implements AbstractSubject {
  public final TrainStop stop;
  private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();

  TrainStopSubject(TrainStop stop) {
    this.stop = stop;
    properties.put(
        DESTINATION_PROPERTY,
        new SimpleIntegerProperty(this, DESTINATION_PROPERTY, stop.getStationBlockID()));
    properties.put(
        ARRIVAL_TIME_PROPERTY,
        new SimpleStringProperty(
            this, ARRIVAL_TIME_PROPERTY, convertDoubleToClockTime((double) stop.getArrivalTime())));
    properties.put(
        DEPARTURE_TIME_PROPERTY,
        new SimpleStringProperty(
            this,
            DEPARTURE_TIME_PROPERTY,
            convertDoubleToClockTime((double) stop.getDepartureTime())));
    properties.put(
        STOP_INDEX_PROPERTY,
        new SimpleIntegerProperty(this, STOP_INDEX_PROPERTY, stop.getStopIndex()));
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

  public IntegerProperty getIntegerProperty(String propertyName) {
    return (IntegerProperty) getProperty(propertyName);
  }

  public TrainStop getStop() {
    return stop;
  }
}
