package CTCOffice;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectFactory;

public class ScheduleSubjectFactory extends SubjectFactory<TrainScheduleSubject> {
    private static final ScheduleSubjectFactory INSTANCE = new ScheduleSubjectFactory();
    private ObservableHashMap<Integer, TrainScheduleSubject> subjects = new ObservableHashMap<>();
    private ScheduleSubjectFactory() {}
    public static ScheduleSubjectFactory getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<Integer, TrainScheduleSubject> getSubjects() {
        return subjects;
    }
    public void registerSubject(int ID, TrainScheduleSubject subject) {
        subjects.put(ID, subject);
    }
}
