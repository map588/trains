package trainController;

import Common.TrainController;
import Common.TrainModel;

public class TrainControllerFactory {
    private static final TrainControllerFactory INSTANCE = new TrainControllerFactory();

    public static boolean HWController = false;
    private static boolean trainControllerLock = false;

    private TrainControllerFactory() {
    }

    public static TrainControllerFactory getInstance() {
        return INSTANCE;
    }

    public static boolean getTrainLock() {
        return trainControllerLock;
    }

    public static void setTrainLock(boolean lock) {
        trainControllerLock = lock;
    }

    public TrainController createTrainController(TrainModel m, int id) {
        if(HWController && !trainControllerLock && id != -1) {
            HWController = false;
            trainControllerLock = true;
            return new TrainControllerHW(m, id);
        }else {
            return new TrainControllerImpl(m, id);
        }
    }
}
