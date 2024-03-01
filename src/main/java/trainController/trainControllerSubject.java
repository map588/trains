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
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private final TrainController controller;
    public boolean isGUIUpdate = false;
    public boolean isLogicUpdate = false;

    public trainControllerSubject(TrainController controller) {
        this.controller = controller;
        initializeProperties();
        trainControllerSubjectMap.getInstance().registerSubject(controller.getID(), this);
    }

    // Simplified property initialization
    private void initializeProperties() {
        // Initialize properties with correct initial values from controller if available
        properties.put(authority_p, new SimpleIntegerProperty(controller.getAuthority()));
        //properties.put(blocksToNextStation, new SimpleIntegerProperty(controller.getBlocksToNextStation()));
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

    public void notifyChange(String propertyName, Object newValue) {
        // Update property from controller, Internal Logic takes precedence over GUI updates
        if (!isGUIUpdate) {
            updateFromLogic(() -> {
                    Property<?> property = properties.get(propertyName);
                    updateProperty(property, newValue);
            });
            }
    }


    public void setProperty(String propertyName, Object newValue) {
        Runnable updateTask = () -> {
            Property<?> property = properties.get(propertyName);
                updateProperty(property, newValue);
                controller.setValue(propertyName, newValue);
        };

        if (isLogicUpdate) {
            executorService.scheduleWithFixedDelay(() -> {
                if (!isLogicUpdate) {
                    System.out.println("Delayed setProperty from GUI");
                    Platform.runLater(() -> updateFromGUI(updateTask));
                }
            }, 0, 10, TimeUnit.MILLISECONDS);
        } else {
            Platform.runLater(() -> updateFromGUI(updateTask));
        }
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    // Update property safely with the correct type
    public <T> void updateProperty(Property<T> property, Object newValue) {
        if(newValue == null) {
            System.err.println("Null value for property " + property.getName());
            return;
        }
        if (property instanceof IntegerProperty && newValue instanceof Number) {
            ((IntegerProperty) property).set(((Number) newValue).intValue());
        } else if (property instanceof DoubleProperty && newValue instanceof Number) {
            ((DoubleProperty) property).set(((Number) newValue).doubleValue());
        } else if (property instanceof BooleanProperty && newValue instanceof Boolean) {
            ((BooleanProperty) property).set((Boolean) newValue);
        } else if (property instanceof StringProperty && newValue instanceof String){
            ((StringProperty) property).set((String) newValue);
        }
        else{
            throw new IllegalArgumentException("Mismatch in property type and value type for " + property);
        }
    }


    public StringProperty getStringProperty(String propertyName){
        Property<?> property = getProperty(propertyName);
        try{
            return (StringProperty) property;
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Property " + propertyName + " is not a StringProperty");
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
    public void updateFromGUI(Runnable updateLogic) {
        System.out.println("Called from updateFromGUI.");
        isGUIUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isGUIUpdate = false;
        }
    }

    public void updateFromLogic(Runnable updateLogic) {
        isLogicUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isLogicUpdate = false;
        }
    }
    public void calculatePower(){
        controller.calculatePower();
    }
}
