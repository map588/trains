package CTCOffice;

import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.*;
import static CTCOffice.Properties.OfficeProperties.*;
import static Utilities.TimeConvert.*;



public class CTCOfficeSubject implements AbstractSubject{
    public final CTCOfficeImpl office;
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();

    CTCOfficeSubject(CTCOfficeImpl office){
        properties.put(TIME_PROPERTY, new SimpleStringProperty(this, TIME_PROPERTY, convertDoubleToClockTime(office.getTime())));
        properties.put(TICKET_SALES_PROPERTY, new SimpleIntegerProperty(this, TICKET_SALES_PROPERTY, office.getTicketSales()));
        properties.put(MODE_PROPERTY, new SimpleIntegerProperty(this, MODE_PROPERTY, office.getMode()));
        properties.put(MANUAL_MODE_PROPERTY, new SimpleBooleanProperty(this, MANUAL_MODE_PROPERTY, office.getManualMode()));
        properties.put(MAINTENANCE_MODE_PROPERTY, new SimpleBooleanProperty(this, MAINTENANCE_MODE_PROPERTY, office.getMaintenanceMode()));
        properties.put(AUTO_MODE_PROPERTY, new SimpleBooleanProperty(this, AUTO_MODE_PROPERTY, office.getAutoMode()));
        this.office = office;
        properties.get(TIME_PROPERTY).addListener((observable, oldValue, newValue) -> {
            if(!office.dispatchTimes.isEmpty() && office.dispatchTimes.get(0) <= convertClockTimeToDouble((String) newValue)){
                TrainIdentity train = office.trainDispatches.get(office.dispatchTimes.get(0));
                office.DispatchTrain(train.line(), train.trainID(), train.carCount(), train.dispatchTime());
            }

        });
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

}


