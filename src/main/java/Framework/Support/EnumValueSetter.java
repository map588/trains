package Framework.Support;

public interface EnumValueSetter<T extends Enum<T>> {
    void setValue(T propertyName, Object newValue);
}
