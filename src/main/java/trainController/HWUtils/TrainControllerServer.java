package trainController.HWUtils;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class TrainControllerServer {
  public static void main(String[] args) {
    try {
      // TrainControllerRemoteImpl trainControllerRemote = new TrainControllerRemoteImpl(train,
      // trainID);
      Registry registry = LocateRegistry.createRegistry(1099);
      // registry.bind("TrainController", trainControllerRemote);
      System.out.println("Train Controller server started.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
