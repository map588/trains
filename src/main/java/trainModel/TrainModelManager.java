package trainModel;

import Framework.Support.ListenerReference;
import Framework.Support.ObservableHashMap;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainModel.NullObjects.NullTrainSubject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainModelManager {
    
    Logger logger = LoggerFactory.getLogger(TrainModelManager.class);

    @FXML
    public Button eBrakeBtn;
    @FXML
    public ChoiceBox<Integer> trainDropDown;
    @FXML
    public ToggleButton brakeFailureBtn, powerFailureBtn, signalFailureBtn;
    @FXML
    public Label gradeLabel, maxPowerLabel, medAccelerationLabel, maxVelocityLabel, trainLengthLabel, trainHeightLabel, trainWidthLabel, numCarsLabel;
    @FXML
    public Label numPassengerLabel, crewCountLabel, massLabel;
    @FXML
    public Gauge actualPowerDisp, actualVelocityDisp, actualAccelerationDisp, cmdSpeedDisp, authorityDisp, setTempDisp, realTempDisp;
    @FXML
    public Circle extLightsEn, intLightsEn, leftDoorsEn, rightDoorsEn, sBrakeEn, eBrakeEn;

    TrainModelSubjectMap subjectMap;
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();
    private TrainModelSubject subject;
    //private TrainModelTB testBench;
    private final TrainModelSubject nullSubject = new TrainModelSubject();
    @FXML
    public void initialize() {
        logger.info("Started TrainModelManager initialize");

        subjectMap = TrainModelSubjectMap.getInstance();
        setupMapChangeListener();

        if (!subjectMap.getSubjects().isEmpty()) {
            Integer firstKey = subjectMap.getSubjects().keySet().iterator().next();
            changeTrainView(firstKey); // Switch to the first available subject
        } else {
            subject = NullTrainSubject.getInstance(); // Use the null object
            updateViewForNullSubject(); // Prepare UI for no subject selected
        }
        updateChoiceBoxItems(); // Reflect current subjects in the UI


        trainDropDown.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeTrainView(newSelection);
            }
        });

        setUpCircleColors();
        logger.info("Finished TrainModelManager initialize");
    }

    private void bindLabels() {
        maxPowerLabel.setText("643.68");
        maxVelocityLabel.setText("43.48");
        medAccelerationLabel.setText("1.64");
        trainHeightLabel.setText("11.22");
        trainWidthLabel.setText("8.69");

        bindLabelToProperty("mass", massLabel);
        bindLabelToProperty("length", trainLengthLabel);
        bindLabelToProperty("numCars", numCarsLabel);
        bindLabelToProperty("numPassengers", numPassengerLabel);
        bindLabelToProperty("crewCount", crewCountLabel);
        bindLabelToProperty("grade", gradeLabel);
    }

    private void bindLabelToProperty(String property, Label label) {
        appendListener(subject.getProperty(property) ,(obs, oldValue, newValue) -> {
                String newVal = newValue.toString();
                //logger.info("newVal: " + newVal);
                if(newVal.isEmpty()) {return;}
                try {
                    label.setText(newVal);
                } catch (NumberFormatException e) {
                    label.setText("");
                }
        });
    }


    private void bindGauges() {
        actualPowerDisp.valueProperty().bind(subject.getDoubleProperty("power"));
        actualVelocityDisp.valueProperty().bind(subject.getDoubleProperty("actualSpeed"));
        actualAccelerationDisp.valueProperty().bind(subject.getDoubleProperty("acceleration"));
        cmdSpeedDisp.valueProperty().bind(subject.getDoubleProperty("commandSpeed"));
        authorityDisp.valueProperty().bind(subject.getIntegerProperty("authority"));
        setTempDisp.valueProperty().bind(subject.getDoubleProperty("setTemperature"));
        realTempDisp.valueProperty().bind(subject.getDoubleProperty("realTemperature"));
    }

    private void bindIndicators() {
        appendListener(subject.getBooleanProperty("emergencyBrake"), (obs, oldSelection, newSelection) -> updateEBrakeIndicator(newSelection));
        appendListener(subject.getBooleanProperty("serviceBrake"), (obs, oldSelection, newSelection) -> updateSBrakeIndicator(newSelection));
        appendListener(subject.getBooleanProperty("extLights"), (obs, oldSelection, newSelection) -> updateExtLightsIndicator(newSelection));
        appendListener(subject.getBooleanProperty("intLights"), (obs, oldSelection, newSelection) -> updateIntLightsIndicator(newSelection));
        appendListener(subject.getBooleanProperty("leftDoors"), (obs, oldSelection, newSelection) -> updateLeftDoorsIndicator(newSelection));
        appendListener(subject.getBooleanProperty("rightDoors"), (obs, oldSelection, newSelection) -> updateRightDoorsIndicator(newSelection));
    }

    private void bindControls() {
        eBrakeBtn.setOnAction(event -> subject.setProperty("emergencyBrake", true));

        brakeFailureBtn.setOnAction(event -> {
            BooleanProperty brakeFailure = subject.getBooleanProperty("brakeFailure");
            subject.setProperty("brakeFailure", !brakeFailure.get());
        });

        powerFailureBtn.setOnAction(event -> {
            BooleanProperty powerFailure = subject.getBooleanProperty("powerFailure");
            subject.setProperty("powerFailure", !powerFailure.get());
        });

        signalFailureBtn.setOnAction(event -> {
            BooleanProperty signalFailure = subject.getBooleanProperty("signalFailure");
            subject.setProperty("signalFailure", !signalFailure.get());
        });
    }

    private void updateEBrakeIndicator(boolean active) {
        eBrakeEn.setFill(active ? Color.RED : Color.GRAY);
    }
    private void updateSBrakeIndicator(boolean active) {
        sBrakeEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }
    private void updateExtLightsIndicator(boolean active) {
        extLightsEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }
    private void updateIntLightsIndicator(boolean active) {
        intLightsEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }
    private void updateLeftDoorsIndicator(boolean active) {
        leftDoorsEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }
    private void updateRightDoorsIndicator(boolean active) {
        rightDoorsEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }

    private void changeTrainView(Integer trainID) {
        if (trainID == null || trainID == -1 || !subjectMap.getSubjects().containsKey(trainID)) {
            subject = NullTrainSubject.getInstance(); // Fallback to null object
            updateViewForNullSubject(); // Special UI update for no subject
        } else {
            subject = subjectMap.getSubjects().get(trainID);
            updateView(); // Regular UI update for an actual subject
        }
    }


    private void updateViewForNullSubject() {
        // Example UI adjustments for the null subject
        actualPowerDisp.setValue(0);
        actualVelocityDisp.setValue(0);
        actualAccelerationDisp.setValue(0);
        cmdSpeedDisp.setValue(0);
        authorityDisp.setValue(0);
        setTempDisp.setValue(0);
        realTempDisp.setValue(0);
        brakeFailureBtn.setSelected(false);
        powerFailureBtn.setSelected(false);
        signalFailureBtn.setSelected(false);
    }



    private void updateView() {
        if(subject != null) {
            unbindValues();
            bindControls();
            bindGauges();
            bindIndicators();
            bindLabels();
        }
    }

    private void unbindValues() {
        listenerReferences.forEach(ListenerReference::detach);
        listenerReferences.clear();

        cmdSpeedDisp.valueProperty().unbind();
        authorityDisp.valueProperty().unbind();
        actualVelocityDisp.valueProperty().unbind();
        actualPowerDisp.valueProperty().unbind();
        actualAccelerationDisp.valueProperty().unbind();
        setTempDisp.valueProperty().unbind();
        realTempDisp.valueProperty().unbind();

        brakeFailureBtn.selectedProperty().unbind();
        powerFailureBtn.selectedProperty().unbind();
        signalFailureBtn.selectedProperty().unbind();
    }

    private <T> void appendListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        listenerReferences.add(new ListenerReference<>(observable, listener));
    }

    private void setupMapChangeListener() {
        ObservableHashMap<Integer, TrainModelSubject> subjects = subjectMap.getSubjects();

        // Create a listener that reacts to any change (add, remove, update) by updating choice box items
        ObservableHashMap.MapListener<Integer, TrainModelSubject> genericListener = new ObservableHashMap.MapListener<>() {
            @Override
            public void onAdded(Integer key, TrainModelSubject value) {
                Platform.runLater(() -> {
                    updateChoiceBoxItems();
                    if(subjectMap.getSubjects().size() == 1) { // If it's the only train, select it
                        trainDropDown.getSelectionModel().select(key);
                    }
                });
            }

            @Override
            public void onRemoved(Integer key, TrainModelSubject value) {
                Platform.runLater(() -> {
                    updateChoiceBoxItems();
                    // Additional logic can be added here to select another item if the current selection was removed
                });
            }

            @Override
            public void onUpdated(Integer key, TrainModelSubject oldValue, TrainModelSubject newValue) {
                Platform.runLater(() -> updateChoiceBoxItems());
            }
        };

        subjects.addChangeListener(genericListener);
        updateChoiceBoxItems(); // Initial population
    }

    private void updateChoiceBoxItems() {
        Integer currentSelection = trainDropDown.getSelectionModel().getSelectedItem();

        List<Integer> trainIDs = new ArrayList<>(subjectMap.getSubjects().keySet());
            trainIDs.add(0, -1); // Assuming -1 as the ID for the null train

            trainDropDown.setItems(FXCollections.observableArrayList(trainIDs));

            if (!subjectMap.getSubjects().isEmpty()) {
                if (subjectMap.getSubjects().containsKey(currentSelection)) {
                    trainDropDown.getSelectionModel().select(currentSelection);
                } else {
                    // Select the first actual subject if the current selection is invalid
                    trainDropDown.getSelectionModel().selectFirst();
                }
            } else {
                // Select the "no selection" option when no subjects are available
                trainDropDown.getSelectionModel().select(Integer.valueOf(-1));
            }

        // Additional logic to handle no selection case can be added here
    }

    private TrainModelTB launchTestBench() {
       // logger.info(System.getProperty("Preparing to launch test bench"));
        try {
            String tbFile = "/Framework/GUI/FXML/trainModel_TB.fxml";
            URL url = getClass().getResource(tbFile);
            FXMLLoader loader = new FXMLLoader(url);
            Node content = loader.load();
            Stage newStage = new Stage();
            Scene newScene = new Scene(new VBox(content));
            newStage.setScene(newScene);
            newStage.setTitle("Train Model Test Bench");
            newStage.show();
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
       //     logger.info("Failed to launch test bench");
            throw new RuntimeException(e);
        }
    }
    private void setUpCircleColors() {
        List<Circle> circleList =  Arrays.asList(extLightsEn, intLightsEn, leftDoorsEn, rightDoorsEn, sBrakeEn, eBrakeEn);
        for (Circle c : circleList){
            c.setFill(Color.GRAY);
        }
    }
}