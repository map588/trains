package Framework.GUI.Manangers;

import eu.hansolo.medusa.Clock;
import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import trainController.trainControllerSubject;

public class trainControllerManager {

    @FXML
    public Clock trainControllerClock;

    @FXML
    private Slider trainControllerSpeedSlider;

    @FXML
    private Gauge trainControllerPower;

    @FXML
    private TextFlow powerTextOut;

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


    private trainControllerSubject subject = new trainControllerSubject(); // Assuming constructor takes an ID

    @FXML
    public void initialize() {
        trainControllerSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            subject.setOverrideSpeed(newValue.doubleValue());
        });

        eBrakeButton.setOnAction(event -> subject.setEmergencyBrake(!subject.emergencyBrakeProperty().get()));
        sBrakeButton.setOnAction(event -> subject.setServiceBrake(!subject.serviceBrakeProperty().get()));

        // Assuming subjectImpl provides a way to observe changes, e.g., JavaFX properties or custom listener mechanism


        subject.emergencyBrakeProperty().addListener((obs, wasEmergencyBrakeActive, isEmergencyBrakeActive) -> {
            updateEBrakeIndicator(isEmergencyBrakeActive);
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
            setSpeedDisplay.setValue(newValue.doubleValue());
        });

        subject.maxSpeedProperty().addListener((observable, oldValue, newValue) -> {
            maxSpeedDisplay.setValue(newValue.doubleValue());
        });

        subject.currentSpeedProperty().addListener((observable, oldValue, newValue) -> {
            currentSpeedDisplay.setValue(newValue.doubleValue());
        });

        maxSpeedDisplay.valueProperty().bind(subject.maxSpeedProperty());
        currentSpeedDisplay.valueProperty().bind(subject.currentSpeedProperty());


        updateEBrakeIndicator(subject.emergencyBrakeProperty().get());
        updateSBrakeIndicator(subject.serviceBrakeProperty().get());
    }

    // Assuming continuation from previous code snippet

    private void updatePowerText(double power) {
        Text powerText = new Text("Power: %.2f HP".formatted(power));
        powerTextOut.getChildren().clear();
        powerTextOut.getChildren().add(powerText);
    }

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
