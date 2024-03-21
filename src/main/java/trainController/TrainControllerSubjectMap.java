package trainController;

import Framework.Support.ObservableHashMap;
import Framework.Support.SubjectMap;

public class TrainControllerSubjectMap extends SubjectMap<Integer, TrainControllerSubject> {

    private static final TrainControllerSubjectMap INSTANCE = new TrainControllerSubjectMap();

    private TrainControllerSubjectMap() {
        super();
    }

    public static TrainControllerSubjectMap getInstance() {
        return INSTANCE;
    }

    public ObservableHashMap<Integer, TrainControllerSubject> getSubjects() {
        return super.getSubjects();
    }
}
