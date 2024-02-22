package CTCOffice;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectFactory;

public class ScheduleSubjectFactory extends SubjectFactory<ScheduleSubject> {
    private static ScheduleSubjectFactory INSTANCE = new ScheduleSubjectFactory();
    private ObservableHashMap<Integer, ScheduleSubject> subjects = new ObservableHashMap<>();
    private ScheduleSubjectFactory() {}
    public static ScheduleSubjectFactory getInstance() {
        System.out.println("getting instance" + INSTANCE);
        return INSTANCE;
    }
    public ObservableHashMap<Integer, ScheduleSubject> getSubjects() {
        return subjects;
    }

    public void registerSubject(int ID, ScheduleSubject subject) {
        System.out.println("registering subject: " + ID);
        subjects.put(ID, subject);
    }
}
