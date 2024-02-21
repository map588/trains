package trainController;

import Common.TrainController;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;

public class trainControllerSubject implements AbstractSubject {
    private ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private TrainController controller;
    private boolean isLogicUpdate = false;

    public trainControllerSubject(TrainController controller) {
        this.controller = controller;
        initializeProperties();
        trainControllerSubjectFactory.getInstance().registerSubject(controller.getID(), this);
    }

    // Simplified property initialization
    private void initializeProperties() {
        // Initialize properties with correct initial values from controller if available
        properties.put("authority", new SimpleIntegerProperty(controller.getAuthority()));
        // Continue for other properties...
    }

    public void notifyChange(String propertyName, Object newValue) {
        // Update property from controller, Internal Logic takes precedence over GUI updates
        updateFromLogic(() -> {
        Platform.runLater(() -> {
            Property<?> property = properties.get(propertyName);
            if (property != null) {
                updateProperty(property, newValue);
            } else {
                System.err.println("No property found for string: " + propertyName);
            }
        });
        });
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        // Update property from GUI, GUI updates are delayed if internal logic is updating
        while(isLogicUpdate) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Platform.runLater(() -> {
            Property<?> property = properties.get(propertyName);
            if (property != null) {
                    updateProperty(property, newValue);
            } else {
                System.err.println("No property found for string: " + propertyName);
            }
        });
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
    public void updateFromLogic(Runnable updateLogic) {
        isLogicUpdate = true;
        try {
            Platform.runLater(updateLogic);
        } finally {
            isLogicUpdate = false;
        }
    }
}
