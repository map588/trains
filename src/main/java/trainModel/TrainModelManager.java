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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trainController.NullController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class TrainModelManager {

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


    private final TrainModelSubjectMap subjectMap = TrainModelSubjectMap.getInstance();
    private final TrainModelSubject nullSubject = new TrainModelSubject();

    private TrainModelSubject subject;

    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(TrainModelManager.class);


    final static ReentrantLock subjectChangeLock = new ReentrantLock();


    @FXML
    public void initialize() {
        logger.info("Started TrainModelManager initialize");

        subject = nullSubject;
        setupMapChangeListener();
        updateChoiceBoxItems();


        if (!subjectMap.getSubjects().isEmpty()) {
            Integer firstKey = subjectMap.getSubjects().keySet().iterator().next();
            logger.info("Initialized Train Model UI with train ID: {}", firstKey);
            changeTrainView(firstKey); // Switch to the first available subject
        } else {
            subject = nullSubject; // Use the null object
            logger.warn("No trains available to initialize Train Model");
            updateViewForNullSubject(); // Prepare UI for no subject selected
        }


        trainDropDown.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(subjectMap.getSubjects().isEmpty()){
                logger.warn("No trains available to select");
                changeTrainView(NullController.getInstance().getID());
            }
            if(newSelection == null){
                logger.warn("No selection made");
                changeTrainView(oldSelection);
            }else {
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

    private void changeTrainView(Integer trainID) {
            executeUpdate(() -> {
                unbindValues();
                if (trainID == -1) {
                    subject = nullSubject;
                    updateViewForNullSubject();
                    logger.info("Train Controller switched to null subject");
                } else {
                    subject = subjectMap.getSubjects().get(trainID);
                    bindAll();
                    updateView();
                    logger.info("Train Controller switched to train ID: {}", trainID);
                }
            });
    }

    void executeUpdate(Runnable updateOperation) {
        if (subjectChangeLock.tryLock()) {
            try {
                updateOperation.run();
            } finally {
                subjectChangeLock.unlock();
            }
        } else {
            Platform.runLater(updateOperation);
            logger.warn("Unable to acquire lock for update operation");
        }
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

    private void bindAll(){
        bindControls();
        bindGauges();
        bindIndicators();
        bindLabels();
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
            public void onAdded(Integer key, TrainModelSubject value) {
                    updateChoiceBoxItems();
                    trainDropDown.getSelectionModel().select(value.getModel().getTrainNumber());
            }
            public void onRemoved(Integer key, TrainModelSubject value) {
                    updateChoiceBoxItems();
                    // Additional logic can be added here to select another item if the current selection was removed
            }
            public void onUpdated(Integer key, TrainModelSubject oldValue, TrainModelSubject newValue) {
                 updateChoiceBoxItems();
                trainDropDown.getSelectionModel().select(newValue.getModel().getTrainNumber());
            }
        };
        subjects.addChangeListener(genericListener);
    }

    private void updateChoiceBoxItems() {
        List<Integer> trainIDs = new ArrayList<>(subjectMap.getSubjects().keySet());
            trainDropDown.setItems(FXCollections.observableArrayList(trainIDs));

            if (!trainIDs.isEmpty()) {
                if(trainIDs.size() == 1){
                    trainDropDown.getSelectionModel().selectFirst();
                }
                Integer previousSelection = trainDropDown.getSelectionModel().getSelectedItem();
                trainDropDown.getSelectionModel().select(previousSelection);
            } else {
                logger.info("No trains available to select");
                changeTrainView(-1);
            }
    }

    private void setUpCircleColors() {
        List<Circle> circleList =  Arrays.asList(extLightsEn, intLightsEn, leftDoorsEn, rightDoorsEn, sBrakeEn, eBrakeEn);
        for (Circle c : circleList){
            c.setFill(Color.GRAY);
        }
    }
}