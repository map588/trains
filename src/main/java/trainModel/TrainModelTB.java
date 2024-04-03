package trainModel;


import Common.TrainModel;
import Framework.Support.ListenerReference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

public class TrainModelTB {
    @FXML
    public ToggleButton tbSBrake, tbIntLights, tbExtLights, tbLeftDoors, tbRightDoors;
    public Button tbTimeButton, tbEBrake;
    @FXML
    public TextField tbPower, tbSpeed, tbAuthority, tbGrade, tbTemp, tbTimeDelta;
    @FXML
    public Circle tbBrakeFailure, tbPowerFailure, tbSignalFailure;
    private TrainModelSubject subject;
    private TrainModelSubjectMap subjectMap;
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();

    @FXML
    public void initialize() {
        subjectMap = TrainModelSubjectMap.getInstance();

        if (!subjectMap.getSubjects().isEmpty()) {
            Integer firstKey = subjectMap.getSubjects().keySet().iterator().next();
            subject = subjectMap.getSubjects().get(firstKey);
        }else{
            System.out.println("No damn subjects.");
        }

        bindTrain();
    }
    public void changeModels(TrainModel model) {
        unbindTrain();
        subject = subjectMap.getSubject(model.getTrainNumber());
        bindTrain();
    }

    private void updateBrakeFailureIndicator(boolean active) {
        tbBrakeFailure.setFill(active ? Color.YELLOW : Color.GRAY);
    }

    private void updatePowerFailureIndicator(boolean active) {
        tbPowerFailure.setFill(active ? Color.YELLOW : Color.GRAY);
    }

    private void updateSignalFailureIndicator(boolean active) {
        tbSignalFailure.setFill(active ? Color.YELLOW : Color.GRAY);
    }

    private void bindTrain() {
        tbEBrake.setOnAction(event -> {
            BooleanProperty eBrake = subject.getBooleanProperty("emergencyBrake");
            subject.setProperty("emergencyBrake", !eBrake.get());
        });
        tbSBrake.setOnAction(event -> {
            BooleanProperty sBrake = subject.getBooleanProperty("serviceBrake");
            subject.setProperty("serviceBrake", !sBrake.get());
        });
        tbIntLights.setOnAction(event -> {
            BooleanProperty intLights = subject.getBooleanProperty("intLights");
            subject.setProperty("intLights", !intLights.get());
        });
        tbExtLights.setOnAction(event -> {
            BooleanProperty extLights = subject.getBooleanProperty("extLights");
            subject.setProperty("extLights", !extLights.get());
        });
        tbLeftDoors.setOnAction(event -> {
            BooleanProperty leftDoors = subject.getBooleanProperty("leftDoors");
            subject.setProperty("leftDoors", !leftDoors.get());
        });
        tbRightDoors.setOnAction(event -> {
            BooleanProperty rightDoors = subject.getBooleanProperty("rightDoors");
            subject.setProperty("rightDoors", !rightDoors.get());
        });

        tbPower.setOnAction(event -> subject.setProperty("power", Double.parseDouble(tbPower.getText())));
        tbSpeed.setOnAction(event -> subject.setProperty("commandSpeed", Double.parseDouble(tbSpeed.getText())));
        tbAuthority.setOnAction(event -> subject.setProperty(Properties.AUTHORITY_PROPERTY, Integer.parseInt(tbAuthority.getText())));
        tbGrade.setOnAction(event -> subject.setProperty("grade", Double.parseDouble(tbGrade.getText())));
        tbTemp.setOnAction(event -> subject.setProperty("setTemperature", Double.parseDouble(tbTemp.getText())));
        tbTimeDelta.setOnAction(event -> subject.setProperty("timeDelta", Integer.parseInt(tbTimeDelta.getText())));

        appendListener(subject.getBooleanProperty("brakeFailure"), (observable, oldValue, newValue) -> updateBrakeFailureIndicator(newValue));
        appendListener(subject.getBooleanProperty("powerFailure"), (observable, oldValue, newValue) -> updatePowerFailureIndicator(newValue));
        appendListener(subject.getBooleanProperty("signalFailure"), (observable, oldValue, newValue) -> updateSignalFailureIndicator(newValue));
    }

    private void unbindTrain(){
        listenerReferences.forEach(ListenerReference::detach);
        listenerReferences.clear();
    }


    private <T> void appendListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        listenerReferences.add(new ListenerReference<>(observable, listener));
    }

}
