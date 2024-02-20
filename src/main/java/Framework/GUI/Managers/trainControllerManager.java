package Framework.GUI.Managers;

import Common.TrainController;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import trainController.trainControllerSubjectFactory;
import trainController.trainControllerSubject;

import java.util.ArrayList;
import java.util.function.Consumer;

public class trainControllerManager {

    @FXML
    private CheckBox trainController_intLight_CheckBox, trainController_extLight_CheckBox, trainController_openDoorLeft_CheckBox, trainController_openDoorRight_CheckBox, trainController_toggleServiceBrake_CheckBox, trainController_autoMode_CheckBox;
    @FXML
    private TextField trainController_setTemperature_TextField, trainController_setKi_TextField, trainController_setKp_TextField, trainController_setSpeed_TextField;
    @FXML
    private Button trainController_emergencyBrake_Button, trainController_makeAnnouncements_Button;
    @FXML
    private Slider trainController_setSpeed_Slider;
    @FXML
    private Gauge trainController_currentSpeed_Gauge, trainController_speedLimit_Gauge, trainController_commandedSpeed_Gauge, trainController_Authority_Gauge, trainController_powerOutput_Gauge, trainController_blocksToNextStation_Gauge;
    @FXML
    private Circle trainController_eBrake_Status, trainController_signalFailure_Status, trainController_brakeFailure_Status, trainController_powerFailure_Status;
    @FXML
    private ChoiceBox<Integer> trainController_trainNo_ChoiceBox;

    private trainControllerSubjectFactory factory;
    private trainControllerSubject currentSubject;

