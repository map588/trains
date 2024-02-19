package trainModel;
import Common.TrainModel;

import javafx.beans.property.*;

import static trainModel.trainSubjectFactory.subjectMap;

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
    private TrainModel train;
    private int trainID;

    trainModelSubject(TrainModel train, int ID) {
        this.trainID = ID;
        this.train = train;
        this.extLights = new SimpleBooleanProperty(train.getExtLights());
        this.intLights = new SimpleBooleanProperty(train.getIntLights());
        this.leftDoors = new SimpleBooleanProperty(train.getLeftDoors());
        this.rightDoors = new SimpleBooleanProperty(train.getRightDoors());

        this.authority = new SimpleIntegerProperty(train.getAuthority());
        this.commandSpeed = new SimpleDoubleProperty(train.getCommandSpeed());
        this.speed = new SimpleDoubleProperty(train.getSpeed());
        this.acceleration = new SimpleDoubleProperty(train.getAcceleration());
        this.power = new SimpleDoubleProperty(train.getPower());
        this.serviceBrake = new SimpleBooleanProperty(train.getServiceBrake());
        this.emergencyBrake = new SimpleBooleanProperty(train.getEmergencyBrake());
        this.brakeFailure = new SimpleBooleanProperty(train.getBrakeFailure());
        this.powerFailure = new SimpleBooleanProperty(train.getPowerFailure());
        this.signalFailure = new SimpleBooleanProperty(train.getSignalFailure());
        this.temperature = new SimpleDoubleProperty(train.getTemperature());
        this.numCars = new SimpleIntegerProperty(train.getNumCars());
        this.numPassengers = new SimpleIntegerProperty(train.getNumPassengers());
        subjectMap.put(ID, this);

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

    public TrainModel getTrain() {
        return train;
    }
}
