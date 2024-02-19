package trainModel;

import Common.SubjectFactory;

import java.util.HashMap;
import java.util.Map;

public class trainSubjectFactory implements SubjectFactory<trainModelSubject> {

    Map<Integer, trainModelSubject> subjects = new HashMap<Integer, trainModelSubject>();

    public trainSubjectFactory() {}

    public trainModelSubject getSubject(int ID) {
        subjects.getOrDefault(ID, new trainModelSubject(ID));
        return subjects.get(ID);
    }

    public void addSubject(trainModelSubject subject) {
        subjects.put(subject.getID(), subject);
    }

    public void deleteSubject(int ID) {

    }

    public int getNumSubjects() {
        return 0;
    }
}
