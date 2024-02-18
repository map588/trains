package trainController.GUI;

import Common.subjectFactory;

import java.util.HashMap;
import java.util.Map;

public class controllerSubjectFactory implements subjectFactory<trainControllerSubject>{

    private static final Map<Integer, trainControllerSubject> subjectMap = new HashMap<Integer, trainControllerSubject>();

    public controllerSubjectFactory() {}

    //This is really making a subject
    public trainControllerSubject getSubject(int ID) {
        if(subjectMap.containsKey(ID)) {
            return subjectMap.get(ID);
        }
        else {
            //Null trainControllerSubject
            return new trainControllerSubject();
        }
    }

    public void addSubject(trainControllerSubject subject) {
        subjectMap.put(subject.getTrainNumber(),subject);
    }

    public void deleteSubject(int ID) {
        subjectMap.remove(ID);
    }

    public int getNumSubjects() {
        return subjectMap.size();
    }

}