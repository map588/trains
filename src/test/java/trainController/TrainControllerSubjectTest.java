package trainController;

import Common.TrainController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static trainController.Controller_Property.*;
import static trainController.Properties.*;

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
        subject.setProperty(COMMAND_SPEED_PROPERTY, 50.0);
        verify(controller).setValue(COMMAND_SPEED_PROPERTY, 50.0);
    }

    @Test
    void testNotifyChange() {
        subject.notifyChange(AUTHORITY, 1000);
        assertEquals(1000, subject.getIntegerProperty(AUTHORITY).get());
    }

    // Add more test methods for other properties and scenarios
}