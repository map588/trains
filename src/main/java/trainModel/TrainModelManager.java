package trainModel;

import Framework.Support.ListenerReference;
import Framework.Support.ObservableHashMap;
import eu.hansolo.medusa.Gauge;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    TrainModelSubjectMap subjectMap;
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();
    private TrainModelSubject subject;
    //private TrainModelTB testBench;
    private final TrainModelSubject nullSubject = new TrainModelSubject();
    @FXML
    public void initialize() {
        System.out.println("Started TrainModelManager initialize");

        subjectMap = TrainModelSubjectMap.getInstance();
        setupMapChangeListener();

        trainDropDown.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeTrainView(newSelection);
            }
        });
        setUpCircleColors();

        if(!subjectMap.getSubjects().isEmpty()) {
            changeTrainView(subjectMap.getSubjects().keySet().iterator().next());
        }else{
            System.out.println("No trains to display");
            subject = nullSubject;
            updateView();
        }

        System.out.println("Finished TrainModelManager initialize");
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
                //System.out.println("newVal: " + newVal);
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

    private void changeTrainView(int trainID) {
        subject = subjectMap.getSubject(trainID);
        if(subject == null) {
            subject = nullSubject;
        }
        updateView();
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
                updateChoiceBoxItems();
            }

            @Override
            public void onRemoved(Integer key, TrainModelSubject value) {
                updateChoiceBoxItems();
            }

            @Override
            public void onUpdated(Integer key, TrainModelSubject oldValue, TrainModelSubject newValue) {
                updateChoiceBoxItems();
            }
        };

        subjects.addChangeListener(genericListener);
        updateChoiceBoxItems();
    }

    private void updateChoiceBoxItems() {
        int previousSelection = trainDropDown.getSelectionModel().getSelectedIndex();
        trainDropDown.setItems(FXCollections.observableArrayList(new ArrayList<>(subjectMap.getSubjects().keySet())));
        if(previousSelection != -1 || trainDropDown.getItems().isEmpty() || previousSelection >= trainDropDown.getItems().size()) {
            previousSelection = 0;
        }
        trainDropDown.getSelectionModel().select(previousSelection);
    }

    private TrainModelTB launchTestBench() {
       // System.out.println(System.getProperty("Preparing to launch test bench"));
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
       //     System.out.println("Failed to launch test bench");
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