package trackModel;

import Utilities.Enums.Lines;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


class LineSubjectMap {

    private static final ConcurrentHashMap<Integer, TrackBlockSubject> greenlineSubjects = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, TrackBlockSubject> redlineSubjects = new ConcurrentHashMap<>();

    public static void addLineSubject(Lines line, TrackBlockSubject subject) {
        if(line == Lines.GREEN)
            greenlineSubjects.put(subject.getBlockNumber(), subject);
        else if(line == Lines.RED)
            redlineSubjects.put(subject.getBlockNumber(), subject);
    }

    public static ObservableList<TrackBlockSubject> getLineSubject(Lines line) {
        if(line == Lines.GREEN)
            return FXCollections.observableArrayList(greenlineSubjects.values());
        else
            return FXCollections.observableArrayList(redlineSubjects.values());
    }

    public static List<String> getLineNames() {
        return Arrays.asList("GREEN", "RED");
    }

}

