package Framework.Support;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class ListenerReference<T> {
    private ObservableValue<T> property;
    private ChangeListener<T> listener;

    public ListenerReference(ObservableValue<T> property, ChangeListener<T> listener) {
        this.property = property;
        this.listener = listener;
    }

    public void detach() {
        this.property.removeListener(this.listener);
    }
}
