package Framework.GUI.Managers;

import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import trainController.trainControllerSubject;

public class trainControllerManager {

    @FXML
    public CheckBox trainController_intLight_CheckBox;

    @FXML
    public CheckBox trainController_extLight_CheckBox;

    @FXML
    public CheckBox trainController_openDoorLeft_CheckBox;

    @FXML
    public CheckBox trainController_openDoorRight_CheckBox;

    @FXML
    public TextField trainController_setTemperature_TextField;

    @FXML
    public Button trainController_makeAnnouncements_Button;

    // Engineer's Input
    @FXML
    public TextField trainController_setKi_TextField;

    @FXML
    public TextField trainController_setKp_TextField;

    // Speed Controller
    @FXML
    public Slider trainController_setSpeed_Slider;
    @FXML
    public TextField trainController_setSpeed_TextField;

    @FXML
    public Gauge trainController_currentSpeed_Gauge;

    @FXML
    public Gauge trainController_speedLimit_Gauge;

    @FXML
    public Gauge trainController_commandSpeed_Gauge;

    // Authority
    @FXML
     public Gauge trainController_blocksToNextStation_Gauge;

    @FXML
    public Gauge trainController_Authority_Gauge;

    // Power/Brake
    @FXML
    public Button trainController_emergencyBrake_Button;

    @FXML
    public Circle trainController_eBrake_Status;

    @FXML
    public CheckBox trainController_toggleServiceBrake_CheckBox;

    @FXML
    public Gauge trainiController_powerOutput_Gauge;

    // Failure States
    @FXML
    public Circle trainController_powerFailure_Status;
    @FXML
    public Circle trainController_brakeFailure_Status;
    @FXML
    public Circle trainController_signalFailure_Status;

    // Train Selector
    @FXML
    public ChoiceBox trainController_trainNo_ChoiceBox;

    @FXML
    public CheckBox trainController_autoMode_CheckBox;

    private trainControllerSubject subject = new trainControllerSubject(); // Assuming constructor takes an ID

    @FXML
    public void initialize() {
        trainController_setSpeed_Slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            subject.setOverrideSpeed(newValue.doubleValue());
        });

        trainController_toggleServiceBrake_CheckBox = new CheckBox();

        trainController_emergencyBrake_Button.setOnAction(event -> subject.setEmergencyBrake(!subject.emergencyBrakeProperty().get()));
        trainController_emergencyBrake_Button.setOnAction(event -> subject.setServiceBrake(!subject.serviceBrakeProperty().get()));

        // Assuming subjectImpl provides a way to observe changes, e.g., JavaFX properties or custom listener mechanism


        subject.emergencyBrakeProperty().addListener((obs, wasEmergencyBrakeActive, isEmergencyBrakeActive) -> {
            updateSBrakeIndicator(isEmergencyBrakeActive);
        });

        subject.serviceBrakeProperty().addListener((obs, wasServiceBrakeActive, isServiceBrakeActive) -> {
            updateSBrakeIndicator(isServiceBrakeActive);
        });

        subject.powerProperty().addListener((observable, oldValue, newValue) -> {
            subject.setPower(newValue.doubleValue());
        });

        subject.powerProperty().addListener((observable, oldValue, newValue) -> {
            updatePowerText(newValue.doubleValue());
        });

        subject.overrideSpeedProperty().addListener((observable, oldValue, newValue) -> {
            setTextField(trainController_setSpeed_TextField, newValue.doubleValue());
        });

        subject.maxSpeedProperty().addListener((observable, oldValue, newValue) -> {
            trainController_speedLimit_Gauge.setValue(newValue.doubleValue());
        });

        subject.currentSpeedProperty().addListener((observable, oldValue, newValue) -> {
            trainController_currentSpeed_Gauge.setValue(newValue.doubleValue());
        });

        trainController_speedLimit_Gauge.valueProperty().bind(subject.maxSpeedProperty());
        trainController_currentSpeed_Gauge.valueProperty().bind(subject.currentSpeedProperty());
        trainController_commandSpeed_Gauge.valueProperty().bind(subject.commandSpeedProperty());
        trainController_Authority_Gauge.valueProperty().bind(subject.authorityProperty());

        subject.serviceBrakeProperty().addListener((observable, oldValue, newValue) -> {
            subject.setServiceBrake(newValue.booleanValue());
        });

        subject.emergencyBrakeProperty().addListener((observable, oldValue, newValue) -> {
            subject.setEmergencyBrake(newValue);
        });
        updateSBrakeIndicator(subject.serviceBrakeProperty().get());
    }

    // Assuming continuation from previous code snippet

    private void updatePowerText(double power) {
        trainiController_powerOutput_Gauge.setValue(power);
    }
    private void setTextField(TextField textField, double value) {
        textField.setText(String.valueOf(value));
    }


    private void updateSBrakeIndicator(boolean isEmergencyBrakeActive) {
        if (isEmergencyBrakeActive) {
            trainController_eBrake_Status.setFill(Color.RED);
        } else {
            trainController_eBrake_Status.setFill(Color.GRAY);
        }
    }

}
