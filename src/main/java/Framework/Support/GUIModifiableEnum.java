package Framework.Support;

public interface GUIModifiableEnum<T extends Enum<T>> {
    default void setValue(Enum<?> propertyName, Object newValue){};
}
