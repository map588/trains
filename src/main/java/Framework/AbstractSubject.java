package Framework;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;

public interface AbstractSubject {
//     void updateProperty(BooleanProperty property, boolean newValue);
//     void updateProperty(DoubleProperty property, double newValue);
//     void updateProperty(IntegerProperty property, int newValue);

     BooleanProperty getBooleanProperty (String propertyName);
     DoubleProperty getDoubleProperty (String propertyName);
     IntegerProperty getIntegerProperty (String propertyName);

     <T> void updateProperty(Property<T> property, Object newValue);
}
