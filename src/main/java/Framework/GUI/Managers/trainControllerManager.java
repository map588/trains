package Framework.GUI.Managers;

import Common.TrainController;
import Framework.Support.ObservableHashMap;
import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import trainController.trainControllerImpl;
import trainController.trainControllerSubject;
import trainController.trainControllerSubjectFactory;

import java.util.ArrayList;
import java.util.function.Consumer;

public class trainControllerManager {

    @FXML
    private CheckBox trainController_intLight_CheckBox, trainController_extLight_CheckBox, trainController_openDoorLeft_CheckBox, trainController_openDoorRight_CheckBox, trainController_toggleServiceBrake_CheckBox, trainController_autoMode_CheckBox;
    @FXML
    private TextField trainController_setTemperature_TextField, trainController_setKi_TextField, trainController_setKp_TextField, trainController_setSpeed_TextField;
    @FXML
    private Button trainController_emergencyBrake_Button, trainController_makeAnnouncements_Button;
    @FXML
    private Slider trainController_setSpeed_Slider;
    @FXML
    private Gauge trainController_currentSpeed_Gauge, trainController_speedLimit_Gauge, trainController_commandedSpeed_Gauge, trainController_Authority_Gauge, trainController_powerOutput_Gauge;//, trainController_blocksToNextStation_Gauge;
    @FXML
    private Circle trainController_eBrake_Status, trainController_signalFailure_Status, trainController_brakeFailure_Status, trainController_powerFailure_Status, trainController_stationSideLeft_Status,trainController_stationSideRight_Status, trainController_inTunnel_Status;
    @FXML
    private ChoiceBox<Integer> trainController_trainNo_ChoiceBox;

    private trainControllerSubjectFactory factory;
    private trainControllerSubject currentSubject;

    //private trainControllerTB testBench;

    @FXML
    public void initialize() {
        //testBench = launchTestBench();

        trainControllerImpl trainController = new trainControllerImpl(0);
        factory = trainControllerSubjectFactory.getInstance();
        setupMapChangeListener();
        currentSubject = factory.getSubjects().get(0);
        bindGauges();
        bindControls();
        bindIndicators();

        trainController_trainNo_ChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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


        //subjects.addChangeListener(listener); *****************************************************************************************************************Commented out by Ty for compilation

        updateChoiceBoxItems();
    }


    private void updateChoiceBoxItems() {
        Platform.runLater(() -> {
            trainController_trainNo_ChoiceBox.setItems(FXCollections.observableArrayList(
                    new ArrayList<>(factory.getSubjects().keySet())));
        });
    }

    private void bindGauges() {
        trainController_currentSpeed_Gauge.valueProperty().bind(currentSubject.getDoubleProperty("currentSpeed"));
        trainController_commandedSpeed_Gauge.valueProperty().bind(currentSubject.getDoubleProperty("commandSpeed"));
        trainController_speedLimit_Gauge.valueProperty().bind(currentSubject.getDoubleProperty("maxSpeed"));
        trainController_Authority_Gauge.valueProperty().bind(currentSubject.getIntegerProperty("authority"));
        trainController_powerOutput_Gauge.valueProperty().bind(currentSubject.getDoubleProperty("power"));
        //trainController_blocksToNextStation_Gauge.valueProperty().bind(currentSubject.getIntegerProperty("blocksToNextStation"));
    }

    private void bindIndicators() {
        currentSubject.getBooleanProperty("emergencyBrake").addListener((obs, oldSelection, newSelection) -> updateEBrakeIndicator(newSelection));
        currentSubject.getBooleanProperty("signalFailure").addListener((obs, oldSelection, newSelection) -> updateSignalFailureIndicator(newSelection));
        currentSubject.getBooleanProperty("brakeFailure").addListener((obs, oldSelection, newSelection) -> updateBrakeFailureIndicator(newSelection));
        currentSubject.getBooleanProperty("powerFailure").addListener((obs, oldSelection, newSelection) -> updatePowerFailureIndicator(newSelection));
        currentSubject.getBooleanProperty("inTunnel").addListener((obs,oldSelection, newSelection)->updateInTunnelIndicator(newSelection));
        currentSubject.getBooleanProperty("leftPlatform").addListener((obs,oldSelection, newSelection)->updateleftPlatformIndicator(newSelection));
        currentSubject.getBooleanProperty("rightPlatform").addListener((obs,oldSelection, newSelection)->updateRightPlatformIndicator(newSelection));
    }

