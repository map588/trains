package Framework.Support;

import javafx.beans.property.*;

//public interface AbstractSubject extends Notifications {
public interface AbstractSubject {
//     void updateProperty(BooleanProperty property, boolean newValue);
//     void updateProperty(DoubleProperty property, double newValue);
//     void updateProperty(IntegerProperty property, int newValue);

//     BooleanProperty getBooleanProperty (String propertyName);
//     DoubleProperty getDoubleProperty (String propertyName);
//     IntegerProperty getIntegerProperty (String propertyName);

    void setProperty(String propertyName, Object newValue);
    Property<?> getProperty(String propertyName);

  default <T> void updateProperty(Property<T> property, Object newValue){
        if (property instanceof DoubleProperty && newValue instanceof Number) {
            ((DoubleProperty) property).set(((Number) newValue).doubleValue());
        } else if (property instanceof IntegerProperty && newValue instanceof Number) {
            ((IntegerProperty) property).set(((Number) newValue).intValue());
        } else if (property instanceof BooleanProperty && newValue instanceof Boolean) {
            ((BooleanProperty) property).set((Boolean) newValue);
        } else if(property instanceof StringProperty && newValue instanceof String) {
            ((StringProperty) property).set((String) newValue);

        } else if(property == null){
            System.err.println("Attempted null property update with value " + newValue);
            return;
        } else if(newValue == null){
            System.err.println("Null value for property" + property.getName());
            return;
        } else{
            System.err.println("Mismatch in property type and value type for " + property.getName());
            return;
        }

 }
}
