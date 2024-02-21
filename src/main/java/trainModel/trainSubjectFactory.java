package trainModel;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectFactory;

public class trainSubjectFactory extends SubjectFactory<trainModelSubject> {
    // Singleton
    private trainSubjectFactory() {
        super();
    }
    private static final trainSubjectFactory INSTANCE = new trainSubjectFactory();

    public static trainSubjectFactory getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<Integer, trainModelSubject> getSubjects() {
        return super.getSubjects();
    }
    public void registerSubject(int ID, trainModelSubject subject) {
        super.registerSubject(ID, subject);
    }
}