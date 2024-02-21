package Framework.GUI.Managers;

import Framework.Support.SubjectFactory;
import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import trainModel.trainModelImpl;
import trainModel.trainModelSubject;
import trainModel.trainSubjectFactory;

import java.net.URL;

public class trainModelManager {

    @FXML
    public Button eBrakeBtn;
    @FXML
    public ChoiceBox<Integer> trainDropDown;
    @FXML
    public ToggleButton brakeFailureBtn, powerFailureBtn, signalFailureBtn;
    @FXML
    public Label gradeLabel, maxPowerLabel, medAccelerationLabel, maxVelocityLabel, trainLengthLabel, trainHeightLabel, trainWidthLabel, numCarsLabel;
    public Label numPassengersLabel, crewCountLabel, emptyWeightLabel, loadedWeightLabel;
    @FXML
    public Gauge actualPowerDisp, actualVelocityDisp, actualAccelerationDisp, cmdSpeedDisp, authorityDisp;
    @FXML
    public Circle extLightsEn, intLightEn, leftDoorsEn, rightDoorsEn, sBrakeEn, eBrakeEn;

    private SubjectFactory<trainModelSubject> factory;
    private trainModelSubject subject;

    private trainModelTB testBench;

    @FXML
    public void initialize() {

        testBench = launchTestBench();

        trainModelImpl trainModel = new trainModelImpl(0);
        factory = trainSubjectFactory.getInstance();

        subject = factory.getSubjects().get(0);

        bindLabels();
        bindGauges();
        bindIndicators();
        bindControls();

        trainDropDown.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeTrainView(newSelection);
            }
        });
    }

    private void bindLabels() {
        gradeLabel.textProperty().bindBidirectional(subject.getStringProperty("grade"));
        maxPowerLabel.setText("643.68");
        maxVelocityLabel.setText("43.48");
        medAccelerationLabel.setText("1.64");
        trainLengthLabel.setText("105.74");
        trainHeightLabel.setText("11.22");
        trainWidthLabel.setText("8.69");
        numCarsLabel.textProperty().bindBidirectional(subject.getStringProperty("numCars"));

        numPassengersLabel.textProperty().bindBidirectional(subject.getStringProperty("numPassengers"));
        crewCountLabel.textProperty().bindBidirectional(subject.getStringProperty("crewCount"));
        emptyWeightLabel.setText("40.9");
        loadedWeightLabel.setText("56.7");
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

        gradeLabel.textProperty().unbindBidirectional(subject.getStringProperty("grade"));
        numCarsLabel.textProperty().unbindBidirectional(subject.getStringProperty("numCars"));
        numPassengersLabel.textProperty().unbindBidirectional(subject.getStringProperty("numPassengers"));
        crewCountLabel.textProperty().unbindBidirectional(subject.getStringProperty("crewCount"));
    }

    private trainModelTB launchTestBench() {
        System.out.println(System.getProperty("Preparing to launch test bench"));
        try {
            String tbFile = "/Framework/GUI/FXML/trainModel_TB.fxml";
            URL url = getClass().getResource(tbFile);
            FXMLLoader loader = new FXMLLoader(url);
            Node content = loader.load();
            Stage newStage = new Stage();
            Scene newScene = new Scene(new VBox(content));
            newStage.setScene(newScene);
            newStage.setTitle("Train Model Test Bench");
            newStage.show();
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to launch test bench");
            throw new RuntimeException(e);
        }
    }
}