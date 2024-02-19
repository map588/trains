package Framework.GUI.Managers;

import Common.TrainController;
import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import trainController.controllerSubjectFactory;
import trainController.trainControllerSubject;

import java.util.function.Consumer;

public class trainControllerManager {

    @FXML
    private CheckBox trainController_intLight_CheckBox, trainController_extLight_CheckBox, trainController_openDoorLeft_CheckBox, trainController_openDoorRight_CheckBox, trainController_toggleServiceBrake_CheckBox, trainController_autoMode_CheckBox;
    @FXML
    private TextField trainController_setTemperature_TextField, trainController_setKi_TextField, trainController_setKp_TextField, trainController_setSpeed_TextField;
    @FXML
    private Button trainController_emergencyBrake_Button;
    @FXML
    private Slider trainController_setSpeed_Slider;
    @FXML
    private Gauge trainController_currentSpeed_Gauge, trainController_speedLimit_Gauge, trainController_commandSpeed_Gauge, trainController_Authority_Gauge, trainiController_powerOutput_Gauge;
    @FXML
    private Circle trainController_eBrake_Status;
    @FXML
    private ChoiceBox<Integer> trainController_trainNo_ChoiceBox;

    private controllerSubjectFactory factory;
    private trainControllerSubject currentSubject;

    @FXML
    public void initialize() {
        factory = new controllerSubjectFactory();
        bindGauges();
        bindControls();

        trainController_trainNo_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeTrainView(newSelection);
            }
        });
    }

    private void bindGauges() {
        trainController_currentSpeed_Gauge.valueProperty().bind(currentSubject.currentSpeedProperty());
        trainController_commandSpeed_Gauge.valueProperty().bind(currentSubject.commandSpeedProperty());
        trainController_speedLimit_Gauge.valueProperty().bind(currentSubject.maxSpeedProperty());
        trainController_Authority_Gauge.valueProperty().bind(currentSubject.authorityProperty());
        trainiController_powerOutput_Gauge.valueProperty().bind(currentSubject.powerProperty());
    }

    private void bindControls() {
        bindSliderAndTextField(trainController_setSpeed_Slider, trainController_setSpeed_TextField, currentSubject::updateSetSpeed);
        bindCheckBox(trainController_intLight_CheckBox, currentSubject::updateExtLights);
        bindCheckBox(trainController_extLight_CheckBox, currentSubject::updateIntLights);
        bindCheckBox(trainController_openDoorLeft_CheckBox, currentSubject::updateLeftDoors);
        bindCheckBox(trainController_openDoorRight_CheckBox, currentSubject::updateRightDoors);
        bindCheckBox(trainController_toggleServiceBrake_CheckBox, currentSubject::updateServiceBrake);
        bindCheckBox(trainController_autoMode_CheckBox, currentSubject::updateAutomaticMode);
        bindTextField(trainController_setTemperature_TextField, currentSubject::updateTemperature);
        bindTextField(trainController_setKi_TextField, currentSubject::updateKi);
        bindTextField(trainController_setKp_TextField, currentSubject::updateKp);
        trainController_emergencyBrake_Button.setOnAction(event -> {
            boolean newEBrake = !currentSubject.emergencyBrakeProperty().get();
            currentSubject.updateEmergencyBrake(newEBrake);
            updateEBrakeIndicator(newEBrake);
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
        textField.textProperty().addListener((obs, oldSelection, newSelection) -> consumer.accept(Double.parseDouble(newSelection)));
    }

    public void addTrain(TrainController controller) {
        trainController_trainNo_ChoiceBox.getItems().add(controller.getID());
    }

    private void updateEBrakeIndicator(boolean isEmergencyBrakeActive) {
        trainController_eBrake_Status.setFill(isEmergencyBrakeActive ? Color.RED : Color.GRAY);
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
        trainController_commandSpeed_Gauge.valueProperty().unbind();
        trainController_speedLimit_Gauge.valueProperty().unbind();
        trainController_Authority_Gauge.valueProperty().unbind();
        trainiController_powerOutput_Gauge.valueProperty().unbind();

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