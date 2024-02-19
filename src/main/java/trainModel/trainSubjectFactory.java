package trainModel;

import Common.SubjectFactory;
import Common.TrainModel;

import java.util.HashMap;
import java.util.Map;

public class trainSubjectFactory implements SubjectFactory<trainModelSubject> {

    protected static final Map<Integer, trainModelSubject> subjectMap = new HashMap<Integer, trainModelSubject>();

    public trainSubjectFactory() {}

    public trainModelSubject getSubject(int ID) {
        subjectMap.getOrDefault(ID, new trainModelSubject(new trainModelImpl(ID), ID));
        return subjectMap.get(ID);
    }

    public void deleteSubject(int ID) {

    }

    public int getNumSubjects() {
        return 0;
    }
}
