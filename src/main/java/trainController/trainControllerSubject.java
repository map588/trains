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


/*
package trainController;

import Common.trainController;
import Common.trainModel;
import Utilities.Constants;
import javafx.beans.property.*;
import java.lang.System;

public class testTrainControllerImpl implements trainController {
    private IntegerProperty authority;
    private DoubleProperty commandSpeed;
    DoubleProperty currentSpeed;
    DoubleProperty overrideSpeed;
    private DoubleProperty maxSpeed;

    private DoubleProperty Ki;
    private DoubleProperty Kp;

    private DoubleProperty power;
    private BooleanProperty serviceBrake;
    private BooleanProperty emergencyBrake;
    private BooleanProperty automaticMode;

    private IntegerProperty trainID;

    private trainModel train;
    private controlSystem controlSystem;





    public testTrainControllerImpl(int trainID) {
        this.trainID = new SimpleIntegerProperty(trainID);
        this.currentSpeed = new SimpleDoubleProperty(10.0);
        this.commandSpeed = new SimpleDoubleProperty(35.0);
        this.overrideSpeed = new SimpleDoubleProperty(0.0);
        this.automaticMode = new SimpleBooleanProperty(false);
        this.Ki = new SimpleDoubleProperty(1.0);
        this.Kp = new SimpleDoubleProperty(22.0);
        this.power = new SimpleDoubleProperty(0.0);
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);
        this.authority = new SimpleIntegerProperty(2000);
        this.maxSpeed = new SimpleDoubleProperty(50.0);
        controlSystem = new controlSystem(this);
        Thread controlThread = new Thread(controlSystem);
        controlThread.start();
    }

    public void setCommandSpeed(double speed) {
        if(speed == this.getCommandSpeed())
            return;
        System.out.println("Setting command speed to " + speed);
        this.commandSpeed.set(speed);
    }

    public void setAutomaticMode() {
        System.out.println("Setting automatic mode");
        setAutomaticMode(true);
    }

    public void setAutomaticMode(boolean mode) {
        if(mode == this.getAutomaticMode())
            return;
        System.out.println("Setting automatic mode to " + mode);
        this.automaticMode.set(mode);
        this.overrideSpeed.set(0.0);
    }

    public void setOverrideSpeed(double speed) {
        if(speed == this.getOverrideSpeed())
            return;
        System.out.println("Setting override speed to " + speed);
        this.overrideSpeed.set(speed);
    }

    public void setAuthority(int authority) {
        if(authority == this.getAuthority())
            return;
        System.out.println("Setting authority to " + authority);
        this.authority.set(authority);
    }

    public void assignTrainModel(trainModel train) {
        System.out.println("Assigning train model");
        this.train = train;
    }

    public void setKi(double Ki) {
        System.out.println("Setting Ki to " + Ki);
        this.Ki.set(Ki);
    }

    public void setKp(double Kp) {
        System.out.println("Setting Kp to " + Kp);
        this.Kp.set(Kp);
    }

    public void setServiceBrake(boolean brake) {
        if(brake == this.getServiceBrake())
            return;
        System.out.println("Setting service brake to " + brake);
        this.serviceBrake.set(brake);
    }

    public void setEmergencyBrake(boolean brake) {
        this.setAutomaticMode(false);
        if(brake == this.getEmergencyBrake())
            return;
        System.out.println("Setting emergency brake to " + brake);
        this.emergencyBrake.set(brake);
    }

    public int getTrainID() {
        System.out.println("Getting train ID");
        return this.trainID.get();
    }

    public double getSpeed() {
        double speed = this.currentSpeed.get();
        //double speed = this.train.getSpeed();
        return speed;
    }

    public double getPower() {
        return this.power.get();
    }

    public void setPower(double power) {
        //this.train.setPower(power);
        if(power == this.getPower())
            return;
        System.out.println("Setting power to " + power);
        this.power.set(power);
    }

    public boolean getEmergencyBrake() {
        return this.emergencyBrake.get();
    }

    public boolean getServiceBrake() {
        return this.serviceBrake.get();
    }

    public boolean getAutomaticMode() {
        return this.automaticMode.get();
    }

    public double getCommandSpeed() {
        return this.commandSpeed.get();
    }

    public double getOverrideSpeed() {
        return this.overrideSpeed.get();
    }

    public double getMaxSpeed() {
        return this.maxSpeed.get();
    }

    public double getKi() {
        return this.Ki.get();
    }

    public double getKp() {
        return this.Kp.get();
    }

    public int getAuthority() {
        return this.authority.get();
    }

    public DoubleProperty overrideSpeedProperty() {
        return this.overrideSpeed;
    }

    public DoubleProperty currentSpeedProperty() {
        return this.currentSpeed;
    }

    public DoubleProperty powerProperty() {
        return this.power;
    }

    public DoubleProperty maxSpeedProperty() {
        return this.maxSpeed;
    }

    public DoubleProperty commandSpeedProperty() {
        return this.commandSpeed;
    }

    public DoubleProperty KiProperty() {
        return this.Ki;
    }

    public DoubleProperty KpProperty() {
        return this.Kp;
    }

    public BooleanProperty serviceBrakeProperty() {
        return this.serviceBrake;
    }

    public BooleanProperty emergencyBrakeProperty() {
        return this.emergencyBrake;
    }

    public BooleanProperty automaticModeProperty() {
        return this.automaticMode;
    }

    public IntegerProperty authorityProperty() {
        return this.authority;
    }


    This function will effectively be the main control loop for the train.
    There will be a main loop that will run the train controller in a separate thread.


//Probably overkill as the authority will change naturally,
//but there will be some signal to trigger a stop at the authority likely
void calculateMaxSpeed() {
    double authority = this.authority.get();
    this.maxSpeed.set(Math.sqrt(2 * Constants.EMERGENCY_BRAKE_DECELERATION * authority));
    if (this.getSpeed() > this.maxSpeed.get()) {
        this.setEmergencyBrake(true);
        this.setPower(0.0);
        this.setAutomaticMode(false);
    }
}

/*
class controlSystem implements Runnable {
    private testTrainControllerImpl trainController;

    private double integralError;
    private double lastErrorTime;
    private double lastPower;

    public controlSystem(testTrainControllerImpl trainController) {
        this.trainController = trainController;
        this.integralError = 0.0;
        this.lastPower = 0.0;
        this.lastErrorTime = System.currentTimeMillis();
    }

    private void speedController(double setSpeed, double currentSpeed) {
        double max = trainController.getMaxSpeed();
        if (setSpeed > max) {
            setSpeed = max;
        }
        double error = setSpeed - currentSpeed;
        double time = System.currentTimeMillis();
        double deltaTime = time - this.lastErrorTime;

        this.lastErrorTime = time;
        this.integralError += error * deltaTime;

        double powerPercentage = this.trainController.getKi() * error + this.trainController.getKp() * this.integralError;

        //Rough sketch of our control system, subject to change
        if (powerPercentage > 100.0) {
            powerPercentage = 100.0;
        } else if (powerPercentage < 0) {
            //brakePercentage =  Math.abs(powerPercentage) * Constants.MAX_POWER_KW / Constants.SERVICE_BRAKE_POWER;
            if (!this.trainController.getServiceBrake()) {
                this.trainController.setServiceBrake(true);
            }
            powerPercentage = 0.0;
        }
        if(powerPercentage * Constants.MAX_POWER_KW != this.lastPower) {
            this.trainController.setPower(powerPercentage * Constants.MAX_POWER_KW);
            this.lastPower = powerPercentage * Constants.MAX_POWER_KW;
        }
    }

    public void run() {
        double setSpeed;

        //temporarily set the current speed to the override speed
        //TrainModel is not implemented yet
        double currentSpeed = this.trainController.currentSpeed.get();
        double newSpeed = currentSpeed + this.trainController.getPower() * 0.1;
        this.trainController.currentSpeed.set(newSpeed);
        if(this.trainController.getEmergencyBrake())
            this.trainController.currentSpeed.set(currentSpeed - Constants.EMERGENCY_BRAKE_DECELERATION);
        else if(this.trainController.getServiceBrake())
            this.trainController.currentSpeed.set(currentSpeed - Constants.SERVICE_BRAKE_DECELERATION);

        while (true) {
//            this.authority.set(this.train.readAuthority());
//            this.commandSpeed.set(this.train.readCommandSpeed());
            if(this.trainController.getAuthority() != 2000){
                this.trainController.setAuthority(2000);
            }
            if(this.trainController.getOverrideSpeed() == 0.0){
                this.trainController.setAutomaticMode(true);
            }

            this.trainController.calculateMaxSpeed();
            if (this.trainController.getAutomaticMode()){
                setSpeed = this.trainController.getCommandSpeed();
            } else {
                setSpeed = this.trainController.getOverrideSpeed();
            }

            speedController(setSpeed, this.trainController.currentSpeed.get());

        }

    }

}
 */