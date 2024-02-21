package Framework.GUI.Managers;


import Common.TrainModel;
import Framework.Support.ListenerReference;
import Framework.Support.SubjectFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import trainModel.trainModelSubject;
import trainModel.trainSubjectFactory;

import java.util.ArrayList;
import java.util.List;

public class trainModelTB {
    @FXML
    public ToggleButton tbEBrake, tbSBrake, tbIntLights, tbExtLights, tbLeftDoors, tbRightDoors;
    @FXML
    public TextField tbPower, tbSpeed, tbAuthority, tbGrade, tbTemp;
    @FXML
    public Circle tbBrakeFailure, tbPowerFailure, tbSignalFailure;
    private TrainModel model;
    private trainModelSubject subject;
    private SubjectFactory<trainModelSubject> factory;
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();

    @FXML
    public void initialize() {
        factory = trainSubjectFactory.getInstance();


        if (!factory.getSubjects().isEmpty()) {
            Integer firstKey = factory.getSubjects().keySet().iterator().next();
            subject = factory.getSubjects().get(firstKey);
            model = factory.getSubjects().get(firstKey).getModel();
        }
        bindTrain(null);
    }
    public void changeModels(TrainModel model) {
        unbindTrain();
        this.model = model;
        trainModelSubject oldSubject = subject;
        subject = factory.getSubjects().get(model.getTrainNumber());
        bindTrain(oldSubject);
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

    private void bindTrain(trainModelSubject oldSubject) {
        //tbEBrake.setOnAction(event -> model.setEmergencyBrake(!model.getEmergencyBrake()));
        tbSBrake.setOnAction(event -> model.setServiceBrake(!model.getServiceBrake()));
        tbIntLights.setOnAction(event -> model.setIntLights(!model.getIntLights()));
        tbExtLights.setOnAction(event -> model.setExtLights(!model.getExtLights()));
        tbLeftDoors.setOnAction(event -> model.setLeftDoors(!model.getLeftDoors()));
        tbRightDoors.setOnAction(event -> model.setRightDoors(!model.getRightDoors()));

        tbPower.setOnAction(event -> model.setPower(Double.parseDouble(tbPower.getText())));
        tbSpeed.setOnAction(event -> model.setCommandSpeed(Double.parseDouble(tbSpeed.getText())));
        tbAuthority.setOnAction(event -> model.setAuthority(Integer.parseInt(tbSpeed.getText())));
        tbGrade.setOnAction(event -> model.setGrade(Double.parseDouble(tbGrade.getText())));
        tbTemp.setOnAction(event -> model.setTemperature(Double.parseDouble(tbTemp.getText())));

        appendListener(subject.getBooleanProperty("brakeFailure"), (observable, oldValue, newValue) -> updateBrakeFailureIndicator(newValue));
        appendListener(subject.getBooleanProperty("powerFailure"), (observable, oldValue, newValue) -> updatePowerFailureIndicator(newValue));
        appendListener(subject.getBooleanProperty("signalFailure"), (observable, oldValue, newValue) -> updateSignalFailureIndicator(newValue));
        if (oldSubject != null) {
            tbEBrake.selectedProperty().unbindBidirectional(oldSubject.getBooleanProperty("emergencyBrake"));
        }
        tbEBrake.selectedProperty().bindBidirectional(subject.getBooleanProperty("emergencyBrake"));
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
