package trainController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ObservableHashMap<K, V> extends HashMap<K, V> {
    private Set<ChangeListener<K>> listeners = new HashSet<>();

    public interface ChangeListener<K> {
        void onChange(K key);
    }

    public void addChangeListener(ChangeListener<K> listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(ChangeListener<K> listener) {
        listeners.remove(listener);
    }

    @Override
    public V put(K key, V value) {
        V oldValue = super.put(key, value);
        notifyListeners(key);
        return oldValue;
    }

    @Override
    public V remove(Object key) {
        V oldValue = super.remove(key);
        notifyListeners((K) key);
        return oldValue;
    }

    private void notifyListeners(K key) {
        listeners.forEach(listener -> listener.onChange(key));
    }
}
