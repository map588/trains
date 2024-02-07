package trainController;

import  Common.trainController;
import Common.trainModel;

public class trainControllerImpl implements trainController{
    private int authority;
    private double commandSpeed;
    private double overrideSpeed;

    private double Ki;
    private double Kp;

    private double power;
    private boolean serviceBrake;
    private boolean emergencyBrake;
    private boolean automaticMode;

    private trainModel train;
    private int trainID;

    /*
    @param speed - the speed set-point of the train
    Will be overridden if the train is in manual mode
     */
    public trainControllerImpl(int trainID) {
        this.trainID = trainID;
        this.commandSpeed = 0.0;
        this.overrideSpeed = 0.0;
        this.automaticMode = true;
        this.Ki = 0.0;
        this.Kp = 0.0;
        this.power = 0.0;
        this.serviceBrake = false;
        this.emergencyBrake = false;
    }
    
    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
    }

    public void setAutomaticMode() {
        setAutomaticMode(true);
    }

    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
    }
    public void setOverrideSpeed(double speed) {
        this.overrideSpeed = speed;
    }

    public void setAuthority(double authority) {

    }

    public void assignTrainModel(trainModel train) {

    }

   public void setKi(double Ki) {
        this.Ki = Ki;
    }

    public void setKp(double Kp) {
        this.Kp = Kp;
    }

    public void setServiceBrake(boolean brake) {

    }

    public void setEmergencyBrake(boolean brake) {

    }

    public int getTrainID() {
        return this.trainID;
    }

    /*
    This function will effectively be the main control loop for the train.
    There will be a main loop that will run the train controller in a separate thread.
     */
    private void speedController(double setSpeed, double currentSpeed){

        double error = setSpeed - currentSpeed;
        double power = Kp * error + Ki * error;
        this.train.setPower(power);


    }

    private void mainController(){
        double setSpeed = 0.0;

        while(true) {
            if (this.automaticMode) {
                setSpeed = this.commandSpeed;
            } else {
                setSpeed = this.overrideSpeed;
            }
            speedController(setSpeed, this.train.getSpeed());
        }

    }
}
