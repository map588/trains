package Framework.GUI.Controllers;

import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import trainController.testTrainControllerImpl;

public class trainControllerControl {

    @FXML
    public Clock trainControllerClock;

    @FXML
    private Slider trainControllerSpeedSlider;

    @FXML
    private Gauge trainControllerPower;

    @FXML
    private Gauge maxSpeedDisplay;

    @FXML
    private Gauge setSpeedDisplay;

    @FXML
    private Gauge currentSpeedDisplay;

    @FXML
    private Circle eBrakeIndicatorCircle;

    @FXML
    private Circle sBrakeIndicatorCircle;

    @FXML
    private Button eBrakeButton;

    @FXML
    private Button sBrakeButton;


    private testTrainControllerImpl trainController = new testTrainControllerImpl(1); // Assuming constructor takes an ID

    @FXML
    public void initialize() {
        trainControllerSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            trainController.setOverrideSpeed(newValue.doubleValue());
        });

        eBrakeButton.setOnAction(event -> trainController.setEmergencyBrake(!trainController.getEmergencyBrake()));
        sBrakeButton.setOnAction(event -> trainController.setServiceBrake(!trainController.getServiceBrake()));

        // Assuming trainControllerImpl provides a way to observe changes, e.g., JavaFX properties or custom listener mechanism


        trainController.emergencyBrakeProperty().addListener((obs, wasEmergencyBrakeActive, isEmergencyBrakeActive) -> {
            updateEBrakeIndicator(isEmergencyBrakeActive);
        });

        trainController.serviceBrakeProperty().addListener((obs, wasServiceBrakeActive, isServiceBrakeActive) -> {
            updateSBrakeIndicator(isServiceBrakeActive);
        });

        trainController.powerProperty().addListener((observable, oldValue, newValue) -> {
            trainControllerPower.setValue(newValue.doubleValue());
        });

        trainController.overrideSpeedProperty().addListener((observable, oldValue, newValue) -> {
            setSpeedDisplay.setValue(newValue.doubleValue());
        });

        trainController.maxSpeedProperty().addListener((observable, oldValue, newValue) -> {
            maxSpeedDisplay.setValue(newValue.doubleValue());
        });

        trainController.currentSpeedProperty().addListener((observable, oldValue, newValue) -> {
            currentSpeedDisplay.setValue(newValue.doubleValue());
        });

        maxSpeedDisplay.valueProperty().bind(trainController.maxSpeedProperty());
        currentSpeedDisplay.valueProperty().bind(trainController.currentSpeedProperty());


        updateEBrakeIndicator(trainController.getEmergencyBrake());
        updateSBrakeIndicator(trainController.getServiceBrake());
    }

    // Assuming continuation from previous code snippet


    private void updateEBrakeIndicator(boolean isEmergencyBrakeActive) {
        if (isEmergencyBrakeActive) {
            eBrakeIndicatorCircle.setFill(Color.RED);
        } else {
            eBrakeIndicatorCircle.setFill(Color.GRAY);
        }
    }

    private void updateSBrakeIndicator(boolean isServiceBrakeActive) {
        if (isServiceBrakeActive) {
            sBrakeIndicatorCircle.setFill(Color.BLUE);
        } else {
            sBrakeIndicatorCircle.setFill(Color.GRAY);
        }
    }

}
