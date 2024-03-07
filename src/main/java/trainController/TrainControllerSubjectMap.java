package trainController;

import Framework.Support.SubjectMap;
import Framework.Support.ObservableHashMap;

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
