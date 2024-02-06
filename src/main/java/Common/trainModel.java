package Common;

public interface trainModel {

    //----Vital Setter Signals----
    void setEmergencyBrake(boolean brake);
    void setServiceBrake(boolean brake);
    void setPower(double power);

    //Non-Vital Signals
    void setLeftDoors(boolean doors);
    void setRightDoors(boolean doors);
    void setLights(boolean lights);
    void setAnnouncement(String announcement);
    void setTemperature(double temp);


    //Vital Getter Signals
    int     getAuthority();
    double  getCommandSpeed();
    double  getSpeed();
    double  getAcceleration();
    double  getPower();
    boolean getServiceBrake();
    boolean getEmergencyBrake();


    //Non-Vital Getter Signals
    double  getTemperature();
    boolean getAnnouncement();
    boolean getLights();
    boolean getLeftDoors();
    boolean getRightDoors();


    //Vital Signals from Track Model
    int readAuthority();
    double readCommandSpeed();
    void readBeacon();



}
