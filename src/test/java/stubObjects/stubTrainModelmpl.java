package stubObjects;

import Common.trackModel;
import Common.trainModel;

public class stubTrainModelmpl implements trainModel{
    private int authority;
    private double commandSpeed;
    private double speed;
    private double acceleration;
    private double power;
    private boolean serviceBrake;
    private boolean emergencyBrake;
    private double temperature;
    private boolean announcement;
    private boolean lights;
    private boolean leftDoors;
    private boolean rightDoors;

    private trackModel track;

    public stubTrainModelmpl() {
        this.authority = 0;
        this.commandSpeed = 0;
        this.speed = 0;
        this.acceleration = 0;
        this.power = 0;
        this.serviceBrake = false;
        this.emergencyBrake = false;
        this.temperature = 0;
        this.announcement = false;
        this.lights = false;
        this.leftDoors = false;
        this.rightDoors = false;
    }


    public void setEmergencyBrake(boolean brake) {
        this.emergencyBrake = brake;
        System.out.println("Set Emergency Brake: " + brake);
    }

    public void setServiceBrake(boolean brake) {
        this.serviceBrake = brake;
        System.out.println("Set Service Brake: " + brake);
    }

    public void setPower(double power) {
        this.power = power;
        System.out.println("Set Power: " + power);
    }

    public void setLeftDoors(boolean doors) {
        this.leftDoors = doors;
        System.out.println("Set Left Doors: " + doors);
    }

    public void setRightDoors(boolean doors) {
        this.rightDoors = doors;
        System.out.println("Set Right Doors: " + doors);
    }

    public void setLights(boolean lights) {
        this.lights = lights;
        System.out.println("Set Lights: " + lights);
    }

    public void setAnnouncement(String announcement) {
        this.announcement = true;
        System.out.println("Set Announcement: " + announcement);
    }

    public void setTemperature(double temp) {
        this.temperature = temp;
        System.out.println("Set Temperature: " + temp);
    }

    public int getAuthority() {
        System.out.println("Got Authority: " + authority);
        return this.authority;
    }

    public double getCommandSpeed() {
        System.out.println("Got Command Speed: " + commandSpeed);
        return this.commandSpeed;
    }

    public double getSpeed() {
        System.out.println("Got Speed: " + speed);
        return this.speed;
    }

    public double getAcceleration() {
        System.out.println("Got Acceleration: " + acceleration);
        return this.acceleration;
    }

    public double getPower() {
        System.out.println("Got Power: " + power);
        return this.power;
    }

    public boolean getServiceBrake() {
        System.out.println("Got Service Brake: " + serviceBrake);
        return this.serviceBrake;
    }

    public boolean getEmergencyBrake() {
        System.out.println("Got Emergency Brake: " + emergencyBrake);
        return this.emergencyBrake;
    }

    public double getTemperature() {
        System.out.println("Got Temperature: " + temperature);
        return this.temperature;
    }

    public boolean getAnnouncement() {
        System.out.println("Got Announcement: " + announcement);
        return this.announcement;
    }

    public boolean getLights() {
        System.out.println("Got Lights: " + lights);
        return this.lights;
    }

    public boolean getLeftDoors() {
        System.out.println("Got Left Doors: " + leftDoors);
        return this.leftDoors;
    }

    public boolean getRightDoors() {
        System.out.println("Got Right Doors: " + rightDoors);
        return this.rightDoors;
    }

    public int readAuthority() {
        int auth = track.getTrainAuthority(0);
        System.out.println("Read Authority: " + auth);
        return auth;
    }

    public double readCommandSpeed() {
        return 0;
    }

    public int readAuthority(int trainID) {
        int auth = track.getTrainAuthority(trainID);
        System.out.println("Read Authority: " + auth);
        return auth;
    }

    public double readCommandSpeed(int trainID) {
        double comSpeed = track.getCommandedSpeed(trainID);
        System.out.println("Read Command Speed: " + comSpeed);
        return comSpeed;
    }

    public void readBeacon() {

    }
}
