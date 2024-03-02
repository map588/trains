package trainModel;

import Common.TrainModel;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import trainModel.Properties;

public class trainModelSubject implements AbstractSubject{

    public boolean isGUIUpdate;
    public boolean isLogicUpdate;
    private ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private TrainModel model;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final trainModelSubjectMap map = trainModelSubjectMap.getInstance();

    public void intitializeValues() {
        properties.put(Properties.authority_p, new SimpleIntegerProperty(model.getAuthority()));
        properties.put(Properties.commandSpeed_p, new SimpleDoubleProperty(model.getCommandSpeed()));
        properties.put(Properties.actualSpeed_p, new SimpleDoubleProperty(model.getSpeed()));
        properties.put(Properties.grade_p, new SimpleDoubleProperty(model.getGrade()));
        properties.put(Properties.power_p, new SimpleDoubleProperty(model.getPower()));
        properties.put(Properties.temperature_p, new SimpleDoubleProperty(model.getTemperature()));
        properties.put(Properties.leftDoors_p, new SimpleBooleanProperty(model.getLeftDoors()));
        properties.put(Properties.rightDoors_p, new SimpleBooleanProperty(model.getRightDoors()));
        properties.put(Properties.extLights_p, new SimpleBooleanProperty(model.getExtLights()));
        properties.put(Properties.intLights_p, new SimpleBooleanProperty(model.getIntLights()));
        properties.put(Properties.brakeFailure_p, new SimpleBooleanProperty(model.getBrakeFailure()));
        properties.put(Properties.powerFailure_p, new SimpleBooleanProperty(model.getPowerFailure()));
        properties.put(Properties.signalFailure_p, new SimpleBooleanProperty(model.getSignalFailure()));
        properties.put(Properties.serviceBrake_p, new SimpleBooleanProperty(model.getServiceBrake()));
        properties.put(Properties.emergencyBrake_p, new SimpleBooleanProperty(model.getEmergencyBrake()));
        properties.put(Properties.timeDelta_p, new SimpleIntegerProperty(model.getTimeDelta()));
        properties.put(Properties.acceleration_p, new SimpleDoubleProperty(model.getAcceleration()));
        properties.put(Properties.crewCount_p, new SimpleIntegerProperty(model.getCrewCount()));
        properties.put(Properties.numPassengers_p, new SimpleIntegerProperty(model.getNumPassengers()));
        properties.put(Properties.numCars_p, new SimpleIntegerProperty(model.getNumCars()));
    }

    public trainModelSubject(TrainModel trainModel) {
        this.model = trainModel;
        intitializeValues();
        map.registerSubject(trainModel.getTrainNumber(),this);
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

    public void runPhysics() {
        System.out.println("Called runPhysics() from trainModelSubject.");
        updateFromLogic(() -> model.trainModelPhysics());
    }

    public void setProperty(String propertyName, Object newValue) {
        Runnable updateTask = () -> {
            System.out.println("setProperty called from " + Thread.currentThread().getName() + " with " + propertyName + " and " + newValue);
            Property<?> property = properties.get(propertyName);
            updateProperty(property, newValue);
            model.setValue(propertyName, newValue);
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

    public void notifyChange(String propertyName, Object newValue){
        Platform.runLater(() ->
                updateFromLogic(() -> {
                    Property<?> property = properties.get(propertyName);
                    updateProperty(property, newValue);
                })
        );
    }

    public TrainModel getModel() {
        return model;
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }


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
        System.out.println("Called from updateFromLogic.");
         isLogicUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isLogicUpdate = false;
        }
    }
}

