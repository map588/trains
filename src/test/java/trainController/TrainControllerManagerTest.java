package trainController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.api.FxAssert.verifyThat;

import org.testfx.util.WaitForAsyncUtils;
import trainController.trainControllerImpl;
import trainController.trainControllerSubject;
import trainController.trainControllerSubjectMap;



public class TrainControllerManagerTest extends ApplicationTest {

    trainControllerImpl controller;
    trainControllerSubject currentSubject;

    @Override
    public void start(Stage stage) throws Exception {
        // Assuming trainControllerSubjectMap is a singleton class managing subjects
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Framework/GUI/FXML/trainController.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        stage.toFront();
        currentSubject = trainControllerSubjectMap.getInstance().getSubject(1);
        controller = trainControllerSubjectMap.getInstance().getSubject(1).getController();
    }

    @Test
    public void testEmergencyBrakeButtonToggle() {
        Circle eBrakeStatus = lookup("#eBrakeStatus").queryAs(Circle.class);
        Color initialColor = (Color) eBrakeStatus.getFill();

        // Simulate clicking the emergency brake button
        clickOn("#emergencyBrakeButton");

        // Verify the emergency brake status indicator toggles color
        if(initialColor.equals(Color.RED)) {
            verifyThat("#eBrakeStatus", (Circle circle) -> circle.getFill().equals(Color.GRAY));
        } else {
            verifyThat("#eBrakeStatus", (Circle circle) -> circle.getFill().equals(Color.RED));
        }
        // Verify the change in emergency brake status in the controller
        assertTrue(controller.getEmergencyBrake() != initialColor.equals(Color.RED));
    }

    @Test
    public void testServiceBrakeToggle() {
        CheckBox toggleServiceBrakeCheckBox = lookup("#toggleServiceBrakeCheckBox").queryAs(CheckBox.class);
        boolean initialState = toggleServiceBrakeCheckBox.isSelected();

        // Toggle the service brake checkbox
        clickOn(toggleServiceBrakeCheckBox);

        // Verify service brake status has changed
        assertEquals(!initialState, toggleServiceBrakeCheckBox.isSelected());
        // Verify the change in service brake status in the controller
        assertEquals(!initialState, controller.getServiceBrake());
    }


    @Test
    public void testAutomaticModeCheckBox() {
        // Initial state check
        CheckBox autoModeCheckBox = lookup("#autoModeCheckBox").queryAs(CheckBox.class);
        boolean initialState = autoModeCheckBox.isSelected();

        // Toggle the checkbox
        clickOn("#autoModeCheckBox");

        // Verify checkbox state has changed
        assertEquals(!initialState, autoModeCheckBox.isSelected());
        // Verify the change in automatic mode status in the controller
        assertEquals(!initialState, controller.getAutomaticMode());
    }

    @Test
    public void testSetSpeedSlider() {
        // Assuming the slider's initial value is 0 for simplicity
        double newValue = 50.0;
        Slider setSpeedSlider = lookup("#setSpeedSlider").queryAs(Slider.class);

        // Set the slider to a new value
        setSliderValue(setSpeedSlider, newValue);

        // Verify slider's new value
        assertEquals(newValue, setSpeedSlider.getValue());
        // Verify the change in command speed in the controller
        assertEquals(newValue, controller.getOverrideSpeed());
    }

    @Test
    public void testSetTemperatureTextField() {
        String newTemperature = "25.5";
        TextField setTemperatureTextField = lookup("#setTemperatureTextField").queryAs(TextField.class);

        // Set the temperature to a new value
        clickOn(setTemperatureTextField).write(newTemperature);

        // Press Enter to trigger the action
        type(KeyCode.ENTER);

        // Verify the temperature's new value in the controller
        assertEquals(Double.parseDouble(newTemperature), controller.getTemperature());
    }

    @Test
    public void testLightToggle() {
        // Toggle internal light checkbox
        CheckBox intLightCheckBox = lookup("#intLightCheckBox").queryAs(CheckBox.class);
        clickOn(intLightCheckBox);

        // Verify internal light status change
        assertEquals(intLightCheckBox.isSelected(), controller.getIntLights());

        // Toggle external light checkbox
        CheckBox extLightCheckBox = lookup("#extLightCheckBox").queryAs(CheckBox.class);
        clickOn(extLightCheckBox);

        // Verify external light status change
        assertEquals(extLightCheckBox.isSelected(), controller.getExtLights());
    }

    @Test
    public void testDoorOperations() {
        // Toggle open left door checkbox
        CheckBox openDoorLeftCheckBox = lookup("#openDoorLeftCheckBox").queryAs(CheckBox.class);
        clickOn(openDoorLeftCheckBox);

        // Verify left door status change
        assertEquals(openDoorLeftCheckBox.isSelected(), controller.getLeftDoors());

        // Toggle open right door checkbox
        CheckBox openDoorRightCheckBox = lookup("#openDoorRightCheckBox").queryAs(CheckBox.class);
        clickOn(openDoorRightCheckBox);

        // Verify right door status change
        assertEquals(openDoorRightCheckBox.isSelected(), controller.getRightDoors());
    }

    @Test
    public void directTestSetTemperature() {
        double newTemperature = 25.5;
        controller.setTemperature(newTemperature); // Assuming such a setter exists
        assertEquals(newTemperature, controller.getTemperature(), "The temperature should match the newly set value.");
    }

    private void setSliderValue(Slider slider, double value) {
        interact(() -> slider.setValue(value));
    }
}