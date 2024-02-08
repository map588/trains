package trainModel;

import Common.trackModel;
import Common.trainController;
import Common.trainModel;
import javafx.beans.property.*;
import trainController.trainControllerImpl;

public class trainModelImpl implements trainModel{
    private IntegerProperty authority;
    private DoubleProperty commandSpeed;
    private DoubleProperty speed;
    private DoubleProperty acceleration;
    private DoubleProperty power;

    private DoubleProperty temperature;
    private BooleanProperty serviceBrake;
    private BooleanProperty emergencyBrake;
    private BooleanProperty lights;
    private BooleanProperty leftDoors;
    private BooleanProperty rightDoors;

    private IntegerProperty numCars;
    private IntegerProperty numPassengers;

    private trackModel track;
    private trainController controller;

    public trainModelImpl(int trainID, trackModel track){
        this.authority = new SimpleIntegerProperty(0);
        this.commandSpeed = new SimpleDoubleProperty(0);
        this.speed = new SimpleDoubleProperty(0);
        this.acceleration = new SimpleDoubleProperty(0);
        this.power = new SimpleDoubleProperty(0);
        this.temperature = new SimpleDoubleProperty(0);
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);
        this.lights = new SimpleBooleanProperty(false);
        this.leftDoors = new SimpleBooleanProperty(false);
        this.rightDoors = new SimpleBooleanProperty(false);
        this.numCars = new SimpleIntegerProperty(0);
        this.numPassengers = new SimpleIntegerProperty(0);

        this.track = track;
        this.controller = new trainControllerImpl(trainID);
        this.controller.assignTrainModel(this);
    }


    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake.set(brake);
    }
    public void setServiceBrake(boolean brake) {
        this.serviceBrake.set(brake);
    }
    public void setPower(double power) {
        this.power.set(power);
    }
    public void setNumCars(int numCars) {
        this.numCars.set(numCars);
    }
    public void setNumPassengers(int numPassengers) {
        this.numPassengers.set(numPassengers);
    }

    public void setLeftDoors(boolean doors) {
        this.leftDoors.set(doors);
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors.set(doors);
    }
    public void setLights(boolean lights) {
        this.lights.set(lights);
    }
    public void setTemperature(double temp) {
        this.temperature.set(temp);
    }

    //----Getter Signals----
    public int getAuthority() {
        return this.authority.get();
    }
    public double getCommandSpeed() {
        return this.commandSpeed.get();
    }
    public double getSpeed() {
        return this.speed.get();
    }
    public double getAcceleration() {
        return this.acceleration.get();
    }
    public double getPower() {
        return this.power.get();
    }
    public boolean getServiceBrake() {
        return this.serviceBrake.get();
    }
    public boolean getEmergencyBrake() {
        return this.emergencyBrake.get();
    }

    public double getWeightKG() {
        return 0;
    }

    public double getTemperature() {
        return this.temperature.get();
    }
    public boolean getLights() {
        return this.lights.get();
    }
    public boolean getLeftDoors() {
        return this.leftDoors.get();
    }
    public boolean getRightDoors() {
        return this.rightDoors.get();
    }

    //----Vital Signals from Track Model----
    public int readAuthority() {
        return track.getTrainAuthority(controller.getTrainID());
    }
    public double readCommandSpeed() {
        return track.getCommandedSpeed(controller.getTrainID());
    }
    public void readBeacon() {
        return;
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
    public DoubleProperty temperatureProperty() {
        return temperature;
    }
    public BooleanProperty lightsProperty() {
        return lights;
    }
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




    public void calculateSpeed() {

    }

    public void calculateAcceleration() {

    }

}
