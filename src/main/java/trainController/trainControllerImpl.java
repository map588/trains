package trainController;

import Common.TrainController;
import Common.TrainModel;
import Framework.PropertyChangeListener;

import java.util.ArrayList;
import java.util.List;


public class trainControllerImpl implements TrainController {
    private int authority;
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

    private double temperature;

    private int trainID;
    private TrainModel train;

    private final trainControllerSubject subject;
    private final List<PropertyChangeListener> listeners = new ArrayList<>();

    public trainControllerImpl(int trainID) {
        this.trainID = trainID;
        this.subject = new trainControllerSubject(this);
    }

    public trainControllerImpl() {
        this.subject = new trainControllerSubject(this);
    }

    public void addChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

     protected void notifyChange(String propertyName, Object newValue) {
        listeners.forEach(listener -> listener.onPropertyChange(propertyName, newValue));
    }

    //-----------------Setters-----------------
    public void assignTrainModel(TrainModel train) {
        this.train = train;
        notifyChange("CommandSpeed", this.commandSpeed);
        notifyChange("CurrentSpeed", this.currentSpeed);
        notifyChange("AutomaticMode", this.automaticMode);
    }

    public void setAutomaticMode(boolean mode) {
        this.automaticMode = mode;
        notifyChange("AutomaticMode", mode);
    }

    public void setAuthority(int authority) {
        this.authority = authority;
        notifyChange("Authority", authority);
    }

    public void setOverrideSpeed(double speed) {
        this.overrideSpeed = speed;
        notifyChange("OverrideSpeed", speed);
    }

    public void setCommandSpeed(double speed) {
        this.commandSpeed = speed;
        notifyChange("CommandSpeed", speed);
    }

    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        notifyChange("ServiceBrake", brake);
    }

    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        notifyChange("EmergencyBrake", brake);
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
        notifyChange("Power", power);
    }

    public void setIntLights(boolean lights) {
        this.internalLights = lights;
        notifyChange("InternalLights", lights);
    }

    public void setExtLights(boolean lights) {
        this.externalLights = lights;
        notifyChange("ExternalLights", lights);
    }

    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        notifyChange("LeftDoors", doors);
    }

    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        notifyChange("RightDoors", doors);
    }

    public void setTemperature(double temp) {
        this.temperature = temp;
        notifyChange("Temperature", temp);
    }


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

}