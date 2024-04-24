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
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static trainController.ControllerProperty.*;

public class TrainControllerManager {
    @FXML
    public CheckBox HWTrainCheckBox;
    public AnchorPane controllerStatusPane;
    public AnchorPane stationStatusPane;
    public AnchorPane cabinSettingsPane;
    public AnchorPane speedControllerPane;
    public AnchorPane brakePane;
    public Button killTrain;
    @FXML
    AnchorPane masterTrainControllerPane;
    @FXML
    public TextArea statusLog;
    @FXML
    private Text nextStationText;
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

    private final TrainControllerSubjectMap subjectMap = TrainControllerSubjectMap.getInstance();
    private final TrainControllerSubject   nullSubject = NullController.getInstance().getSubject();

    private TrainControllerSubject currentSubject;

    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(TrainControllerManager.class);

    private final ReentrantLock propertyChangeLock = new ReentrantLock();

    private final ScheduledExecutorService textUpdateExecutor = Executors.newSingleThreadScheduledExecutor();



    @FXML
    public void initialize() {
        logger.info("Started Train Controller Manager initialization");

        currentSubject = nullSubject; // Default to null object
        setupMapChangeListener();


        if (!subjectMap.getSubjects().isEmpty()) {
            updateChoiceBoxItems();
            Integer firstKey = subjectMap.getSubjects().keySet().iterator().next();
            logger.info("Initialized Train Controller with train ID: {}", firstKey);
            changeTrainView(firstKey);
        } else {
            statusLog.setText("No Trains Available");
            logger.warn("No trains available to initialize Train Controller");
            updateUIForNullSubject(); // Method to reset or initialize UI for null subject
        }

        trainNoChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if(subjectMap.getSubjects().isEmpty()){
                logger.warn("No controllers available to select");
                changeTrainView(-1);
            }
            if(newSelection == null){
                logger.warn("No selection made");
                changeTrainView(oldSelection);
            }else if (!subjectMap.getSubjects().containsKey(newSelection)) {
                logger.warn("Selected train is not in the map");
            }else{
                changeTrainView(newSelection);
            }
        });


        HWTrainCheckBox.setOnAction(event -> {
            if(TrainControllerFactory.getTrainLock()){
                HWTrainCheckBox.setSelected(false);
                logger.warn("Train Controller is locked, unable to change hardware mode");
                return;
            }

            TrainControllerFactory.HWController = HWTrainCheckBox.isSelected();
            logger.info("Hardware Train Controller set to {}", HWTrainCheckBox.isSelected());
        });

        emergencyBrakeButton.setStyle("-fx-background-color: #ff3333; -fx-text-fill: #ffffff;");
    }


    private void setupMapChangeListener() {
        ObservableHashMap<Integer, TrainControllerSubject> subjects = subjectMap.getSubjects();

        //Defining a generic listener for the map, overriding the methods to only update the choice box items
        ObservableHashMap.MapListener<Integer, TrainControllerSubject> genericListener = new ObservableHashMap.MapListener<>() {
            public void onAdded(Integer key, TrainControllerSubject value) {
                updateChoiceBoxItems();
                trainNoChoiceBox.getSelectionModel().select(value.getController().getID());
            }
            public void onRemoved(Integer key, TrainControllerSubject value) {
                updateChoiceBoxItems();
                if(subjects.isEmpty()) {
                    changeTrainView(-1);
                }else if(trainNoChoiceBox.getSelectionModel().getSelectedItem() == key){
                    trainNoChoiceBox.getSelectionModel().selectFirst();
                }
            }
            public void onUpdated(Integer key, TrainControllerSubject oldValue, TrainControllerSubject newValue) {
                updateChoiceBoxItems();
            }
        };
        subjects.addChangeListener(genericListener);
    }


    private void updateChoiceBoxItems() {
        List<Integer> trainIDs = new ArrayList<>(subjectMap.getSubjects().keySet());

       Platform.runLater(()-> {trainNoChoiceBox.setItems(FXCollections.observableArrayList(trainIDs));

        if (!trainIDs.isEmpty()) {
            if(trainIDs.size() == 1){
                trainNoChoiceBox.getSelectionModel().selectFirst();
            }else {
                Integer previousSelection = trainNoChoiceBox.getSelectionModel().getSelectedItem();
                trainNoChoiceBox.getSelectionModel().select(previousSelection);
            }
        } else {
            logger.info("No trains available after update.");
            changeTrainView(-1); // Explicitly handle no selection
        }
        });
    }


    private void changeTrainView(Integer trainID) {
        logger.warn("Switching Train Controller to train ID: {}", trainID);
            executeUpdate(() -> {
                unbindControls();
                if (trainID == -1 || !subjectMap.getSubjects().containsKey(trainID)) {
                    currentSubject = NullController.getInstance().getSubject();
                    updateUIForNullSubject();
                    logger.info("Train Controller switched to null subject");
                } else {
                    currentSubject = subjectMap.getSubjects().get(trainID);
                    updateView();
                    logger.info("Train Controller switched to train ID: {}", trainID);
                }
            });
    }

    void executeUpdate(Runnable updateOperation) {
        if (propertyChangeLock.tryLock()) {
            try {
                updateOperation.run();
            } finally {
                propertyChangeLock.unlock();
            }
        } else {
            Platform.runLater(updateOperation);
            logger.warn("Unable to acquire lock for update operation");
        }
    }

    private void bindAll(){
        bindControls();
        bindGauges();
        bindIndicators();
    }


    private void bindGauges() {
        appendListener(currentSubject.getDoubleProperty(CURRENT_SPEED), (obs, oldVal, newVal) -> {
            if(Math.abs(currentSpeedGauge.getValue() - newVal.doubleValue()) < 0.01) {return;} // Only update if there is a significant change (0.1 difference)
            currentSpeedGauge.setValue(newVal.doubleValue());
    //        logger.debug("Current speed gauge updated to {}", newVal);
        });
        appendListener(currentSubject.getDoubleProperty(COMMAND_SPEED), (obs, oldVal, newVal) -> {
            if(Math.abs(oldVal.doubleValue() - newVal.doubleValue()) < 0.01) {return;} // Only update if there is a significant change (0.1 difference)
            commandedSpeedGauge.setValue(newVal.doubleValue());
    //        logger.debug("Commanded speed gauge updated to {}", newVal);
        });
        appendListener(currentSubject.getDoubleProperty(SPEED_LIMIT), (obs, oldVal, newVal) -> {
            if(Math.abs(oldVal.doubleValue() - newVal.doubleValue()) < 0.01) {return;} // Only update if there is a significant change (0.1 difference)
            speedLimitGauge.setValue(newVal.doubleValue());
     //       logger.debug("Speed limit gauge updated to {}", newVal);
        });
        appendListener(currentSubject.getIntegerProperty(AUTHORITY), (obs, oldVal, newVal) -> {
            authorityGauge.setValue(newVal.intValue());
            logger.debug("Authority gauge updated to {}", newVal);
        });
        appendListener(currentSubject.getDoubleProperty(SET_TEMPERATURE), (obs, oldVal, newVal) -> {
            if(Math.abs(currentTemperatureGauge.getValue() - newVal.doubleValue()) < 0.1) {return;} // Only update if there is a significant change (0.1 difference)
            currentTemperatureGauge.setValue(newVal.doubleValue());
    //        logger.debug("Current temperature gauge updated to {}", newVal);
        });
        appendListener(currentSubject.getDoubleProperty(POWER), (obs, oldVal, newVal) -> {
            if(Math.abs(powerOutputGauge.getValue() - newVal.doubleValue()) < 0.01) {return;} // Only update if there is a significant change (0.1 difference)
            double p = currentSubject.getDoubleProperty(POWER).get();
            powerOutputGauge.setValue(p);
            if(Math.abs(oldVal.doubleValue() - newVal.doubleValue()) > 10){
                logger.debug("Power output jumped to {} from {}", p, oldVal);
            }
        });
    }

    private void bindIndicators() {
        appendListener(currentSubject.getBooleanProperty(EMERGENCY_BRAKE), (obs, oldVal, newVal) -> Platform.runLater(() -> {
            updateIndicator(Color.RED, eBrakeStatus, newVal);
   //         logger.info("Emergency brake status updated to {}", newVal);
        }));
        appendListener(currentSubject.getBooleanProperty(SIGNAL_FAILURE), (obs, oldVal, newVal) -> Platform.runLater(() -> {
            updateIndicator(Color.RED, signalFailureStatus, newVal);
    //        logger.info("Signal failure status updated to {}", newVal);
        }));
        appendListener(currentSubject.getBooleanProperty(BRAKE_FAILURE), (obs, oldVal, newVal) -> Platform.runLater(() -> {
            updateIndicator(Color.RED, brakeFailureStatus, newVal);
    //        logger.info("Brake failure status updated to {}", newVal);
        }));
        appendListener(currentSubject.getBooleanProperty(POWER_FAILURE), (obs, oldVal, newVal) -> Platform.runLater(() -> {
            updateIndicator(Color.RED, powerFailureStatus, newVal);
    //        logger.info("Power failure status updated to {}", newVal);
        }));
        appendListener(currentSubject.getBooleanProperty(IN_TUNNEL), (obs, oldVal, newVal) -> Platform.runLater(() -> {
            updateIndicator(Color.YELLOW, inTunnelStatus, newVal);
            inTunnelUpdates();
   //         logger.info("In tunnel status updated to {}", newVal);
        }));
        appendListener(currentSubject.getBooleanProperty(LEFT_PLATFORM), (obs, oldVal, newVal) -> Platform.runLater(() -> {
            updateIndicator(Color.LIGHTGREEN, stationSideLeftStatus, newVal);
   //         logger.info("Left platform status updated to {}", newVal);
        }));
        appendListener(currentSubject.getBooleanProperty(RIGHT_PLATFORM), (obs, oldVal, newVal) -> Platform.runLater(() -> {
            updateIndicator(Color.LIGHTGREEN, stationSideRightStatus, newVal);
  //          logger.info("Right platform status updated to {}", newVal);
        }));
        bindStringText(nextStationText);

    }

    private void bindStringText(Text text){
        appendListener(currentSubject.getProperty(NEXT_STATION),(obs, oldVal, newVal) -> Platform.runLater(()-> text.setText((String)newVal)));
        appendListener(currentSubject.getProperty(ARRIVAL_STATION),(obs, oldVal, newVal) -> Platform.runLater(()-> {
            if(newVal != null){
                sendAlert("Arriving at " + newVal);
            }
        }));
    }

    private void updateIndicator(Color color, Circle indicator, boolean isActive) {
        indicator.setFill(isActive ? color : Color.GRAY);
    }

    private void bindControls() {
        bindSliderAndTextField(setSpeedSlider, setSpeedTextField, newValue -> {
            currentSubject.setProperty(OVERRIDE_SPEED, newValue);
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

    private void bindCheckBox(CheckBox checkBox, ControllerProperty property) {
        appendListener(checkBox.selectedProperty(),(obs, oldVal, newVal) -> {
                currentSubject.setProperty(property, newVal);
                queueNotification(property,String.valueOf(checkBox.isSelected()));
        });
    }

    private void bindDoubleTextField(TextField textField, ControllerProperty property) {
        Runnable textFieldUpdate = () -> {
            try {
                currentSubject.setProperty(property, Double.parseDouble(textField.getText()));
                queueNotification(property, textField.getText());
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
            queueNotification(EMERGENCY_BRAKE, String.valueOf(eBrakeProp.get()));
            logger.info("Emergency button toggled to {}", !eBrakeProp.get());
        });
        makeAnnouncementsButton.setOnAction(event -> {
            BooleanProperty announceProp = currentSubject.getBooleanProperty(ANNOUNCEMENTS);
            currentSubject.setProperty(ANNOUNCEMENTS, !announceProp.get());

            if (!nextStationText.getText().contains("yard") && !nextStationText.getText().contains("Yard")) {
                sendAlert("Arriving at " + nextStationText.getText());
            }
        });

        killTrain.setOnAction(event -> {
            if(TrainControllerFactory.getTrainLock()){
                logger.warn("Train Controller is locked, unable to kill train");
                return;
            }
            logger.warn("Killing Train Controller for train ID: {}", currentSubject.getController().getID());
            currentSubject.getController().delete();
        });
    }

    private void sendAlert(String alertMessage){
        queueNotification(ANNOUNCEMENTS,alertMessage);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Arrival");
        alert.setHeaderText(null);
        alert.setContentText(alertMessage);
        alert.showAndWait();
    }



    private void bindSliderAndTextField(Slider slider, TextField textField, Consumer<Double> consumer) {
        appendListener(slider.valueProperty(), (obs, oldVal, newVal) -> {
            if(Math.abs(oldVal.doubleValue() - newVal.doubleValue()) < 0.1) {return;}
            consumer.accept(newVal.doubleValue());
            textField.setText(String.format("%.1f", newVal.doubleValue()));
            sendNotification(OVERRIDE_SPEED,String.format("%.1f",newVal.doubleValue()));
        });
        Runnable textFieldUpdate = () -> {
            try {
                double newValue = Double.parseDouble(textField.getText());
                newValue = (double) Math.round(newValue * 20) / 20.0;
                if (newValue < slider.getMin() || newValue > slider.getMax()) {
                    throw new NumberFormatException();
                }
                slider.setValue(newValue);
                queueNotification(OVERRIDE_SPEED,String.format("%.1f",newValue));
            } catch (NumberFormatException e) {
                textField.setText(String.format("%.1f", slider.getValue()));
                queueNotification(OVERRIDE_SPEED,String.format("%.1f",slider.getValue()));
            }
        };

       textField.setOnAction(event -> textFieldUpdate.run());
    }


    private <T> void appendListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        listenerReferences.add(new ListenerReference<>(observable, listener));
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

        logger.info("Controls unbound from train ID: {}", currentSubject.getController().getID());
    }

    private void updateUIForNullSubject() {
        // Example of resetting UI components
        statusLog.setText("N/A");
        // Disable UI elements or set to default values
        intLightCheckBox.setSelected(false);
        extLightCheckBox.setSelected(false);
        openDoorLeftCheckBox.setSelected(false);
        openDoorRightCheckBox.setSelected(false);
        toggleServiceBrakeCheckBox.setSelected(false);
        autoModeCheckBox.setSelected(false);
        setTemperatureTextField.setText("0.0");
        setKiTextField.setText("0.0");
        setKpTextField.setText("0.0");
        setSpeedTextField.setText("0.0");
        nextStationText.setText("No Station");
        setSpeedSlider.setValue(0.0);
        currentSpeedGauge.setValue(0.0);
        commandedSpeedGauge.setValue(0.0);
        speedLimitGauge.setValue(0.0);
        authorityGauge.setValue(0.0);
        powerOutputGauge.setValue(0.0);
        currentTemperatureGauge.setValue(0.0);
        updateIndicator(Color.GRAY, eBrakeStatus, false);
        updateIndicator(Color.GRAY, signalFailureStatus, false);
        updateIndicator(Color.GRAY, brakeFailureStatus, false);
        updateIndicator(Color.GRAY, powerFailureStatus, false);
        updateIndicator(Color.GRAY, inTunnelStatus, false);
        updateIndicator(Color.GRAY, stationSideLeftStatus, false);
        updateIndicator(Color.GRAY, stationSideRightStatus, false);
        logger.info("UI reset for null subject");
    }

    private void setHWMode(boolean isHW){
        HWTrainCheckBox.setSelected(isHW);
        controllerStatusPane.disableProperty().setValue(isHW);
        stationStatusPane.disableProperty().setValue(isHW);
        cabinSettingsPane.disableProperty().setValue(isHW);
        speedControllerPane.disableProperty().setValue(isHW);
        brakePane.disableProperty().setValue(isHW);
    }


    //Called when controller is switched, updates state of all UI elements
    private void updateView() {
    setHWMode(currentSubject.getController().isHW());

        // Update gauges
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

            logger.debug("UI elements updated for train ID: {}", currentSubject.getProperty(TRAIN_ID));

            bindAll();
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
    private void setNotification(ControllerProperty propertyName, String value) {
        String statusNotification = switch (propertyName) {
            case OVERRIDE_SPEED -> "\nSet Speed to \n" + value + " MPH";
            case SERVICE_BRAKE, LEFT_DOORS, RIGHT_DOORS, INT_LIGHTS,
                    EXT_LIGHTS, AUTOMATIC_MODE ->
                    "\n" + getPropertyLabel(propertyName) + "\n" + (Boolean.parseBoolean(value) ? getOnLabel(propertyName) : getOffLabel(propertyName));
            case EMERGENCY_BRAKE -> "\nEmergency Brake \n" + (Boolean.parseBoolean(value) ? getOffLabel(propertyName):getOnLabel(propertyName)); // For some reason emergency brake boolean values are flipped
            case SET_TEMPERATURE -> "\nTemperature set to \n" + value + " \u00B0F";
            case KI -> "\nKi set to \n" + value;
            case KP -> "\nKp set to \n" + value;
            case ANNOUNCEMENTS -> "\nAnnouncement: " + value +"\n";
            default -> "\nTrain is running";
        };

        Platform.runLater(() -> {
            statusLog.setText(statusNotification);
            statusLog.setWrapText(true);
   //         logger.info("Status notification set for {}: {}", propertyName, statusNotification.trim());
        });
    }

    private String getPropertyLabel(ControllerProperty propertyName) {
        return switch (propertyName) {
            case SERVICE_BRAKE -> "Service Brake";
            case LEFT_DOORS -> "Left Doors";
            case RIGHT_DOORS -> "Right Doors";
            case INT_LIGHTS -> "Interior Lights";
            case EXT_LIGHTS -> "Exterior Lights";
            case AUTOMATIC_MODE -> "Automatic Mode";
            case EMERGENCY_BRAKE -> "Emergency Brake";
            default -> "";
        };
    }

    private String getOnLabel(ControllerProperty propertyName) {
        return switch (propertyName) {
            case SERVICE_BRAKE -> "Engaged";
            case LEFT_DOORS, RIGHT_DOORS -> "Opened";
            case INT_LIGHTS, EXT_LIGHTS, AUTOMATIC_MODE,EMERGENCY_BRAKE -> "On";
            default -> "";
        };
    }

    private String getOffLabel(ControllerProperty propertyName) {
        return switch (propertyName) {
            case SERVICE_BRAKE -> "Disengaged";
            case LEFT_DOORS, RIGHT_DOORS -> "Closed";
            case INT_LIGHTS, EXT_LIGHTS, AUTOMATIC_MODE,EMERGENCY_BRAKE -> "Off";
            default -> "";
        };
    }

    ///TODO
    // Goal: Reflect all changes to the train (aka, listening to all the properties)
    // Goal: Change to an alert
    //      - Entering a new block
    //      - When we get new authority from track
    //      - New Command Speed Updates
    //      - Passenger E-Brake
    // When a new action occurs, store inside queue thats in single-executed thread
    //
    private final Object lock = new Object();
    private Future<?> scheduledFuture = null;
    private Future<?> debounceFuture = null;

    private final long debounceDelay = 100; // Debounce delay in milliseconds for sendNotification

    private final LinkedBlockingDeque<TextUpdateTask> textUpdateQueue = new LinkedBlockingDeque<>();

    private void runQueue() {
        synchronized (lock) {
            if (scheduledFuture == null || scheduledFuture.isDone()) {
                if (!textUpdateQueue.isEmpty()) {
                    TextUpdateTask task = textUpdateQueue.poll();
                    scheduledFuture = textUpdateExecutor.schedule(task::run, task.delay, TimeUnit.MILLISECONDS);
                }
            }
        }
    }

    private void sendNotification(ControllerProperty propertyName, String value) {
        synchronized (lock) {
            textUpdateQueue.clear();
            TextUpdateTask task = new TextUpdateTask(propertyName, value, 0);

            if (debounceFuture != null && !debounceFuture.isDone()) {
                debounceFuture.cancel(false);
            }

            debounceFuture = textUpdateExecutor.schedule(() -> {
                synchronized (lock) {
                    textUpdateQueue.offer(task);
                    runQueue();
                }
            }, debounceDelay, TimeUnit.MILLISECONDS);
        }
    }

    private void queueNotification(ControllerProperty propertyName, String value) {
        synchronized (lock) {
            boolean wasEmpty = textUpdateQueue.isEmpty();
            textUpdateQueue.offer(new TextUpdateTask(propertyName, value, wasEmpty ? 50 : 1000));
            if (wasEmpty) {
                runQueue();
            }
        }
    }

    private class TextUpdateTask implements Runnable {
        private final ControllerProperty propertyName;
        private final String text;
        private final long delay;

        public TextUpdateTask(ControllerProperty propertyName, String value, long delay) {
            this.propertyName = propertyName;
            this.text = value;
            this.delay = delay;
        }

        @Override
        public void run() {
            setNotification(propertyName, text);
        }
    }


}