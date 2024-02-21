package Framework.GUI.Managers;
import Common.TrainController;
import Utilities.BlockInfo;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;
//import trainController;
;
public class trainControllerTB {
    @FXML
    public Button trainControllerTB_AddTrain_Button;
    @FXML
    public Button trainControllerTB_PassengerEBrake_Button;
    @FXML
    public ChoiceBox<String> trainControllerTB_StationName_ChoiceBox;
    @FXML
    public TextField trainControllerTB_trainNo_TextField;
    @FXML
    public TextField trainControllerTB_CurrentSpeed_TextField;
    @FXML
    public TextField trainControllerTB_SpeedLimit_TextField;
    @FXML
    public TextField trainControllerTB_CommandedSpeed_TextField;
    @FXML
    public TextField trainControllerTB_Grade_TextField;
    @FXML
    public TextField trainControllerTB_Authority_TextField;
    @FXML
    public TextField trainControllerTB_CurrentTemperature_TextField;
    @FXML
    public RadioButton trainControllerTB_LeftPlatform_RadioButton;
    @FXML
    public RadioButton trainControllerTB_RightPlatform_RadioButton;
    @FXML
    public CheckBox trainControllerTB_PowerFailure_CheckBox;
    @FXML
    public CheckBox trainControllerTB_BrakeFailure_CheckBox;
    @FXML
    public CheckBox trainControllerTB_SignalFailure_CheckBox;
    @FXML
    public CheckBox trainControllerTB_InTunnel_CheckBox;

    @FXML
    private void initialize(){
        // Make E-Brake Button Red
        trainControllerTB_PassengerEBrake_Button.setStyle("-fx-background-color: #FF5733; -fx-text-fill: #ffffff;");

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



    }


}
