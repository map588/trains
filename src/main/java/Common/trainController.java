package Common;



public interface trainController {

    //Called at train initialization
    void assignTrainModel(trainModel train);

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



    //The train cannot store its own ID, so the train controller must store it
    int getTrainNumber();

    double getSpeed();
    double getAcceleration();
    double getPower();
    boolean getServiceBrake();
    boolean getEmergencyBrake();
    double getCommandSpeed();
    int getAuthority();
    double getKi();
    double getKp();
    double getOverrideSpeed();
    boolean getAutomaticMode();
}
