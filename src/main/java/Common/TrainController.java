package Common;


import Framework.Support.GUIModifiable;
import Utilities.Records.Beacon;
import trainController.TrainControllerSubject;

public interface TrainController extends GUIModifiable {

    //Called by the GUI
    void setAutomaticMode(boolean mode);

    //Called by the train model, which reads the track model
    //Currently it will be set in either blocks or meters
    void setAuthority(int authority);

    //-----Speed functions--------
    //Called by the GUI
    void setOverrideSpeed(double speed);
    //Called by the train based on information from the track model
    void setCommandSpeed(double speed);

    //-----Functions called by the controller function or the train controller UI
    void setServiceBrake(boolean brake);
    void setEmergencyBrake(boolean brake);


    //-----PID functions--------
    //Called by the Engineer GUI
    void setKi(double Ki);
    void setKp(double Kp);

    //----Non-vital setters called by GUI----
    void setIntLights(boolean intLights);
    void setExtLights(boolean extLights);
    void setLeftDoors(boolean leftDoors);
    void setRightDoors(boolean rightDoors);


    //The train cannot store its own ID, so the train controller must store it
    int getID();

    double getSpeed();

    double getAcceleration();
    double getPower();
    double getKi();
    double getKp();
    double getOverrideSpeed();
    double getSpeedLimit();

    boolean getServiceBrake();
    boolean getEmergencyBrake();
    boolean getAutomaticMode();

    boolean getExtLights();
    boolean getIntLights();
    boolean getLeftDoors();
    boolean getRightDoors();
    int getSamplingPeriod();
    double getTemperature();

    double getCommandSpeed();
    int getAuthority();

    void setTemperature(double newTemperature);

    TrainControllerSubject getSubject();

    boolean getAnnouncements();

    boolean getSignalFailure();

    boolean getBrakeFailure();

    boolean getPowerFailure();

    // Station Block Info
    boolean getLeftPlatform();
    boolean getRightPlatform();
    String getStationName();

    void setLeftPlatform(boolean leftPlatform);
    void setRightPlatform(boolean rightPlatform);

    // Extra Authority Info
    boolean getInTunnel();
    void setInTunnel(boolean inTunnel);

    double calculatePower(double currentVelocity);

    double getGrade();
    void updateBeacon(Beacon beacon);
}
