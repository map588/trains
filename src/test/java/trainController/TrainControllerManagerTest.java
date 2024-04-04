package trainController;

import Common.TrainModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.testfx.api.FxAssert.verifyThat;


/**
 * This class contains unit tests for the TrainControllerManager.
 * It extends ApplicationTest from the TestFX library to enable GUI testing.
 */
public class TrainControllerManagerTest extends ApplicationTest {

    TrainControllerImpl controller;
    Integer controllerID = 1;

    Integer index = 1;
    TrainControllerSubjectMap subjects = TrainControllerSubjectMap.getInstance();
    /**
     * This method sets up the testing environment.
     * It loads the trainController.fxml file and displays it in a new Stage.
     * It also initializes the controller and currentSubject variables.
     */

    @BeforeEach
    public void setUp() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        TrainModel mockModel = mock(TrainModel.class);
        controllerID = random.nextInt(1, 39);
        controller = new TrainControllerImpl(mockModel, controllerID);

    }

    @AfterEach
    public void tearDown() {
        subjects.removeSubject(controllerID);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Framework/GUI/FXML/trainController.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    /**
     * This test verifies that the emergency brake button toggles the emergency brake status.
     */
    @Test
    void subjectInMapIsCorrect() {
        assertEquals(controller, TrainControllerSubjectMap.getInstance().getSubject(controllerID).getController());
        assertEquals(controllerID, controller.getID());
    }

    @Test
    void testEmergencyBrakeButtonToggle() {
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

    /**
     * This test verifies that the service brake checkbox toggles the service brake status.
     */
    @Test
    void testServiceBrakeToggle() {
        CheckBox toggleServiceBrakeCheckBox = lookup("#toggleServiceBrakeCheckBox").queryAs(CheckBox.class);
        boolean initialState = toggleServiceBrakeCheckBox.isSelected();

        // Toggle the service brake checkbox
        clickOn(toggleServiceBrakeCheckBox);

        // Verify service brake status has changed
        assertEquals(!initialState, toggleServiceBrakeCheckBox.isSelected());
        // Verify the change in service brake status in the controller
        assertEquals(!initialState, controller.getServiceBrake());
    }

    /**
     * This test verifies that the automatic mode checkbox toggles the automatic mode status.
     */
    @Test

    public void testAutomaticModeCheckBox() {

        CheckBox autoModeCheckBox = lookup("#autoModeCheckBox").queryAs(CheckBox.class);
        boolean initialState = autoModeCheckBox.isSelected();

        // Toggle the checkbox
        clickOn("#autoModeCheckBox");

        // Verify checkbox state has changed
        assertEquals(!initialState, autoModeCheckBox.isSelected());
        // Verify the change in automatic mode status in the controller
        assertEquals(!initialState, controller.getAutomaticMode());
    }

    /**
     * This test verifies that the speed slider correctly sets the override speed in the controller.
     */
    @Test
    public void testSetSpeedSlider() {

        double newValue = 50.0;
        Slider setSpeedSlider = lookup("#setSpeedSlider").queryAs(Slider.class);

        // Set the slider to a new value
        setSliderValue(setSpeedSlider, newValue);

        // Verify slider's new value
        assertEquals(newValue, setSpeedSlider.getValue());
        assertEquals(newValue, controller.getOverrideSpeed());
    }

    /**
     * This test verifies that the internal and external light checkboxes toggle the light status in the controller.
     */
    @Test
    public void testLightToggle() {

        CheckBox intLightCheckBox = lookup("#intLightCheckBox").queryAs(CheckBox.class);
        clickOn(intLightCheckBox);
        // Verify internal light status change
        assertEquals(intLightCheckBox.isSelected(), controller.getIntLights());

        CheckBox extLightCheckBox = lookup("#extLightCheckBox").queryAs(CheckBox.class);
        clickOn(extLightCheckBox);
        assertEquals(extLightCheckBox.isSelected(), controller.getExtLights());
    }

    /**
     * This test verifies that the open door checkboxes toggle the door status in the controller.
     */
    @Test
    public void testDoorOperations() {

        CheckBox openDoorLeftCheckBox = lookup("#openDoorLeftCheckBox").queryAs(CheckBox.class);
        clickOn(openDoorLeftCheckBox);

        // Verify left door status change
        assertEquals(openDoorLeftCheckBox.isSelected(), controller.getLeftDoors());

        CheckBox openDoorRightCheckBox = lookup("#openDoorRightCheckBox").queryAs(CheckBox.class);
        clickOn(openDoorRightCheckBox);

        // Verify right door status change
        assertEquals(openDoorRightCheckBox.isSelected(), controller.getRightDoors());
    }

    /**
     * This test directly sets the temperature in the controller and verifies that it has been set correctly.
     */
    @Test
    void directTestSetTemperature() {
        double newTemperature = 25.5;
        controller.setSetTemperature(newTemperature); // Assuming such a setter exists
        assertEquals(newTemperature, controller.getSetTemperature(), "The temperature should match the newly set value.");
    }

    /**
     * This test sets the temperature via the text field and verifies that it has been set correctly in the controller.
     */
    @Test
    void testSetTemperatureTextField() {
        String newTemperature = "25.5";
        TextField setTemperatureTextField = lookup("#setTemperatureTextField").queryAs(TextField.class);

        // Programmatically set the text field value and fire action event
        interact(() -> {
            setTemperatureTextField.setText(newTemperature);
            setTemperatureTextField.fireEvent(new ActionEvent());
        });

        // Verify the temperature's new value in the controller
        assertEquals(Double.parseDouble(newTemperature), controller.getSetTemperature(), "Temperature should be updated based on TextField input.");
    }


    /**
     * This helper method sets the value of a slider in a JavaFX application.
     */
    private void setSliderValue(Slider slider, double value) {
        interact(() -> slider.setValue(value));
    }
}