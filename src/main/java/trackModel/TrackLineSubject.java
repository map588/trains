package trackModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TrackLineSubject {

    private ObservableList<TrackLine> lineList;

    public TrackLineSubject(TrackLine trackLine) {
        lineList = FXCollections.observableArrayList(trackLine);
    }
}
