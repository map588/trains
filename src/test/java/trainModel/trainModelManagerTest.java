package trainModel;


import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class trainModelManagerTest extends ApplicationTest {

    private TrainModelManager trainModelManager;
    private TrainModelImpl trainModel;


    @Override
    public void start(Stage stage) throws Exception {
        trainModel = new TrainModelImpl(0);
        trainModelManager = new TrainModelManager();
        trainModelManager.initialize();
    }



    @BeforeEach
    public void setup() {
        trainModel = new TrainModelImpl(1);

    }

    @AfterEach
    public void tearDown() {
        trainModelManager.subjectMap.getSubjects().clear();
    }

    @Test
    public void testEmergencyBrakeButton() {
        Button emergencyBrakeButton = lookup("#eBrakeBtn").queryButton();
        clickOn(emergencyBrakeButton);
        assertTrue(trainModel.getEmergencyBrake());
    }

    @Test
    public void testBrakeFailureButton() {
        CheckBox brakeFailureButton = lookup("#brakeFailureBtn").queryAs(CheckBox.class);
        clickOn(brakeFailureButton);
        assertTrue(trainModel.getBrakeFailure());
    }

    @Test
    public void testPowerFailureButton() {
        CheckBox powerFailureButton = lookup("#powerFailureBtn").queryAs(CheckBox.class);
        clickOn(powerFailureButton);
        assertTrue(trainModel.getPowerFailure());
    }

    @Test
    public void testSignalFailureButton() {
        CheckBox signalFailureButton = lookup("#signalFailureBtn").queryAs(CheckBox.class);
        clickOn(signalFailureButton);
        assertTrue(trainModel.getSignalFailure());
    }

    @Test
    public void testExternalLightsIndicator() {
        trainModel.setExtLights(true);
        Circle extLightsIndicator = lookup("#extLightsEn").queryAs(Circle.class);
        assertEquals(Color.YELLOW, extLightsIndicator.getFill());
    }

    @Test
    public void testInternalLightsIndicator() {
        trainModel.setIntLights(true);
        Circle intLightsIndicator = lookup("#intLightsEn").queryAs(Circle.class);
        assertEquals(Color.YELLOW, intLightsIndicator.getFill());
    }

    @Test
    public void testLeftDoorsIndicator() {
        trainModel.setLeftDoors(true);
        Circle leftDoorsIndicator = lookup("#leftDoorsEn").queryAs(Circle.class);
        assertEquals(Color.YELLOW, leftDoorsIndicator.getFill());
    }

    @Test
    public void testRightDoorsIndicator() {
        trainModel.setRightDoors(true);
        Circle rightDoorsIndicator = lookup("#rightDoorsEn").queryAs(Circle.class);
        assertEquals(Color.YELLOW, rightDoorsIndicator.getFill());
    }

    @Test
    public void testTrainDropDown() {
        TrainModelImpl newTrainModel = new TrainModelImpl(1);
//        trainModelManager.subjectMap.getSubjects().put(1, newTrainModel);

        ChoiceBox<Integer> trainDropDown = lookup("#trainDropDown").queryAs(ChoiceBox.class);
        clickOn(trainDropDown);
        clickOn("1");

//        assertEquals(newTrainModel, trainModelManager.subject);
    }

    @Test
    public void testLabels() {
        Label massLabel = lookup("#massLabel").queryAs(Label.class);
        Label numPassengersLabel = lookup("#numPassengerLabel").queryAs(Label.class);
        Label crewCountLabel = lookup("#crewCountLabel").queryAs(Label.class);

        trainModel.setMass(100);
        trainModel.setNumPassengers(50);
        trainModel.setCrewCount(2);

        assertEquals("100.0", massLabel.getText());
        assertEquals("50", numPassengersLabel.getText());
        assertEquals("2", crewCountLabel.getText());
    }
}