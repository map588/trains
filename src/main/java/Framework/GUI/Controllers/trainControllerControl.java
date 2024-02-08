package Framework.GUI.Controllers;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import trainController.trainControllerImpl;

public class trainControllerControl {

    @FXML
    private Slider trainControllerSpeedSlider;

    @FXML
    private Gauge trainControllerPower;

    @FXML
    private Circle eBrakeIndicatorCircle;

    @FXML
    private Button trainControllerEBrakeButton;

    @FXML
    private TextFlow trainControllerSpeedView;

    private trainControllerImpl trainController = new trainControllerImpl(1); // Assuming constructor takes an ID

    @FXML
    public void initialize() {
        trainControllerSpeedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            trainController.setOverrideSpeed(newValue.doubleValue());
        });

        trainControllerEBrakeButton.setOnAction(event -> trainController.setEmergencyBrake(!trainController.getEmergencyBrake()));

        // Assuming trainControllerImpl provides a way to observe changes, e.g., JavaFX properties or custom listener mechanism
        trainController.overrideSpeedProperty().addListener((observable, oldValue, newValue) -> {
            updateSpeedView(newValue.doubleValue());
        });


        trainController.emergencyBrakeProperty().addListener((obs, wasEmergencyBrakeActive, isEmergencyBrakeActive) -> {
            updateEBrakeIndicator(isEmergencyBrakeActive);
        });

        trainController.powerProperty().addListener((observable, oldValue, newValue) -> {
            trainControllerPower.setValue(newValue.doubleValue());
        });

        updateEBrakeIndicator(trainController.getEmergencyBrake());
    }

    // Assuming continuation from previous code snippet

    private void updateSpeedView(double speed) {
        // Example: Update the TextFlow with the current speed. This might require converting to Text nodes or similar.
        // This implementation might vary based on how you plan to display the speed.
        Text speedText = new Text(String.format("Current Set Speed: %.2f mph", speed));
        trainControllerSpeedView.getChildren().clear();
        trainControllerSpeedView.getChildren().add(speedText);
    }

    private void updateEBrakeIndicator(boolean isEmergencyBrakeActive) {
        if (isEmergencyBrakeActive) {
            eBrakeIndicatorCircle.setFill(Color.RED);
        } else {
            eBrakeIndicatorCircle.setFill(Color.GRAY);
        }
    }

}
