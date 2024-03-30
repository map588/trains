package Framework.Support;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ObservableHashMap<K, V> extends ConcurrentHashMap<K, V> {
    private final Set<MapListener<K, V>> listeners = ConcurrentHashMap.newKeySet();
    private ExecutorService notificationExecutor;

    public interface MapListener<K, V> {
        default void onAdded(K key, V value) {
        }
        default void onRemoved(K key, V value){
        }
        default void onUpdated(K key, V oldValue, V newValue){
        }
    }

    public void addChangeListener(MapListener<K, V> listener) {
        if (listeners.isEmpty()) {
            notificationExecutor = Executors.newSingleThreadExecutor();
        }
        listeners.add(listener);
    }

    public void removeChangeListener(MapListener<K, V> listener) {
        listeners.remove(listener);
        if (listeners.isEmpty() && notificationExecutor != null) {
            notificationExecutor.shutdown();
            notificationExecutor = null;
        }
    }

    @Override
    public V put(K key, V value) {
        V oldValue = super.put(key, value);
        if (oldValue == null && notificationExecutor != null) {
            notifyListenersAdded(key, value);
        } else if (notificationExecutor != null){
            notifyListenersUpdated(key, oldValue, value);
        }
        return oldValue;
    }

    public V remove(Object key) {
        V oldValue = super.remove(key);
        if (oldValue != null && notificationExecutor != null) {
            notifyListenersRemoved((K) key, oldValue);
        }
        return oldValue;
    }

    private void notifyListenersAdded(K key, V value) {
        listeners.forEach(listener -> notificationExecutor.submit(() -> listener.onAdded(key, value)));
    }

    private void notifyListenersRemoved(K key, V value) {
        listeners.forEach(listener -> notificationExecutor.submit(() -> listener.onRemoved(key, value)));
    }

    private void notifyListenersUpdated(K key, V oldValue, V newValue) {
        listeners.forEach(listener -> notificationExecutor.submit(() -> listener.onUpdated(key, oldValue, newValue)));
    }

}
