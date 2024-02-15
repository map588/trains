package Common;

public interface trainModel {

    //----Vital Setter Signals----
    void setEmergencyBrake(boolean brake);
    void setServiceBrake(boolean brake);
    void setPower(double power);
    void setNumCars(int numCars);
    void setNumPassengers(int numPassengers);

    //Non-Vital Signals
    void setLeftDoors(boolean doors);
    void setRightDoors(boolean doors);
    void setLights(boolean lights);
    void setTemperature(double temp);


    //Vital Getter Signals
    int     getAuthority();
    int     getTrainNumber();
    double  getCommandSpeed();
    double  getSpeed();
    double  getAcceleration();
    double  getPower();
    boolean getServiceBrake();
    boolean getEmergencyBrake();
    double  getWeightKG();


    //Non-Vital Getter Signals
    double  getTemperature();
    boolean getLights();
    boolean getLeftDoors();
    boolean getRightDoors();


    //Vital Signals from Track Model
    int readAuthority();
    double readCommandSpeed();
    void readBeacon();

    //Vital Functions for simulating the train physics
    void calculateSpeed();
    void calculateAcceleration();


}
