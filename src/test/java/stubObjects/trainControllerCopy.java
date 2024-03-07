package stubObjects;

import trainController.TrainControllerImpl;

public class trainControllerCopy {
    public int trainID;


    public double commandSpeed = 0.0, currentSpeed = 0.0, overrideSpeed = 0.0,
            speedLimit = 0.0, Ki = 1.0, Kp = 1.0, power = 0.0, grade = 0.0,
            temperature = 0.0, rollingError = 0.0, prevError = 0.0, error = 0.0;

    public int samplingPeriod = 10, authority = 0;

    public boolean serviceBrake = false, emergencyBrake = false, automaticMode = false,
            internalLights = false, externalLights = false, leftDoors = false,
            rightDoors = false, announcements = false, signalFailure = false,
            brakeFailure = false, powerFailure = false, leftPlatform = false,
            rightPlatform = false, inTunnel = false;

    public String nextStationName;

    private final TrainControllerImpl controller;

    public trainControllerCopy(TrainControllerImpl controller) {
        this.controller = controller;
        updateVars();
    }

    public void updateVars(){
        this.commandSpeed = controller.getCommandSpeed();
        this.currentSpeed = controller.getSpeed();
        this.overrideSpeed = controller.getOverrideSpeed();
        this.speedLimit = controller.getSpeedLimit();
        this.Ki = controller.getKi();
        this.Kp = controller.getKp();
        this.power = controller.getPower();
        this.grade = controller.getGrade();
        this.temperature = controller.getTemperature();
        this.samplingPeriod = controller.getSamplingPeriod();
        this.authority = controller.getAuthority();
        this.serviceBrake = controller.getServiceBrake();
        this.emergencyBrake = controller.getEmergencyBrake();
        this.automaticMode = controller.getAutomaticMode();
        this.internalLights = controller.getIntLights();
        this.externalLights = controller.getExtLights();
        this.leftDoors = controller.getLeftDoors();
        this.rightDoors = controller.getRightDoors();
        this.announcements = controller.getAnnouncements();
        this.signalFailure = controller.getSignalFailure();
        this.brakeFailure = controller.getBrakeFailure();
        this.powerFailure = controller.getPowerFailure();
        this.leftPlatform = controller.getLeftPlatform();
        this.rightPlatform = controller.getRightPlatform();
        this.inTunnel = controller.getInTunnel();
        this.nextStationName = controller.getStationName();
    }
}
