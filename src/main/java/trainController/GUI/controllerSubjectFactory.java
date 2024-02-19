package trainController.GUI;

import Common.subjectFactory;

import java.util.HashMap;
import java.util.Map;

public class controllerSubjectFactory implements subjectFactory<trainControllerSubject>{

    protected static final Map<Integer, trainControllerSubject> subjectMap = new HashMap<Integer, trainControllerSubject>();

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

    public void deleteSubject(int ID) {
        subjectMap.remove(ID);
    }

    public int getNumSubjects() {
        return subjectMap.size();
    }

}