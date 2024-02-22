package trainController;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.Notifications;
import Utilities.Constants;
import trainModel.stubTrainModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static Utilities.Constants.EMERGENCY_BRAKE_DECELERATION;
import static Utilities.Constants.SERVICE_BRAKE_DECELERATION;
import static Utilities.Conversion.convertPower;
import static Utilities.Conversion.convertVelocity;
import static Utilities.Conversion.powerUnits.HORSEPOWER;
import static Utilities.Conversion.powerUnits.WATTS;
import static Utilities.Conversion.velocityUnit.MPH;
import static Utilities.Conversion.velocityUnit.MPS;


public class trainControllerImpl implements TrainController, Notifications {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private int authority;
    private int blocksToNextStation;
    private double commandSpeed;
    private double currentSpeed;
    private double overrideSpeed;
    private double speedLimit;

    private double Ki;
    private double Kp;

    //These are actually private, property mirror not necessary
    private double rollingError;
    private int samplingPeriod;
    private double prevError;
    private double error;

    private double power;
    private double grade;

    private boolean serviceBrake;
    private boolean emergencyBrake;
    private boolean automaticMode;

    private boolean internalLights;
    private boolean externalLights;
    private boolean leftDoors;
    private boolean rightDoors;
    private boolean announcements;
    private boolean signalFailure;
    private boolean brakeFailure;
    private boolean powerFailure;

    private boolean leftPlatform;
    private boolean rightPlatform;
    private boolean inTunnel;

    private double temperature;

    private String nextStationName;
    private final int trainID;
    private TrainModel train;

    private final trainControllerSubject subject;

    //Passing with trainID actually adds the train to the subject list
    public trainControllerImpl(int trainID) {
        this.trainID = trainID;
        this.authority = 0;
        this.commandSpeed = 0.0;
        this.currentSpeed = 0.0;
        this.overrideSpeed = 0.0;
        this.speedLimit = 0.0;
        this.Ki = 1.0;
        this.Kp = 1.0;
        this.power = 0.0;
        this.serviceBrake = false;
        this.emergencyBrake = false;
        this.automaticMode = false;
        this.internalLights = false;
        this.externalLights = false;
        this.leftDoors = false;
        this.rightDoors = false;
        this.temperature = 0.0;
        this.inTunnel = false;
        this.rightPlatform = false;
        this.leftPlatform = false;
        this.subject = new trainControllerSubject(this);
        this.train = stubTrainModel.createstubTrainModel();
        this.samplingPeriod = 10;
        this.nextStationName = "Yard";
        this.grade = 0;
        this.rollingError = 0;
        this.prevError = 0;
        this.error = 0;
        this.power = 0;

        this.executorService.scheduleAtFixedRate(this::calculatePower, samplingPeriod, samplingPeriod, TimeUnit.MILLISECONDS);

    }

    public trainControllerSubject getSubject() {
        return this.subject;
    }



    //-----------------Setters-----------------
    public void assignTrainModel(TrainModel train) {
        this.train = train;
        this.setServiceBrake(train.getServiceBrake());
        this.setEmergencyBrake(train.getEmergencyBrake());
        this.setIntLights(train.getIntLights());
        this.setExtLights(train.getExtLights());
        this.setLeftDoors(train.getLeftDoors());
        this.setRightDoors(train.getRightDoors());
        this.setTemperature(train.getTemperature());
        this.setSignalFailure(train.getSignalFailure());
        this.setBrakeFailure(train.getBrakeFailure());
        this.setPowerFailure(train.getPowerFailure());
    }

    public void notifyChange(String propertyName, Object newValue) {
            System.out.println("Variable: " + propertyName + " changed to " + newValue);
        if(!subject.isGUIUpdate) {
            subject.notifyChange(propertyName, newValue);
        }
    }

    public void notifyChange(String propertyName, Object newValue, boolean suppressOutput) {
        if(!suppressOutput) {
            System.out.println("Variable: " + propertyName + " changed to " + newValue);
        }
        if(!subject.isGUIUpdate) {
            subject.notifyChange(propertyName, newValue);
        }
    }

