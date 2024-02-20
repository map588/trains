package trainController;

public class trainControllerSubjectFactory {

    private static final trainControllerSubjectFactory INSTANCE = new trainControllerSubjectFactory();
    private ObservableHashMap<Integer, trainControllerSubject> subjects = new ObservableHashMap<>();

    private trainControllerSubjectFactory() {}

    public static trainControllerSubjectFactory getInstance() {
        return INSTANCE;
    }

    public ObservableHashMap<Integer, trainControllerSubject> getSubjects() {
        return subjects;
    }

    public void deleteSubject(int ID) {
        if(subjects.containsKey(ID)) {
            subjects.remove(ID);
        } else {
            System.out.println("Attempted to delete a non-existent subject with ID: " + ID);
        }
    }

    public void registerSubject(int ID, trainControllerSubject subject){
        subjects.put(ID, subject);
    }
}
