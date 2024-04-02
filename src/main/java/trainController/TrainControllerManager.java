package trainController;

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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static trainController.Properties.*;

public class TrainControllerManager {
    @FXML AnchorPane masterTrainControllerPane;
    @FXML
    public Text controllerStatus;
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

    private TrainControllerSubjectMap subjectMap;
    private TrainControllerSubject currentSubject;
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();



    @FXML
    public void initialize() {
        //new TrainControllerImpl(1);
        subjectMap = TrainControllerSubjectMap.getInstance();
        setupMapChangeListener();
        if (!subjectMap.getSubjects().isEmpty()) {
            Integer firstKey = subjectMap.getSubjects().keySet().iterator().next();
            changeTrainView(firstKey);
        }
        trainNoChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeTrainView(newSelection);
            }
        });
        currentSubject.setProperty(AUTOMATIC_MODE_PROPERTY, true);
        emergencyBrakeButton.setStyle("-fx-background-color: #ff3333; -fx-text-fill: #ffffff;");
    }

    private void setupMapChangeListener() {
        ObservableHashMap<Integer, TrainControllerSubject> subjects = subjectMap.getSubjects();

        //Defining a generic listener for the map, overriding the methods to only update the choice box items
        ObservableHashMap.MapListener<Integer, TrainControllerSubject> genericListener = new ObservableHashMap.MapListener<>() {
            public void onAdded(Integer key, TrainControllerSubject value) {
                updateChoiceBoxItems();
            }
            public void onRemoved(Integer key, TrainControllerSubject value) {
                updateChoiceBoxItems();
            }
            public void onUpdated(Integer key, TrainControllerSubject oldValue, TrainControllerSubject newValue) {
                updateChoiceBoxItems();
            }
        };

        subjects.addChangeListener(genericListener);
        updateChoiceBoxItems();
    }


    private void updateChoiceBoxItems() {
        int previousSelection = trainNoChoiceBox.getSelectionModel().getSelectedIndex();
        Platform.runLater(() -> {
            trainNoChoiceBox.setItems(FXCollections.observableArrayList(
                    new ArrayList<>(subjectMap.getSubjects().keySet())));
            trainNoChoiceBox.getSelectionModel().select(previousSelection);
        });
    }

    private void bindGauges() {
        currentSpeedGauge.valueProperty().bind(currentSubject.getDoubleProperty(CURRENT_SPEED_PROPERTY));
        commandedSpeedGauge.valueProperty().bind(currentSubject.getDoubleProperty(COMMAND_SPEED_PROPERTY));
        speedLimitGauge.valueProperty().bind(currentSubject.getDoubleProperty(SPEED_LIMIT_PROPERTY));
        authorityGauge.valueProperty().bind(currentSubject.getIntegerProperty(AUTHORITY_PROPERTY));
        appendListener(currentSubject.getDoubleProperty(POWER_PROPERTY), (obs, oldVal, newVal) -> {
            double p = currentSubject.getDoubleProperty(POWER_PROPERTY).get();
            powerOutputGauge.setValue(p);
        });
    }

    private void bindIndicators() {
        appendListener(currentSubject.getBooleanProperty(EMERGENCY_BRAKE_PROPERTY), (obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.RED, eBrakeStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(SIGNAL_FAILURE_PROPERTY),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.RED, signalFailureStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(BRAKE_FAILURE_PROPERTY),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.RED, brakeFailureStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(POWER_FAILURE_PROPERTY),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.RED, powerFailureStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(IN_TUNNEL_PROPERTY),(obs, oldVal, newVal) ->     Platform.runLater(() -> {updateIndicator(Color.YELLOW, inTunnelStatus, newVal); inTunnelUpdates();}));
        appendListener(currentSubject.getBooleanProperty(LEFT_PLATFORM_PROPERTY),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.LIGHTGREEN, stationSideLeftStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(RIGHT_PLATFORM_PROPERTY),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.LIGHTGREEN, stationSideRightStatus, newVal)));
        bindStringText(nextStationText, NEXT_STATION_PROPERTY);
    }
    private void bindStringText(Text text, String propertyName){
        appendListener(currentSubject.getProperty(propertyName),(obs,oldVal,newVal) -> {
            Platform.runLater(()-> text.setText((String)newVal));
        });
    }

    private void updateIndicator(Color color, Circle indicator, boolean isActive) {
        indicator.setFill(isActive ? color : Color.GRAY);
    }

    private void bindControls() {
        bindSliderAndTextField(setSpeedSlider, setSpeedTextField, newValue -> {
            currentSubject.setProperty(OVERRIDE_SPEED_PROPERTY, newValue);
        });
        bindCheckBox(intLightCheckBox, INT_LIGHTS_PROPERTY);
        bindCheckBox(extLightCheckBox, EXT_LIGHTS_PROPERTY);
        bindCheckBox(openDoorLeftCheckBox, LEFT_DOORS_PROPERTY);
        bindCheckBox(openDoorRightCheckBox, RIGHT_DOORS_PROPERTY);
        bindCheckBox(toggleServiceBrakeCheckBox, SERVICE_BRAKE_PROPERTY);
        bindCheckBox(autoModeCheckBox, AUTOMATIC_MODE_PROPERTY);
        bindDoubleTextField(setTemperatureTextField, TEMPERATURE_PROPERTY);
        bindDoubleTextField(setKiTextField, KI_PROPERTY);
        bindDoubleTextField(setKpTextField, KP_PROPERTY);
        setupButtonActions();
    }

    private void bindCheckBox(CheckBox checkBox, String propertyName) {
        appendListener(checkBox.selectedProperty(),(obs, oldVal, newVal) -> {
                currentSubject.setProperty(propertyName, newVal);
        });
    }

    private void bindDoubleTextField(TextField textField, String propertyName) {
        Runnable textFieldUpdate = () -> {
            try {
                currentSubject.setProperty(propertyName, Double.parseDouble(textField.getText()));
            } catch (NumberFormatException e) {
                textField.setText("");
            }
        };
        textField.setOnAction(event -> textFieldUpdate.run());
    }

    private void setupButtonActions() {
        emergencyBrakeButton.setOnAction(event -> {
            BooleanProperty eBrakeProp = currentSubject.getBooleanProperty(EMERGENCY_BRAKE_PROPERTY);
            currentSubject.setProperty(EMERGENCY_BRAKE_PROPERTY, !eBrakeProp.get());
        });
        makeAnnouncementsButton.setOnAction(event -> {
            BooleanProperty announceProp = currentSubject.getBooleanProperty(ANNOUNCEMENTS_PROPERTY);
            currentSubject.setProperty(ANNOUNCEMENTS_PROPERTY, !announceProp.get());

            if (!nextStationText.getText().contains("yard") && !nextStationText.getText().contains("Yard")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Arrival");
                alert.setHeaderText(null);
                alert.setContentText("Arriving at " + nextStationText.getText());
                alert.showAndWait();
            }
        });
    }



    private void bindSliderAndTextField(Slider slider, TextField textField, Consumer<Double> consumer) {
        appendListener(slider.valueProperty(), (obs, oldVal, newVal) -> {
            if(Math.abs(oldVal.doubleValue() - newVal.doubleValue()) < 0.1) {return;}
            consumer.accept(newVal.doubleValue());
            textField.setText(String.format("%.1f", newVal.doubleValue()));
            String statusNotification = "Set speed to " + String.format("%.1f",newVal.doubleValue())+ " MPH";
            setNotification(statusNotification);
        });
        Runnable textFieldUpdate = () -> {
            try {
                double newValue = Double.parseDouble(textField.getText());
                if (newValue < slider.getMin() || newValue > slider.getMax()) {
                    throw new NumberFormatException();
                }
                slider.setValue(newValue);
                String statusNotification = "Set speed to " + String.format("%.1f",newValue) + " MPH";
                setNotification(statusNotification);
            } catch (NumberFormatException e) {
                textField.setText(String.format("%.1f", slider.getValue()));
                String statusNotification = "Set speed to " + String.format("%.1f",slider.getValue()) + " MPH";
                setNotification(statusNotification);
            }
        };

       textField.setOnAction(event -> textFieldUpdate.run());
    }


    private <T> void appendListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        listenerReferences.add(new ListenerReference<>(observable, listener));
    }


    private void changeTrainView(Integer trainID) {
        currentSubject = subjectMap.getSubject(trainID);
        if(currentSubject != null) {
            unbindControls();
            updateAll();
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

    //Called when controller is switched, updates state of all UI elements
    private void updateAll() {
        if (currentSubject == null) {
            System.out.println("No subject to update");
            return;
        }

        //Batch update all properties
            try{
            currentSpeedGauge.setValue(currentSubject.getDoubleProperty(CURRENT_SPEED_PROPERTY).get());
            commandedSpeedGauge.setValue(currentSubject.getDoubleProperty(COMMAND_SPEED_PROPERTY).get());
            speedLimitGauge.setValue(currentSubject.getDoubleProperty(SPEED_LIMIT_PROPERTY).get());
            authorityGauge.setValue(currentSubject.getIntegerProperty(AUTHORITY_PROPERTY).get());
            powerOutputGauge.setValue(currentSubject.getDoubleProperty(POWER_PROPERTY).get());

            // Update indicators
            updateIndicator(Color.RED, eBrakeStatus, currentSubject.getBooleanProperty(EMERGENCY_BRAKE_PROPERTY).get());
            updateIndicator(Color.RED, signalFailureStatus, currentSubject.getBooleanProperty(SIGNAL_FAILURE_PROPERTY).get());
            updateIndicator(Color.RED, brakeFailureStatus, currentSubject.getBooleanProperty(BRAKE_FAILURE_PROPERTY).get());
            updateIndicator(Color.RED, powerFailureStatus, currentSubject.getBooleanProperty(POWER_FAILURE_PROPERTY).get());
            updateIndicator(Color.YELLOW, inTunnelStatus, currentSubject.getBooleanProperty(IN_TUNNEL_PROPERTY).get());
            updateIndicator(Color.LIGHTGREEN, stationSideLeftStatus, currentSubject.getBooleanProperty(LEFT_PLATFORM_PROPERTY).get());
            updateIndicator(Color.LIGHTGREEN, stationSideRightStatus, currentSubject.getBooleanProperty(RIGHT_PLATFORM_PROPERTY).get());

            // Update checkboxes
            intLightCheckBox.setSelected(currentSubject.getBooleanProperty(INT_LIGHTS_PROPERTY).get());
            extLightCheckBox.setSelected(currentSubject.getBooleanProperty(EXT_LIGHTS_PROPERTY).get());
            openDoorLeftCheckBox.setSelected(currentSubject.getBooleanProperty(LEFT_DOORS_PROPERTY).get());
            openDoorRightCheckBox.setSelected(currentSubject.getBooleanProperty(RIGHT_DOORS_PROPERTY).get());
            toggleServiceBrakeCheckBox.setSelected(currentSubject.getBooleanProperty(SERVICE_BRAKE_PROPERTY).get());
            autoModeCheckBox.setSelected(currentSubject.getBooleanProperty(AUTOMATIC_MODE_PROPERTY).get());

            // Update text fields
            setTemperatureTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(TEMPERATURE_PROPERTY).get()));
            setKiTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(KI_PROPERTY).get()));
            setKpTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(KP_PROPERTY).get()));
            setSpeedTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(OVERRIDE_SPEED_PROPERTY).get()));
            nextStationText.setText(currentSubject.getStringProperty(NEXT_STATION_PROPERTY).get());

            // Update slider (Assuming it should match the overrideSpeed)
            setSpeedSlider.setValue(currentSubject.getDoubleProperty(OVERRIDE_SPEED_PROPERTY).get());
            }catch (Exception e){
                e.printStackTrace();
            }

    }

    // Calls when the inTunnelStatus is updated, it checks off the intLights and extLights
    private void inTunnelUpdates(){

        if (autoModeCheckBox.isSelected()){ // Runs in Auto Mode

            boolean inTunnel = inTunnelStatus.getFill().equals(Color.YELLOW); // Get tunnel state

            // Set the Light states according to in tunnel status
            intLightCheckBox.setSelected(inTunnel);
            extLightCheckBox.setSelected(inTunnel);

            // Set the Color of the Background
            String colorFormat = inTunnel ? "-fx-background-color: #000033;" : "-fx-background-color: #FFFFFF;";
            masterTrainControllerPane.setStyle(colorFormat);
        }
    }

    // Set the current action
    private void setNotification(String statusNotification){
        controllerStatus.setText(statusNotification);
    }
}