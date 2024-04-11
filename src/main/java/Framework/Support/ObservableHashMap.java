package Framework.Support;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ObservableHashMap<K, V> extends ConcurrentHashMap<K, V> {
    private final Set<MapListener<K, V>> listeners = ConcurrentHashMap.newKeySet();

    public interface MapListener<K, V> {
        default void onAdded(K key, V value) {
        }
        default void onRemoved(K key, V value){
        }
        default void onUpdated(K key, V oldValue, V newValue){
        }
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

    public V putIfAbsent(K key, V value) {
        V oldValue = super.putIfAbsent(key, value);
        if (oldValue == null) {
            notifyListenersAdded(key, value);
        }
        return oldValue;
    }

    public boolean remove(Object key, Object value) {
        boolean removed = super.remove(key, value);
        if (removed) {
            notifyListenersRemoved((K) key, (V) value);
        }
        return removed;
    }

    public boolean replace(K key, V oldValue, V newValue) {
        boolean replaced = super.replace(key, oldValue, newValue);
        if (replaced) {
            notifyListenersUpdated(key, oldValue, newValue);
        }
        return replaced;
    }

    public V replace(K key, V value) {
        V oldValue = super.replace(key, value);
        if (oldValue != null) {
            notifyListenersUpdated(key, oldValue, value);
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

    public void clear() {
        super.clear();
        listeners.clear();
    }

    public ObservableHashMap(int size) {
        super(size);
    }

    public ObservableHashMap() {
        super();
    }
}
