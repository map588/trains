package trainController;

import Common.TrainController;
import Integration.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static trainController.Controller_Property.AUTHORITY;
import static trainController.Controller_Property.COMMAND_SPEED;

class TrainControllerSubjectTest extends BaseTest {
    private TrainControllerSubject subject;
    private TrainController controller;

    private TrainControllerSubjectMap subjectMap;
    @BeforeEach
    void setUp() {
        controller = mock(TrainControllerImpl.class);
        subject = subjectMap.getSubject(controller.getID());
    }

    @Test
    void testSetProperty() {
        subject.setProperty(COMMAND_SPEED.getPropertyName(), 50.0);
        verify(controller).setValue(COMMAND_SPEED.getPropertyName(), 50.0);
    }

    @Test
    void testNotifyChange() {
        subject.notifyChange(AUTHORITY, 1000);
        assertEquals(1000, subject.getIntegerProperty(AUTHORITY).get());
    }

    // Add more test methods for other properties and scenarios
}