    @FXML
    public void initialize() {
        factory = new trainControllerSubjectFactory();
        createTrainController(0);

        setupMapChangeListener();
        bindGauges();
        bindControls();
        bindIndicators();

        trainController_trainNo_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeTrainView(newSelection);
            }
        });

    }

    private void setupMapChangeListener() {
        factory.subjectMap.addChangeListener((Integer key) -> {

            updateChoiceBoxItems();
        });
        updateChoiceBoxItems();
    }

    private void updateChoiceBoxItems() {
        Platform.runLater(() -> {
            trainController_trainNo_ChoiceBox.setItems(FXCollections.observableArrayList(
                    new ArrayList<>(factory.subjectMap.keySet())));
        });
    }

    private void createTrainController(int trainID) {
        currentSubject = factory.getSubject(trainID);
    }

    private void bindGauges() {
        trainController_currentSpeed_Gauge.valueProperty().bind(currentSubject.getDoubleProperty("currentSpeed"));
        trainController_commandedSpeed_Gauge.valueProperty().bind(currentSubject.getDoubleProperty("commandSpeed"));
        trainController_speedLimit_Gauge.valueProperty().bind(currentSubject.getDoubleProperty("maxSpeed"));
        trainController_Authority_Gauge.valueProperty().bind(currentSubject.getIntegerProperty("authority"));
        trainController_powerOutput_Gauge.valueProperty().bind(currentSubject.getDoubleProperty("power"));
        trainController_blocksToNextStation_Gauge.valueProperty().bind(currentSubject.getIntegerProperty("blocksToNextStation"));
    }

    private void bindIndicators() {
        currentSubject.getBooleanProperty("emergencyBrake").addListener((obs, oldSelection, newSelection) -> updateEBrakeIndicator(newSelection));
        currentSubject.getBooleanProperty("signalFailure").addListener((obs, oldSelection, newSelection) -> updateSignalFailureIndicator(newSelection));
        currentSubject.getBooleanProperty("brakeFailure").addListener((obs, oldSelection, newSelection) -> updateBrakeFailureIndicator(newSelection));
        currentSubject.getBooleanProperty("powerFailure").addListener((obs, oldSelection, newSelection) -> updatePowerFailureIndicator(newSelection));
    }

    private void bindControls() {
        bindSliderAndTextField(trainController_setSpeed_Slider, trainController_setSpeed_TextField, ((DoubleProperty)currentSubject.getDoubleProperty("overrideSpeed"))::set);
        bindCheckBox(trainController_intLight_CheckBox, currentSubject.getBooleanProperty("intLights")::set);
        bindCheckBox(trainController_extLight_CheckBox, currentSubject.getBooleanProperty("extLights")::set);
        bindCheckBox(trainController_openDoorLeft_CheckBox, currentSubject.getBooleanProperty("leftDoors")::set);
        bindCheckBox(trainController_openDoorRight_CheckBox, currentSubject.getBooleanProperty("rightDoors")::set);
        bindCheckBox(trainController_toggleServiceBrake_CheckBox, currentSubject.getBooleanProperty("serviceBrake")::set);
        bindCheckBox(trainController_autoMode_CheckBox, currentSubject.getBooleanProperty("automaticMode")::set);
        bindTextField(trainController_setTemperature_TextField, currentSubject.getDoubleProperty("temperature")::set);
        bindTextField(trainController_setKi_TextField, currentSubject.getDoubleProperty("Ki")::set);
        bindTextField(trainController_setKp_TextField, currentSubject.getDoubleProperty("Kp")::set);
        trainController_emergencyBrake_Button.setOnAction(event -> {
            boolean newEBrake = !currentSubject.getBooleanProperty("emergencyBrake").get();
            currentSubject.updateProperty(currentSubject.getBooleanProperty("emergencyBrake"), newEBrake);
            updateEBrakeIndicator(newEBrake);
        });
        trainController_makeAnnouncements_Button.setOnAction(event -> {
            boolean enable = !currentSubject.getBooleanProperty("announcements").get();
            currentSubject.updateProperty(currentSubject.getBooleanProperty("announcements"), enable);
        });
    }

    private void bindSliderAndTextField(Slider slider, TextField textField, Consumer<Double> consumer) {
        slider.valueProperty().addListener((obs, oldSelection, newSelection) -> {
            consumer.accept(newSelection.doubleValue());
            textField.setText(String.valueOf(newSelection.doubleValue()));
        });
        textField.textProperty().addListener((obs, oldSelection, newSelection) -> {
            double value = Double.parseDouble(newSelection);
            consumer.accept(value);
            slider.setValue(value);
        });
    }

    private void bindCheckBox(CheckBox checkBox, Consumer<Boolean> consumer) {
        checkBox.selectedProperty().addListener((obs, oldSelection, newSelection) -> consumer.accept(newSelection));
    }

    private void bindTextField(TextField textField, Consumer<Double> consumer) {
        textField.textProperty().addListener((obs, oldSelection, newSelection) -> {
            try{
                double value = Double.parseDouble(newSelection);
                consumer.accept(value);
            } catch (NumberFormatException e) {
                consumer.accept(Double.parseDouble(oldSelection));
            }
        });
    }

    public synchronized void addTrainToList(TrainController controller) {
        trainController_trainNo_ChoiceBox.getItems().add(controller.getID());
    }

    private void updateEBrakeIndicator(boolean isEmergencyBrakeActive) {
        trainController_eBrake_Status.setFill(isEmergencyBrakeActive ? Color.RED : Color.GRAY);
    }
    private void updateSignalFailureIndicator(boolean isSignalFailureActive) {
        trainController_signalFailure_Status.setFill(isSignalFailureActive ? Color.RED : Color.GRAY);
    }
    private void updateBrakeFailureIndicator(boolean isBrakeFailureActive) {
        trainController_brakeFailure_Status.setFill(isBrakeFailureActive ? Color.RED : Color.GRAY);
    }
    private void updatePowerFailureIndicator(boolean isPowerFailureActive) {
        trainController_powerFailure_Status.setFill(isPowerFailureActive ? Color.RED : Color.GRAY);
    }

    private void changeTrainView(int trainID) {
        currentSubject = factory.getSubject(trainID);
        if(currentSubject != null) {
            unbindControls();
            bindControls();
        }
    }

    private void unbindControls() {
        trainController_currentSpeed_Gauge.valueProperty().unbind();
        trainController_commandedSpeed_Gauge.valueProperty().unbind();
        trainController_speedLimit_Gauge.valueProperty().unbind();
        trainController_Authority_Gauge.valueProperty().unbind();
        trainController_powerOutput_Gauge.valueProperty().unbind();

        trainController_setSpeed_Slider.valueProperty().unbind();
        trainController_setSpeed_TextField.textProperty().unbind();

        trainController_intLight_CheckBox.selectedProperty().unbind();
        trainController_extLight_CheckBox.selectedProperty().unbind();
        trainController_openDoorLeft_CheckBox.selectedProperty().unbind();
        trainController_openDoorRight_CheckBox.selectedProperty().unbind();
        trainController_toggleServiceBrake_CheckBox.selectedProperty().unbind();
        trainController_autoMode_CheckBox.selectedProperty().unbind();

        trainController_setTemperature_TextField.textProperty().unbind();
        trainController_setKi_TextField.textProperty().unbind();
        trainController_setKp_TextField.textProperty().unbind();
    }
}