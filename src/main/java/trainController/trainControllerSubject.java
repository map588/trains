package trainController;

import Common.TrainController;
import Framework.Support.AbstractSubject;
import Framework.Support.Notifications;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static trainController.Properties.*;

public class trainControllerSubject implements AbstractSubject, Notifications {
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
        properties.put(authority_p, new SimpleIntegerProperty(controller.getAuthority()));
        properties.put(samplingPeriod_p, new SimpleIntegerProperty(controller.getSamplingPeriod()));
        properties.put(commandSpeed_p, new SimpleDoubleProperty(controller.getCommandSpeed()));
        properties.put(currentSpeed_p, new SimpleDoubleProperty(controller.getSpeed()));
        properties.put(overrideSpeed_p, new SimpleDoubleProperty(controller.getOverrideSpeed()));
        properties.put(speedLimit_p, new SimpleDoubleProperty(controller.getSpeedLimit()));
        properties.put(Ki_p, new SimpleDoubleProperty(controller.getKi()));
        properties.put(Kp_p, new SimpleDoubleProperty(controller.getKp()));
        properties.put(power_p, new SimpleDoubleProperty(controller.getPower()));
        properties.put(serviceBrake_p, new SimpleBooleanProperty(controller.getServiceBrake()));
        properties.put(emergencyBrake_p, new SimpleBooleanProperty(controller.getEmergencyBrake()));
        properties.put(automaticMode_p, new SimpleBooleanProperty(controller.getAutomaticMode()));
        properties.put(extLights_p, new SimpleBooleanProperty(controller.getExtLights()));
        properties.put(intLights_p, new SimpleBooleanProperty(controller.getIntLights()));
        properties.put(announcements_p, new SimpleBooleanProperty(controller.getAnnouncements()));
        properties.put(signalFailure_p, new SimpleBooleanProperty(controller.getSignalFailure()));
        properties.put(brakeFailure_p, new SimpleBooleanProperty(controller.getBrakeFailure()));
        properties.put(powerFailure_p, new SimpleBooleanProperty(controller.getPowerFailure()));
        properties.put(temperature_p, new SimpleDoubleProperty(controller.getTemperature()));
        properties.put(leftDoors_p, new SimpleBooleanProperty(controller.getLeftDoors()));
        properties.put(rightDoors_p, new SimpleBooleanProperty(controller.getRightDoors()));
        properties.put(inTunnel_p, new SimpleBooleanProperty(controller.getInTunnel()));
        properties.put(leftPlatform_p, new SimpleBooleanProperty(controller.getLeftPlatform()));
        properties.put(rightPlatform_p, new SimpleBooleanProperty(controller.getRightPlatform()));
        properties.put(nextStationName_p, new SimpleStringProperty(controller.getStationName()));
        properties.put(trainID_p, new SimpleIntegerProperty(controller.getID()));
        properties.put(grade_p, new SimpleDoubleProperty(controller.getGrade()));

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
