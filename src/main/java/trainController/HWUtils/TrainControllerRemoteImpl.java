package trainController.HWUtils;

import Common.TrainModel;
import Utilities.Records.Beacon;
import trainController.TrainControllerImpl;
import trainController.TrainControllerSubject;
import trainModel.Records.UpdatedTrainValues;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * This class is the remote implementation of the TrainControllerRemote interface.
 * It is used to communicate with the TrainController Server, which is hosted by
 * the remote device.  From this applications perspective, the server is the remote device.
 */

public class TrainControllerRemoteImpl extends UnicastRemoteObject implements TrainControllerRemote {
    private TrainControllerImpl trainControllerImpl;

    public TrainControllerRemoteImpl(TrainModel train, int trainID) throws RemoteException {
        trainControllerImpl = new TrainControllerImpl(train, trainID);
    }

    
    public void setAuthority(int authority) {
        trainControllerImpl.setAuthority(authority);
    }

    
    public void setCommandSpeed(double speed) {
        trainControllerImpl.setCommandSpeed(speed);
    }

    
    public void setCurrentTemperature(double temp) {
        trainControllerImpl.setCurrentTemperature(temp);
    }

    
    public void updateBeacon(Beacon beacon) {
        trainControllerImpl.updateBeacon(beacon);
    }

    
    public void setEmergencyBrake(boolean brake) {
        trainControllerImpl.setEmergencyBrake(brake);
    }

    
    public void setPassengerEBrake() {
        trainControllerImpl.setPassengerEBrake();
    }

    
    public TrainControllerSubject getSubject() {
        return trainControllerImpl.getSubject();
    }

    
    public int getID() {
        return trainControllerImpl.getID();
    }

    
    public double getPower() {
        return trainControllerImpl.getPower();
    }

    
    public double getOverrideSpeed() {
        return trainControllerImpl.getOverrideSpeed();
    }

    
    public double getSpeedLimit() {
        return trainControllerImpl.getSpeedLimit();
    }

    
    public boolean getServiceBrake() {
        return trainControllerImpl.getServiceBrake();
    }

    
    public boolean getEmergencyBrake() {
        return trainControllerImpl.getEmergencyBrake();
    }

    
    public boolean getAutomaticMode() {
        return trainControllerImpl.getAutomaticMode();
    }

    
    public boolean getExtLights() {
        return trainControllerImpl.getExtLights();
    }

    
    public boolean getIntLights() {
        return trainControllerImpl.getIntLights();
    }

    
    public boolean getLeftDoors() {
        return trainControllerImpl.getLeftDoors();
    }

    
    public boolean getRightDoors() {
        return trainControllerImpl.getRightDoors();
    }

    
    public double getSetTemperature() {
        return trainControllerImpl.getSetTemperature();
    }

    
    public double getCurrentTemperature() {
        return trainControllerImpl.getCurrentTemperature();
    }

    
    public double getCommandSpeed() {
        return trainControllerImpl.getCommandSpeed();
    }

    
    public int getAuthority() {
        return trainControllerImpl.getAuthority();
    }

    
    public void setSetTemperature(double newTemperature) {
        trainControllerImpl.setSetTemperature(newTemperature);
    }

    
    public boolean getAnnouncements() {
        return trainControllerImpl.getAnnouncements();
    }

    
    public boolean getSignalFailure() {
        return trainControllerImpl.getSignalFailure();
    }

    
    public boolean getBrakeFailure() {
        return trainControllerImpl.getBrakeFailure();
    }

    
    public boolean getPowerFailure() {
        return trainControllerImpl.getPowerFailure();
    }

    
    public void delete() {

    }


    
    public double calculatePower(double currentVelocity) {
        return 0;
    }

    
    public double getGrade() {
        return 0;
    }

    
    public UpdatedTrainValues sendUpdatedTrainValues() {
        return null;
    }

    
    public TrainModel getTrain() {
        return null;
    }

    
    public void checkFailures(double power) {

    }

    
    public double getSpeed() {
        return 0;
    }

    
    public Beacon getBeacon() {
        return null;
    }

    
    public void onBlock() {

    }

    
    public void setValue(Enum<?> propertyName, Object newValue) {

    }

    // Implement other methods from the TrainControllerRemote interface
}