    //Functions called by the internal logic to notify of changes
    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        notifyChange("automaticMode", mode);
    }
    public void setAuthority(int authority) {
        this.authority = authority;
        notifyChange("authority", authority);
    }
    public void setOverrideSpeed(double speed) {
        this.overrideSpeed = speed;
        notifyChange("overrideSpeed", speed);
        //calculatePower();
    }
    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        notifyChange("commandSpeed", speed);
        //calculatePower();
    }
    public void setSpeed(double speed) {
        this.currentSpeed = speed;
        notifyChange("currentSpeed", speed, true);
    }
    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        notifyChange("serviceBrake", brake);
    }
    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        notifyChange("emergencyBrake", brake);

    }
    public void setKi(double Ki) {
        this.Ki = Ki;
        notifyChange("Ki", Ki);
    }
    public void setKp(double Kp) {
        this.Kp = Kp;
        notifyChange("Kp", Kp);
    }
    public void setPower(double power) {
        this.power = power;
        subject.updateFromLogic(() -> {
            notifyChange("power", power, true);
        });
    }
    public void setIntLights(boolean lights) {
        this.internalLights = lights;
        notifyChange("intLights", lights); // This might've been the issue interiorLights -> intLights
    }
    public void setExtLights(boolean lights) {
        this.externalLights = lights;

        notifyChange("extLights", lights); // This might've been the issue exteriorLights -> extLights
    }
    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        notifyChange("leftDoors", doors);
    }
    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        notifyChange("rightDoors", doors);
    }
    public void setTemperature(double temp) {
        this.temperature = temp;
        notifyChange("temperature", temp);
    }
    public void setAnnouncements(boolean announcements) {
        this.announcements = announcements;
        notifyChange("announcements", announcements);
    }
    public void setSignalFailure(boolean signalFailure) {
        this.signalFailure = signalFailure;
        notifyChange("signalFailure", signalFailure);
    }
    public void setBrakeFailure(boolean brakeFailure) {
        this.brakeFailure = brakeFailure;
        notifyChange("brakeFailure", brakeFailure);
    }
    public void setPowerFailure(boolean powerFailure) {
        this.powerFailure = powerFailure;
        notifyChange("powerFailure", powerFailure);
    }
    public void setInTunnel(boolean tunnel){
        this.inTunnel = tunnel;
        notifyChange("inTunnel",tunnel);
    }
    public void setLeftPlatform(boolean platform){
        this.leftPlatform = platform;
        notifyChange("leftPlatform",platform);
    }
    public void setRightPlatform(boolean platform){
        this.rightPlatform = platform;
        notifyChange("rightPlatform",platform);
    }
    public void setSamplingPeriod(int period){
        this.samplingPeriod = period;
        notifyChange("samplingPeriod",period);
    }
    public void setSpeedLimit(double speedLimit){
        this.speedLimit = speedLimit;
        notifyChange("speedLimit",speedLimit);
    }
    public void setNextStationName(String name){
        this.nextStationName = name;
        notifyChange("nextStationName",name);
    }
    private void setGrade(double newValue) {
        this.grade = newValue;
        notifyChange("grade",newValue);
    }

    public void setValue(String propertyName, Object newValue) {
        switch (propertyName) {
            case "automaticMode" -> setAutomaticMode((boolean) newValue);
            case "authority" -> setAuthority((int) newValue);
            case "overrideSpeed" -> setOverrideSpeed((double) newValue);
            case "commandSpeed" -> setCommandSpeed((double) newValue);
            case "currentSpeed" -> setSpeed((double) newValue);
            case "serviceBrake" -> setServiceBrake((boolean) newValue);
            case "emergencyBrake" -> setEmergencyBrake((boolean) newValue);
            case "Ki" -> setKi((double) newValue);
            case "Kp" -> setKp((double) newValue);
            case "power" -> setPower((double) newValue);
            case "intLights" -> setIntLights((boolean) newValue);
            case "extLights" -> setExtLights((boolean) newValue);
            case "leftDoors" -> setLeftDoors((boolean) newValue);
            case "rightDoors" -> setRightDoors((boolean) newValue);
            case "temperature" -> setTemperature((double) newValue);
            case "announcements" -> setAnnouncements((boolean) newValue);
            case "signalFailure" -> setSignalFailure((boolean) newValue);
            case "brakeFailure" -> setBrakeFailure((boolean) newValue);
            case "powerFailure" -> setPowerFailure((boolean) newValue);
            case "inTunnel" -> setInTunnel((boolean) newValue);
            case "leftPlatform" -> setLeftPlatform((boolean) newValue);
            case "rightPlatform" -> setRightPlatform((boolean) newValue);
            case "samplingPeriod" -> setSamplingPeriod((int) newValue);
            case "speedLimit" -> setSpeedLimit((double) newValue);
            case "nextStationName" -> setNextStationName((String) newValue);
            case "grade" -> setGrade((double) newValue);
            default -> System.err.println("Property " + propertyName + " not found");
        }
        //calculatePower();
    }




    //-----------------Getters-----------------
    public int getID() {
        return this.trainID;
    }

    //    public int getBlocksToNextStation() {
