package trainModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void setAcceleration(double acceleration) {
    }
    
    @Override
    public void setPower(double power) {
        logger.warn("NullTrain Power to {}", power);
    }
    
    @Override
    public void setGrade(double grade) {
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
    public void setSetTemperature(double setTemperature) {
    }

    @Override
    public void setLeftDoors(boolean leftDoors) {
    }

    @Override
    public void setRightDoors(boolean rightDoors) {
    }

    @Override
    public void setExtLights(boolean extLights) {
    }

    @Override
    public void setIntLights(boolean intLights) {
    }

    @Override
    public void setNumCars(int numCars) {
    }

    @Override
    public void setCrewCount(int crewCount) {
    }

    @Override
    public void setMass(double mass) {
    }

    @Override
    public void setDistanceTraveled(double distanceTraveled) {
    }

    @Override
    public void setLength(double length) {
    }

    @Override
    public int getTrainNumber() {
        logger.warn("NullTrain ID requested");
        return -1;
    }
}
