package Integration;

import CTCOffice.CTCOfficeImpl;
import Common.TrainModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import stubs.trainStub;
import trainController.TrainControllerSubjectMap;

import static org.testfx.api.FxAssert.verifyThat;


/**
 * This class contains unit tests for the TrainControllerManager.
 * It extends ApplicationTest from the TestFX library to enable GUI testing.
 */
class TrainsStuckTest extends ApplicationTest {

    CTCOfficeImpl ctc;


    Integer controllerID = 1;
    TrainControllerSubjectMap subjects = TrainControllerSubjectMap.getInstance();

    /**
     * This method sets up the testing environment.
     * It loads the trainController.fxml file and displays it in a new Stage.
     * It also initializes the controller and currentSubject variables.
     */

    @BeforeEach
    public void setUp() {
        TrainModel mockModel = new trainStub();
//        controller = mockModel.getController().getSubject().getController();
        //controllerID = controller.getID();
    }

    @AfterEach
    public void tearDown() {
        subjects.removeSubject(controllerID);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Framework/GUI/FXML/CTC_Main_UI.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }


    @Test
    void testEmergencyBrakeButtonToggle() {
        Circle eBrakeStatus = lookup("#eBrakeStatus").queryAs(Circle.class);
        Color initialColor = (Color) eBrakeStatus.getFill();

        // Simulate clicking the emergency brake button
        clickOn("#emergencyBrakeButton");

        // Verify the emergency brake status indicator toggles color
        if (initialColor.equals(Color.RED)) {
            verifyThat("#eBrakeStatus", (Circle circle) -> circle.getFill().equals(Color.GRAY));
        } else {
            verifyThat("#eBrakeStatus", (Circle circle) -> circle.getFill().equals(Color.RED));
        }
        // Verify the change in emergency brake status in the controller
       // assertTrue(controller.getEmergencyBrake() != initialColor.equals(Color.RED));
    }


}