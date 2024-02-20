package Framework.Support;

@FunctionalInterface
public interface PropertyChangeListener {
    void onPropertyChange(String propertyName, Object newValue);
}
