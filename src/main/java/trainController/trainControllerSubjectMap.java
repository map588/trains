package trainController;

import Framework.Support.SubjectMap;
import Framework.Support.ObservableHashMap;

public class trainControllerSubjectMap extends SubjectMap<Integer, trainControllerSubject> {

    private static final trainControllerSubjectMap INSTANCE = new trainControllerSubjectMap();

    private trainControllerSubjectMap() {
        super();
    }

    public static trainControllerSubjectMap getInstance() {
        return INSTANCE;
    }

    public ObservableHashMap<Integer, trainControllerSubject> getSubjects() {
        return super.getSubjects();
    }
}
