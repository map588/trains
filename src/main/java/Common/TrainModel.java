package Common;

import Utilities.Enums.Direction;
import Utilities.Records.Beacon;
import Utilities.Records.UpdatedTrainValues;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface  TrainModel {

    void delete();
    //----Vital Setter Signals----
    void setEmergencyBrake(boolean brake);
    void setServiceBrake(boolean brake);
    void setPower(double power);
    void setCommandSpeed(double speed);

    void setAuthority(int authority);
    void setNumCars(int numCars);
    void setNumPassengers(int numPassengers);
    void setCrewCount(int crewCount);
    void setGrade(double grade);
    void setDirection(Direction direction);
    void changeDirection();
    void passBeacon(Beacon beacon);


    //Murphy Signals
    void setBrakeFailure(boolean failure);
    void setPowerFailure(boolean failure);
    void setSignalFailure(boolean failure);

    //Non-Vital Signals
    void setLeftDoors(boolean doors);
    void setRightDoors(boolean doors);
    void setExtLights(boolean lights);
    void setIntLights(boolean lights);
    void setSetTemperature(double temp);
    void setAnnouncement(String announcement);



    //Vital Getter Signals
    int     getAuthority();
    int     getTrainNumber();
    double  getCommandSpeed();
    double  getSpeed();
    double  getAcceleration();
    double  getPower();
    boolean getServiceBrake();
    boolean getEmergencyBrake();
    double  getMass();

    Direction getDirection();

    //Murphy Getter Signals
    boolean getBrakeFailure();
    boolean getPowerFailure();
    boolean getSignalFailure();


    //Non-Vital Getter Signals
    double  getRealTemperature();
    boolean getExtLights();
    boolean getIntLights();
    boolean getLeftDoors();
    boolean getRightDoors();
    double getlength();
    int getPassengerCount();
    int getCrewCount();
    double getGrade();
    int getNumCars();




    //Vital Functions for simulating the train physics
    void setValue(String propertyName, Object newValue);
    void changeTimeDelta(int v);
    double getDistanceTraveled();



    TrainController getController();

    void trainModelTimeStep(Future<UpdatedTrainValues> updatedTrainValuesFuture) throws ExecutionException, InterruptedException;
    void trainModelPhysics() throws ExecutionException, InterruptedException;
}
