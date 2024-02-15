package trainController;

import Common.trainController;
import javafx.beans.property.*;



public class trainControllerSubject {
    private IntegerProperty authority;
    private DoubleProperty commandSpeed;
    private DoubleProperty currentSpeed;
    private DoubleProperty overrideSpeed;
    private DoubleProperty maxSpeed;

    private DoubleProperty Ki;
    private DoubleProperty Kp;

    private DoubleProperty power;
    private BooleanProperty serviceBrake;
    private BooleanProperty emergencyBrake;
    private BooleanProperty automaticMode;

    private IntegerProperty trainNumber;

    private trainController controller;

    public trainControllerSubject(trainController controller) {
        this.trainNumber = new SimpleIntegerProperty(controller.getTrainNumber());
        this.currentSpeed = new SimpleDoubleProperty(controller.getSpeed());
        this.commandSpeed = new SimpleDoubleProperty(controller.getCommandSpeed());
        this.overrideSpeed = new SimpleDoubleProperty(controller.getOverrideSpeed());
        this.automaticMode = new SimpleBooleanProperty(controller.getAutomaticMode());
        this.Ki = new SimpleDoubleProperty(1.0);
        this.Kp = new SimpleDoubleProperty(22.0);
        this.power = new SimpleDoubleProperty(0.0);
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);
        this.authority = new SimpleIntegerProperty(2000);
        this.maxSpeed = new SimpleDoubleProperty(50.0);
    }






}
