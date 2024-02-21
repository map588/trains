package Framework.Support;

import javafx.beans.value.ChangeListener;

public interface Notifications {
    void addChangeListener(ChangeListener<? super Object> listener);
}