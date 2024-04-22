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

    public TrainController createTrainController(TrainModel m, int id) {
        if(HWController && !trainControllerLock) {
            HWController = false;
            trainControllerLock = true;
            return new TrainControllerHW(m, id);
        }else {
            return new TrainControllerImpl(m, id);
        }
    }
}
