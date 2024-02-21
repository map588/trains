package Framework.GUI.Managers;


import Framework.Support.SubjectFactory;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import Common.TrainModel;
import trainModel.trainModelSubject;
import trainModel.trainSubjectFactory;

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

    @FXML
    public void initialize() {
        tbEBrake.setOnAction(event -> model.setEmergencyBrake(tbEBrake.isSelected()));
        tbSBrake.setOnAction(event -> model.setServiceBrake(tbSBrake.isSelected()));
        tbIntLights.setOnAction(event -> model.setIntLights(tbIntLights.isSelected()));
        tbExtLights.setOnAction(event -> model.setExtLights(tbExtLights.isSelected()));
        tbLeftDoors.setOnAction(event -> model.setLeftDoors(tbLeftDoors.isSelected()));
        tbRightDoors.setOnAction(event -> model.setRightDoors(tbRightDoors.isSelected()));

        tbPower.setOnAction(event -> model.setPower(Double.parseDouble(tbPower.getText())));
        tbSpeed.setOnAction(event -> model.setCommandSpeed(Double.parseDouble(tbSpeed.getText())));
        tbAuthority.setOnAction(event -> model.setAuthority(Integer.parseInt(tbSpeed.getText())));
        tbGrade.setOnAction(event -> model.setGrade(Double.parseDouble(tbGrade.getText())));
        tbTemp.setOnAction(event -> model.setTemperature(Double.parseDouble(tbTemp.getText())));
    }
    public void changeModels(TrainModel model) {
        this.model = model;

        factory = trainSubjectFactory.getInstance();

        subject = factory.getSubjects().get(model.getTrainNumber());

        subject.getBooleanProperty("brakeFailure").addListener((obs, oldSelection, newSelection) -> updateBrakeFailureIndicator(newSelection));
        subject.getBooleanProperty("powerFailure").addListener((obs, oldSelection, newSelection) -> updatePowerFailureIndicator(newSelection));
        subject.getBooleanProperty("signalFailure").addListener((obs, oldSelection, newSelection) -> updateSignalFailureIndicator(newSelection));
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
}
