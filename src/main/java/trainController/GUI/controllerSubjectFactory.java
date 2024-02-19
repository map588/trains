package trainController.GUI;

import Common.subjectFactory;

import java.util.HashMap;
import java.util.Map;

public class controllerSubjectFactory implements subjectFactory<trainControllerSubject>{

    private static final Map<Integer, trainControllerSubject> subjectMap = new HashMap<Integer, trainControllerSubject>();
    private static int currentID = 0;

    public controllerSubjectFactory() {}

    //This is really making a subject
    public trainControllerSubject getSubject(int ID) {
        return subjectMap.getOrDefault(ID, null);
    }

    public void deleteSubject(int ID) {
        if (subjectMap.containsKey(ID)) {
            subjectMap.remove(ID);
        }
    }

    public int getNumSubjects() {
        return subjectMap.size();
    }

}