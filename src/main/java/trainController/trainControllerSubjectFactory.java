package trainController;

import Framework.SubjectFactory;

import java.util.HashMap;
import java.util.Map;

public class trainControllerSubjectFactory implements SubjectFactory<trainControllerSubject> {

    protected static final Map<Integer, trainControllerSubject> subjectMap = new HashMap<Integer, trainControllerSubject>();

    public trainControllerSubjectFactory() {}

    //This is making a subject
    public trainControllerSubject getSubject(int ID) {
        if(subjectMap.containsKey(ID)) {
            return subjectMap.get(ID);
        }
        else {
            //Null trainControllerSubject
            return new trainControllerSubject(-1);
        }
    }
    public synchronized void addSubject(int ID, trainControllerSubject subject) {
        subjectMap.put(ID, subject);
    }
    //subject may or may not be deallocated
    public void deleteSubject(int ID) {
        subjectMap.remove(ID);
    }

    public int getNumSubjects() {
        return subjectMap.size();
    }

}