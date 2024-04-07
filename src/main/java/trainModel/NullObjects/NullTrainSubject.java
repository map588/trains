package trainModel.NullObjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainModel.TrainModelSubject;

public final class NullTrainSubject extends TrainModelSubject {

    public static final NullTrainSubject INSTANCE = new NullTrainSubject();

    private static final Logger logger = LoggerFactory.getLogger(NullTrainSubject.class);

    private NullTrainSubject() {
        super(NullTrain.getInstance());
    }

    public static NullTrainSubject getInstance() {
        return INSTANCE;
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        logger.info("NullTrainSubject setProperty called with " + propertyName + " and " + newValue);
    }

}

