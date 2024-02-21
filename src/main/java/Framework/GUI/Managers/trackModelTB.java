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

    public int getTempInput(){
        return Integer.parseInt(tbTempInput.getText());
    }

   public void setTemp(int temp){
        tbTempInput.setText(Integer.toString(temp));
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

        tbSectionsColumn.setCellValueFactory(block -> block.getValue().sectionProperty());
        tbBlockColumn.setCellValueFactory(block -> block.getValue().blockNumberProperty().asObject());
        tbSwitchColumn.setCellValueFactory(block -> block.getValue().isSwitchProperty());
        tbSignalColumn.setCellValueFactory(block -> block.getValue().isSignalProperty());
        tbOccupiedColumn.setCellValueFactory(block -> block.getValue().isOccupiedProperty());

        tbChooseLine.getItems().add("blue");
        tbChooseLine.setOnAction(event -> updateTable());

        // set labels by getting text
        trackProperties.setTicketSales(tbTicketSalesInput.getText());
        trackProperties.setPassEmbarked(tbPassEmbarkedInput.getText());
        trackProperties.setPassDisembarked(tbPassDisembarkedInput.getText());
        trackModel.getTrackInfo();

    }

    public void updateTable() {
        ObservableList<TrackLayoutInfo> trackList = FXCollections.observableArrayList(trackModel.getTrackInfo());
        tbTable.setItems(trackList);
    }

    public void setLine(String line) {
        tbChooseLine.setValue(line);
    }

    public void setTrackModel(trackModelImpl trackModel) {
        this.trackModel = trackModel;
    }

    public void setPassEmbarked(int passEmbarked){
        trackProperties.setPassEmbarked(Integer.toString(passEmbarked));
    }

    public void setPassDisembarked(int passDisembarked){
        trackProperties.setPassDisembarked(Integer.toString(passDisembarked));
    }

    public void setTicketSales(int ticketSales){
        trackProperties.setTicketSales(Integer.toString(ticketSales));
    }
   }


