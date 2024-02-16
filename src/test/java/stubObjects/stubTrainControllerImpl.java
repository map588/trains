package stubObjects;

import Common.trainController;
import Common.trainModel;
import Utilities.Constants;

public class stubTrainControllerImpl implements trainController{
    private  int  authority;
    private  double  commandSpeed;
    private  double  overrideSpeed;

    private  double  Ki;
    private  double  Kp;

    private  double  power;
    private   boolean serviceBrake;
    private   boolean emergencyBrake;
    private   boolean automaticMode;

    private trainModel train;
    private  int  trainID;


    private  double integralError;
    private  double lastErrorTime;
    private  double maxSpeed;



    public stubTrainControllerImpl(int trainID) {
        this.trainID= trainID;
        this.commandSpeed =  0.0;
        this.overrideSpeed =  0.0;
        this.automaticMode =  true;
        this.Ki =  0.0;
        this.Kp =  0.0;
        this.power =  0.0;
        this.serviceBrake =  false;
        this.emergencyBrake =  false;
        this.authority =  0;
        this.integralError = 0.0;
    }

    public void setCommandSpeed( double speed) {
        this.commandSpeed = speed;
    }

    public void setAutomaticMode() {
        System.out.println("Setting automatic mode");
        setAutomaticMode(true);
    }

    public void setAutomaticMode(boolean mode) {
        System.out.println("Setting automatic mode to " + mode);
        this.automaticMode = mode;
        this.overrideSpeed = 0.0;
    }
    public void setOverrideSpeed( double speed) {
        System.out.println("Setting override speed to " + speed);
        this.overrideSpeed = speed;
    }

    public void setAuthority(int authority) {
        System.out.println("Setting authority to " + authority);
        this.authority = authority;
    }

    public void assignTrainModel(trainModel train) {
        System.out.println("Assigning train model");
        this.train = train;
    }

    public void setKi( double Ki) {
        System.out.println("Setting Ki to " + Ki);
        this.Ki = Ki;
    }

    public void setKp( double Kp) {
        System.out.println("Setting Kp to " + Kp);
        this.Kp = Kp;
    }

    public int getTrainNumber() {
        return 0;
    }

    public void setServiceBrake( boolean brake) {
        System.out.println("Setting service brake to " + brake);
        this.serviceBrake = brake;
    }

    public void setEmergencyBrake(boolean brake) {
        System.out.println("Setting emergency brake to " + brake);
        this.setAutomaticMode(false);
        this.emergencyBrake = brake;
    }

    public int getTrainID() {
        System.out.println("Getting train ID");
        return this.trainID;
    }

    public  double getSpeed() {
        System.out.println("Getting speed" + this.train.getSpeed());
        return this.train.getSpeed();
    }

    public  double getAcceleration() {
        return 0;
    }

    public  double getPower() {
        return 0;
    }

    public boolean getServiceBrake() {
        return false;
    }

    public boolean getEmergencyBrake() {
        return false;
    }

    public  double getCommandSpeed() {
        return 0;
    }

    public int getAuthority() {
        return 0;
    }

    public  double getKi() {
        return 0;
    }

    public  double getKp() {
        return 0;
    }

    public  double getOverrideSpeed() {
        return 0;
    }

    public boolean getAutomaticMode() {
        return false;
    }

    /*
    This function will effectively be the main control loop for the train.
    There will be a main loop that will run the train controller in a separate thread.
     */
    private void speedController( double setSpeed,  double currentSpeed){
        if(setSpeed > this.maxSpeed) {
            setSpeed = this.maxSpeed;
        }
         double error = setSpeed - currentSpeed;
         double time = System.currentTimeMillis();
         double deltaTime = time - this.lastErrorTime;

        this.lastErrorTime = time;
        this.integralError += error * deltaTime;

         double powerPercentage = this.Kp * error + this.Ki * this.integralError;

        //Rough sketch of our control system, subject to change
        if (powerPercentage > 100.0) {
            powerPercentage = 100.0;
            this.train.setPower(powerPercentage *  Constants.MAX_POWER_KW);
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
         double authority = this.authority;
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
            this.authority = this.train.readAuthority();
            this.commandSpeed = this.train.readCommandSpeed();
            this.calculateMaxSpeed();

            if (this.automaticMode) {
                setSpeed = this.commandSpeed;
            }
            else {
                setSpeed = this.overrideSpeed;
            }

            speedController(setSpeed, this.train.getSpeed());
        }

    }
}
