package Framework.Support;

public interface GUIModifiableEnum<T extends Enum<T>> {
    void setValue(Enum<?> propertyName, Object newValue);
}
