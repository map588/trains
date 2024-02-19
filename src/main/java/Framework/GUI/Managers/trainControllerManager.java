package Framework.GUI.Managers;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import Common.TrainController;
import trainController.controllerSubjectFactory;
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

    public controllerSubjectFactory factory;
    public trainControllerSubject currentSubject;

    /*  What needs to be Initialize
     *  - Cabin Settings
     *      - Doors: Closed
     *      - Lights: Closed
     *      - Temperature: 75 C
     *  - Speed Controller
     *      - SPD LIMIT: 0 MPH
     *      - Command Speed: 0 MPH
     *      - Current Speed: 0 MPH
     *      - Set Speed: 0 MPH
     *  - Engineer's Input
     *      - Ki = 1
     *      - Kp = 1
     *  - Brake/Power
     *      - Power: 0 HP
     *      - EBrake Status: OFF
     *  - Failure State
     *      - Power Failure: OFF
     *      - Brake Failure: OFF
     *      - Signal Failure: OFF
     *  - Train Selector
     *      -
     */

    @FXML
    public void initialize() {

        factory = new controllerSubjectFactory();

        trainController_setSpeed_Slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            currentSubject.setOverrideSpeed(newValue.doubleValue());
        });

        trainController_toggleServiceBrake_CheckBox = new CheckBox();

        trainController_emergencyBrake_Button.setOnAction(event -> currentSubject.setEmergencyBrake(!currentSubject.emergencyBrakeProperty().get()));
        trainController_toggleServiceBrake_CheckBox.setOnAction(event -> currentSubject.setServiceBrake(!currentSubject.serviceBrakeProperty().get()));

        // Assuming subjectImpl provides a way to observe changes, e.g., JavaFX properties or custom listener mechanism

        trainController_trainNo_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            changeTrainView((Integer) newValue);
        });

        currentSubject.emergencyBrakeProperty().addListener((obs, wasEmergencyBrakeActive, isEmergencyBrakeActive) -> {
            updateSBrakeIndicator(isEmergencyBrakeActive);
        });

        currentSubject.serviceBrakeProperty().addListener((obs, wasServiceBrakeActive, isServiceBrakeActive) -> {
            updateSBrakeIndicator(isServiceBrakeActive);
        });

        currentSubject.powerProperty().addListener((observable, oldValue, newValue) -> {
            currentSubject.setPower(newValue.doubleValue());
        });

        currentSubject.powerProperty().addListener((observable, oldValue, newValue) -> {
            updatePowerText(newValue.doubleValue());
        });

        currentSubject.overrideSpeedProperty().addListener((observable, oldValue, newValue) -> {
            setTextField(trainController_setSpeed_TextField, newValue.doubleValue());
        });

        trainController_speedLimit_Gauge.valueProperty().bind(currentSubject.maxSpeedProperty());
        trainController_currentSpeed_Gauge.valueProperty().bind(currentSubject.currentSpeedProperty());
        trainController_commandSpeed_Gauge.valueProperty().bind(currentSubject.commandSpeedProperty());
        trainController_Authority_Gauge.valueProperty().bind(currentSubject.authorityProperty());

        currentSubject.serviceBrakeProperty().addListener((observable, oldValue, newValue) -> {
            currentSubject.setServiceBrake(newValue);
        });

        currentSubject.emergencyBrakeProperty().addListener((observable, oldValue, newValue) -> {
            currentSubject.setEmergencyBrake(newValue);
        });
        updateSBrakeIndicator(currentSubject.serviceBrakeProperty().get());
    }

    // Assuming continuation from previous code snippet

    public void addTrain(TrainController controller) {
        trainController_trainNo_ChoiceBox.getItems().add(controller.getTrainNumber());
    }

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

    private void changeTrainView(int trainID) {
        currentSubject = factory.getSubject(trainID);
        currentSubject.emergencyBrakeProperty().addListener((obs, wasEmergencyBrakeActive, isEmergencyBrakeActive) -> {
            updateSBrakeIndicator(isEmergencyBrakeActive);
        });

        currentSubject.serviceBrakeProperty().addListener((obs, wasServiceBrakeActive, isServiceBrakeActive) -> {
            updateSBrakeIndicator(isServiceBrakeActive);
        });

        currentSubject.powerProperty().addListener((observable, oldValue, newValue) -> {
            currentSubject.setPower(newValue.doubleValue());
        });

        currentSubject.powerProperty().addListener((observable, oldValue, newValue) -> {
            updatePowerText(newValue.doubleValue());
        });

        currentSubject.overrideSpeedProperty().addListener((observable, oldValue, newValue) -> {
            setTextField(trainController_setSpeed_TextField, newValue.doubleValue());
        });

        trainController_speedLimit_Gauge.valueProperty().bind(currentSubject.maxSpeedProperty());
        trainController_currentSpeed_Gauge.valueProperty().bind(currentSubject.currentSpeedProperty());
        trainController_commandSpeed_Gauge.valueProperty().bind(currentSubject.commandSpeedProperty());
        trainController_Authority_Gauge.valueProperty().bind(currentSubject.authorityProperty());

        currentSubject.serviceBrakeProperty().addListener((observable, oldValue, newValue) -> {
            currentSubject.setServiceBrake(newValue);
        });

        currentSubject.emergencyBrakeProperty().addListener((observable, oldValue, newValue) -> {
            currentSubject.setEmergencyBrake(newValue);
        });
        updateSBrakeIndicator(currentSubject.serviceBrakeProperty().get());
    }
}

