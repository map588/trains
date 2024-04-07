package trainController.NullObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainController.ControllerProperty;
import trainController.TrainControllerImpl;
import trainModel.NullObjects.NullTrain;

public final class NullController extends TrainControllerImpl {

    private static final Logger logger = LoggerFactory.getLogger(NullController.class);
    public static final NullController INSTANCE = new NullController();

    private NullController() {
        super(NullTrain.getInstance(), -1);
    }

    public static NullController getInstance() {
        return INSTANCE;
    }

    @Override
    public void setValue(ControllerProperty property, Object value) {
        logger.warn("NullController setProperty called with " + property + " and " + value);
    }

    // Override other control methods similarly
}

