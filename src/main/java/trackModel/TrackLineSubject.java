package trackModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TrackLineSubject {

    private ObservableList<TrackBlockSubject> blockList;

    public TrackLineSubject(TrackLine trackLine) {
        ObservableList<TrackBlockSubject> selectedTrackBlockSubject = FXCollections.observableArrayList(trackLine.getSubject());
    }
}
