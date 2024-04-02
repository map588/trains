package CTCOffice.ScheduleInfo;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectMap;

public class ScheduleLibrary extends SubjectMap<String, ScheduleFileSubject> {
    private static final ScheduleLibrary INSTANCE = new ScheduleLibrary();
    private ScheduleLibrary() {
        super();
    }
    public static ScheduleLibrary getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<String, ScheduleFileSubject> getSubjects() {
        return super.getSubjects();
    }
}
