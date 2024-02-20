package trainController;

import Framework.SubjectFactory;

import java.util.HashMap;

public class trainControllerSubjectFactory implements SubjectFactory<trainControllerSubject> {


    public ObservableHashMap<Integer, trainControllerSubject> subjectMap;

    public trainControllerSubjectFactory() {
        subjectMap = new ObservableHashMap<>((key) -> {
                System.out.println("subjectMap has changed. Key: " + key);
        });
    }

    public trainControllerSubject getSubject(int ID) {
        if(subjectMap.containsKey(ID)) {
            return subjectMap.get(ID);
        }
        else {
            throw new IllegalArgumentException("Subject not found");
        }
    }//subject may or may not be deallocated
    public void deleteSubject(int ID) {
        subjectMap.remove(ID);
    }

    public int getNumSubjects() {
        return subjectMap.size();
    }

}