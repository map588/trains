package trainController;

import Common.TrainController;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class trainControllerSubject implements AbstractSubject {
    private ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private TrainController controller;
    private boolean isGuiUpdate = false;

    public trainControllerSubject(TrainController controller) {
        this.controller = controller;
        initializeProperties();
        controller.addChangeListener(this::handleControllerChange);
        trainControllerSubjectFactory.getInstance().registerSubject(controller.getID(), this);
    }

    // Modified to correctly interpret property changes
    private void handleControllerChange(ObservableValue<?> observableValue, Object oldValue, Object newValue) {
        // Extract property name from the observable's user data or another reliable source
        String propertyName = observableValue.getUserData().toString();
        // Ensure updates are run on the JavaFX thread and check against feedback loops
        if (!isGuiUpdate) {
            Platform.runLater(() -> setProperty(propertyName, newValue));
        }
    }

    // Simplified property initialization
    private void initializeProperties() {
        // Initialize properties with correct initial values from controller if available
        properties.put("authority", new SimpleIntegerProperty(controller.getAuthority()));
        // Continue for other properties...
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        if (!isGuiUpdate) {
            Property<?> property = properties.get(propertyName);
            if (property != null) {
                Platform.runLater(() -> updateProperty(property, newValue));
            } else {
                System.err.println("No property found for string: " + propertyName);
            }
        }
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    // Update property safely with the correct type
    @Override
    public <T> void updateProperty(Property<T> property, Object newValue) {
        if (property instanceof IntegerProperty && newValue instanceof Number) {
            ((IntegerProperty) property).set(((Number) newValue).intValue());
        } else if (property instanceof DoubleProperty && newValue instanceof Number) {
            ((DoubleProperty) property).set(((Number) newValue).doubleValue());
        } else if (property instanceof BooleanProperty && newValue instanceof Boolean) {
            ((BooleanProperty) property).set((Boolean) newValue);
        } else {
            throw new IllegalArgumentException("Mismatch in property type and value type for " + property.getName());
        }
    }

    // Directly accessing typed properties for GUI binding
    public BooleanProperty getBooleanProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        try {
            return (BooleanProperty) property;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property " + propertyName + " is not a BooleanProperty");
        }
    }

    public DoubleProperty getDoubleProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        try {
            return (DoubleProperty) property;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property " + propertyName + " is not a DoubleProperty");
        }
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        try {
            return (IntegerProperty) property;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Property " + propertyName + " is not an IntegerProperty");
        }
    }

    // Handling updates from the GUI
    public void updateFromGui(Runnable updateLogic) {
        isGuiUpdate = true;
        try {
            Platform.runLater(updateLogic);
        } finally {
            isGuiUpdate = false;
        }
    }
}
