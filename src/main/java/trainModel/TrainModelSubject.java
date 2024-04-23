package trainModel;

import Common.TrainModel;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import Utilities.Conversion;
import javafx.application.Platform;
import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static trainModel.Properties.*;

public class TrainModelSubject implements AbstractSubject{

    private static final Logger logger = LoggerFactory.getLogger(TrainModelSubject.class);


    private final ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private final TrainModelImpl model;
    private final TrainModelSubjectMap trainSubjectMap = TrainModelSubjectMap.getInstance();
    private final int trainID;




    public TrainModelSubject(TrainModelImpl trainModel) {
        this.model = trainModel;
        int trainID = trainModel.getTrainNumber();
        this.trainID = trainID;
        intitializeValues();
        if(trainModel.getTrainNumber() == -1 ){
            return;
        }
        if(trainSubjectMap.getSubjects().containsKey(trainID)){
            trainSubjectMap.removeSubject(trainID);
        }
        trainSubjectMap.registerSubject(trainID, this);
        logger.info("Train Subject added to map with ID: {}", trainID);
    }


    public TrainModelSubject() {
        this.model = new NullTrain();
        intitializeValues();
        this.trainID = -1;
        //Noteably, we do not register the NullTrain with the map
    }

    public void setProperty(String propertyName, Object newValue) {
        Platform.runLater(() -> {
            Property<?> property = properties.get(propertyName);
            updateProperty(property, newValue);
            model.setValue(propertyName, newValue);
        });
    }

    public void notifyChange(String propertyName, Object newValue) {
        Platform.runLater(() -> {
            Property<?> property = properties.get(propertyName);
            updateProperty(property, newValue);
        });
    }


    public void subjectDelete() {
        trainSubjectMap.removeSubject(model.getTrainNumber());
        logger.info("Train Subject removed from map with ID: {}", trainID);
    }


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
        properties.put(MASS_PROPERTY, new SimpleDoubleProperty(Conversion.convertMass(model.getMass(), Conversion.massUnits.KILOGRAMS, Conversion.massUnits.TONS)));
        properties.put(DISTANCETRAVELED_PROPERTY, new SimpleDoubleProperty(model.getDistanceTraveled()));
        properties.put(LENGTH_PROPERTY, new SimpleDoubleProperty(Conversion.convertDistance(model.getLength(), Conversion.distanceUnit.METERS, Conversion.distanceUnit.FEET)));
        properties.put(ANNOUNCEMENT_PROPERTY, new SimpleStringProperty(model.getAnnouncement()));
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



    public TrainModel getModel() {
        return model;
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

}