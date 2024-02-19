package trainModel;
import Common.TrackModel;
import Common.TrainController;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;

public class trainModelSubject {
    private IntegerProperty authority;
    private DoubleProperty commandSpeed;

    //Vital Variables
    private DoubleProperty speed;
    private DoubleProperty acceleration;
    private DoubleProperty power;
    private BooleanProperty serviceBrake;
    private BooleanProperty emergencyBrake;

    //Murphy Variables
    private BooleanProperty brakeFailure;
    private BooleanProperty powerFailure;
    private BooleanProperty signalFailure;

    //NonVital Variables
    private BooleanProperty extLights;
    private BooleanProperty intLights;
    private BooleanProperty leftDoors;
    private BooleanProperty rightDoors;
    private DoubleProperty temperature;

    private IntegerProperty numCars;
    private IntegerProperty numPassengers;

    //Module Stubs
    private TrackModel track;
    private TrainController controller;

    private trainModelSubject subject;

    trainModelSubject(int ID) {
        this.controller.trainID = ID;
        train = new trainModelImpl(ID);
    }

    //----Property Getters----
    public BooleanProperty serviceBrakeProperty() {
        return serviceBrake;
    }
    public BooleanProperty emergencyBrakeProperty() {
        return emergencyBrake;
    }
    public DoubleProperty powerProperty() {
        return power;
    }
    public BooleanProperty brakeFailureProperty() { return brakeFailure; }
    public BooleanProperty powerFailureProperty() { return powerFailure; }
    public BooleanProperty signalFailureProperty() { return signalFailure; }
    public DoubleProperty temperatureProperty() {
        return temperature;
    }
    public BooleanProperty extLightsProperty() {
        return extLights;
    }
    public BooleanProperty intLightsProperty() { return intLights; }
    public BooleanProperty leftDoorsProperty() {
        return leftDoors;
    }
    public BooleanProperty rightDoorsProperty() {
        return rightDoors;
    }
    public IntegerProperty authorityProperty() {
        return authority;
    }
    public DoubleProperty commandSpeedProperty() {
        return commandSpeed;
    }
    public DoubleProperty speedProperty() {
        return speed;
    }
    public DoubleProperty accelerationProperty() {
        return acceleration;
    }

}
