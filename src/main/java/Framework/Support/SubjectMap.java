package Framework.Support;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SubjectMap<T> {
    // Maintains instances of SubjectFactory for each type.
    private static final Map<Class<?>, SubjectMap<?>> INSTANCES = new ConcurrentHashMap<>();

    // ObservableMap for managing subjects.
    private final ObservableMap<Integer, T> subjects = FXCollections.observableHashMap();

    // Private constructor to prevent external instantiation.
    protected SubjectMap() {}

    /**
     * Returns a singleton instance of SubjectMap for the specified type.
     * Uses a safely suppressed unchecked cast because we control the insertion into INSTANCES.
     */
    @SuppressWarnings("unchecked")
    public static <T> SubjectMap<T> getInstance(Class<T> type) {
        return (SubjectMap<T>) INSTANCES.computeIfAbsent(type, k -> new SubjectMap<>());
    }

    public ObservableMap<Integer, T> getSubjects() {
        return subjects;
    }

    public void registerSubject(int ID, T subject) {
        subjects.put(ID, subject);
        // Additional logic to react to changes can be implemented here if needed.
    }

    // Optionally, if you need to handle removals or other specific map operations in a specialized way
    public void removeSubject(int ID) {
        subjects.remove(ID);
        // Handle additional cleanup or notifications as needed.
    }
}
