package trainController;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.AbstractSubject;
import Framework.Support.Notifier;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;
import trainModel.TrainModelSubjectMap;

import static trainController.Controller_Property.*;

public class TrainControllerSubject implements AbstractSubject, Notifier {
    private final ObservableHashMap<Controller_Property, Property<?>> properties = new ObservableHashMap<>();
    private TrainController controller;

    public  volatile boolean  isGUIUpdateInProgress     = false;
    private volatile boolean  isLogicUpdateInProgress   = false;

    private TrainModelSubjectMap trainModelSubjectMap = TrainModelSubjectMap.getInstance();

    public TrainControllerSubject(TrainController controller) {
        this.controller = controller;
        initializeProperties();
        if(TrainControllerSubjectMap.getInstance().getSubjects().containsKey(controller.getID())){
            TrainControllerSubjectMap.getInstance().removeSubject(controller.getID());
        }
        TrainControllerSubjectMap.getInstance().registerSubject(controller.getID(), this);
    }

    public TrainControllerSubject(){
        if(trainModelSubjectMap.getSubjects().isEmpty()) {
            this.controller = new TrainControllerImpl();
        }else{
            TrainModel model = trainModelSubjectMap.getSubject(trainModelSubjectMap.getSubjects().keySet().iterator().next()).getModel();
            this.controller = new TrainControllerImpl(model, model.getTrainNumber());
        }

        properties.put(AUTHORITY, new SimpleIntegerProperty(0));
        properties.put(SAMPLING_PERIOD, new SimpleIntegerProperty(0));
        properties.put(COMMAND_SPEED, new SimpleDoubleProperty(0.0));
        properties.put(CURRENT_SPEED, new SimpleDoubleProperty(0.0));
        properties.put(OVERRIDE_SPEED, new SimpleDoubleProperty(0.0));
        properties.put(SPEED_LIMIT, new SimpleDoubleProperty(0.0));
        properties.put(KI, new SimpleDoubleProperty(0.0));
        properties.put(KP, new SimpleDoubleProperty(0.0));
        properties.put(POWER, new SimpleDoubleProperty(0));
        properties.put(SERVICE_BRAKE, new SimpleBooleanProperty(false));
        properties.put(EMERGENCY_BRAKE, new SimpleBooleanProperty(false));
        properties.put(AUTOMATIC_MODE, new SimpleBooleanProperty(false));
        properties.put(EXT_LIGHTS, new SimpleBooleanProperty(false));
        properties.put(INT_LIGHTS, new SimpleBooleanProperty(false));
        properties.put(ANNOUNCEMENTS, new SimpleBooleanProperty(false));
        properties.put(SIGNAL_FAILURE, new SimpleBooleanProperty(false));
        properties.put(BRAKE_FAILURE, new SimpleBooleanProperty(false));
        properties.put(POWER_FAILURE, new SimpleBooleanProperty(false));
        properties.put(SET_TEMPERATURE, new SimpleDoubleProperty(0));
        properties.put(CURRENT_TEMPERATURE, new SimpleDoubleProperty(0));
        properties.put(LEFT_DOORS, new SimpleBooleanProperty(false));
        properties.put(RIGHT_DOORS, new SimpleBooleanProperty(false));
        properties.put(IN_TUNNEL, new SimpleBooleanProperty(false));
        properties.put(LEFT_PLATFORM, new SimpleBooleanProperty(false));
        properties.put(RIGHT_PLATFORM, new SimpleBooleanProperty(false));
        properties.put(NEXT_STATION, new SimpleStringProperty("N/A"));
        properties.put(TRAIN_ID, new SimpleIntegerProperty(0));
        properties.put(GRADE, new SimpleDoubleProperty(0));
        properties.put(ERROR, new SimpleStringProperty(""));
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
        properties.put(NEXT_STATION, new SimpleStringProperty("N/A"));
        properties.put(TRAIN_ID, new SimpleIntegerProperty(controller.getID()));
        properties.put(GRADE, new SimpleDoubleProperty(controller.getGrade()));
        properties.put(ERROR, new SimpleStringProperty(""));
    }

    //Change coming from the logic side
    @Override
    public void notifyChange(String propertyName, Object newValue) {
        Controller_Property property = Controller_Property.valueOf(propertyName.toUpperCase());
        if (newValue != null) {
            executeUpdate(() -> notifyChange(property, newValue), false);
        }
    }

    public void notifyChange(Controller_Property propertyName, Object newValue) {
        Property<?> property = properties.get(propertyName);
        if (property != null && newValue != null) {
            executeUpdate(() -> updateProperty(property, newValue), true);
        }
    }

    //Change coming from the GUI side
    @Override
    public void setProperty(String propertyName, Object newValue) {
        Controller_Property property_e = Controller_Property.valueOf(propertyName.toUpperCase());
        Property<?> property = properties.get(property_e);

        if (property != null && !isLogicUpdateInProgress) {
            isGUIUpdateInProgress = true;
            try {
                executeUpdate(() -> {
                    System.out.println("Setting property " + propertyName + " to " + newValue);
                    updateProperty(property, newValue);
                    controller.setValue(propertyName, newValue);
                }, false);
            } finally {
                isGUIUpdateInProgress = false;
            }
        }
    }
    public void setProperty(Controller_Property propertyName, Object newValue) {

        Property<?> property = properties.get(propertyName);

        if (property != null && !isLogicUpdateInProgress) {
            isGUIUpdateInProgress = true;
            try {
                executeUpdate(() -> {
                    //System.out.println("Setting property " + propertyName + " to " + newValue);
                    updateProperty(property, newValue);
                    controller.setValue(propertyName.getPropertyName(), newValue);
                }, false);
            } finally {
                isGUIUpdateInProgress = false;
            }
        }
    }

    void delete() {
        TrainControllerSubjectMap.getInstance().removeSubject(controller.getID());
    }

    @Override
    public Property<?> getProperty(String propertyName) {
        return properties.get(Controller_Property.valueOf(propertyName.toUpperCase()));
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



    public Property<?> getProperty(Controller_Property propertyName) {
        return properties.get(propertyName);
    }

    public StringProperty getStringProperty(Controller_Property propertyName){
        Property<?> property = getProperty(propertyName);
            return (StringProperty) property;
    }
    // Directly accessing typed properties for GUI binding
    public BooleanProperty getBooleanProperty(Controller_Property propertyName) {
        Property<?> property = getProperty(propertyName);
            return (BooleanProperty) property;
    }

    public DoubleProperty getDoubleProperty(Controller_Property propertyName) {
        Property<?> property = getProperty(propertyName);
        return (DoubleProperty) property;
    }

    public IntegerProperty getIntegerProperty(Controller_Property propertyName) {
        Property<?> property = getProperty(propertyName);
        return (IntegerProperty) property;
    }


    public TrainControllerImpl getController() {
        return  (TrainControllerImpl) controller;
    }
}
