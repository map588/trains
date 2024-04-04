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

import static trainController.Controller_Property.*;

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

    private final TrainControllerSubject nullSubject = new TrainControllerSubject();
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();


    @FXML
    public void initialize() {
        System.out.println("Started Train Controller Manager Initialize");

        subjectMap = TrainControllerSubjectMap.getInstance();
        setupMapChangeListener();
        trainNoChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs,oldSelection,newSelection) ->{
            if (newSelection != null){
                changeTrainView(newSelection);
            }else{
                changeTrainView(oldSelection);
            }
        });

//        if (!subjectMap.getSubjects().isEmpty()){
//            changeTrainView(subjectMap.getSubjects().keySet().iterator().next());
//        }else{
//            System.out.println("No trains to display");
//            statusLog.setText("No Trains to Display");
//            currentSubject = nullSubject;
//            updateAll();
//        }

        if (!subjectMap.getSubjects().isEmpty()) {
            Integer firstKey = subjectMap.getSubjects().keySet().iterator().next();
            changeTrainView(firstKey);
            currentSubject = subjectMap.getSubject(firstKey);

            //currentSubject.setProperty(AUTOMATIC_MODE_PROPERTY, true);
        }else{
            statusLog.setText("No Trains Available");
        }


        trainNoChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                changeTrainView(newSelection);
            }
        });


        currentSubject.setProperty(AUTOMATIC_MODE.getPropertyName(), true);

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
            trainNoChoiceBox.setItems(FXCollections.observableArrayList(new ArrayList<>(subjectMap.getSubjects().keySet())));
            trainNoChoiceBox.getSelectionModel().select(previousSelection);
    }

    private void bindGauges() {
        currentSpeedGauge.valueProperty().bind(currentSubject.getDoubleProperty(CURRENT_SPEED));
        commandedSpeedGauge.valueProperty().bind(currentSubject.getDoubleProperty(COMMAND_SPEED));
        speedLimitGauge.valueProperty().bind(currentSubject.getDoubleProperty(SPEED_LIMIT));
        authorityGauge.valueProperty().bind(currentSubject.getIntegerProperty(AUTHORITY));
        currentTemperatureGauge.valueProperty().bind(currentSubject.getDoubleProperty(CURRENT_TEMPERATURE));
        appendListener(currentSubject.getDoubleProperty(POWER), (obs, oldVal, newVal) -> {
            double p = currentSubject.getDoubleProperty(POWER).get();
            powerOutputGauge.setValue(p);
        });
    }

    private void bindIndicators() {
        appendListener(currentSubject.getBooleanProperty(EMERGENCY_BRAKE), (obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.RED, eBrakeStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(SIGNAL_FAILURE),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.RED, signalFailureStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(BRAKE_FAILURE),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.RED, brakeFailureStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(POWER_FAILURE),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.RED, powerFailureStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(IN_TUNNEL),(obs, oldVal, newVal) ->     Platform.runLater(() -> {updateIndicator(Color.YELLOW, inTunnelStatus, newVal); inTunnelUpdates();}));
        appendListener(currentSubject.getBooleanProperty(LEFT_PLATFORM),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.LIGHTGREEN, stationSideLeftStatus, newVal)));
        appendListener(currentSubject.getBooleanProperty(RIGHT_PLATFORM),(obs, oldVal, newVal) -> Platform.runLater(() -> updateIndicator(Color.LIGHTGREEN, stationSideRightStatus, newVal)));
        bindStringText(nextStationText, NEXT_STATION.getPropertyName());
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
            currentSubject.setProperty(OVERRIDE_SPEED.getPropertyName(), newValue);
        });
        bindCheckBox(intLightCheckBox, INT_LIGHTS);
        bindCheckBox(extLightCheckBox, EXT_LIGHTS);
        bindCheckBox(openDoorLeftCheckBox, LEFT_DOORS);
        bindCheckBox(openDoorRightCheckBox, RIGHT_DOORS);
        bindCheckBox(toggleServiceBrakeCheckBox, SERVICE_BRAKE);
        bindCheckBox(autoModeCheckBox, AUTOMATIC_MODE);
        bindDoubleTextField(setTemperatureTextField, SET_TEMPERATURE);
        bindDoubleTextField(setKiTextField, KI);
        bindDoubleTextField(setKpTextField, KP);
        setupButtonActions();

        appendListener(currentSubject.getStringProperty(ERROR), (obs, oldVal, newVal) -> {
            showErrorDialog("Error: ", newVal);
        });
    }

    private void bindCheckBox(CheckBox checkBox, Controller_Property propertyName) {
        appendListener(checkBox.selectedProperty(),(obs, oldVal, newVal) -> {
                currentSubject.setProperty(propertyName.getPropertyName(), newVal);
                setNotification(propertyName,String.valueOf(checkBox.isSelected()));
        });
    }

    private void bindDoubleTextField(TextField textField, Controller_Property property) {
        Runnable textFieldUpdate = () -> {
            try {
                currentSubject.setProperty(property.getPropertyName(), Double.parseDouble(textField.getText()));
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
            BooleanProperty eBrakeProp = currentSubject.getBooleanProperty(EMERGENCY_BRAKE);
            currentSubject.setProperty(EMERGENCY_BRAKE, !eBrakeProp.get());
            setNotification(EMERGENCY_BRAKE,String.valueOf(eBrakeProp.get()));
        });
        makeAnnouncementsButton.setOnAction(event -> {
            BooleanProperty announceProp = currentSubject.getBooleanProperty(ANNOUNCEMENTS);
            currentSubject.setProperty(ANNOUNCEMENTS.getPropertyName(), !announceProp.get());

            setNotification(ANNOUNCEMENTS,"");
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
            setNotification(OVERRIDE_SPEED,String.format("%.1f",newVal.doubleValue()));
        });
        Runnable textFieldUpdate = () -> {
            try {
                double newValue = Double.parseDouble(textField.getText());
                if (newValue < slider.getMin() || newValue > slider.getMax()) {
                    throw new NumberFormatException();
                }
                slider.setValue(newValue);
                setNotification(OVERRIDE_SPEED,String.format("%.1f",newValue));
            } catch (NumberFormatException e) {
                textField.setText(String.format("%.1f", slider.getValue()));
                setNotification(OVERRIDE_SPEED,String.format("%.1f",slider.getValue()));
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
            currentSpeedGauge.setValue(currentSubject.getDoubleProperty(CURRENT_SPEED).get());
            commandedSpeedGauge.setValue(currentSubject.getDoubleProperty(COMMAND_SPEED).get());
            speedLimitGauge.setValue(currentSubject.getDoubleProperty(SPEED_LIMIT).get());
            authorityGauge.setValue(currentSubject.getIntegerProperty(AUTHORITY).get());
            powerOutputGauge.setValue(currentSubject.getDoubleProperty(POWER).get());

            // Update indicators
            updateIndicator(Color.RED, eBrakeStatus, currentSubject.getBooleanProperty(EMERGENCY_BRAKE).get());
            updateIndicator(Color.RED, signalFailureStatus, currentSubject.getBooleanProperty(SIGNAL_FAILURE).get());
            updateIndicator(Color.RED, brakeFailureStatus, currentSubject.getBooleanProperty(BRAKE_FAILURE).get());
            updateIndicator(Color.RED, powerFailureStatus, currentSubject.getBooleanProperty(POWER_FAILURE).get());
            updateIndicator(Color.YELLOW, inTunnelStatus, currentSubject.getBooleanProperty(IN_TUNNEL).get());
            updateIndicator(Color.LIGHTGREEN, stationSideLeftStatus, currentSubject.getBooleanProperty(LEFT_PLATFORM).get());
            updateIndicator(Color.LIGHTGREEN, stationSideRightStatus, currentSubject.getBooleanProperty(RIGHT_PLATFORM).get());

            // Update checkboxes
            intLightCheckBox.setSelected(currentSubject.getBooleanProperty(INT_LIGHTS).get());
            extLightCheckBox.setSelected(currentSubject.getBooleanProperty(EXT_LIGHTS).get());
            openDoorLeftCheckBox.setSelected(currentSubject.getBooleanProperty(LEFT_DOORS).get());
            openDoorRightCheckBox.setSelected(currentSubject.getBooleanProperty(RIGHT_DOORS).get());
            toggleServiceBrakeCheckBox.setSelected(currentSubject.getBooleanProperty(SERVICE_BRAKE).get());
            autoModeCheckBox.setSelected(currentSubject.getBooleanProperty(AUTOMATIC_MODE).get());

            // Update text fields
            setTemperatureTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(SET_TEMPERATURE).get()));
            setKiTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(KI).get()));
            setKpTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(KP).get()));
            setSpeedTextField.setText(String.format("%.2f", currentSubject.getDoubleProperty(OVERRIDE_SPEED).get()));
            nextStationText.setText(currentSubject.getStringProperty(NEXT_STATION).get());

            // Update slider (Assuming it should match the overrideSpeed)
            setSpeedSlider.setValue(currentSubject.getDoubleProperty(OVERRIDE_SPEED).get());
            }catch (Exception e){
                System.out.println("Error updating UI elements");
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
    private void setNotification(Controller_Property propertyName, String value) {
        String statusNotification = switch (propertyName) {
            case OVERRIDE_SPEED -> "\nSet Speed to \n" + value + " MPH";
            case SERVICE_BRAKE, LEFT_DOORS, RIGHT_DOORS, INT_LIGHTS,
                    EXT_LIGHTS, AUTOMATIC_MODE ->
                    "\n" + getPropertyLabel(propertyName) + "\n" + (Boolean.parseBoolean(value) ? getOnLabel(propertyName) : getOffLabel(propertyName));
            case EMERGENCY_BRAKE -> "\nEmergency Brake \n" + value;
            case SET_TEMPERATURE -> "\nTemperature set to \n" + value + " \u00B0F";
            case KI -> "\nKi set to \n" + value;
            case KP -> "\nKp set to \n" + value;
            case ANNOUNCEMENTS -> "\nAnnouncements Created\n";
            default -> "\nTrain is running";
        };

        Platform.runLater(() -> {
            statusLog.setText(statusNotification);
            statusLog.setWrapText(true);
        });
    }

    private String getPropertyLabel(Controller_Property propertyName) {
        return switch (propertyName) {
            case SERVICE_BRAKE -> "Service Brake";
            case LEFT_DOORS -> "Left Doors";
            case RIGHT_DOORS -> "Right Doors";
            case INT_LIGHTS -> "Interior Lights";
            case EXT_LIGHTS -> "Exterior Lights";
            case AUTOMATIC_MODE -> "Automatic Mode";
            default -> "";
        };
    }

    private String getOnLabel(Controller_Property propertyName) {
        return switch (propertyName) {
            case SERVICE_BRAKE -> "Engaged";
            case LEFT_DOORS, RIGHT_DOORS -> "Opened";
            case INT_LIGHTS, EXT_LIGHTS, AUTOMATIC_MODE -> "On";
            default -> "";
        };
    }

    private String getOffLabel(Controller_Property propertyName) {
        return switch (propertyName) {
            case SERVICE_BRAKE -> "Disengaged";
            case LEFT_DOORS, RIGHT_DOORS -> "Closed";
            case INT_LIGHTS, EXT_LIGHTS, AUTOMATIC_MODE -> "Off";
            default -> "";
        };
    }
}