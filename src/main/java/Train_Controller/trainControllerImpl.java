package Train_Controller;

import  Common.trainController;
import Common.trainModel;

public class trainControllerImpl implements trainController{
    private int authority;
    private double commandSpeed;
    private double speed;
    private double acceleration;
    private double power;
    private boolean serviceBrake;
    private boolean emergencyBrake;

    private trainModel train;
    private int trainID;

    public void setCommandSpeed(double power) {

    }

    public void setAuthority(double authority) {

    }

    public void assignTrainModel(trainModel train) {

    }

    public void setTemperature(double temp) {

    }

    public void setAnnouncement(String announcement) {

    }

    public void setLights(boolean lights) {

    }

    public void setLeftDoors(boolean doors) {

    }

    public void setRightDoors(boolean doors) {

    }

    public void setServiceBrake(boolean brake) {

    }

    public void setEmergencyBrake(boolean brake) {

    }

    public int getTrainID() {
        return this.trainID;
    }
}
