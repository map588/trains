package CTCOffice;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectFactory;

public class ScheduleSubjectFactory extends SubjectFactory<SingleTrainScheduleSubject> {
    private static final ScheduleSubjectFactory INSTANCE = new ScheduleSubjectFactory();
    private ObservableHashMap<Integer, SingleTrainScheduleSubject> subjects = new ObservableHashMap<>();
    private ScheduleSubjectFactory() {}
    public static ScheduleSubjectFactory getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<Integer, SingleTrainScheduleSubject> getSubjects() {
        return subjects;
    }

    public void registerSubject(int ID, SingleTrainScheduleSubject subject) {
        subjects.put(ID, subject);
    }
}
