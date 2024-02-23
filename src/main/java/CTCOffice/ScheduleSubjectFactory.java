package CTCOffice;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectFactory;

public class ScheduleSubjectFactory extends SubjectFactory<ScheduleSubject> {
    private static final ScheduleSubjectFactory INSTANCE = new ScheduleSubjectFactory();
    private ObservableHashMap<Integer, ScheduleSubject> subjects = new ObservableHashMap<>();
    private ScheduleSubjectFactory() {}
    public static ScheduleSubjectFactory getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<Integer, ScheduleSubject> getSubjects() {
        return subjects;
    }

    public void registerSubject(int ID, ScheduleSubject subject) {
        subjects.put(ID, subject);
    }
}
