package trainModel;

import Framework.AbstractSubject;
import javafx.beans.property.*;
import Common.TrainModel;
import javafx.beans.value.ObservableValue;

import javax.swing.*;
import java.util.List;

public class trainModelSubject implements AbstractSubject {

    private IntegerProperty authority, numCars, numPassengers;
    private DoubleProperty commandSpeed, actualSpeed, acceleration, power, temperature;

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
    }

    public trainModelSubject(trainModelImpl trainModel) {
        this.model = trainModel;

        this.authority = new SimpleIntegerProperty(0);
        this.commandSpeed = new SimpleDoubleProperty(0);
        this.actualSpeed = new SimpleDoubleProperty(0);
        this.acceleration = new SimpleDoubleProperty(0);
        this.power = new SimpleDoubleProperty(0);
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
            }
        }));
    }

    public void updateProperty(BooleanProperty property, boolean newValue) {
        if (property.get() != newValue)
            property.set(newValue);
    }

    public void updateProperty(DoubleProperty property, double newValue) {
        if (property.get() != newValue)
            property.set(newValue);
    }

    public void updateProperty(IntegerProperty property, int newValue) {
        if (property.get() != newValue)
            property.set(newValue);
    }

    public BooleanProperty getBooleanProperty (String propertyName) {
        switch(propertyName) {
            case "serviceBrake":
                return serviceBrake;
            case "emergencyBrake":
                return emergencyBrake;
            case "brakeFailure":
                return brakeFailure;
            case "powerFailure":
                return powerFailure;
            case "signalFailure":
                return signalFailure;
            case "extLights":
                return extLights;
            case "intLights":
                return intLights;
            case "leftDoors":
                return leftDoors;
            case "rightDoors":
                return rightDoors;
            default:
                return null;
        }
    }

    public DoubleProperty getDoubleProperty (String propertyName) {
        switch (propertyName) {
            case "commandSpeed":
                return commandSpeed;
            case "actualSpeed":
                return actualSpeed;
            case "acceleration":
                return acceleration;
            case "power":
                return power;
            case "temperature":
                return temperature;
            default:
                return null;
        }
    }

    public IntegerProperty getIntegerProperty (String propertyName) {
        switch (propertyName) {
            case "authority":
                return authority;
            case "numCars":
                return numCars;
            case "numPassengers":
                return numPassengers;
            default:
                return null;
        }
    }
}

