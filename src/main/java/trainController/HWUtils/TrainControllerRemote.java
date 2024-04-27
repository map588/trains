package trainController.HWUtils;

import Common.TrainModel;
import java.rmi.Remote;
import trainModel.Records.UpdatedTrainValues;

public interface TrainControllerRemote extends Remote {
  void setAuthority(int authority) throws java.rmi.RemoteException;

  void setCommandSpeed(double speed) throws java.rmi.RemoteException;

  void setEmergencyBrake(boolean brake) throws java.rmi.RemoteException;

  trainController.TrainControllerSubject getSubject() throws java.rmi.RemoteException;

  UpdatedTrainValues sendUpdatedTrainValues() throws java.rmi.RemoteException;

  double calculatePower(double currentVelocity) throws java.rmi.RemoteException;

  TrainModel getTrain() throws java.rmi.RemoteException;

  double getPower() throws java.rmi.RemoteException;

  double getSpeedLimit() throws java.rmi.RemoteException;

  boolean getServiceBrake() throws java.rmi.RemoteException;

  boolean getEmergencyBrake() throws java.rmi.RemoteException;

  boolean getExtLights() throws java.rmi.RemoteException;

  boolean getIntLights() throws java.rmi.RemoteException;

  boolean getLeftDoors() throws java.rmi.RemoteException;

  boolean getRightDoors() throws java.rmi.RemoteException;

  double getSetTemperature() throws java.rmi.RemoteException;

  double getCommandSpeed() throws java.rmi.RemoteException;

  int getAuthority() throws java.rmi.RemoteException;

  boolean getSignalFailure() throws java.rmi.RemoteException;

  boolean getBrakeFailure() throws java.rmi.RemoteException;

  boolean getPowerFailure() throws java.rmi.RemoteException;

  void delete() throws java.rmi.RemoteException;

  double getGrade() throws java.rmi.RemoteException;

  void checkFailures(double power);

  double getSpeed() throws java.rmi.RemoteException;

  void onBlock() throws java.rmi.RemoteException;
}
