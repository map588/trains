package trainModel;

import Framework.Support.SubjectFactory;
import Framework.Support.ObservableHashMap;

public class trainSubjectFactory extends SubjectFactory<trainModelSubject> {
    // Singleton
    private static final trainSubjectFactory INSTANCE = new trainSubjectFactory();

    // ObservableHashMap to store subjects
    private ObservableHashMap<Integer, trainModelSubject> subjects = new ObservableHashMap<>();

    // Private constructor
    private trainSubjectFactory() {}

    // Singleton getter
    public static trainSubjectFactory getInstance() {
        return INSTANCE;
    }

    // Getter for subjects
    public ObservableHashMap<Integer, trainModelSubject> getSubjects() {
        return subjects;
    }

    // Register a subject
    public void registerSubject(int ID, trainModelSubject subject) {
        subjects.put(ID, subject);
    }
}