package trainController;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectFactory;

public class trainControllerSubjectFactory extends SubjectFactory<trainControllerSubject> {

    private static final trainControllerSubjectFactory INSTANCE = new trainControllerSubjectFactory();

    private trainControllerSubjectFactory() {
        super();
    }

    public static trainControllerSubjectFactory getInstance() {
        return INSTANCE;
    }
}