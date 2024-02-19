package trainController;

import Framework.SubjectFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class controllerSubjectFactory implements SubjectFactory<trainControllerSubject> {

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

    //subject may or may not be deallocated
    public void deleteSubject(int ID) {
        subjectMap.remove(ID);
    }

    public int getNumSubjects() {
        ArrayList<Integer> keys = new ArrayList<Integer>(subjectMap.keySet());


        return subjectMap.size();
    }

}