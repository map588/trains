package trainModel;

import Framework.Support.SubjectMap;
import Framework.Support.ObservableHashMap;
import trainModel.trainModelSubject;

public class trainModelSubjectMap extends SubjectMap<Integer, trainModelSubject> {

    private static final trainModelSubjectMap INSTANCE = new trainModelSubjectMap();

    private trainModelSubjectMap() {
        super();
    }

    public static trainModelSubjectMap getInstance() {
        return INSTANCE;
    }
}
