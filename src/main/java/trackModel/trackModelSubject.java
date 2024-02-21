package trackModel;

import Common.TrackModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class trackModelSubject {
    private StringProperty tempProperty;

    public trackModelSubject(TrackModel model)  {
        tempProperty = new SimpleStringProperty();
    }

    public StringProperty tempProperty() {
        return tempProperty;
    }
}
