package Framework.GUI.Managers;

import Framework.Support.ListenerReference;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import trainController.trainControllerImpl;
import trainController.trainControllerSubject;
import trainController.trainControllerSubjectFactory;
import trainController.trainControllerSubjectMap;
import trainModel.trainModelImpl;

import java.util.ArrayList;
import java.util.List;

public class trainControllerTB {
    @FXML
    private Button trainControllerTB_AddTrain_Button;
    @FXML
    private Button trainControllerTB_CalculatePower_Button;
    @FXML
    private TextField trainControllerTB_Authority_TextField;

    @FXML
    private CheckBox trainControllerTB_BrakeFailure_CheckBox;

    @FXML
    private TextField trainControllerTB_CommandedSpeed_TextField;

    @FXML
    private TextField trainControllerTB_CurrentSpeed_TextField;

    @FXML
    private TextField trainControllerTB_CurrentTemperature_TextField;

    @FXML
    private TextField trainControllerTB_Grade_TextField;

    @FXML
    private TextField trainControllerTB_SamplingPeriod_TextField;

    @FXML
    private CheckBox trainControllerTB_InTunnel_CheckBox;

    @FXML
    private RadioButton trainControllerTB_LeftPlatform_RadioButton;

    @FXML
    private Button trainControllerTB_PassengerEBrake_Button;

    @FXML
    private CheckBox trainControllerTB_PowerFailure_CheckBox;

    @FXML
    private RadioButton trainControllerTB_RightPlatform_RadioButton;

    @FXML
    private CheckBox trainControllerTB_SignalFailure_CheckBox;

    @FXML
    private TextField trainControllerTB_SpeedLimit_TextField;

    @FXML
    private ChoiceBox<String> trainControllerTB_StationName_ChoiceBox;

    @FXML
    private TextField trainControllerTB_trainNo_TextField;

    private trainControllerSubjectMap subjectMap = trainControllerSubjectMap.getInstance();
    private trainControllerSubject subject;
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();
    @FXML
    private void initialize(){
        if (!subjectMap.getSubjects().isEmpty()) {
            Integer firstKey = subjectMap.getSubjects().keySet().iterator().next();
            subject = subjectMap.getSubject(firstKey);
        }

        initializeTestBench();

    }

    private void initializeTestBench(){
        // Make E-Brake Button Red
        trainControllerTB_PassengerEBrake_Button.setStyle("-fx-background-color: #FF5733; -fx-text-fill: #ffffff;");

        trainControllerTB_AddTrain_Button.setOnAction(event -> createTrainController(Integer.parseInt(trainControllerTB_trainNo_TextField.getText())));
        trainControllerTB_PassengerEBrake_Button.setOnMouseClicked(event -> subject.setProperty("emergencyBrake", true));
        trainControllerTB_CalculatePower_Button.setOnAction(event -> subject.calculatePower());
        trainControllerTB_BrakeFailure_CheckBox.setOnAction(event -> subject.setProperty("brakeFailure", !subject.getBooleanProperty("brakeFailure").getValue()));
        trainControllerTB_PowerFailure_CheckBox.setOnAction(event -> subject.setProperty("powerFailure", !subject.getBooleanProperty("powerFailure").getValue()));
        trainControllerTB_SignalFailure_CheckBox.setOnAction(event -> subject.setProperty("signalFailure", !subject.getBooleanProperty("signalFailure").getValue()));
        trainControllerTB_LeftPlatform_RadioButton.setOnAction(event -> {
            boolean left = subject.getBooleanProperty("leftPlatform").getValue();
            subject.setProperty("leftPlatform", !left);
        });
        trainControllerTB_RightPlatform_RadioButton.setOnAction(event -> {
            boolean right = subject.getBooleanProperty("rightPlatform").getValue();
            subject.setProperty("rightPlatform", !right);
        });
        trainControllerTB_InTunnel_CheckBox.setOnAction(event -> {
            boolean currentState = subject.getBooleanProperty("inTunnel").getValue();
            subject.setProperty("inTunnel", !currentState);
        });
        trainControllerTB_StationName_ChoiceBox.setOnAction(event ->{
            subject.setProperty("nextStationName",trainControllerTB_StationName_ChoiceBox.getValue());
        });

        // Set up ChoiceBox
        ObservableList<String> stationNamesList = FXCollections.observableArrayList("Station B","Station C");
        trainControllerTB_StationName_ChoiceBox.setItems(stationNamesList);

        // Set up TextFields
        trainControllerTB_Authority_TextField.setText("0");
        trainControllerTB_CommandedSpeed_TextField.setText("0");
        trainControllerTB_CurrentSpeed_TextField.setText("0");
        trainControllerTB_trainNo_TextField.setText("1");
        trainControllerTB_Grade_TextField.setText("0");
        trainControllerTB_CurrentTemperature_TextField.setText("72");
        trainControllerTB_SpeedLimit_TextField.setText("0");
        createTrainController(Integer.parseInt(trainControllerTB_trainNo_TextField.getText()));
    }

