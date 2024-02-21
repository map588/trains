package trainModel;

import Common.TrainModel;
import Framework.Support.AbstractSubject;
import Framework.Support.ObservableHashMap;
import javafx.beans.property.*;

public class trainModelSubject implements AbstractSubject{

    private ObservableHashMap<String, Property<?>> properties = new ObservableHashMap<>();
    private TrainModel model;

    private IntegerProperty authority;
    private DoubleProperty commandSpeed, actualSpeed, acceleration, power, temperature;

    private StringProperty numCars, numPassengers, crewCount, grade;
    private BooleanProperty serviceBrake, emergencyBrake, brakeFailure, powerFailure;
    private BooleanProperty signalFailure, extLights, intLights, rightDoors, leftDoors;

    public void intitializeValues() {
        this.authority = new SimpleIntegerProperty(0);
        this.commandSpeed = new SimpleDoubleProperty(0);
        this.actualSpeed = new SimpleDoubleProperty(0);
        this.acceleration = new SimpleDoubleProperty(0);
        this.power = new SimpleDoubleProperty(0);
        this.grade = new SimpleStringProperty("0");
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);
        this.brakeFailure = new SimpleBooleanProperty(false);
        this.powerFailure = new SimpleBooleanProperty(false);
        this.signalFailure = new SimpleBooleanProperty(false);
        this.temperature = new SimpleDoubleProperty(0);
        this.extLights = new SimpleBooleanProperty(false);
        this.intLights = new SimpleBooleanProperty(false);
        this.leftDoors = new SimpleBooleanProperty(false);
        this.rightDoors = new SimpleBooleanProperty(false);
        this.numCars = new SimpleStringProperty("0");
        this.numPassengers = new SimpleStringProperty("0");
        this.crewCount = new SimpleStringProperty("0");
        trainSubjectFactory.getInstance().registerSubject(0, this);
    }

    public trainModelSubject(TrainModel trainModel) {
        this.model = trainModel;

        properties.put("authority", new SimpleIntegerProperty(0));
        properties.put("commandSpeed", new SimpleDoubleProperty(0));
        properties.put("actualSpeed", new SimpleDoubleProperty(0));
        properties.put("acceleration", new SimpleDoubleProperty(0));
        properties.put("power", new SimpleDoubleProperty(0));
        properties.put("grade", new SimpleStringProperty("0"));
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
        properties.put("numCars", new SimpleStringProperty("0"));
        properties.put("numPassengers", new SimpleStringProperty("0"));
        properties.put("crewCount", new SimpleStringProperty("0"));
    }

    public BooleanProperty getBooleanProperty (String propertyName) {
        return switch (propertyName) {
            case "serviceBrake" -> serviceBrake;
            case "emergencyBrake" -> emergencyBrake;
            case "brakeFailure" -> brakeFailure;
            case "powerFailure" -> powerFailure;
            case "signalFailure" -> signalFailure;
            case "extLights" -> extLights;
            case "intLights" -> intLights;
            case "leftDoors" -> leftDoors;
            case "rightDoors" -> rightDoors;
            default -> null;
        };
    }

    public DoubleProperty getDoubleProperty (String propertyName) {
        return switch (propertyName) {
            case "commandSpeed" -> commandSpeed;
            case "actualSpeed" -> actualSpeed;
            case "acceleration" -> acceleration;
            case "power" -> power;
            case "temperature" -> temperature;
            default -> null;
        };
    }

    public IntegerProperty getIntegerProperty (String propertyName) {
        return switch (propertyName) {
            case "authority" -> authority;
            default -> null;
        };
    }

    public StringProperty getStringProperty (String propertyName) {
        return switch (propertyName) {
            case "grade" -> grade;
            case "numCars" -> numCars;
            case "numPassengers" -> numPassengers;
            case "crewCount" -> crewCount;
            default -> null;
        };
    }

    public void setProperty(String propertyName, Object newValue) {
        if (newValue == null) {
            System.err.println("Null value for property " + propertyName);
            return;
        }
        switch (propertyName) {
            case "authority" -> updateProperty(authority, newValue);
            case "commandSpeed" -> updateProperty(commandSpeed, newValue);
            case "actualSpeed" -> updateProperty(actualSpeed, newValue);
            case "acceleration" -> updateProperty(acceleration, newValue);
            case "power" -> updateProperty(power, newValue);
            case "serviceBrake" -> updateProperty(serviceBrake, newValue);
            case "emergencyBrake" -> updateProperty(emergencyBrake, newValue);
            case "brakeFailure" -> updateProperty(brakeFailure, newValue);
            case "powerFailure" -> updateProperty(powerFailure, newValue);
            case "signalFailure" -> updateProperty(signalFailure, newValue);
            case "extLights" -> updateProperty(extLights, newValue);
            case "intLights" -> updateProperty(intLights, newValue);
            case "leftDoors" -> updateProperty(leftDoors, newValue);
            case "rightDoors" -> updateProperty(rightDoors, newValue);
            case "temperature" -> updateProperty(temperature, newValue);
            case "numCars" -> updateProperty(numCars, newValue.toString());
            case "numPassengers" -> updateProperty(numPassengers, newValue.toString());
            case "crewCount" -> updateProperty(crewCount, newValue.toString());
            case "grade" -> updateProperty(grade, newValue.toString());
            default -> System.err.println("Unknown property " + propertyName);
        }
    }

    public Property<?> getProperty(String propertyName) {
        return null;
    }
}

