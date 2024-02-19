package Common;


import Framework.Notifications;
import Framework.PropertyChangeListener;

public interface TrainController extends Notifications {

    //Called at train initialization
    void assignTrainModel(TrainModel train);

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
    double getMaxSpeed();

    boolean getServiceBrake();
    boolean getEmergencyBrake();
    boolean getAutomaticMode();

    boolean getExtLights();
    boolean getIntLights();
    boolean getLeftDoors();
    boolean getRightDoors();

    double getTemperature();

    double getCommandSpeed();
    int getAuthority();

    void setTemperature(double newTemperature);
}
