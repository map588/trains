package trainController;

import Framework.Support.AbstractSubject;

import Common.TrainController;

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class trainControllerSubject implements AbstractSubject {
    private Map<String, Property<?>> properties = new HashMap<>();
    private List<ChangeListener<? super Object>> listeners = new ArrayList<>();

    private TrainController controller;
    private boolean isGuiUpdate = false;

    public trainControllerSubject(TrainController controller) {
        this.controller = controller;
        initializeProperties();
        controller.addChangeListener(this::handleControllerChange);
        trainControllerSubjectFactory.getInstance().registerSubject(controller.getID(), this);
    }

    private void handleControllerChange(ObservableValue<?> observableValue, Object oldValue, Object newValue) {
        String propertyName = observableValue.toString();
        Platform.runLater(() -> setProperty(propertyName, newValue));
    }

    protected void notifyChange(String propertyName, Object newValue) {
        listeners.forEach(listener -> listener.changed(null, null, newValue));
    }

    private void handleControllerChange(String propertyName, Object newValue) {
        // Update the property based on the change notification from the controller
        Platform.runLater(() -> setProperty(propertyName, newValue));
    }

    private void initializeProperties() {
        properties.put("authority", new SimpleIntegerProperty(this, "authority",0));
        properties.put("trainID", new SimpleIntegerProperty(this, "trainID",0));
        properties.put("commandSpeed", new SimpleDoubleProperty(this, "commandSpeed",0));
        properties.put("currentSpeed", new SimpleDoubleProperty(this, "currentSpeed",0));
        properties.put("overrideSpeed", new SimpleDoubleProperty(this, "overrideSpeed",0));
        properties.put("maxSpeed", new SimpleDoubleProperty(this, "maxSpeed",0));
        properties.put("Ki", new SimpleDoubleProperty(this, "Ki",0));
        properties.put("Kp", new SimpleDoubleProperty(this, "Kp",0));
        properties.put("power", new SimpleDoubleProperty(this, "power",0));
        properties.put("serviceBrake", new SimpleBooleanProperty(this, "serviceBrake",false));
        properties.put("emergencyBrake", new SimpleBooleanProperty(this, "emergencyBrake",false));
        properties.put("automaticMode", new SimpleBooleanProperty(this, "automaticMode",false));
        properties.put("intLights", new SimpleBooleanProperty(this, "intLights",false));
        properties.put("extLights", new SimpleBooleanProperty(this, "extLights",false));
        properties.put("leftDoors", new SimpleBooleanProperty(this, "leftDoors",false));
        properties.put("rightDoors", new SimpleBooleanProperty(this, "rightDoors",false));
        properties.put("announcements", new SimpleBooleanProperty(this, "announcements",false));
        properties.put("signalFailure", new SimpleBooleanProperty(this, "signalFailure",false));
        properties.put("brakeFailure", new SimpleBooleanProperty(this, "brakeFailure",false));
        properties.put("powerFailure", new SimpleBooleanProperty(this, "powerFailure",false));
        properties.put("temperature", new SimpleDoubleProperty(this, "temperature",0));
        properties.put("inTunnel", new SimpleBooleanProperty(this,"inTunnel",false));
        properties.put("leftPlatform", new SimpleBooleanProperty(this,"leftPlatform",false));
        properties.put("rightPlatform", new SimpleBooleanProperty(this,"rightPlatform",false));
    }

    public void setProperty(String propertyName, Object newValue) {
        if (isGuiUpdate) {
            return;
        }

        Property<?> property = properties.get(propertyName);
        if(property != null) {
            updateProperty(property, newValue);
        } else {
            System.err.println("No property found for " + propertyName);
        }
    }

    public Property<?> getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public BooleanProperty getBooleanProperty(String propertyName) {
        return (BooleanProperty) getProperty(propertyName);
    }

    public DoubleProperty getDoubleProperty(String propertyName) {
        return (DoubleProperty) getProperty(propertyName);
    }

    public IntegerProperty getIntegerProperty(String propertyName) {
        return (IntegerProperty) getProperty(propertyName);
    }

    public void updateFromGui(Runnable updateLogic) {
        isGuiUpdate = true;
        try {
            updateLogic.run();
        } finally {
            isGuiUpdate = false;
        }
    }




}
