package trainModel;

import Framework.Support.SubjectMap;

public class TrainModelSubjectMap extends SubjectMap<Integer, TrainModelSubject> {

    private static final TrainModelSubjectMap INSTANCE = new TrainModelSubjectMap();

    private TrainModelSubjectMap() {
        super();
    }

    public static TrainModelSubjectMap getInstance() {
        return INSTANCE;
    }
}
