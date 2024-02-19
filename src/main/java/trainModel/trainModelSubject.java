package trainModel;

import javafx.beans.property.*;
import Common.TrainModel;
import javafx.beans.value.ObservableValue;

import javax.swing.*;
import java.util.List;

public class trainModelSubject {

    private IntegerProperty authority, trainNumber, numCars, numPassengers;
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
                case "speed":
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
                case "trainNumber":
                    trainNumber.set((int) newValue);
                    break;
            }
        }));
    }
}

