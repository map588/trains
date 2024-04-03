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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static trainController.Properties.*;

public class TrainControllerManager {

    @FXML
    AnchorPane masterTrainControllerPane;
    @FXML
    public TextArea statusLog;
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
    private Gauge currentSpeedGauge, speedLimitGauge, commandedSpeedGauge, authorityGauge, powerOutputGauge, currentTemperatureGauge;
    @FXML
    private Circle eBrakeStatus, signalFailureStatus, brakeFailureStatus, powerFailureStatus, stationSideLeftStatus, stationSideRightStatus, inTunnelStatus;
    @FXML
    private ChoiceBox<Integer> trainNoChoiceBox;

    private TrainControllerSubjectMap subjectMap;
    private TrainControllerSubject currentSubject;
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();


    @FXML
    public void initialize() {
        subjectMap = TrainControllerSubjectMap.getInstance();
        setupMapChangeListener();
        if (!subjectMap.getSubjects().isEmpty()) {
            Integer firstKey = subjectMap.getSubjects().keySet().iterator().next();
            changeTrainView(firstKey);
            currentSubject = subjectMap.getSubject(firstKey);

            //currentSubject.setProperty(AUTOMATIC_MODE_PROPERTY, true);
        }else{
            statusLog.setText("No Trains Available");
        }

        trainNoChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("Inside trainNoChoiceBox");
            if (newSelection != null) {
                System.out.println("Inside trainNoChoiceBox, inside if");
                changeTrainView(newSelection);
            }
        });

        // Error Occurs Here: currentSubject is Null here. Moved to line 64
        currentSubject.setProperty(AUTOMATIC_MODE_PROPERTY, true);

        emergencyBrakeButton.setStyle("-fx-background-color: #ff3333; -fx-text-fill: #ffffff;");

        System.out.println("End of initialization");
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
        currentTemperatureGauge.valueProperty().bind(currentSubject.getDoubleProperty(CURRENT_TEMPERATURE_PROPERTY));
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
        bindDoubleTextField(setTemperatureTextField, SET_TEMPERATURE_PROPERTY);
        bindDoubleTextField(setKiTextField, KI_PROPERTY);
        bindDoubleTextField(setKpTextField, KP_PROPERTY);
        setupButtonActions();

        appendListener(currentSubject.getStringProperty(ERROR_PROPERTY), (obs, oldVal, newVal) -> {
            showErrorDialog("Error: ", newVal);
        });
    }

    private void bindCheckBox(CheckBox checkBox, String propertyName) {
        appendListener(checkBox.selectedProperty(),(obs, oldVal, newVal) -> {
                currentSubject.setProperty(propertyName, newVal);
                setNotification(propertyName,String.valueOf(checkBox.isSelected()));
        });
    }

    private void bindDoubleTextField(TextField textField, String property) {
        Runnable textFieldUpdate = () -> {
            try {
                currentSubject.setProperty(property, Double.parseDouble(textField.getText()));
                setNotification(property, textField.getText());
            } catch (NumberFormatException e) {
                showErrorDialog("Invalid input", "Please enter a valid number.");
                textField.setText("");
            }
        };
        textField.setOnAction(event -> textFieldUpdate.run());
    }

     void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupButtonActions() {
        emergencyBrakeButton.setOnAction(event -> {
            BooleanProperty eBrakeProp = currentSubject.getBooleanProperty(EMERGENCY_BRAKE_PROPERTY);
            currentSubject.setProperty(EMERGENCY_BRAKE_PROPERTY, !eBrakeProp.get());
            setNotification(EMERGENCY_BRAKE_PROPERTY,"");
        });
        makeAnnouncementsButton.setOnAction(event -> {
            BooleanProperty announceProp = currentSubject.getBooleanProperty(ANNOUNCEMENTS_PROPERTY);
            currentSubject.setProperty(ANNOUNCEMENTS_PROPERTY, !announceProp.get());

            setNotification(ANNOUNCEMENTS_PROPERTY,"");
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
            setNotification(OVERRIDE_SPEED_PROPERTY,String.format("%.1f",newVal.doubleValue()));
        });
        Runnable textFieldUpdate = () -> {
            try {
                double newValue = Double.parseDouble(textField.getText());
                if (newValue < slider.getMin() || newValue > slider.getMax()) {
                    throw new NumberFormatException();
                }
                slider.setValue(newValue);
                setNotification(OVERRIDE_SPEED_PROPERTY,String.format("%.1f",newValue));
            } catch (NumberFormatException e) {
                textField.setText(String.format("%.1f", slider.getValue()));
                setNotification(OVERRIDE_SPEED_PROPERTY,String.format("%.1f",slider.getValue()));
            }
        };

       textField.setOnAction(event -> textFieldUpdate.run());
    }


    private <T> void appendListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        listenerReferences.add(new ListenerReference<>(observable, listener));
    }


    private void changeTrainView(Integer trainID) {
        if(currentSubject != null) {
            statusLog.clear();
            statusLog.setText("\n Train is running");
            unbindControls();
            updateAll();
            currentSubject = subjectMap.getSubject(trainID);
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
            setTemperatureTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(SET_TEMPERATURE_PROPERTY).get()));
            setKiTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(KI_PROPERTY).get()));
            setKpTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(KP_PROPERTY).get()));
            setSpeedTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(OVERRIDE_SPEED_PROPERTY).get()));
            nextStationText.setText(currentSubject.getStringProperty(NEXT_STATION_PROPERTY).get());

            // Update slider (Assuming it should match the overrideSpeed)
            setSpeedSlider.setValue(currentSubject.getDoubleProperty(OVERRIDE_SPEED_PROPERTY).get());
            }catch (Exception e){

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
    private void setNotification(String propertyName, String value) {
        String statusNotification = switch (propertyName) {
            case OVERRIDE_SPEED_PROPERTY -> "\nSet Speed to \n" + value + " MPH";
            case SERVICE_BRAKE_PROPERTY, LEFT_DOORS_PROPERTY, RIGHT_DOORS_PROPERTY, INT_LIGHTS_PROPERTY,
                    EXT_LIGHTS_PROPERTY, AUTOMATIC_MODE_PROPERTY ->
                    "\n" + getPropertyLabel(propertyName) + "\n" + (Boolean.parseBoolean(value) ? getOnLabel(propertyName) : getOffLabel(propertyName));
            case EMERGENCY_BRAKE_PROPERTY -> "\nEmergency Brake \n" + value;
            case SET_TEMPERATURE_PROPERTY -> "\nTemperature set to \n" + value + "°F";
            case KI_PROPERTY -> "\nKi set to \n" + value;
            case KP_PROPERTY -> "\nKp set to \n" + value;
            case ANNOUNCEMENTS_PROPERTY -> "\nAnnouncements Created\n";
            default -> "\nTrain is running";
        };

        Platform.runLater(() -> {
            statusLog.setText(statusNotification);
            statusLog.setWrapText(true);
        });
    }

    private String getPropertyLabel(String propertyName) {
        return switch (propertyName) {
            case SERVICE_BRAKE_PROPERTY -> "Service Brake";
            case LEFT_DOORS_PROPERTY -> "Left Doors";
            case RIGHT_DOORS_PROPERTY -> "Right Doors";
            case INT_LIGHTS_PROPERTY -> "Interior Lights";
            case EXT_LIGHTS_PROPERTY -> "Exterior Lights";
            case AUTOMATIC_MODE_PROPERTY -> "Automatic Mode";
            default -> "";
        };
    }

    private String getOnLabel(String propertyName) {
        return switch (propertyName) {
            case SERVICE_BRAKE_PROPERTY -> "Engaged";
            case LEFT_DOORS_PROPERTY, RIGHT_DOORS_PROPERTY -> "Opened";
            case INT_LIGHTS_PROPERTY, EXT_LIGHTS_PROPERTY, AUTOMATIC_MODE_PROPERTY -> "On";
            default -> "";
        };
    }

    private String getOffLabel(String propertyName) {
        return switch (propertyName) {
            case SERVICE_BRAKE_PROPERTY -> "Disengaged";
            case LEFT_DOORS_PROPERTY, RIGHT_DOORS_PROPERTY -> "Closed";
            case INT_LIGHTS_PROPERTY, EXT_LIGHTS_PROPERTY, AUTOMATIC_MODE_PROPERTY -> "Off";
            default -> "";
        };
    }
}