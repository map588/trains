package Common;


import Framework.Support.GUIModifiable;
import Utilities.Records.Beacon;
import trainController.TrainControllerSubject;

public interface TrainController extends GUIModifiable {

    //To clear the situation, all setters and getters are called from logic, not from the GUI
    //GUI sets values through the setVaalue function

    //Called by the train model, which reads the track model
    //Currently it will be set in either blocks or meters
    void setAuthority(int authority);

    //-----Speed functions--------
    //Called by the train based on information from the track model
    void setCommandSpeed(double speed);

    //-----Functions called by the controller function or the train controller UI
    void setEmergencyBrake(boolean brake);

    TrainControllerSubject getSubject();

    //The train cannot store its own ID, so the train controller must store it
    int getID();

    //Should only be for testing purposes. Train Model calls calculate power when it needs it.
    double getPower();
    double getKi();
    double getKp();
    double getOverrideSpeed();
    double getSpeedLimit();

    boolean getServiceBrake();
    boolean getEmergencyBrake();
    boolean getAutomaticMode();

    //Testing Functions
    boolean getExtLights();
    boolean getIntLights();
    boolean getLeftDoors();
    boolean getRightDoors();
    double getTemperature();


    double getCommandSpeed();
    int    getAuthority();

    void setTemperature(double newTemperature);


    boolean getAnnouncements();
    boolean getSignalFailure();
    boolean getBrakeFailure();
    boolean getPowerFailure();





    double calculatePower(double currentVelocity);

    double getGrade();
    void updateBeacon(Beacon beacon);
}
