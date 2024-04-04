package trainModel;

import Common.TrainModel;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;

import static trainModel.Properties.*;

public class TrainModelSubject implements AbstractSubject{

    public boolean isGUIUpdate;
    public boolean isLogicUpdate;
    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private final TrainModelImpl model;
    private final TrainModelSubjectMap map = TrainModelSubjectMap.getInstance();

    public void intitializeValues() {
        properties.put(AUTHORITY_PROPERTY, new SimpleIntegerProperty(model.getAuthority()));
        properties.put(COMMANDSPEED_PROPERTY, new SimpleDoubleProperty(model.getCommandSpeed()));
        properties.put(ACTUALSPEED_PROPERTY, new SimpleDoubleProperty(model.getSpeed()));
        properties.put(ACCELERATION_PROPERTY, new SimpleDoubleProperty(model.getAcceleration()));
        properties.put(POWER_PROPERTY, new SimpleDoubleProperty(model.getPower()));
        properties.put(GRADE_PROPERTY, new SimpleDoubleProperty(model.getGrade()));
        properties.put(SERVICEBRAKE_PROPERTY, new SimpleBooleanProperty(model.getServiceBrake()));
        properties.put(EMERGENCYBRAKE_PROPERTY, new SimpleBooleanProperty(model.getEmergencyBrake()));
        properties.put(BRAKEFAILURE_PROPERTY, new SimpleBooleanProperty(model.getBrakeFailure()));
        properties.put(POWERFAILURE_PROPERTY, new SimpleBooleanProperty(model.getPowerFailure()));
        properties.put(SIGNALFAILURE_PROPERTY, new SimpleBooleanProperty(model.getSignalFailure()));
        properties.put(SETTEMPERATURE_PROPERTY, new SimpleDoubleProperty(model.getSetTemperature()));
        properties.put(REALTEMPERATURE_PROPERTY, new SimpleDoubleProperty(model.getRealTemperature()));
        properties.put(EXTLIGHTS_PROPERTY, new SimpleBooleanProperty(model.getExtLights()));
        properties.put(INTLIGHTS_PROPERTY, new SimpleBooleanProperty(model.getIntLights()));
        properties.put(LEFTDOORS_PROPERTY, new SimpleBooleanProperty(model.getLeftDoors()));
        properties.put(RIGHTDOORS_PROPERTY, new SimpleBooleanProperty(model.getRightDoors()));
        properties.put(NUMCARS_PROPERTY, new SimpleIntegerProperty(model.getNumCars()));
        properties.put(NUMPASSENGERS_PROPERTY, new SimpleIntegerProperty(model.getPassengerCount()));
        properties.put(CREWCOUNT_PROPERTY, new SimpleIntegerProperty(model.getCrewCount()));
        properties.put(TIMEDELTA_PROPERTY, new SimpleDoubleProperty(model.getTimeDelta()));
        properties.put(MASS_PROPERTY, new SimpleDoubleProperty(model.getMass()));
        properties.put(DISTANCETRAVELED_PROPERTY, new SimpleDoubleProperty(model.getDistanceTraveled()));
        properties.put(LENGTH_PROPERTY, new SimpleDoubleProperty(model.getlength()));
//        properties.put(BEACON_PROPERTY, new SimpleStringProperty(model.getBeacon()));
        properties.put(ANNOUNCEMENT_PROPERTY, new SimpleStringProperty(model.getAnnouncement()));
    }
    public TrainModelSubject(TrainModelImpl trainModel) {
        this.model = trainModel;
        intitializeValues();
        map.registerSubject(trainModel.getTrainNumber(),this);
    }

    public TrainModelSubject() {
        this.model = new TrainModelImpl();

        properties.put(AUTHORITY_PROPERTY, new SimpleIntegerProperty(0));
        properties.put(COMMANDSPEED_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(ACTUALSPEED_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(ACCELERATION_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(POWER_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(GRADE_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(SERVICEBRAKE_PROPERTY, new SimpleBooleanProperty(false));
        properties.put(EMERGENCYBRAKE_PROPERTY, new SimpleBooleanProperty(false));
        properties.put(BRAKEFAILURE_PROPERTY, new SimpleBooleanProperty(false));
        properties.put(POWERFAILURE_PROPERTY, new SimpleBooleanProperty(false));
        properties.put(SIGNALFAILURE_PROPERTY, new SimpleBooleanProperty(false));
        properties.put(SETTEMPERATURE_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(REALTEMPERATURE_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(EXTLIGHTS_PROPERTY, new SimpleBooleanProperty(false));
        properties.put(INTLIGHTS_PROPERTY, new SimpleBooleanProperty(false));
        properties.put(LEFTDOORS_PROPERTY, new SimpleBooleanProperty(false));
        properties.put(RIGHTDOORS_PROPERTY, new SimpleBooleanProperty(false));
        properties.put(NUMCARS_PROPERTY, new SimpleIntegerProperty(0));
        properties.put(NUMPASSENGERS_PROPERTY, new SimpleIntegerProperty(0));
        properties.put(CREWCOUNT_PROPERTY, new SimpleIntegerProperty(0));
        properties.put(TIMEDELTA_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(MASS_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(DISTANCETRAVELED_PROPERTY, new SimpleDoubleProperty(0));
        properties.put(LENGTH_PROPERTY, new SimpleDoubleProperty(0));
//        properties.put(BEACON_PROPERTY, new SimpleStringProperty(""));
        properties.put(ANNOUNCEMENT_PROPERTY, new SimpleStringProperty(""));
    }

    public void subjectDelete() {
        map.removeSubject(model.getTrainNumber());
    }

    public BooleanProperty getBooleanProperty (String propertyName) {
        return (BooleanProperty) getProperty(propertyName);
    }

    public DoubleProperty getDoubleProperty (String propertyName) {
        return (DoubleProperty) getProperty(propertyName);
    }

    public IntegerProperty getIntegerProperty (String propertyName) {
        return (IntegerProperty) getProperty(propertyName);
    }

    public StringProperty getStringProperty (String propertyName) {
        return (StringProperty) getProperty(propertyName);
    }


    public void setProperty(String propertyName, Object newValue) {
        Runnable updateTask = () -> {
            //System.out.println("setProperty called from " + Thread.currentThread().getName() + " with " + propertyName + " and " + newValue);
            Property<?> property = properties.get(propertyName);
            updateProperty(property, newValue);
            model.setValue(propertyName, newValue);
        };
        if(!isLogicUpdate){
            Platform.runLater(() -> updateFromGUI(updateTask));
        }else{
            throw new IllegalStateException("Cannot call setProperty from logic update, or perform GUI update during logic update.");
        }
    }

    public void notifyChange(String propertyName, Object newValue){
        updateFromLogic(() -> {
            Property<?> property = properties.get(propertyName);
            updateProperty(property, newValue);
        });
    }

    public TrainModel getModel() {
        return model;
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }


    public void updateFromGUI(Runnable updateLogic) {
        //System.out.println("Called from updateFromGUI.");
        isGUIUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isGUIUpdate = false;
        }
    }

    public void updateFromLogic(Runnable updateLogic) {
        //System.out.println("Called from updateFromLogic.");
         isLogicUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isLogicUpdate = false;
        }
    }
}