package Framework.GUI.Managers;

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
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import trainController.trainControllerImpl;
import trainController.trainControllerSubject;
import trainController.trainControllerSubjectFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class trainControllerManager {

    @FXML
    private Text nextStationText;
    @FXML
    private Rectangle stationInf, blockInfo;
    @FXML
    private CheckBox intLightCheckBox, extLightCheckBox, openDoorLeftCheckBox, openDoorRightCheckBox, toggleServiceBrakeCheckBox, autoModeCheckBox;
    @FXML
    private TextField setTemperatureTextField, setKiTextField, setKpTextField, setSpeedTextField;
    @FXML
    private Button emergencyBrakeButton, makeAnnouncementsButton;
    @FXML
    private Slider setSpeedSlider;
    @FXML
    private Gauge currentSpeedGauge, speedLimitGauge, commandedSpeedGauge, authorityGauge, powerOutputGauge;
    @FXML
    private Circle eBrakeStatus, signalFailureStatus, brakeFailureStatus, powerFailureStatus, stationSideLeftStatus, stationSideRightStatus, inTunnelStatus;
    @FXML
    private ChoiceBox<Integer> trainNoChoiceBox;

    private trainControllerSubjectFactory factory;
    private trainControllerSubject currentSubject;
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();


    private trainControllerTB testBench;

    @FXML
    public void initialize() {
        //Creating a trainControllerImpl object results in a subject being created
        //and that subject being added to the factories Map of subjects
        new trainControllerImpl(0);
        new trainControllerImpl(1);

        factory = trainControllerSubjectFactory.getInstance();
        setupMapChangeListener();


        // Select the first train by default if available
        if (!factory.getSubjects().isEmpty()) {
            Integer firstKey = factory.getSubjects().keySet().iterator().next();
            changeTrainView(firstKey);
        }
        trainNoChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeTrainView(newSelection);
            }
        });
        testBench = launchTestBench();
        emergencyBrakeButton.setStyle("-fx-background-color: #FF5733; -fx-text-fill: #ffffff;");

    }

    private void setupMapChangeListener() {
        ObservableHashMap<Integer, trainControllerSubject> subjects = factory.getSubjects();

        // Create a listener that reacts to any change (add, remove, update) by updating choice box items
        ObservableHashMap.MapListener<Integer, trainControllerSubject> genericListener = new ObservableHashMap.MapListener<>() {
            @Override
            public void onAdded(Integer key, trainControllerSubject value) {
                updateChoiceBoxItems();
            }

            @Override
            public void onRemoved(Integer key, trainControllerSubject value) {
                updateChoiceBoxItems();
            }

            @Override
            public void onUpdated(Integer key, trainControllerSubject oldValue, trainControllerSubject newValue) {
                updateChoiceBoxItems();
            }
        };

        subjects.addChangeListener(genericListener);
        updateChoiceBoxItems();
    }


    private void updateChoiceBoxItems() {
        Platform.runLater(() -> {
            trainNoChoiceBox.setItems(FXCollections.observableArrayList(
                    new ArrayList<>(factory.getSubjects().keySet())));
        });
    }

    private void bindGauges() {
        currentSpeedGauge.valueProperty().bind(currentSubject.getDoubleProperty("currentSpeed"));
        commandedSpeedGauge.valueProperty().bind(currentSubject.getDoubleProperty("commandSpeed"));
        speedLimitGauge.valueProperty().bind(currentSubject.getDoubleProperty("maxSpeed"));
        authorityGauge.valueProperty().bind(currentSubject.getIntegerProperty("authority"));
        powerOutputGauge.valueProperty().bind(currentSubject.getDoubleProperty("power"));
        //trainController_blocksToNextStation_Gauge.valueProperty().bind(currentSubject.getIntegerProperty("blocksToNextStation"));
    }

    private void bindIndicators() {
        appendListener(currentSubject.getBooleanProperty("emergencyBrake"), (obs, oldVal, newVal) -> updateIndicator(Color.RED, eBrakeStatus, newVal));
        appendListener(currentSubject.getBooleanProperty("signalFailure"),(obs, oldVal, newVal) -> updateIndicator(Color.RED, signalFailureStatus, newVal));
        appendListener(currentSubject.getBooleanProperty("brakeFailure"),(obs, oldVal, newVal) -> updateIndicator(Color.RED, brakeFailureStatus, newVal));
        appendListener(currentSubject.getBooleanProperty("powerFailure"),(obs, oldVal, newVal) -> updateIndicator(Color.RED, powerFailureStatus, newVal));
        appendListener(currentSubject.getBooleanProperty("inTunnel"),(obs, oldVal, newVal) -> updateIndicator(Color.YELLOW, inTunnelStatus, newVal));
        appendListener(currentSubject.getBooleanProperty("leftPlatform"),(obs, oldVal, newVal) -> updateIndicator(Color.LIGHTGREEN, stationSideLeftStatus, newVal));
        appendListener(currentSubject.getBooleanProperty("rightPlatform"),(obs, oldVal, newVal) -> updateIndicator(Color.LIGHTGREEN, stationSideRightStatus, newVal));
    }


    private void updateIndicator(Color color, Circle indicator, boolean isActive) {
        Platform.runLater(() -> indicator.setFill(isActive ? color : Color.GRAY));
    }

    private void bindControls() {
        // Binding Slider and TextField for "overrideSpeed"
        bindSliderAndTextField(setSpeedSlider, setSpeedTextField, newValue -> {
            currentSubject.setProperty("overrideSpeed", newValue);
        });

        // Binding CheckBoxes
        bindCheckBox(intLightCheckBox, "intLights");
        bindCheckBox(extLightCheckBox, "extLights");
        bindCheckBox(openDoorLeftCheckBox, "leftDoors");
        bindCheckBox(openDoorRightCheckBox, "rightDoors");
        bindCheckBox(toggleServiceBrakeCheckBox, "serviceBrake");
        bindCheckBox(autoModeCheckBox, "automaticMode");

        // Binding TextFields for numeric properties
        bindTextField(setTemperatureTextField, "temperature");
        bindTextField(setKiTextField, "Ki");
        bindTextField(setKpTextField, "Kp");

        // Setting up Button actions
        setupButtonActions();
    }

    private void bindCheckBox(CheckBox checkBox, String propertyName) {
        appendListener(checkBox.selectedProperty(),(obs, oldVal, newVal) -> {
                currentSubject.setProperty(propertyName, newVal);
        });
    }


    private void bindTextField(TextField textField, String propertyName) {
        appendListener(textField.textProperty(),(obs, oldVal, newVal) -> {
            try {
                // Parse and update property
                currentSubject.setProperty(propertyName, Double.parseDouble(newVal));
            } catch (NumberFormatException e) {
                // Clear if invalid input
                textField.setText("");
            }
        });
    }

    private void setupButtonActions() {
        emergencyBrakeButton.setOnAction(event -> {
            BooleanProperty eBrakeProp = currentSubject.getBooleanProperty("emergencyBrake");
            currentSubject.setProperty("emergencyBrake", !eBrakeProp.get());
        });
        makeAnnouncementsButton.setOnAction(event -> {
            BooleanProperty announceProp = currentSubject.getBooleanProperty("announcements");
            currentSubject.setProperty("announcements", !announceProp.get());
        });
    }



    private void bindSliderAndTextField(Slider slider, TextField textField, Consumer<Double> consumer) {
        appendListener(slider.valueProperty(), (obs, oldVal, newVal) -> {
            if(Math.abs(oldVal.doubleValue() - newVal.doubleValue()) < 0.1) {return;}
            textField.setText(String.format("%.1f", newVal));
            consumer.accept(newVal.doubleValue());
        });
        appendListener(textField.focusedProperty(),(obs, wasFocused, isNowFocused) -> {
            if (wasFocused && !isNowFocused) { // TextField has lost focus
                String newVal = textField.getText();
                if(newVal.isEmpty()) {return;}
                try {
                    double value = Double.parseDouble(newVal);
                    // Preventing feedback loop
                    if (Math.abs(value - slider.getValue()) > 0.1) {
                        consumer.accept(value);
                        slider.setValue(value); // This line may be redundant due to the consumer updating the model
                    }
                } catch (NumberFormatException e) {
                    textField.setText("");
                }
            }
        });
    }


    private <T> void appendListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        listenerReferences.add(new ListenerReference<>(observable, listener));
    }


    private void changeTrainView(Integer trainID) {
        currentSubject = factory.getSubjects().get(trainID);
        if(currentSubject != null) {
            unbindControls();
            bindControls();
            bindGauges();
            bindIndicators();
        }
    }

    private void unbindControls() {
        listenerReferences.forEach(ListenerReference::detach);
        listenerReferences.clear();

        currentSpeedGauge.valueProperty().unbind();
        commandedSpeedGauge.valueProperty().unbind();
        speedLimitGauge.valueProperty().unbind();
        authorityGauge.valueProperty().unbind();
        powerOutputGauge.valueProperty().unbind();

        setSpeedSlider.valueProperty().unbind(); // Assuming you might bind this bidirectionally in another part of your code
        setSpeedTextField.textProperty().unbind();

        intLightCheckBox.selectedProperty().unbind();
        extLightCheckBox.selectedProperty().unbind();
        openDoorLeftCheckBox.selectedProperty().unbind();
        openDoorRightCheckBox.selectedProperty().unbind();
        toggleServiceBrakeCheckBox.selectedProperty().unbind();
        autoModeCheckBox.selectedProperty().unbind();

        setTemperatureTextField.textProperty().unbind();
        setKiTextField.textProperty().unbind();
        setKpTextField.textProperty().unbind();
    }

    private trainControllerTB launchTestBench(){
        System.out.println(System.getProperty("Preparing to launch test bench"));
        try {
            String tbFile = "/Framework/GUI/FXML/trainController_TB.fxml";
            URL url = getClass().getResource(tbFile);
            FXMLLoader loader = new FXMLLoader(url);
            Node content = loader.load();
            Stage newStage = new Stage();
            Scene newScene = new Scene(new VBox(content));
            newStage.setScene(newScene);
            newStage.setTitle("Train Controller Test Bench");
            newStage.show();
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to launch test bench");
            throw new RuntimeException(e);
        }
    }
}