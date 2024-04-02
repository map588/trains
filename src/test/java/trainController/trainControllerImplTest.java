package trainController;

import Common.TrainModel;
import javafx.application.Platform;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.MockedConstructionImpl;

import static org.mockito.Mockito.*;

class trainControllerImplTest {

    private TrainControllerImpl controller;
    private TrainModel mockModel;



    @BeforeEach
    void setUp() {
        mockModel = mock(TrainModel.class); // Mocking TrainModel
        controller = new TrainControllerImpl(mockModel, 1); // Assuming '1' is a valid trainID
        controller.assignTrainModel(mockModel);
    }

    @Test
    void testCalculatePower() {
        // Set conditions for the test
        controller.setAutomaticMode(true);
        controller.setCommandSpeed(50); // Example condition

        // Invocations and Assertions go here
    }

    // Additional tests for property setters, assignTrainModel, notifyChange, etc.
}