    private void bindControls() {
        bindSliderAndTextField(trainController_setSpeed_Slider, trainController_setSpeed_TextField, ((DoubleProperty)currentSubject.getProperty("overrideSpeed"))::set);
        bindCheckBox(trainController_intLight_CheckBox, currentSubject.getBooleanProperty("intLights")::set);
        bindCheckBox(trainController_extLight_CheckBox, currentSubject.getBooleanProperty("extLights")::set);
        bindCheckBox(trainController_openDoorLeft_CheckBox, currentSubject.getBooleanProperty("leftDoors")::set);
        bindCheckBox(trainController_openDoorRight_CheckBox, currentSubject.getBooleanProperty("rightDoors")::set);
        bindCheckBox(trainController_toggleServiceBrake_CheckBox, currentSubject.getBooleanProperty("serviceBrake")::set);
        bindCheckBox(trainController_autoMode_CheckBox, currentSubject.getBooleanProperty("automaticMode")::set);
        bindTextField(trainController_setTemperature_TextField, currentSubject.getDoubleProperty("temperature")::set);
        bindTextField(trainController_setKi_TextField, currentSubject.getDoubleProperty("Ki")::set);
        bindTextField(trainController_setKp_TextField, currentSubject.getDoubleProperty("Kp")::set);
        trainController_emergencyBrake_Button.setOnAction(event -> {
            boolean newEBrake = !currentSubject.getBooleanProperty("emergencyBrake").get();
            currentSubject.updateProperty(currentSubject.getBooleanProperty("emergencyBrake"), newEBrake);
            updateEBrakeIndicator(newEBrake);
        });
        trainController_makeAnnouncements_Button.setOnAction(event -> {
            boolean enable = !currentSubject.getBooleanProperty("announcements").get();
            currentSubject.updateProperty(currentSubject.getBooleanProperty("announcements"), enable);
        });
    }

    private void bindSliderAndTextField(Slider slider, TextField textField, Consumer<Double> consumer) {
        slider.valueProperty().addListener((obs, oldSelection, newSelection) -> {
            consumer.accept(newSelection.doubleValue());
            textField.setText(String.valueOf(newSelection.doubleValue()));
        });
        textField.textProperty().addListener((obs, oldSelection, newSelection) -> {
            double value = Double.parseDouble(newSelection);
            consumer.accept(value);
            slider.setValue(value);
        });
    }

    private void bindCheckBox(CheckBox checkBox, Consumer<Boolean> consumer) {
        checkBox.selectedProperty().addListener((obs, oldSelection, newSelection) -> consumer.accept(newSelection));
    }

    private void bindTextField(TextField textField, Consumer<Double> consumer) {
        textField.textProperty().addListener((obs, oldSelection, newSelection) -> {
            try{
                double value = Double.parseDouble(newSelection);
                consumer.accept(value);
            } catch (NumberFormatException e) {
                consumer.accept(Double.parseDouble(oldSelection));
            }
        });
    }

    public synchronized void addTrainToList(TrainController controller) {
        trainController_trainNo_ChoiceBox.getItems().add(controller.getID());
    }

    private void updateEBrakeIndicator(boolean isEmergencyBrakeActive) {
        trainController_eBrake_Status.setFill(isEmergencyBrakeActive ? Color.RED : Color.GRAY);
    }
    private void updateSignalFailureIndicator(boolean isSignalFailureActive) {
        trainController_signalFailure_Status.setFill(isSignalFailureActive ? Color.RED : Color.GRAY);
    }
    private void updateBrakeFailureIndicator(boolean isBrakeFailureActive) {
        trainController_brakeFailure_Status.setFill(isBrakeFailureActive ? Color.RED : Color.GRAY);
    }
    private void updatePowerFailureIndicator(boolean isPowerFailureActive) {
        trainController_powerFailure_Status.setFill(isPowerFailureActive ? Color.RED : Color.GRAY);
    }
    private void updateleftPlatformIndicator(boolean isLeftPlatformActive){
        trainController_stationSideLeft_Status.setFill(isLeftPlatformActive ? Color.RED : Color.GRAY);
    }
    private void updateRightPlatformIndicator(boolean isRightPlatformActive){
        trainController_stationSideRight_Status.setFill(isRightPlatformActive ? Color.RED : Color.GRAY);
    }
    private void updateInTunnelIndicator(boolean isInTunnelActive){
        trainController_inTunnel_Status.setFill(isInTunnelActive ? Color.RED : Color.GRAY);
    }

    private void changeTrainView(int trainID) {
        currentSubject = factory.getSubjects().get(trainID);
        if(currentSubject != null) {
            unbindControls();
            bindControls();
        }
    }

    private void unbindControls() {
        trainController_currentSpeed_Gauge.valueProperty().unbind();
        trainController_commandedSpeed_Gauge.valueProperty().unbind();
        trainController_speedLimit_Gauge.valueProperty().unbind();
        trainController_Authority_Gauge.valueProperty().unbind();
        trainController_powerOutput_Gauge.valueProperty().unbind();

        trainController_setSpeed_Slider.valueProperty().unbind();
        trainController_setSpeed_TextField.textProperty().unbind();

        trainController_intLight_CheckBox.selectedProperty().unbind();
        trainController_extLight_CheckBox.selectedProperty().unbind();
        trainController_openDoorLeft_CheckBox.selectedProperty().unbind();
        trainController_openDoorRight_CheckBox.selectedProperty().unbind();
        trainController_toggleServiceBrake_CheckBox.selectedProperty().unbind();
        trainController_autoMode_CheckBox.selectedProperty().unbind();

        trainController_setTemperature_TextField.textProperty().unbind();
        trainController_setKi_TextField.textProperty().unbind();
        trainController_setKp_TextField.textProperty().unbind();
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