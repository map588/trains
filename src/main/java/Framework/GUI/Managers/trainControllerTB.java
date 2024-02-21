package Framework.GUI.Managers;
import Common.TrainController;
import Utilities.BlockInfo;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;
;
public class trainControllerTB {
    @FXML
    public Button trainControllerTB_AddTrain_Button;
    @FXML
    public Button trainControllerTB_PassengerEBrake_Button;
    @FXML
    public ChoiceBox trainControllerTB_StationName_ChoiceBox;
    @FXML
    public TextField trainControllerTB_trainNo_TextField;
    @FXML
    public TextField trainControllerTB__TextField;
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

    }
}
