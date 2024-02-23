package UXTests.trainController;

import Common.TrainController;
import javafx.beans.property.Property;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import trainController.trainControllerSubject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class TrainControllerSubjectTest {
    private trainControllerSubject subject;
    private TrainController controller;

    @BeforeEach
    public void setUp() {
        controller = Mockito.mock(TrainController.class);
        subject = new trainControllerSubject(controller);
    }

    @Test
    public void testSetProperty() {
        subject.setProperty("testProperty", 123);
        Property<?> property = subject.getProperty("testProperty");
        assertEquals(123, property.getValue());
    }

    @Test
    public void testGetProperty() {
        subject.setProperty("testProperty", 123);
        Property<?> property = subject.getProperty("testProperty");
        assertEquals(123, property.getValue());
    }

    @Test
    public void testUpdateFromGui() {
        Runnable mockRunnable = Mockito.mock(Runnable.class);
        subject.updateFromGui(mockRunnable);
        verify(mockRunnable).run();
    }
}