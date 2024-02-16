package Framework.GUI.Controllers;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.CheckBox;
import javafx.scene.shape.Circle;
import trainController.testTrainControllerImpl;

public class trainControllerControl {

    // Cabin Settings
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


    private testTrainControllerImpl trainController = new testTrainControllerImpl(1); // Assuming constructor takes an ID

    @FXML
    public void initialize() {
//        trainController_setSpeed_Slider.valueProperty().addListener((observable, oldValue, newValue) -> {
//            trainController.setOverrideSpeed(newValue.doubleValue());
//        });
//
//        eBrakeButton.setOnAction(event -> trainController.setEmergencyBrake(!trainController.getEmergencyBrake()));
//        sBrakeButton.setOnAction(event -> trainController.setServiceBrake(!trainController.getServiceBrake()));

        // Assuming trainControllerImpl provides a way to observe changes, e.g., JavaFX properties or custom listener mechanism


//        trainController.emergencyBrakeProperty().addListener((obs, wasEmergencyBrakeActive, isEmergencyBrakeActive) -> {
//            updateEBrakeIndicator(isEmergencyBrakeActive);
//        });
//
//        trainController.serviceBrakeProperty().addListener((obs, wasServiceBrakeActive, isServiceBrakeActive) -> {
//            updateSBrakeIndicator(isServiceBrakeActive);
//        });
//
//        trainController.powerProperty().addListener((observable, oldValue, newValue) -> {
//            trainControllerPower.setValue(newValue.doubleValue());
//        });
//
//        trainController.overrideSpeedProperty().addListener((observable, oldValue, newValue) -> {
//            setSpeedDisplay.setValue(newValue.doubleValue());
//        });
//
//        trainController.maxSpeedProperty().addListener((observable, oldValue, newValue) -> {
//            maxSpeedDisplay.setValue(newValue.doubleValue());
//        });
//
//        trainController.currentSpeedProperty().addListener((observable, oldValue, newValue) -> {
//            currentSpeedDisplay.setValue(newValue.doubleValue());
//        });
//
//        maxSpeedDisplay.valueProperty().bind(trainController.maxSpeedProperty());
//        currentSpeedDisplay.valueProperty().bind(trainController.currentSpeedProperty());
//
//
//        updateEBrakeIndicator(trainController.getEmergencyBrake());
//        updateSBrakeIndicator(trainController.getServiceBrake());
    }

    // Assuming continuation from previous code snippet


//    private void updateEBrakeIndicator(boolean isEmergencyBrakeActive) {
//        if (isEmergencyBrakeActive) {
//            eBrakeIndicatorCircle.setFill(Color.RED);
//        } else {
//            eBrakeIndicatorCircle.setFill(Color.GRAY);
//        }
//    }
//
//    private void updateSBrakeIndicator(boolean isServiceBrakeActive) {
//        if (isServiceBrakeActive) {
//            sBrakeIndicatorCircle.setFill(Color.BLUE);
//        } else {
//            sBrakeIndicatorCircle.setFill(Color.GRAY);
//        }
//    }

}
