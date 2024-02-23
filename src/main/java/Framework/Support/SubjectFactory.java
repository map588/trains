package Framework.Support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubjectFactory<T> {
    // Maintains instances of SubjectFactory for each type.
    private static final Map<Class<?>, SubjectFactory<?>> INSTANCES = new ConcurrentHashMap<>();

    // ObservableHashMap for managing subjects.
    private final ObservableHashMap<Integer, T> subjects = new ObservableHashMap<>();

    // Private constructor to prevent external instantiation.
    protected SubjectFactory() {}

    /**
     * Returns a singleton instance of SubjectFactory for the specified type.
     * Uses a safely suppressed unchecked cast because we control the insertion into INSTANCES.
     */
    @SuppressWarnings("unchecked")
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