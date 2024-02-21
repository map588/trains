package Framework.GUI.Managers;

import Framework.Support.ObservableHashMap;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import trainController.trainControllerImpl;
import trainController.trainControllerSubject;
import trainController.trainControllerSubjectFactory;

import java.util.ArrayList;
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

    //private trainControllerTB testBench;

    @FXML
    public void initialize() {
        //testBench = launchTestBench();

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
        currentSubject.getBooleanProperty("emergencyBrake").addListener((obs, oldVal, newVal) -> updateIndicator(eBrakeStatus, newVal));
        currentSubject.getBooleanProperty("signalFailure").addListener((obs, oldVal, newVal) -> updateIndicator(signalFailureStatus, newVal));
        currentSubject.getBooleanProperty("brakeFailure").addListener((obs, oldVal, newVal) -> updateIndicator(brakeFailureStatus, newVal));
        currentSubject.getBooleanProperty("powerFailure").addListener((obs, oldVal, newVal) -> updateIndicator(powerFailureStatus, newVal));
        currentSubject.getBooleanProperty("inTunnel").addListener((obs, oldVal, newVal) -> updateIndicator(inTunnelStatus, newVal));
        currentSubject.getBooleanProperty("leftPlatform").addListener((obs, oldVal, newVal) -> updateIndicator(stationSideLeftStatus, newVal));
        currentSubject.getBooleanProperty("rightPlatform").addListener((obs, oldVal, newVal) -> updateIndicator(stationSideRightStatus, newVal));
    }


    private void updateIndicator(Circle indicator, boolean isActive) {
        Platform.runLater(() -> indicator.setFill(isActive ? Color.RED : Color.GRAY));
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
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                currentSubject.setProperty(propertyName, newVal);
        });
    }


    private void bindTextField(TextField textField, String propertyName) {
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                // Parse and update property
                currentSubject.setProperty(propertyName, Double.parseDouble(newVal));
            } catch (NumberFormatException e) {
                textField.setText(oldVal); // Revert if parsing fails
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
        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            textField.setText(String.format("%.1f", newVal));
        });
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                double value = Double.parseDouble(newVal);
                // Preventing feedback loop
                if (Math.abs(value - slider.getValue()) > 0.01) {
                    consumer.accept(value);
                    slider.setValue(value); // This line may be redundant due to the consumer updating the model
                }
            } catch (NumberFormatException e) {
                textField.setText(oldVal);
            }
        });
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

//    private trainControllerTB launchTestBench(){
//        System.out.println("Preparing to launch test bench");
//        try{
//            String tbFile = "Framework/GUI/FXML/trainController_TB.fxml";
//            URL url = getClass().getResource(tbFile);
//            FXMLLoader loader = new FXMLLoader(url);
//            Node content = loader.load();
//            Stage newStage = new Stage();
//            Scene newScene = new Scene(new VBox(content));
//            newStage.setScene(newScene);
//            newStage.setTitle("Train Controller Test Bench");
//            newStage.show();
//            return loader.getController();
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            System.out.println("Failed to launch test bench");
//            throw new RuntimeException();
//        }
//    }
}