package trainModel.NullObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainModel.TrainModelImpl;

public final class NullTrain extends TrainModelImpl {

    private static final Logger logger = LoggerFactory.getLogger(NullTrain.class);
    
    public static final NullTrain INSTANCE = new NullTrain();
    
    private NullTrain() {
        super();
    }

    public static NullTrain getInstance() {
        return INSTANCE;
    }

    @Override
    public void setAuthority(int authority) {
        logger.warn("NullTrain Authority {}", authority);
    }
    
    @Override
    public void setCommandSpeed(double commandSpeed) {
        logger.warn("NullTrain Command Speed to {}", commandSpeed);
    }

    
    @Override
    public void setPower(double power) {
        logger.warn("NullTrain Power to {}", power);
    }

    
    @Override
    public void setServiceBrake(boolean serviceBrake) {
        logger.warn("NullTrain Service brake to {}", serviceBrake);
    }
    
    @Override
    public void setEmergencyBrake(boolean emergencyBrake) {
        logger.warn("NullTrain Emergency brake to {}", emergencyBrake);
    }
    @Override
    public void setBrakeFailure(boolean brakeFailure) {
        logger.warn("NullTrain Brake failure to {}", brakeFailure);
    }
    
    @Override
    public void setPowerFailure(boolean powerFailure) {
        logger.warn("NullTrain Power failure to {}", powerFailure);
    }
    
    @Override
    public void setSignalFailure(boolean signalFailure) {
        logger.warn("NullTrain Signal failure to {}", signalFailure);
    }

    @Override
    public int getTrainNumber() {
        logger.warn("NullTrain ID requested");
        return -1;
    }


    @Override
    public void setValue(String propertyName, Object newValue) {
        logger.warn("NullTrain setProperty called with " + propertyName + " and " + newValue);
    }
}
