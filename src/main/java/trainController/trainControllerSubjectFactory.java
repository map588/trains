package trainController;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectFactory;

public class trainControllerSubjectFactory extends SubjectFactory<trainControllerSubject> {
    private trainControllerSubjectFactory() {
        super();
    }
    private static final trainControllerSubjectFactory INSTANCE = new trainControllerSubjectFactory();
    public static trainControllerSubjectFactory getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<Integer, trainControllerSubject> getSubjects() {
        return super.getSubjects();
    }
    public void registerSubject(int ID, trainControllerSubject subject) {
        super.registerSubject(ID, subject);
    }

}
gut