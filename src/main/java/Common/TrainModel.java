package Common;

public interface TrainModel {

    //----Vital Setter Signals----
    void setEmergencyBrake(boolean brake);
    void setServiceBrake(boolean brake);
    void setPower(double power);
    void setCommandSpeed(double speed);
    void setActualSpeed(double speed);
    void setAuthority(int authority);
    void setNumCars(int numCars);
    void setNumPassengers(int numPassengers);
    void setCrewCount(int crewCount);
    void setGrade(double grade);
    void setAcceleration(double acceleration);

    //Murphy Signals
    void setBrakeFailure(boolean failure);
    void setPowerFailure(boolean failure);
    void setSignalFailure(boolean failure);

    //Non-Vital Signals
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
    void trainModelPhysics();

    void setValue(String propertyName, Object newValue);

    void setTimeDelta(int v);

    int getTimeDelta();

    double getGrade();

    int getCrewCount();

    int getNumPassengers();

    int getNumCars();
}
