package trainModel;

import Common.TrainModel;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.application.Platform;
import javafx.beans.property.*;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class trainModelSubject implements AbstractSubject{

    public boolean isGUIUpdate;
    public boolean isLogicUpdate;
    private ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private TrainModel model;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();


    public void intitializeValues() {
        properties.put("authority", new SimpleIntegerProperty(0));
        properties.put("commandSpeed", new SimpleDoubleProperty(0));
        properties.put("actualSpeed", new SimpleDoubleProperty(0));
        properties.put("acceleration", new SimpleDoubleProperty(0));
        properties.put("power", new SimpleDoubleProperty(0));
        properties.put("grade", new SimpleDoubleProperty(0));
        properties.put("serviceBrake", new SimpleBooleanProperty(false));
        properties.put("emergencyBrake", new SimpleBooleanProperty(false));
        properties.put("brakeFailure", new SimpleBooleanProperty(false));
        properties.put("powerFailure", new SimpleBooleanProperty(false));
        properties.put("signalFailure", new SimpleBooleanProperty(false));
        properties.put("temperature", new SimpleDoubleProperty(0));
        properties.put("extLights", new SimpleBooleanProperty(false));
        properties.put("intLights", new SimpleBooleanProperty(false));
        properties.put("leftDoors", new SimpleBooleanProperty(false));
        properties.put("rightDoors", new SimpleBooleanProperty(false));
        properties.put("numCars", new SimpleIntegerProperty(0));
        properties.put("numPassengers", new SimpleIntegerProperty(0));
        properties.put("crewCount", new SimpleIntegerProperty(0));
    }

    public trainModelSubject(TrainModel trainModel) {
        this.model = trainModel;
        intitializeValues();
        trainSubjectFactory.getInstance().registerSubject(0, this);
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

