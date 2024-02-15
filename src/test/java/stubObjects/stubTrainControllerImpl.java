package stubObjects;

import Common.trainController;
import Common.trainModel;
import Utilities.Constants;
import javafx.beans.property.*;

public class stubTrainControllerImpl implements trainController{
    private IntegerProperty authority;
    private DoubleProperty commandSpeed;
    private DoubleProperty overrideSpeed;

    private DoubleProperty Ki;
    private DoubleProperty Kp;

    private DoubleProperty power;
    private BooleanProperty serviceBrake;
    private BooleanProperty emergencyBrake;
    private BooleanProperty automaticMode;

    private trainModel train;
    private IntegerProperty trainID;


    private double integralError;
    private double lastErrorTime;
    private double maxSpeed;
    private double brakePercentage;
    public stubTrainControllerImpl(int trainID) {
        this.trainID= new SimpleIntegerProperty(trainID);
        this.commandSpeed = new SimpleDoubleProperty(0.0);
        this.overrideSpeed = new SimpleDoubleProperty(0.0);
        this.automaticMode = new SimpleBooleanProperty(true);
        this.Ki = new SimpleDoubleProperty(0.0);
        this.Kp = new SimpleDoubleProperty(0.0);
        this.power = new SimpleDoubleProperty(0.0);
        this.serviceBrake = new SimpleBooleanProperty(false);
        this.emergencyBrake = new SimpleBooleanProperty(false);
        this.authority = new SimpleIntegerProperty(0);
        this.integralError = 0.0;
    }

    public void setCommandSpeed(double speed) {
        this.commandSpeed.set(speed);
    }

    public void setAutomaticMode() {
        System.out.println("Setting automatic mode");
        setAutomaticMode(true);
    }

    public void setAutomaticMode(boolean mode) {
        System.out.println("Setting automatic mode to " + mode);
        this.automaticMode.set(mode);
        this.overrideSpeed.set(0.0);
    }
    public void setOverrideSpeed(double speed) {
        System.out.println("Setting override speed to " + speed);
        this.overrideSpeed.set(speed);
    }

    public void setAuthority(int authority) {
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

    public int getTrainNumber() {
        return 0;
    }

    public void setServiceBrake(boolean brake) {
        System.out.println("Setting service brake to " + brake);
        this.serviceBrake.set(brake);
    }

    public void setEmergencyBrake(boolean brake) {
        System.out.println("Setting emergency brake to " + brake);
        this.setAutomaticMode(false);
        this.emergencyBrake.set(brake);
    }

    public int getTrainID() {
        System.out.println("Getting train ID");
        return this.trainID.get();
    }

    public double getSpeed() {
        System.out.println("Getting speed" + this.train.getSpeed());
        return this.train.getSpeed();
    }

    public double getAcceleration() {
        return 0;
    }

    public double getPower() {
        return 0;
    }

    public boolean getServiceBrake() {
        return false;
    }

    public boolean getEmergencyBrake() {
        return false;
    }

    public double getCommandSpeed() {
        return 0;
    }

    public int getAuthority() {
        return 0;
    }

    public double getKi() {
        return 0;
    }

    public double getKp() {
        return 0;
    }

    public double getOverrideSpeed() {
        return 0;
    }

    public boolean getAutomaticMode() {
        return false;
    }

    /*
    This function will effectively be the main control loop for the train.
    There will be a main loop that will run the train controller in a separate thread.
     */
    private void speedController(double setSpeed, double currentSpeed){
        if(setSpeed > this.maxSpeed) {
            setSpeed = this.maxSpeed;
        }
        double error = setSpeed - currentSpeed;
        double time = System.currentTimeMillis();
        double deltaTime = time - this.lastErrorTime;

        this.lastErrorTime = time;
        this.integralError += error * deltaTime;

        double powerPercentage = this.Kp.get() * error + this.Ki.get() * this.integralError;

        //Rough sketch of our control system, subject to change
        if (powerPercentage > 100.0) {
            powerPercentage = 100.0;
            this.train.setPower(powerPercentage* Constants.MAX_POWER_KW);
        } else if (powerPercentage < 0) {
            //brakePercentage =  Math.abs(powerPercentage) * Constants.MAX_POWER_KW / Constants.SERVICE_BRAKE_POWER;
            this.train.setServiceBrake(true);
            this.train.setPower(0);
        }
        else {
            this.train.setServiceBrake(false);
            this.train.setPower(Constants.MAX_POWER_KW * powerPercentage / 100.0);
        }
    }

    //Probably overkill as the authority will change naturally,
    //but there will be some signal to trigger a stop at the authority likely
    private void calculateMaxSpeed(){
        double authority = this.authority.get();
        this.maxSpeed = Math.sqrt(2 * Constants.EMERGENCY_BRAKE_DECELERATION * authority);
        if(this.train.getSpeed() > this.maxSpeed) {
            this.train.setEmergencyBrake(true);
            this.train.setPower(0.0);
            this.setAutomaticMode(false);
        }
    }

    private void mainController(){
        double setSpeed = 0.0;

        while(true) {
            this.authority.set(this.train.readAuthority());
            this.commandSpeed.set(this.train.readCommandSpeed());
            this.calculateMaxSpeed();

            if (this.automaticMode.get()) {
                setSpeed = this.commandSpeed.get();
            }
            else {
                setSpeed = this.overrideSpeed.get();
            }

            speedController(setSpeed, this.train.getSpeed());
        }

    }
}
