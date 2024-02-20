package trainController;

import Common.WaysideController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class trainControllerSubjectFactory {

    private static final trainControllerSubjectFactory INSTANCE = new trainControllerSubjectFactory();
    private ObservableHashMap<Integer, trainControllerSubject> subjects = new ObservableHashMap<>();
    private trainControllerSubjectFactory() {}

    public static trainControllerSubjectFactory getInstance() {
        return INSTANCE;
    }

    public ObservableHashMap<Integer, trainControllerSubject> getSubjects() {
        return subjects;
    }

    public void registerSubject(int ID, trainControllerSubject subject) {
        subjects.put(ID, subject);
    }

}
