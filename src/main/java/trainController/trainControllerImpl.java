package trainController;

import Common.TrainController;
import Common.TrainModel;
import Framework.Support.Notifications;
import Framework.Support.PropertyChangeListener;
import trainModel.stubTrainModel;

import java.util.ArrayList;
import java.util.List;


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
    private final List<PropertyChangeListener> listeners = new ArrayList<>();

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
        this.train = new stubTrainModel();
    }

    public trainControllerSubject getSubject() {
        return this.subject;
    }

//    public int getBlocksToNextStation() {
//        return this.blocksToNextStation;
//    }

    public void addChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    protected void notifyChange(String propertyName, Object newValue) {
        System.out.println("Changed variable: " + propertyName + " to " + newValue.toString() + "in trainControllerImpl.");
        listeners.forEach(listener -> listener.onPropertyChange(propertyName, newValue));
    }

    //-----------------Setters-----------------
    public void assignTrainModel(TrainModel train) {
        this.train = train;
        notifyChange("CommandSpeed", this.commandSpeed);
        notifyChange("CurrentSpeed", this.currentSpeed);
        notifyChange("AutomaticMode", this.automaticMode);
    }

    //Functions called by the internal logic to notify of changes
    public void setAutomaticMode(boolean mode) {this.automaticMode = mode;notifyChange("AutomaticMode", mode);}
    public void setAuthority(int authority) {this.authority = authority;notifyChange("Authority", authority);}
    public void setOverrideSpeed(double speed) {this.overrideSpeed = speed;notifyChange("OverrideSpeed", speed);}
    public void setCommandSpeed(double speed) {this.commandSpeed = speed;notifyChange("CommandSpeed", speed);}
    public void setServiceBrake(boolean brake) {this.serviceBrake = brake;notifyChange("ServiceBrake", brake);}
    public void setEmergencyBrake(boolean brake) {this.emergencyBrake = brake;notifyChange("EmergencyBrake", brake);}
    public void setKi(double Ki) {this.Ki = Ki;notifyChange("Ki", Ki);}
    public void setKp(double Kp) {this.Kp = Kp;notifyChange("Kp", Kp);}
    public void setPower(double power) {this.power = power;notifyChange("Power", power);}
    public void setIntLights(boolean lights) {this.internalLights = lights;notifyChange("InternalLights", lights);}
    public void setExtLights(boolean lights) {this.externalLights = lights;notifyChange("ExternalLights", lights);}
    public void setLeftDoors(boolean doors) {this.leftDoors = doors;notifyChange("LeftDoors", doors);}
    public void setRightDoors(boolean doors) {this.rightDoors = doors;notifyChange("RightDoors", doors);}
    public void setTemperature(double temp) {this.temperature = temp;notifyChange("Temperature", temp);}
    public void setAnnouncements(boolean announcements) {this.announcements = announcements;notifyChange("Announcements", announcements);}
    public void setSignalFailure(boolean signalFailure) {this.signalFailure = signalFailure;notifyChange("SignalFailure", signalFailure);}
    public void setBrakeFailure(boolean brakeFailure) {this.brakeFailure = brakeFailure;notifyChange("BrakeFailure", brakeFailure);}
    public void setPowerFailure(boolean powerFailure) {this.powerFailure = powerFailure;notifyChange("PowerFailure", powerFailure);}
    public void setInTunnel(boolean tunnel){this.inTunnel = tunnel;notifyChange("InTunnel",tunnel);}
    public void setLeftPlatform(boolean platform){this.leftPlatform = platform; notifyChange("LeftPlatform",platform);}

    public void setRightPlatform(boolean platform){this.rightPlatform = platform; notifyChange("rightPlatform",platform);}

    //-----------------Getters-----------------
    public int getID() {
        return this.trainID;
    }


    public double getSpeed() {
        return this.currentSpeed;
    }
    public double getAcceleration() {
        return this.train.getAcceleration();
    }
    public double getPower() {
        return this.power;
    }
    public boolean getServiceBrake() {
        return this.serviceBrake;
    }
    public boolean getEmergencyBrake() {
        return this.emergencyBrake;
    }
    public double getCommandSpeed() {
        return this.commandSpeed;
    }
    public int getAuthority() {
        return this.authority;
    }
    public double getKi() {
        return this.Ki;
    }
    public double getKp() {
        return this.Kp;
    }
    public double getOverrideSpeed() {
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

    public double getMaxSpeed() {
        return this.maxSpeed;
    }
    public boolean getLeftDoors() {
        return this.leftDoors;
    }
    public boolean getRightDoors() {
        return this.rightDoors;
    }
    public double getTemperature() {
        return this.temperature;
    }

    public boolean getLeftPlatform(){return this.leftPlatform;}
    public boolean getRightPlatform(){return this.rightPlatform;}
    public boolean getInTunnel(){return this.inTunnel;}

    // Power Calculations
    // P = Fv;  Power = Force * velocity
    // F = ma;  Force = Mass * Accceleration
}