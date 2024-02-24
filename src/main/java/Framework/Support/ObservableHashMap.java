package Framework.Support;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ObservableHashMap<K, V> extends HashMap<K, V> {
    private final Set<MapListener<K, V>> listeners = ConcurrentHashMap.newKeySet();

    public interface MapListener<K, V> {
        void onAdded(K key, V value);
        void onRemoved(K key, V value);
        void onUpdated(K key, V oldValue, V newValue);
    }

    public void addChangeListener(MapListener<K, V> listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(MapListener<K, V> listener) {
        listeners.remove(listener);
    }

    @Override
    public V put(K key, V value) {
        V oldValue = super.put(key, value);
        if (oldValue == null) {
            notifyListenersAdded(key, value);
        } else {
            notifyListenersUpdated(key, oldValue, value);
        }
        return oldValue;
    }

    public V remove (Object key) {
        V oldValue = super.remove(key);
        if (oldValue != null) {
            notifyListenersRemoved((K) key, oldValue);
        }
        return oldValue;
    }

    private void notifyListenersAdded(K key, V value) {
        listeners.forEach(listener -> listener.onAdded(key, value));
    }

    private void notifyListenersRemoved(K key, V value) {
        listeners.forEach(listener -> listener.onRemoved(key, value));
    }

    private void notifyListenersUpdated(K key, V oldValue, V newValue) {
        listeners.forEach(listener -> listener.onUpdated(key, oldValue, newValue));
    }
}
