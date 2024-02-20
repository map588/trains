package trainController;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class ObservableHashMap<K, V> extends HashMap<K, V> {
    // Use a list to support multiple listeners
    private List<ChangeListener<K>> changeListeners = new ArrayList<>();

    public ObservableHashMap() {
        super();
    }

    // Method to add a change listener
    public void addChangeListener(ChangeListener<K> listener) {
        changeListeners.add(listener);
    }

    // Optionally, a method to remove a change listener
    public void removeChangeListener(ChangeListener<K> listener) {
        changeListeners.remove(listener);
    }

    @Override
    public V put(K key, V value) {
        V oldValue = super.put(key, value);
        notifyChangeListeners(key);
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        V oldValue = super.remove(key);
        notifyChangeListeners((K) key);
        return oldValue;
    }

    // Helper method to notify all registered listeners of a change
    private void notifyChangeListeners(K key) {
        for (ChangeListener<K> listener : changeListeners) {
            listener.onChange(key);
        }
    }

    public interface ChangeListener<K> {
        void onChange(K key);
    }
}
