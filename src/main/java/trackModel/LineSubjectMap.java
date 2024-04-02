package trackModel;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectMap;
import Utilities.Enums.Lines;

public class LineSubjectMap extends SubjectMap<Lines, LineSubjectMap> {

    private static final LineSubjectMap INSTANCE = new LineSubjectMap();

    private LineSubjectMap() {
        super();
    }

    public static LineSubjectMap getInstance() {
        return INSTANCE;
    }

    public ObservableHashMap<Lines, LineSubjectMap> getSubjects() {
        return super.getSubjects();
    }

}
