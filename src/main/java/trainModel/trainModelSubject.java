package trainModel;

import javafx.beans.property.*;
import Common.TrainModel;

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
    private IntegerProperty trainNumber;

    private IntegerProperty numCars;
    private IntegerProperty numPassengers;
    private TrainModel model;

    public trainModelSubject(TrainModel trainModel, int ID) {
        this.model = trainModel;
        this.authority = new SimpleIntegerProperty(0);
        this.commandSpeed = new SimpleDoubleProperty(0);
        this.speed = new SimpleDoubleProperty(0);
        this.acceleration = new SimpleDoubleProperty(0);
        this.power = new SimpleDoubleProperty(0);
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);

        this.brakeFailure = new SimpleBooleanProperty(false);
        this.powerFailure = new SimpleBooleanProperty(false);
        this.signalFailure = new SimpleBooleanProperty(false);

        this.trainNumber = new SimpleIntegerProperty(0);
        this.temperature = new SimpleDoubleProperty(0);
        this.extLights = new SimpleBooleanProperty(false);
        this.intLights = new SimpleBooleanProperty(false);
        this.leftDoors = new SimpleBooleanProperty(false);
        this.rightDoors = new SimpleBooleanProperty(false);
        this.numCars = new SimpleIntegerProperty(0);
        this.numPassengers = new SimpleIntegerProperty(0);
    }

    public void assignTrainModel(TrainModel model) { this.model = model; }

    //Vital Setters
    public void setEmergencyBrake(boolean brake) {
        System.out.println("Setting emergency Brake to " + brake);
        this.emergencyBrake.set(brake);
    }
    public void setServiceBrake(boolean brake) {
        System.out.println("Setting service Brake to " + brake);
        this.serviceBrake.set(brake);
    }
    public void setPower(double power) {
        System.out.println("Setting power to " + power);
        this.power.set(power);
    }

    //Murphy Setters
    public void setBrakeFailure(boolean failure) {
        System.out.println("Setting Brake Failure to " + failure);
        this.brakeFailure.set(failure);
    }
    public void setPowerFailure(boolean failure) {
        System.out.println("Setting Power Failure to " + failure);
        this.powerFailure.set(failure);
    }
    public void setSignalFailure(boolean failure) {
        System.out.println("Setting Signal Pickup Failure to " + failure);
        this.signalFailure.set(failure);
    }

    //NonVital Setters
    public void setNumCars(int numCars) {
        System.out.println("Setting number of Cars to " + numCars);
        this.numCars.set(numCars);
    }
    public void setNumPassengers(int numPassengers) {
        System.out.println("Setting number of passengers to " + numPassengers);
        this.numPassengers.set(numPassengers);
    }
    public void setLeftDoors(boolean doors) {
        System.out.println("Setting Left doors to " + doors);
        this.leftDoors.set(doors);
    }
    public void setRightDoors(boolean doors) {
        System.out.println("Setting Right doors to " + doors);
        this.rightDoors.set(doors);
    }
    public void setExtLights(boolean lights) {
        System.out.println("Setting Exterior Lights to " + lights);
        this.extLights.set(lights);
    }
    public void setIntLights(boolean lights) {
        System.out.println("Setting Interior Lights to " + lights);
        this.intLights.set(lights);
    }
    public void setTemperature(double temp) {
        System.out.println("Setting Temperature to " + temp);
        this.temperature.set(temp);
    }
    public int getTrainNumber() {
        System.out.println("Getting train ID");
        return this.trainNumber.get();
    }
}
