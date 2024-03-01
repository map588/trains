package Framework.Support;

public interface AbstractSubjectMap<K, S> {

    void registerSubject(K ID, S subject);

    S getSubject(K ID);

    void removeSubject(K ID);

    ObservableHashMap<K, S> getSubjects();
}

