package Common;



public interface trainController {
    public void setCommandSpeed(double speed);
    public void setAuthority(double authority);
    public void assignTrainModel(trainModel train);
    public void setAutomaticMode(boolean mode);
    public void setOverrideSpeed(double speed);


//    void setTemperature(double temp);
//    void setAnnouncement(String announcement);
//    void setLights(boolean lights);
//    void setLeftDoors(boolean doors);
//    void setRightDoors(boolean doors);
    void setServiceBrake(boolean brake);
    void setEmergencyBrake(boolean brake);


    int getTrainID();
}