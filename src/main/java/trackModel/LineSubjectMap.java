package trackModel;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectMap;

public class LineSubjectMap extends SubjectMap<String, LineSubjectMap> {


    private static final LineSubjectMap INSTANCE = new LineSubjectMap();

    private LineSubjectMap() {
        super();
    }

    public static LineSubjectMap getInstance() {
        return INSTANCE;
    }

    public ObservableHashMap<String, LineSubjectMap> getSubjects() {
        return super.getSubjects();
    }

}
