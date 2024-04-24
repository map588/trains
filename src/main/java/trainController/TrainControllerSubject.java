package trainController;

import Common.TrainController;
import Framework.Support.NotifierEnum;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static trainController.ControllerProperty.*;

public class TrainControllerSubject implements NotifierEnum {
    private final ObservableHashMap<ControllerProperty, Property<?>> properties = new ObservableHashMap<>();

    private final TrainController controller;

    private final Logger logger = LoggerFactory.getLogger(TrainControllerSubject.class);

    private final TrainControllerSubjectMap controllerSubjectMap = TrainControllerSubjectMap.getInstance();

    private final boolean isHWController;

    public TrainControllerSubject(TrainController controller) {
        this.controller = controller;
        int controllerID = controller.getID();

        isHWController = controller.getClass().getSimpleName().equals("TrainControllerHW");

        initializeProperties();
        if(controller.getID() == -1){
            return;
        }
        logger.info("Creating Train Controller Subject with ID: {}", controllerID);
        if(controllerSubjectMap.getSubjects().containsKey(controllerID)){
            controllerSubjectMap.removeSubject(controllerID);
        }
        controllerSubjectMap.registerSubject(controllerID, this);
        logger.info("Train Controller Subject created with ID: {} ", controllerID);
    }


    @Override
    public void notifyChange(Enum<?> propertyName, Object newValue) {
        Platform.runLater(() -> {
                Property<?> property = properties.get((ControllerProperty) propertyName);
                updateProperty(property, newValue);
        });
    }


    public void setProperty(ControllerProperty propertyName, Object newValue) {
        Platform.runLater(() -> {
            Property<?> property = properties.get(propertyName);
            updateProperty(property, newValue);
            controller.setValue(propertyName, newValue);
        });
    }

    void delete() {
        controllerSubjectMap.removeSubject(controller.getID());
        logger.info("Train Controller Subject deleted with ID: {}", controller.getID());
    }


    public Property<?> getProperty(String propertyName) {
        return properties.get(ControllerProperty.valueOf(propertyName.toUpperCase()));
    }


    // Update property safely with the correct type
    public <T> void updateProperty(Property<T> property, Object newValue) {
            if (newValue == null) {
                System.err.println("Null value for property " + property.getName());
                return;
            }
            try {
                if (property instanceof IntegerProperty) {
                    ((IntegerProperty) property).set(((Number) newValue).intValue());
                } else if (property instanceof DoubleProperty) {
                    ((DoubleProperty) property).set(((Number) newValue).doubleValue());
                } else if (property instanceof BooleanProperty) {
                    ((BooleanProperty) property).set((Boolean) newValue);
                } else if (property instanceof StringProperty) {
                    ((StringProperty) property).set((String) newValue);
                }
            } catch (ClassCastException e) {
                String error = ("Type mismatch for property " + property.getName() + ": " + e.getMessage());
                updateProperty(properties.get(ERROR), error);
            }
    }

    // Simplified property initialization
    private void initializeProperties() {
        // Initialize properties with correct initial values from controller if available
        properties.put(AUTHORITY, new SimpleIntegerProperty(controller.getAuthority()));
        properties.put(SAMPLING_PERIOD, new SimpleIntegerProperty());
        properties.put(COMMAND_SPEED, new SimpleDoubleProperty(controller.getCommandSpeed()));
        properties.put(CURRENT_SPEED, new SimpleDoubleProperty(0.0));
        properties.put(OVERRIDE_SPEED, new SimpleDoubleProperty(controller.getOverrideSpeed()));
        properties.put(SPEED_LIMIT, new SimpleDoubleProperty(controller.getSpeedLimit()));
        properties.put(KI, new SimpleDoubleProperty(controller.getKi()));
        properties.put(KP, new SimpleDoubleProperty(controller.getKp()));
        properties.put(POWER, new SimpleDoubleProperty(controller.getPower()));
        properties.put(SERVICE_BRAKE, new SimpleBooleanProperty(controller.getServiceBrake()));
        properties.put(EMERGENCY_BRAKE, new SimpleBooleanProperty(controller.getEmergencyBrake()));
        properties.put(AUTOMATIC_MODE, new SimpleBooleanProperty(controller.getAutomaticMode()));
        properties.put(EXT_LIGHTS, new SimpleBooleanProperty(controller.getExtLights()));
        properties.put(INT_LIGHTS, new SimpleBooleanProperty(controller.getIntLights()));
        properties.put(ANNOUNCEMENTS, new SimpleBooleanProperty(controller.getAnnouncements()));
        properties.put(SIGNAL_FAILURE, new SimpleBooleanProperty(controller.getSignalFailure()));
        properties.put(BRAKE_FAILURE, new SimpleBooleanProperty(controller.getBrakeFailure()));
        properties.put(POWER_FAILURE, new SimpleBooleanProperty(controller.getPowerFailure()));
        properties.put(SET_TEMPERATURE, new SimpleDoubleProperty(controller.getSetTemperature()));
        properties.put(CURRENT_TEMPERATURE, new SimpleDoubleProperty(controller.getCurrentTemperature()));
        properties.put(LEFT_DOORS, new SimpleBooleanProperty(controller.getLeftDoors()));
        properties.put(RIGHT_DOORS, new SimpleBooleanProperty(controller.getRightDoors()));
        properties.put(IN_TUNNEL, new SimpleBooleanProperty(false));
        properties.put(LEFT_PLATFORM, new SimpleBooleanProperty(false));
        properties.put(RIGHT_PLATFORM, new SimpleBooleanProperty(false));
        properties.put(NEXT_STATION, new SimpleStringProperty(""));
        properties.put(TRAIN_ID, new SimpleIntegerProperty(controller.getID()));
        properties.put(GRADE, new SimpleDoubleProperty(controller.getGrade()));
        properties.put(ERROR, new SimpleStringProperty(""));
        properties.put(ARRIVAL_STATION, new SimpleStringProperty(""));
    }


    public Property<?> getProperty(ControllerProperty propertyName) {
        return properties.get(propertyName);
    }

    public StringProperty getStringProperty(ControllerProperty propertyName){
        Property<?> property = getProperty(propertyName);
            return (StringProperty) property;
    }
    // Directly accessing typed properties for GUI binding
    public BooleanProperty getBooleanProperty(ControllerProperty propertyName) {
        Property<?> property = getProperty(propertyName);
            return (BooleanProperty) property;
    }

    public DoubleProperty getDoubleProperty(ControllerProperty propertyName) {
        Property<?> property = getProperty(propertyName);
        return (DoubleProperty) property;
    }

    public IntegerProperty getIntegerProperty(ControllerProperty propertyName) {
        Property<?> property = getProperty(propertyName);
        return (IntegerProperty) property;
    }


    public TrainController getController() {
        return controller;
    }

}
