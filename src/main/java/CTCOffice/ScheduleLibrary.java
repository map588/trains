package CTCOffice;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectMap;

import java.util.HashMap;

public class ScheduleLibrary extends SubjectMap<String, FullScheduleFileSubject> {
    private static final ScheduleLibrary INSTANCE = new ScheduleLibrary();
    private ScheduleLibrary() {
        super();
    }
    public static ScheduleLibrary getInstance() {
        return INSTANCE;
    }
    public ObservableHashMap<String, FullScheduleFileSubject> getSubjects() {
        return super.getSubjects();
    }




}
