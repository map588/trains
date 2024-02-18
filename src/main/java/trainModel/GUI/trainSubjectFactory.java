package trainModel.GUI;

import Common.subjectFactory;

import java.util.HashMap;
import java.util.Map;

public class trainSubjectFactory implements subjectFactory<trainModelSubject> {

    Map<Integer, trainModelSubject> subjects = new HashMap<Integer, trainModelSubject>();

    public trainSubjectFactory() {}

    public trainModelSubject getSubject(int ID) {
        subjects.getOrDefault(ID, new trainModelSubject(ID));
        return subjects.get(ID);
    }

    public void deleteSubject(int ID) {

    }

    public int getNumSubjects() {
        return 0;
    }
}
