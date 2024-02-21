package Framework.GUI.Managers;

import Utilities.TrackLayoutInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import trackModel.trackModelImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;


public class trackModelTB {

    @FXML
    private Label tbLogo;
    @FXML
    private ComboBox<String> tbChooseLine;
    @FXML
    private TextField tbTempInput;
    @FXML
    private TextField tbPassEmbarkedInput;
    @FXML
    private TextField tbPassDisembarkedInput;
    @FXML
    private TextField tbTicketSalesInput;
    @FXML
    private TableView<TrackLayoutInfo> tbTable;
    @FXML
    private TableColumn<TrackLayoutInfo, String> tbSectionsColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Integer> tbBlockColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> tbSwitchColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> tbSignalColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> tbOccupiedColumn;

    private TrackLayoutInfo trackProperties = new TrackLayoutInfo();
    private trackModelImpl trackModel;


    public trackModelTB(){

        this.tbBlockColumn = new TableColumn<>();
        this.tbSectionsColumn = new TableColumn<>();
        this.tbSwitchColumn = new TableColumn<>();
        this.tbSignalColumn = new TableColumn<>();
        this.tbOccupiedColumn = new TableColumn<>();
    }


    public void initialize() {

        StringConverter<Integer> intConverter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer integer) {
                return integer.toString();
            }

            @Override
            public Integer fromString(String s) {
                return Integer.parseInt(s);
            }
        };
        StringConverter<Boolean> boolConverter = new StringConverter<Boolean>() {
            @Override
            public String toString(Boolean bool) {
                return bool.toString();
            }

            @Override
            public Boolean fromString(String s) {
                return Boolean.parseBoolean(s);
            }
        };

        tbSectionsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tbBlockColumn.setCellFactory(TextFieldTableCell.forTableColumn(intConverter));
        tbSwitchColumn.setCellFactory(TextFieldTableCell.forTableColumn(boolConverter));
        tbSignalColumn.setCellFactory(TextFieldTableCell.forTableColumn(boolConverter));
        tbOccupiedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(tbOccupiedColumn));

        tbSectionsColumn.setCellValueFactory(new PropertyValueFactory<>("section"));
        tbBlockColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
        tbSwitchColumn.setCellValueFactory(new PropertyValueFactory<>("isSwitch"));
        tbSignalColumn.setCellValueFactory(new PropertyValueFactory<>("isSignal"));
        tbOccupiedColumn.setCellValueFactory(new PropertyValueFactory<>("isOccupied"));

        tbChooseLine.setOnAction(event -> updateTable());

       // tbChooseLine.setOnAction(event -> updateTable());

    }

    public void updateTable() {

        ObservableList<TrackLayoutInfo> trackList = FXCollections.observableArrayList();
        tbTable.setItems(trackList);
    }

    public void setLine(String line) {
        tbChooseLine.setValue(line);
    }

    public void setTrackModel(trackModelImpl trackModel) {
        this.trackModel = trackModel;
    }



}
