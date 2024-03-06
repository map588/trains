package trainController;

import Common.TrainController;
import Framework.Support.AbstractSubject;
import Framework.Support.Notifier;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;

import static trainController.Properties.*;

public class trainControllerSubject implements AbstractSubject, Notifier {
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private final TrainController controller;

    public  volatile boolean isGUIUpdateInProgress   = false;
    private volatile boolean isLogicUpdateInProgress = false;


    public trainControllerSubject(TrainController controller) {
        this.controller = controller;
        initializeProperties();
        trainControllerSubjectMap.getInstance().registerSubject(controller.getID(), this);
    }

    // Simplified property initialization
    private void initializeProperties() {
        // Initialize properties with correct initial values from controller if available
        properties.put(AUTHORITY_PROPERTY, new SimpleIntegerProperty(controller.getAuthority()));
        properties.put(SAMPLING_PERIOD_PROPERTY, new SimpleIntegerProperty(controller.getSamplingPeriod()));
        properties.put(COMMAND_SPEED_PROPERTY, new SimpleDoubleProperty(controller.getCommandSpeed()));
        properties.put(CURRENT_SPEED_PROPERTY, new SimpleDoubleProperty(controller.getSpeed()));
        properties.put(OVERRIDE_SPEED_PROPERTY, new SimpleDoubleProperty(controller.getOverrideSpeed()));
        properties.put(SPEED_LIMIT_PROPERTY, new SimpleDoubleProperty(controller.getSpeedLimit()));
        properties.put(KI_PROPERTY, new SimpleDoubleProperty(controller.getKi()));
        properties.put(KP_PROPERTY, new SimpleDoubleProperty(controller.getKp()));
        properties.put(POWER_PROPERTY, new SimpleDoubleProperty(controller.getPower()));
        properties.put(SERVICE_BRAKE_PROPERTY, new SimpleBooleanProperty(controller.getServiceBrake()));
        properties.put(EMERGENCY_BRAKE_PROPERTY, new SimpleBooleanProperty(controller.getEmergencyBrake()));
        properties.put(AUTOMATIC_MODE_PROPERTY, new SimpleBooleanProperty(controller.getAutomaticMode()));
        properties.put(EXT_LIGHTS_PROPERTY, new SimpleBooleanProperty(controller.getExtLights()));
        properties.put(INT_LIGHTS_PROPERTY, new SimpleBooleanProperty(controller.getIntLights()));
        properties.put(ANNOUNCEMENTS_PROPERTY, new SimpleBooleanProperty(controller.getAnnouncements()));
        properties.put(SIGNAL_FAILURE_PROPERTY, new SimpleBooleanProperty(controller.getSignalFailure()));
        properties.put(BRAKE_FAILURE_PROPERTY, new SimpleBooleanProperty(controller.getBrakeFailure()));
        properties.put(POWER_FAILURE_PROPERTY, new SimpleBooleanProperty(controller.getPowerFailure()));
        properties.put(TEMPERATURE_PROPERTY, new SimpleDoubleProperty(controller.getTemperature()));
        properties.put(LEFT_DOORS_PROPERTY, new SimpleBooleanProperty(controller.getLeftDoors()));
        properties.put(RIGHT_DOORS_PROPERTY, new SimpleBooleanProperty(controller.getRightDoors()));
        properties.put(IN_TUNNEL_PROPERTY, new SimpleBooleanProperty(controller.getInTunnel()));
        properties.put(LEFT_PLATFORM_PROPERTY, new SimpleBooleanProperty(controller.getLeftPlatform()));
        properties.put(RIGHT_PLATFORM_PROPERTY, new SimpleBooleanProperty(controller.getRightPlatform()));
        properties.put(NEXT_STATION_PROPERTY, new SimpleStringProperty(controller.getStationName()));
        properties.put(TRAIN_ID_PROPERTY, new SimpleIntegerProperty(controller.getID()));
        properties.put(GRADE_PROPERTY, new SimpleDoubleProperty(controller.getGrade()));

    }

    //Change coming from the logic side
    public void notifyChange(String propertyName, Object newValue) {
        Property<?> property = properties.get(propertyName);
        if (property != null && newValue != null) {
            executeUpdate(() -> updateProperty(property, newValue), !isLogicUpdateInProgress);
        }
    }

    //Change coming from the GUI side
    public void setProperty(String propertyName, Object newValue) {
        Property<?> property = properties.get(propertyName);

        if (property != null && !isLogicUpdateInProgress) {
            isGUIUpdateInProgress = true;
            try {
                executeUpdate(() -> {
                    updateProperty(property, newValue);
                    controller.setValue(propertyName, newValue);
                }, false);
            } finally {
                isGUIUpdateInProgress = false;
            }
        }
    }

    private void executeUpdate(Runnable updateTask, boolean runImmediately) {
        if (runImmediately) {
            updateTask.run();
        } else {
            Platform.runLater(updateTask);
        }
    }

    // Update property safely with the correct type
    public <T> void updateProperty(Property<T> property, Object newValue) {
        Runnable updateAction = () -> {
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
                System.err.println("Type mismatch for property " + property.getName() + ": " + e.getMessage());
            }
        };

        if (Platform.isFxApplicationThread()) {
            updateAction.run();
        } else {
            Platform.runLater(updateAction);
        }
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public StringProperty getStringProperty(String propertyName){
        Property<?> property = getProperty(propertyName);
            return (StringProperty) property;
    }
    // Directly accessing typed properties for GUI binding
    public BooleanProperty getBooleanProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
            return (BooleanProperty) property;
    }

    public DoubleProperty getDoubleProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        return (DoubleProperty) property;
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        Property<?> property = getProperty(propertyName);
        return (IntegerProperty) property;
    }

    public void calculatePower(){
        controller.calculatePower();
    }

    public trainControllerImpl getController() {
        return (trainControllerImpl) controller;
    }
}
