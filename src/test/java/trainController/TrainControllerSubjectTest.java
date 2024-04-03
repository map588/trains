package trainController;

import Common.TrainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainControllerSubjectTest {
    private TrainControllerSubject subject;
    private TrainController controller;

    @BeforeEach
    void setUp() {
        controller = mock(TrainController.class);
        subject = new TrainControllerSubject(controller);
    }

    @Test
    void testSetProperty() {
        subject.setProperty(Properties.COMMAND_SPEED_PROPERTY, 50.0);
        verify(controller).setValue(Properties.COMMAND_SPEED_PROPERTY, 50.0);
    }

    @Test
    void testNotifyChange() {
        subject.notifyChange(Properties.AUTHORITY_PROPERTY, 1000);
        assertEquals(1000, subject.getIntegerProperty(Properties.AUTHORITY_PROPERTY).get());
    }

    // Add more test methods for other properties and scenarios
}