    private void createTrainController(int trainID){
        if(subjectMap.getSubjects().containsKey(trainID)) {
            changeTrainView(trainID);
        }
        else {
            try {
                new trainControllerImpl(trainID);

            } finally{
                subject = subjectMap.getSubjects().get(trainID);
                changeTrainView(trainID);
            }
        }
    }

    private void bindDoubleTextField(TextField textField, String propertyName) {
        Runnable textFieldUpdate = () -> {
            try {
                // Parse and update property
                subject.setProperty(propertyName, Double.parseDouble(textField.getText()));
            } catch (NumberFormatException e) {
                // Clear if invalid input
                textField.setText("");
            }
        };
        textField.setOnAction(event -> textFieldUpdate.run());
    }

    private void bindIntTextField(TextField textField, String propertyName) {
        Runnable textFieldUpdate = () -> {
            try {
                // Parse and update property
                subject.setProperty(propertyName, Integer.parseInt(textField.getText()));
            } catch (NumberFormatException e) {
                // Clear if invalid input
                textField.setText("");
            }
        };
        textField.setOnAction(event -> textFieldUpdate.run());
    }

    private void changeTrainView(int trainID){
        if(subjectMap.getSubjects().containsKey(trainID)) {
            subject = subjectMap.getSubject(trainID);
            unbindTrainController();
            bindTrainController();
        }
        else
            System.out.println("Train not found");
    }

    private <T> void appendListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        listenerReferences.add(new ListenerReference<>(observable, listener));
    }

    private void bindTrainController(){
        bindIntTextField(trainControllerTB_Authority_TextField, "authority");
        bindDoubleTextField(trainControllerTB_CommandedSpeed_TextField, "commandSpeed");
        bindDoubleTextField(trainControllerTB_CurrentSpeed_TextField, "currentSpeed");
        bindDoubleTextField(trainControllerTB_Grade_TextField, "grade");
        bindDoubleTextField(trainControllerTB_CurrentTemperature_TextField, "temperature");
        bindIntTextField(trainControllerTB_SamplingPeriod_TextField, "samplingPeriod");
        bindDoubleTextField(trainControllerTB_SpeedLimit_TextField, "speedLimit");



        appendListener(subject.getBooleanProperty("brakeFailure"), (observable, oldValue, newValue) -> trainControllerTB_BrakeFailure_CheckBox.setSelected(newValue));
        appendListener(subject.getBooleanProperty("powerFailure"), (observable, oldValue, newValue) -> trainControllerTB_PowerFailure_CheckBox.setSelected(newValue));
        appendListener(subject.getBooleanProperty("signalFailure"), (observable, oldValue, newValue) -> trainControllerTB_SignalFailure_CheckBox.setSelected(newValue));
        appendListener(subject.getBooleanProperty("inTunnel"),     (observable, oldValue, newValue) -> trainControllerTB_InTunnel_CheckBox.setSelected(newValue));
        appendListener(subject.getBooleanProperty("leftPlatform"), (observable, oldValue, newValue) -> trainControllerTB_LeftPlatform_RadioButton.setSelected(newValue));
        appendListener(subject.getBooleanProperty("rightPlatform"), (observable, oldValue, newValue) -> trainControllerTB_RightPlatform_RadioButton.setSelected(newValue));

    }

    private void unbindTrainController(){
        listenerReferences.forEach(ListenerReference::detach);
        listenerReferences.clear();



    }

}
