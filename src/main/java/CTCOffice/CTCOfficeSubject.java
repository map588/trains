package CTCOffice;

import Framework.Support.AbstractSubject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;


public class CTCOfficeSubject implements AbstractSubject{
    private final IntegerProperty time;
    private IntegerProperty ticketSales;
    private IntegerProperty mode;
    private BooleanProperty manualMode;
    private BooleanProperty maintenanceMode;
    private BooleanProperty autoMode;

    CTCOfficeSubject(CTCOfficeImpl office){
        this.time = new SimpleIntegerProperty(this, "time", office.getTime());
    }

    public void setProperty(String propertyName, Object newValue) {
        switch (propertyName) {
            case "time": updateProperty(time, newValue);
            case "ticketSales": updateProperty(ticketSales, newValue);
            case "mode": updateProperty(mode, newValue);
            case "manualMode": updateProperty(manualMode, newValue);
            case "maintenanceMode": updateProperty(maintenanceMode, newValue);
            case "autoMode": updateProperty(autoMode, newValue);

        }
    }

    public Property<?> getProperty(String propertyName) {
        return switch (propertyName) {
            case "time" -> time;
            case "ticketSales" -> ticketSales;
            case "mode" -> mode;
            case "manualMode" -> manualMode;
            case "maintenanceMode" -> maintenanceMode;
            case "autoMode" -> autoMode;
            default -> null;
        };
    }
}


