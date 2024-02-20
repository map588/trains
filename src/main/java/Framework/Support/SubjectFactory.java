package Framework.Support;

import trainController.ObservableHashMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubjectFactory<T> {
    private static final Map<Class<?>, SubjectFactory<?>> INSTANCES = new ConcurrentHashMap<>();
    private final ObservableHashMap<Integer, T> subjects = new ObservableHashMap<>();

    public SubjectFactory() {}

    public static <T> SubjectFactory<T> getInstance(Class<T> type) {
        return (SubjectFactory<T>) INSTANCES.computeIfAbsent(type, k -> new SubjectFactory<>());
    }

    public ObservableHashMap<Integer, T> getSubjects() {
        return subjects;
    }

    public void registerSubject(int ID, T subject) {
        subjects.put(ID, subject);
    }
}