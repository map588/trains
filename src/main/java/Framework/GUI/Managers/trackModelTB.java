package Framework.GUI.Managers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;


public class trackModelTB {

    @FXML
    private Label tbLogo;
    @FXML
    private ComboBox tbChooseLine;
    @FXML
    private TextField tbTempInput;
    @FXML
    private TextField tbPassEmbarkedInput;
    @FXML
    private TextField tbPassDisembarkedInput;
    @FXML
    private TextField tbTicketSalesInput;
    @FXML
    private TableView tbTable;
    @FXML
    private TableColumn tbSectionsColumn;
    @FXML
    private TableColumn tbBlockColumn;
    @FXML
    private TableColumn tbSwitchColumn;
    @FXML
    private TableColumn tbSignalColumn;
    @FXML
    private TableColumn tbOccupiedColumn;

    public void initialize() {
        tbSectionsColumn.setCellValueFactory(new PropertyValueFactory<>("section"));
        tbBlockColumn.setCellValueFactory(new PropertyValueFactory<>("block"));
        tbSwitchColumn.setCellValueFactory(new PropertyValueFactory<>("switch"));
        tbSignalColumn.setCellValueFactory(new PropertyValueFactory<>("signal"));
        tbOccupiedColumn.setCellValueFactory(new PropertyValueFactory<>("occupied"));
    }




}
