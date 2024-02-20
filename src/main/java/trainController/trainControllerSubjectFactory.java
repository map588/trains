package trainController;

import Framework.SubjectFactory;

class trainControllerSubjectFactory extends SubjectFactory<trainControllerSubject> {

    // Singleton
    private static final trainControllerSubjectFactory INSTANCE = new trainControllerSubjectFactory();

    // ObservableHashMap to store subjects
    private ObservableHashMap<Integer, trainControllerSubject> subjects = new ObservableHashMap<>();

    // Private constructor
    private trainControllerSubjectFactory() {}

    // Singleton getter
    public static trainControllerSubjectFactory getInstance() {
        return INSTANCE;
    }

    // Getter for subjects
    public ObservableHashMap<Integer, trainControllerSubject> getSubjects() {
        return subjects;
    }

    // Register a subject
    public void registerSubject(int ID, trainControllerSubject subject) {
        subjects.put(ID, subject);
    }
}
