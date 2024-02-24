package trainController;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectFactory;

public class trainControllerSubjectFactory extends SubjectFactory<trainControllerSubject> {

    private static final trainControllerSubjectFactory INSTANCE = new trainControllerSubjectFactory();

    private final ObservableHashMap<Integer, trainControllerSubject> subjects = new ObservableHashMap<>();

    private trainControllerSubjectFactory() {
        super();
    }

    public static trainControllerSubjectFactory getInstance() {
        return INSTANCE;
    }

    public ObservableHashMap<Integer, trainControllerSubject> getSubject() {
        return subjects;
    }

    public void registerSubject(Integer ID, trainControllerSubject subject) {
        subjects.put(ID, subject);
    }

}