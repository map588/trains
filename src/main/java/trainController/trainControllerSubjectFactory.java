package trainController;

import Common.WaysideController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class trainControllerSubjectFactory {

    private static final trainControllerSubjectFactory INSTANCE = new trainControllerSubjectFactory();
    private ObjectProperty<ObservableList<trainControllerSubject>> subjects = new SimpleObjectProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
    private trainControllerSubjectFactory() {}

    public static trainControllerSubjectFactory getInstance() {
        return INSTANCE;
    }

    public ObjectProperty<ObservableList<trainControllerSubject>> getSubjects() {
        return subjects;
    }

    public void deleteSubject(int ID) {
        if (subjects.get().remove(ID) != null) {
            System.out.println("Deleted subject with ID: " + ID);
        } else {
            System.out.println("Attempted to delete a non-existent subject with ID: " + ID);
        }
    }

    public void addSubject(int ID) {
        trainControllerSubject newSubject = new trainControllerSubject(ID);
        subjects.get().add(newSubject);
    }
}
