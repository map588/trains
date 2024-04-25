package trainModel;

import Framework.Support.ListenerReference;
import Framework.Support.ObservableHashMap;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static trainModel.Properties.*;

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
    @FXML
    public AnchorPane masterTrainModelPane;

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
            if(subjectMap.getSubjects().isEmpty() || newSelection == null){
                logger.warn("No trains available to select");
                changeTrainView(-1);
            }else {
                changeTrainView(newSelection);
            }
        });

        //This information is required to be presented in the GUI.
        maxPowerLabel.setText("643.68");
        maxVelocityLabel.setText("43.48");
        medAccelerationLabel.setText("1.64");
        trainHeightLabel.setText("11.22");
        trainWidthLabel.setText("8.69");

        setUpCircleColors();
        eBrakeBtn.setStyle("-fx-background-color: #ff3333; -fx-text-fill: #ffffff;");
        logger.info("Finished TrainModelManager initialize");
    }

    private void bindLabels() {

        bindLabelToProperty(MASS_PROPERTY, massLabel);
        bindLabelToProperty(LENGTH_PROPERTY, trainLengthLabel);
        bindLabelToProperty(NUMCARS_PROPERTY, numCarsLabel);
        bindLabelToProperty(NUMPASSENGERS_PROPERTY, numPassengerLabel);
        bindLabelToProperty(CREWCOUNT_PROPERTY, crewCountLabel);
        bindLabelToProperty(GRADE_PROPERTY, gradeLabel);
    }

    private void bindLabelToProperty(String property, Label label) {
        appendListener(subject.getProperty(property) ,(obs, oldValue, newValue) -> {
                String newVal = newValue.toString();
                if(newVal.isEmpty()) {return;}
                try {
                    if(property.equals(MASS_PROPERTY) || property.equals(LENGTH_PROPERTY) || property.equals(GRADE_PROPERTY)) {
                        newVal = String.format("%.2f", Double.parseDouble(newVal));
                    }
                    label.setText(newVal);
                } catch (NumberFormatException e) {
                    label.setText("");
                }
        });
        if(property.equals(MASS_PROPERTY) || property.equals(LENGTH_PROPERTY) || property.equals(GRADE_PROPERTY)) {
            DoubleProperty prop = subject.getDoubleProperty(property);
            label.setText(String.format("%.2f", prop.getValue()));
        } else {
            label.setText(subject.getProperty(property).getValue().toString());
        }
    }

    private void changeTrainView(Integer trainID) {
            executeUpdate(() -> {
                unbindAll();
                if (trainID == -1) {
                    subject = nullSubject;
                    updateViewForNullSubject();
                    logger.info("Train Controller switched to null subject");
                } else {
                    subject = subjectMap.getSubjects().get(trainID);
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


    private void updateView() {
        actualPowerDisp.setValue(subject.getDoubleProperty(POWER_PROPERTY).get());
        actualVelocityDisp.setValue(subject.getDoubleProperty(ACTUALSPEED_PROPERTY).get());
        actualAccelerationDisp.setValue(subject.getDoubleProperty(ACCELERATION_PROPERTY).get());
        cmdSpeedDisp.setValue(subject.getDoubleProperty(COMMANDSPEED_PROPERTY).get());
        authorityDisp.setValue(subject.getIntegerProperty(AUTHORITY_PROPERTY).get());
        setTempDisp.setValue(subject.getDoubleProperty(SETTEMPERATURE_PROPERTY).get());
        realTempDisp.setValue(subject.getDoubleProperty(REALTEMPERATURE_PROPERTY).get());

        brakeFailureBtn.setSelected(subject.getBooleanProperty(BRAKEFAILURE_PROPERTY).get());
        powerFailureBtn.setSelected(subject.getBooleanProperty(POWERFAILURE_PROPERTY).get());
        signalFailureBtn.setSelected(subject.getBooleanProperty(SIGNALFAILURE_PROPERTY).get());

        updateEBrakeIndicator(subject.getBooleanProperty(EMERGENCYBRAKE_PROPERTY).get());
        updateSBrakeIndicator(subject.getBooleanProperty(SERVICEBRAKE_PROPERTY).get());
        updateExtLightsIndicator(subject.getBooleanProperty(EXTLIGHTS_PROPERTY).get());
        updateIntLightsIndicator(subject.getBooleanProperty(INTLIGHTS_PROPERTY).get());
        updateLeftDoorsIndicator(subject.getBooleanProperty(LEFTDOORS_PROPERTY).get());
        updateRightDoorsIndicator(subject.getBooleanProperty(RIGHTDOORS_PROPERTY).get());
        bindAll();
    }

    private void bindAll(){
        bindControls();
        bindGauges();
        bindIndicators();
        bindLabels();
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

    private void bindGauges() {
        actualPowerDisp.valueProperty().bind(subject.getDoubleProperty(POWER_PROPERTY));
        actualVelocityDisp.valueProperty().bind(subject.getDoubleProperty(ACTUALSPEED_PROPERTY));
        actualAccelerationDisp.valueProperty().bind(subject.getDoubleProperty(ACCELERATION_PROPERTY));
        cmdSpeedDisp.valueProperty().bind(subject.getDoubleProperty(COMMANDSPEED_PROPERTY));
        authorityDisp.valueProperty().bind(subject.getIntegerProperty(AUTHORITY_PROPERTY));
        setTempDisp.valueProperty().bind(subject.getDoubleProperty(SETTEMPERATURE_PROPERTY));
        realTempDisp.valueProperty().bind(subject.getDoubleProperty(REALTEMPERATURE_PROPERTY));
    }

    private void bindIndicators() {
        appendListener(subject.getBooleanProperty(EMERGENCYBRAKE_PROPERTY), (obs, oldSelection, newSelection) -> updateEBrakeIndicator(newSelection));
        appendListener(subject.getBooleanProperty(SERVICEBRAKE_PROPERTY), (obs, oldSelection, newSelection) -> updateSBrakeIndicator(newSelection));
        appendListener(subject.getBooleanProperty(EXTLIGHTS_PROPERTY), (obs, oldSelection, newSelection) -> updateExtLightsIndicator(newSelection));
        appendListener(subject.getBooleanProperty(INTLIGHTS_PROPERTY), (obs, oldSelection, newSelection) -> updateIntLightsIndicator(newSelection));
        appendListener(subject.getBooleanProperty(LEFTDOORS_PROPERTY), (obs, oldSelection, newSelection) -> updateLeftDoorsIndicator(newSelection));
        appendListener(subject.getBooleanProperty(RIGHTDOORS_PROPERTY), (obs, oldSelection, newSelection) -> updateRightDoorsIndicator(newSelection));
    }

    private void bindControls() {
        eBrakeBtn.setOnAction(event -> subject.setProperty(EMERGENCYBRAKE_PROPERTY, true));

        brakeFailureBtn.setOnAction(event -> {
            BooleanProperty brakeFailure = subject.getBooleanProperty(BRAKEFAILURE_PROPERTY);
            subject.setProperty(BRAKEFAILURE_PROPERTY, !brakeFailure.get());
        });

        powerFailureBtn.setOnAction(event -> {
            BooleanProperty powerFailure = subject.getBooleanProperty(POWERFAILURE_PROPERTY);
            subject.setProperty(POWERFAILURE_PROPERTY, !powerFailure.get());
        });

        signalFailureBtn.setOnAction(event -> {
            BooleanProperty signalFailure = subject.getBooleanProperty(SIGNALFAILURE_PROPERTY);
            subject.setProperty(SIGNALFAILURE_PROPERTY, !signalFailure.get());
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
        inTunnelBackgroundColor();
    }
    private void updateLeftDoorsIndicator(boolean active) {
        leftDoorsEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }
    private void updateRightDoorsIndicator(boolean active) {
        rightDoorsEn.setFill(active ? Color.YELLOW : Color.GRAY);
    }





    private void unbindAll() {
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
                   Platform.runLater(() -> {
                       updateChoiceBoxItems();
                       trainDropDown.getSelectionModel().select(value.getModel().getTrainNumber());
                   });
            }
            public void onRemoved(Integer key, TrainModelSubject value) {
                Platform.runLater(() -> {
                    updateChoiceBoxItems();
                    // Additional logic can be added here to select another item if the current selection was removed
                });
            }
            public void onUpdated(Integer key, TrainModelSubject oldValue, TrainModelSubject newValue) {
                Platform.runLater(() -> {
                 updateChoiceBoxItems();
                trainDropDown.getSelectionModel().select(newValue.getModel().getTrainNumber());
                });
            }
        };
        subjects.addChangeListener(genericListener);
    }

    private void updateChoiceBoxItems() {
        Platform.runLater(() -> {
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
        });
    }

    private void setUpCircleColors() {
        List<Circle> circleList =  Arrays.asList(extLightsEn, intLightsEn, leftDoorsEn, rightDoorsEn, sBrakeEn, eBrakeEn);
        for (Circle c : circleList){
            c.setFill(Color.GRAY);
        }
    }

    private void inTunnelBackgroundColor(){
        boolean inTunnel = (intLightsEn.getFill() != Color.GRAY);
        String colorFormat = inTunnel ? "-fx-background-color: #000033;" : "-fx-background-color: #FFFFFF;";
        masterTrainModelPane.setStyle(colorFormat);
    }
}