//        return this.blocksToNextStation;
//    }

    public double  getSpeed() {
        return this.currentSpeed;
    }
    public double  getAcceleration() {
        return this.train.getAcceleration();
    }
    public int  getSamplingPeriod(){return this.samplingPeriod;}
    public double  getPower() {
        return this.power;
    }
    public boolean getServiceBrake() {
        return this.serviceBrake;
    }
    public boolean getEmergencyBrake() {
        return this.emergencyBrake;
    }
    public double  getCommandSpeed() {
        return this.commandSpeed;
    }
    public int     getAuthority() {
        return this.authority;
    }
    public double  getKi() {
        return this.Ki;
    }
    public double  getKp() {
        return this.Kp;
    }
    public double  getOverrideSpeed() {
        return this.overrideSpeed;
    }
    public boolean getAutomaticMode() {
        return this.automaticMode;
    }

    public boolean getExtLights() {
        return this.externalLights;
    }
    public boolean getIntLights() {
        return this.internalLights;
    }
    public boolean getAnnouncements() {
        return this.announcements;
    }
    public boolean getSignalFailure() {
        return this.signalFailure;
    }
    public boolean getBrakeFailure() {
        return this.brakeFailure;
    }
    public boolean getPowerFailure() {
        return this.powerFailure;
    }

    public double  getSpeedLimit() {return this.speedLimit;}
    public boolean getLeftDoors() {
        return this.leftDoors;
    }
    public boolean getRightDoors() {
        return this.rightDoors;
    }
    public double  getTemperature() {
        return this.temperature;
    }
    public String  getStationName(){
        return this.nextStationName;
    }
    public boolean getLeftPlatform(){return this.leftPlatform;}
    public boolean getRightPlatform(){return this.rightPlatform;}
    public boolean getInTunnel(){return this.inTunnel;}
    public double  getGrade(){return this.grade;}

    public void calculatePower(){

        // Convert Units
        double setSpeed, currSpeed, pow, accel;

        if (automaticMode){
            setSpeed = convertVelocity(commandSpeed, MPH, MPS);
        }
        else{
            setSpeed = convertVelocity(overrideSpeed, MPH, MPS);
        }

        currSpeed = convertVelocity(currentSpeed, MPH, MPS);
        accel = 0;


        error = setSpeed - currSpeed;
        rollingError += (float)samplingPeriod/2000 * (error + prevError);
        prevError = error;

        pow = Kp * error + Ki * rollingError;
        if(pow > Constants.MAX_POWER) {
            pow = convertPower(Constants.MAX_POWER,HORSEPOWER, WATTS);
        }

        if(automaticMode && (pow < 0)){
            pow = 0;
            setServiceBrake(true);
        }else if(serviceBrake && (pow > 0)){
            setServiceBrake(false);
        }

        if(emergencyBrake || serviceBrake || powerFailure || (pow < 0)) {
            pow = 0;
            if(emergencyBrake){
                accel = -1 * EMERGENCY_BRAKE_DECELERATION;
            }else if(serviceBrake) {
                accel = -1 * SERVICE_BRAKE_DECELERATION;
            }
        } else {
            accel = pow / train.getWeightKG();
        }

        currSpeed += accel * (float)samplingPeriod/1000;
        if(currSpeed < 0){
            currSpeed = 0;
        }

        this.setSpeed(convertVelocity(currSpeed, MPS, MPH));
        this.setPower(convertPower(pow, WATTS, HORSEPOWER));
    }

}