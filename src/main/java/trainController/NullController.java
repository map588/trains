package trainController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NullController extends TrainControllerImpl {

    private static final Logger logger = LoggerFactory.getLogger(NullController.class);

    private static final NullController INSTANCE = new NullController();

    private NullController() {
        super();
    }

    public static NullController getInstance() {
        return INSTANCE;
    }


    @Override
    public void setValue(ControllerProperty property, Object value) {
        logger.warn("NullController setProperty called with {} and {}", property, value);
    }

    @Override
    public void setPower(double power) {
        logger.warn("NullController setPower called with {}", power);
    }

    @Override

    public void setCurrentSpeed(double speed) {
        logger.warn("NullController setSpeed called with {}", speed);
    }

    @Override

    public void setAuthority(int authority) {
        logger.warn("NullController setAuthority called with {}", authority);
    }

    @Override
    public double calculatePower(double speed) {
        logger.warn("NullController calculatePower called with {}", speed);
        return 0;
    }

}

