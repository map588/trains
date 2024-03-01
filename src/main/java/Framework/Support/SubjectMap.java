package Framework.Support;

import trainController.trainControllerSubjectMap;

public abstract class SubjectMap<K, S> implements AbstractSubjectMap<K, S>{

    protected final ObservableHashMap<K, S> subjects = new ObservableHashMap<>();

    public void registerSubject(K ID, S subject) {
        subjects.put(ID, subject);
    }

    public S getSubject(K ID) {
        return subjects.get(ID);
    }

    public void removeSubject(K ID) {
        subjects.remove(ID);
    }

    public ObservableHashMap<K, S> getSubjects() {
        return subjects;
    }
}
