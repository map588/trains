package CTCOffice;

import Framework.Support.SubjectFactory;
import Framework.Support.ObservableHashMap;

public class CTCBlockSubjectFactory extends SubjectFactory<CTCBlockSubject>{
    private static final CTCBlockSubjectFactory INSTANCE = new CTCBlockSubjectFactory();
    private ObservableHashMap<Integer, CTCBlockSubject> subjects = new ObservableHashMap<>();
    private CTCBlockSubjectFactory() {
    }
    public static CTCBlockSubjectFactory getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<Integer, CTCBlockSubject> getSubjects() {
        return subjects;
    }
    public void registerSubject(int ID, CTCBlockSubject subject) {
        subjects.put(ID, subject);
    }
}
