package trainModel;

import Framework.Support.AbstractSubject;
import javafx.beans.property.*;

public class trainModelSubject implements AbstractSubject{

    private IntegerProperty authority, numCars, numPassengers, crewCount;
    private DoubleProperty commandSpeed, actualSpeed, acceleration, power, grade, temperature;

    //Vital Variables
    private BooleanProperty serviceBrake, emergencyBrake, brakeFailure, powerFailure;
    private BooleanProperty signalFailure, extLights, intLights, rightDoors, leftDoors;
    private trainModelImpl model;

    public trainModelSubject() {
        this.authority = new SimpleIntegerProperty(0);
        this.commandSpeed = new SimpleDoubleProperty(0);
        this.actualSpeed = new SimpleDoubleProperty(0);
        this.acceleration = new SimpleDoubleProperty(0);
        this.power = new SimpleDoubleProperty(0);
        this.grade = new SimpleDoubleProperty(0);
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
        this.numCars = new SimpleIntegerProperty(0);
        this.numPassengers = new SimpleIntegerProperty(0);
        this.crewCount = new SimpleIntegerProperty(0);
    }

    public trainModelSubject(trainModelImpl trainModel) {
        this.model = trainModel;

        this.authority = new SimpleIntegerProperty(0);
        this.commandSpeed = new SimpleDoubleProperty(0);
        this.actualSpeed = new SimpleDoubleProperty(0);
        this.acceleration = new SimpleDoubleProperty(0);
        this.power = new SimpleDoubleProperty(0);
        this.grade = new SimpleDoubleProperty(0);
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
        this.numCars = new SimpleIntegerProperty(0);
        this.numPassengers = new SimpleIntegerProperty(0);
        this.crewCount = new SimpleIntegerProperty(0);

        model.addChangeListener(((propertyName, newValue) -> {
            switch (propertyName) {
                case "authority":
                    authority.set((int) newValue);
                    break;
                case "commandSpeed":
                    commandSpeed.set((double) newValue);
                    break;
                case "actualSpeed":
                    actualSpeed.set((double) newValue);
                    break;
                case "acceleration":
                    acceleration.set((double) newValue);
                    break;
                case "power":
                    power.set((double) newValue);
                    break;
                case "grade":
                    grade.set((double) newValue);
                    break;
                case "serviceBrake":
                    serviceBrake.set((boolean) newValue);
                    break;
                case "emergencyBrake":
                    emergencyBrake.set((boolean) newValue);
                    break;
                case "brakeFailure":
                    brakeFailure.set((boolean) newValue);
                    break;
                case "powerFailure":
                    powerFailure.set((boolean) newValue);
                    break;
                case "signalFailure":
                    signalFailure.set((boolean) newValue);
                    break;
                case "extLights":
                    extLights.set((boolean) newValue);
                    break;
                case "intLights":
                    intLights.set((boolean) newValue);
                    break;
                case "leftDoors":
                    leftDoors.set((boolean) newValue);
                    break;
                case "rightDoors":
                    rightDoors.set((boolean) newValue);
                    break;
                case "temperature":
                    temperature.set((double) newValue);
                    break;
                case "numCars":
                    numCars.set((int) newValue);
                    break;
                case "numPassengers":
                    numPassengers.set((int) newValue);
                    break;
                case "crewCount":
                    crewCount.set((int) newValue);
                    break;
            }
        }));
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
            case "grade" -> grade;
            case "temperature" -> temperature;
            default -> null;
        };
    }

    public IntegerProperty getIntegerProperty (String propertyName) {
        return switch (propertyName) {
            case "authority" -> authority;
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
            case "numCars" -> updateProperty(numCars, newValue);
            case "numPassengers" -> updateProperty(numPassengers, newValue);
            case "crewCount" -> updateProperty(crewCount, newValue);
            default -> System.err.println("Unknown property " + propertyName);
        }
    }
}

