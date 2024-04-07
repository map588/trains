package trainController.NullObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainController.ControllerProperty;
import trainController.TrainControllerSubject;

public final class NullControllerSubject extends TrainControllerSubject {

    public static final NullControllerSubject INSTANCE = new NullControllerSubject();

    private static final Logger logger = LoggerFactory.getLogger(NullControllerSubject.class);

    private NullControllerSubject() {
        super(NullController.getInstance());
    }

    public static NullControllerSubject getInstance() {
        return INSTANCE;
    }

    @Override
    public void setProperty(ControllerProperty propertyName, Object newValue){
        logger.info("NullControllerSubject setProperty called with " + propertyName + " and " + newValue);
    }

    // Override other subject methods as needed
}

