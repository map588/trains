package Common;

public interface TrainModel {

    //----Vital Setter Signals----
    void setEmergencyBrake(boolean brake);
    void setServiceBrake(boolean brake);
    void setPower(double power);
    void setNumCars(int numCars);
    void setNumPassengers(int numPassengers);

    //Murphy Signals
    void setBrakeFailure(boolean failure);
    void setPowerFailure(boolean failure);
    void setSignalFailure(boolean failure);

    //Non-Vital Signals
    void setTrainNumber(int number);
    void setLeftDoors(boolean doors);
    void setRightDoors(boolean doors);
    void setExtLights(boolean lights);
    void setIntLights(boolean lights);
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

    //Murphy Getter Signals
    boolean getBrakeFailure();
    boolean getPowerFailure();
    boolean getSignalFailure();


    //Non-Vital Getter Signals
    double  getTemperature();
    boolean getExtLights();
    boolean getIntLights();
    boolean getLeftDoors();
    boolean getRightDoors();

    //Vital Functions for simulating the train physics
    void calculateSpeed();
    void calculateAcceleration();
}
