package Train_Model;

import Common.trainModel;
import Common.trackModel;

public class trainModelImpl implements trainModel{
    private int authority;
    private double commandSpeed;
    private double speed;
    private double acceleration;
    private double power;

    private double temperature;
    private boolean serviceBrake;
    private boolean emergencyBrake;
    private boolean announcement;
    private boolean lights;
    private boolean leftDoors;
    private boolean rightDoors;

    private trackModel track;

    public trainModelImpl(){
        this.authority = 0;
        this.commandSpeed = 0;
        this.speed = 0;
        this.acceleration = 0;
        this.power = 0;
        this.temperature = 0;
        this.serviceBrake = false;
        this.emergencyBrake = false;
        this.announcement = false;
        this.lights = false;
        this.leftDoors = false;
        this.rightDoors = false;
    }


    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
    }

    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
    }

    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
    }

    public void setLights(boolean lights) {
        this.lights = lights;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = true;
    }

    public void setTemperature(double temp) {
        this.temperature = temp;
    }

    public int getAuthority() {
        return this.authority;
    }

    public double getCommandSpeed() {
        return this.commandSpeed;
    }

    public double getSpeed() {
        return this.speed;
    }

    public double getAcceleration() {
        return this.acceleration;
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

    public double getTemperature() {
        return this.temperature;
    }

    public boolean getAnnouncement() {
        return this.announcement;
    }

    public boolean getLights() {
        return this.lights;
    }

    public boolean getLeftDoors() {
        return this.leftDoors;
    }

    public boolean getRightDoors() {
        return this.rightDoors;
    }

    public int readAuthority() {
        return track.getAuthority();
    }

    public double readCommandSpeed() {
        return track.getCommandedSpeed();
    }

    public void readBeacon() {

    }

}
