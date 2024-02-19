package trainModel.GUI;
import Common.trackModel;
import Common.trainController;
import Common.trainModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import trainModel.trainModelImpl;

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
    private trackModel track;
    private trainController controller;

    private trainModelSubject subject;

    trainModelSubject(int ID) {
        this.controller.trainID = ID;
        train = new trainModelImpl(ID);
    }
}
