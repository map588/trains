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
    private TrainModel model;

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

    public trainModelSubject(TrainModel model) {
        this();
        this.model = model;
        this.authority.set(model.getAuthority());
        this.commandSpeed.set(model.getCommandSpeed());
        this.actualSpeed.set(model.getSpeed());
        this.acceleration.set(model.getAcceleration());
        this.power.set(model.getPower());
        this.serviceBrake.set(model.getServiceBrake());
        this.emergencyBrake.set(model.getEmergencyBrake());
        this.brakeFailure.set(model.getBrakeFailure());
        this.powerFailure.set(model.getPowerFailure());
        this.signalFailure.set(model.getSignalFailure());
        this.temperature.set(model.getTemperature());
        this.extLights.set(model.getExtLights());
        this.intLights.set(model.getIntLights());
        this.leftDoors.set(model.getLeftDoors());
        this.rightDoors.set(model.getRightDoors());
        this.numCars.set(model.getNumCars());
        this.numPassengers.set(model.getNumPassengers());
        trainSubjectFactory.subjectMap.put()
        model.addChangeListener(((propertyName, newValue) -> {
            switch (propertyName) {
                case "Authority":
                    authority.set((Integer) newValue);
                    break;
                case "commandSpeed":
                    commandSpeed.set((Double) newValue);
                    break;
                case "ActualSpeed":
                    actualSpeed.set((Double) newValue);
                    break;
                case "Acceleration":
                    acceleration.set((Double) newValue);
                    break;
            }
        }));
    }
}
