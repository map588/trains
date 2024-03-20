package Framework.Simulation;

import Common.TrackModel;
import trackModel.TrackModelImpl;

import java.util.concurrent.ExecutorCompletionService;

public class Main {

    private static final long TIMESTEP = 1000; // Timestep in milliseconds
    private static final Object timestepLock = new Object();

    private static TrackModelImpl trackModel;
    private static WaysideSystem waysideController;
    private static TrainSystem trainSystem;


    public static void main(String[] args) {

        while (true) {
            long startTime = System.currentTimeMillis();

            // Update modules
//            trackModel.update();
//            waysideController.update();
//            trainSystem.update();

            // Synchronize time
            synchronized (timestepLock) {
                long elapsedTime = System.currentTimeMillis() - startTime;
                long sleepTime = TIMESTEP - elapsedTime;
                if (sleepTime > 0) {
                    try {
                        timestepLock.wait(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }




    }
}
