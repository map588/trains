package trainModel;

import Common.TrainController;
import trackModel.TrackLine;
import Common.TrainModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import trainController.TrainControllerImpl;
import trainController.TrainControllerSubjectMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.testfx.api.FxAssert.verifyThat;
public class trainModelManagerTest extends ApplicationTest {
    TrainModelImpl model;
    TrainModelSubject currentSubject;

    @Override
    public void start(Stage stage) throws Exception {
        TrainController mockController = mock(TrainController.class); // Mocking TrainModel
        TrackLine mockLine = mock(TrackLine.class); // Mocking TrackModel
        model = new TrainModelImpl(1, mockLine); // Assuming '1' is a valid trainID
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Framework/GUI/FXML/trainController.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
        stage.toFront();

        currentSubject = TrainModelSubjectMap.getInstance().getSubject(1);
        model = (TrainModelImpl) TrainModelSubjectMap.getInstance().getSubject(1).getModel();
    }

    @Test
    void testEmergencyBrakeButtonToggle() {
        Circle eBrakeStatus = lookup("#eBrakeStatus").queryAs(Circle.class);
        Button eBrakeButton = lookup("#eBrakeButton").queryAs(Button.class);
        eBrakeButton.fire();
        Platform.runLater(() -> {
            assertEquals(Color.RED, eBrakeStatus.getFill());
            assertTrue(model.getEmergencyBrake());
        });
    }

}
