package trainController;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.Notifications;
import trainModel.stubTrainModel;


public class trainControllerImpl implements TrainController, Notifications {
    private int authority;
    private int blocksToNextStation;
    private double commandSpeed;
    private double currentSpeed;
    private double overrideSpeed;
    private double maxSpeed;

    private double Ki;
    private double Kp;

    private double power;
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

    private int trainID;
    private TrainModel train;

    private final trainControllerSubject subject;

    //Passing with trainID actually adds the train to the subject list
    public trainControllerImpl(int trainID) {
        this.trainID = trainID;
        this.authority = 0;
        this.commandSpeed = 0.0;
        this.currentSpeed = 0.0;
        this.overrideSpeed = 0.0;
        this.maxSpeed = 0.0;
        this.Ki = 0.0;
        this.Kp = 0.0;
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
    }

    public trainControllerSubject getSubject() {
        return this.subject;
    }



    //-----------------Setters-----------------
    public void assignTrainModel(TrainModel train) {
        this.train = train;
        setOverrideSpeed(train.getSpeed());
        setCommandSpeed(train.getSpeed());
        setServiceBrake(train.getServiceBrake());
        setEmergencyBrake(train.getEmergencyBrake());
        setIntLights(train.getIntLights());
        setExtLights(train.getExtLights());
        setLeftDoors(train.getLeftDoors());
        setRightDoors(train.getRightDoors());
        setTemperature(train.getTemperature());
        setSignalFailure(train.getSignalFailure());
        setBrakeFailure(train.getBrakeFailure());
        setPowerFailure(train.getPowerFailure());
    }

    public void notifyChange(String propertyName, Object newValue) {
        System.out.println("Variable: " + propertyName + " changed to " + newValue);

        // If the set is called by the GUI, it implies that the property is already changed
        // and we should not notify the subject of the change, because its already changed...
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
    }
    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        notifyChange("commandSpeed", speed);
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
        notifyChange("power", power);
    }
    public void setIntLights(boolean lights) {
        this.internalLights = lights;
        notifyChange("internalLights", lights);
    }
    public void setExtLights(boolean lights) {
        this.externalLights = lights;
        notifyChange("externalLights", lights);
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

    public void setValue(String propertyName, Object newValue) {
        switch (propertyName) {
            case "automaticMode" -> setAutomaticMode((boolean) newValue);
            case "authority" -> setAuthority((int) newValue);
            case "overrideSpeed" -> setOverrideSpeed((double) newValue);
            case "commandSpeed" -> setCommandSpeed((double) newValue);
            case "serviceBrake" -> setServiceBrake((boolean) newValue);
            case "emergencyBrake" -> setEmergencyBrake((boolean) newValue);
            case "Ki" -> setKi((double) newValue);
            case "Kp" -> setKp((double) newValue);
            case "power" -> setPower((double) newValue);
            case "internalLights" -> setIntLights((boolean) newValue);
            case "externalLights" -> setExtLights((boolean) newValue);
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
        }
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

    public double  getMaxSpeed() {
        return this.maxSpeed;
    }
    public boolean getLeftDoors() {
        return this.leftDoors;
    }
    public boolean getRightDoors() {
        return this.rightDoors;
    }
    public double  getTemperature() {
        return this.temperature;
    }

    public boolean getLeftPlatform(){return this.leftPlatform;}
    public boolean getRightPlatform(){return this.rightPlatform;}
    public boolean getInTunnel(){return this.inTunnel;}

    // Power Calculations
    // P = Fv;  Power = Force * velocity
    // F = ma;  Force = Mass * Accceleration
}