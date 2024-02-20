package Framework.GUI.Managers;

import Framework.SubjectFactory;
import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import trainModel.trainModelSubject;
import trainModel.trainSubjectFactory;

public class trainModelManager {

    @FXML
    public Button eBrakeBtn;
    @FXML
    public ChoiceBox<Integer> trainDropDown;
    @FXML
    public ToggleButton brakeFailureBtn, powerFailureBtn, signalFailureBtn;
    @FXML
    public TableView passengerInfoTbl, getTrainInfoTbl;
    @FXML
    public TableColumn passengerInfoClm, passengerInfoNum, trainInfoNum, trainInfoClm;
    @FXML
    public Gauge actualPowerDisp, actualVelocityDisp, actualAccelerationDisp, cmdSpeedDisp, authorityDisp;
    @FXML
    public Circle extLightsEn, intLightEn, leftDoorsEn, rightDoorsEn, sBrakeEn, eBrakeEn;

    private SubjectFactory<trainModelSubject> factory;
    private trainModelSubject subject;

    @FXML
    public void initialize() {

        factory = trainSubjectFactory.getInstance();

        bindGauges();
        bindIndicators();
        bindControls();

        trainDropDown.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeTrainView(newSelection);
            }
        });
    }

    private void bindGauges() {
        actualPowerDisp.valueProperty().bind(subject.getDoubleProperty("power"));
        actualVelocityDisp.valueProperty().bind(subject.getDoubleProperty("actualSpeed"));
        actualAccelerationDisp.valueProperty().bind(subject.getDoubleProperty("acceleration"));
        cmdSpeedDisp.valueProperty().bind(subject.getDoubleProperty("commandSpeed"));
        authorityDisp.valueProperty().bind(subject.getIntegerProperty("authority"));
    }

    private void bindIndicators() {
        subject.getBooleanProperty("emergencyBrake").addListener((obs, oldSelection, newSelection) -> updateEBrakeIndicator(newSelection));
        subject.getBooleanProperty("serviceBrake").addListener((obs, oldSelection, newSelection) -> updateSBrakeIndicator(newSelection));
        subject.getBooleanProperty("extLights").addListener((obs, oldSelection, newSelection) -> updateExtLightsIndicator(newSelection));
        subject.getBooleanProperty("intLights").addListener((obs, oldSelection, newSelection) -> updateIntLightsIndicator(newSelection));
        subject.getBooleanProperty("leftDoors").addListener((obs, oldSelection, newSelection) -> updateLeftDoorsIndicator(newSelection));
        subject.getBooleanProperty("rightDoors").addListener((obs, oldSelection, newSelection) -> updateRightDoorsIndicator(newSelection));
    }

    private void bindControls() {
        eBrakeBtn.setOnAction(event -> subject.updateProperty(subject.getBooleanProperty("emergencyBrake"), true));

        brakeFailureBtn.setOnAction(event -> {
            boolean newBrakeFailure = !subject.getBooleanProperty("brakeFailure").get();
            subject.updateProperty(subject.getBooleanProperty("brakeFailure"), newBrakeFailure);
        });

        powerFailureBtn.setOnAction(event -> {
            boolean newPowerFailure = !subject.getBooleanProperty("powerFailure").get();
            subject.updateProperty(subject.getBooleanProperty("powerFailure"), newPowerFailure);
        });

        signalFailureBtn.setOnAction(event -> {
            boolean newSignalFailure = !subject.getBooleanProperty("signalFailure").get();
            subject.updateProperty(subject.getBooleanProperty("signalFailure"), newSignalFailure);
        });
    }

    private void updateEBrakeIndicator(boolean active) {
        eBrakeEn.setFill(active ? Color.RED : Color.GRAY);
    }
    private void updateSBrakeIndicator(boolean active) {
        sBrakeEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }
    private void updateExtLightsIndicator(boolean active) {
        extLightsEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }
    private void updateIntLightsIndicator(boolean active) {
        intLightEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }
    private void updateLeftDoorsIndicator(boolean active) {
        leftDoorsEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }
    private void updateRightDoorsIndicator(boolean active) {
        rightDoorsEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }

    private void changeTrainView(int trainID) {
        subject = factory.getSubjects().get(trainID);
        if(subject != null) {
            unbindControls();
            bindControls();
        }
    }

    private void unbindControls() {
        cmdSpeedDisp.valueProperty().unbind();
        authorityDisp.valueProperty().unbind();
        actualVelocityDisp.valueProperty().unbind();
        actualPowerDisp.valueProperty().unbind();
        actualAccelerationDisp.valueProperty().unbind();

        brakeFailureBtn.selectedProperty().unbind();
        powerFailureBtn.selectedProperty().unbind();
        signalFailureBtn.selectedProperty().unbind();
    }
}