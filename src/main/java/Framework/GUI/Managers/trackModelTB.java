package Framework.GUI.Managers;

import Utilities.TrackLayoutInfo;
import trackModel.trackModelSubject;
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
    private TableColumn<TrackLayoutInfo, String> tbSectionColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, String> tbBlockColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> tbSwitchColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> tbSignalColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> tbOccupiedColumn;

    private TrackLayoutInfo trackProperties = new TrackLayoutInfo();
    private trackModelImpl trackModel;
    private trackModelSubject trackModelSubject;
    private trackModelManager trackModelManager;

    public int getTempInput(){
        return Integer.parseInt(tbTempInput.getText());
    }

   public void setTemp(int temp){
        tbTempInput.setText(Integer.toString(temp));
    }

    @FXML
    public void initialize() {
        System.out.println("LAUNCHED TESTBENCH");

        trackModelSubject = new trackModelSubject(trackModel);
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

        tbSectionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tbBlockColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tbSwitchColumn.setCellFactory(TextFieldTableCell.forTableColumn(boolConverter));
        tbSignalColumn.setCellFactory(TextFieldTableCell.forTableColumn(boolConverter));
        tbOccupiedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(tbOccupiedColumn));

        tbSectionColumn.setCellValueFactory(block -> block.getValue().sectionProperty());
        tbBlockColumn.setCellValueFactory(block -> block.getValue().blockNumberProperty());
        tbSwitchColumn.setCellValueFactory(block -> block.getValue().isSwitchProperty());
        tbSignalColumn.setCellValueFactory(block -> block.getValue().isSignalProperty());
        tbOccupiedColumn.setCellValueFactory(block -> block.getValue().isOccupiedProperty());

        tbChooseLine.getItems().add("blue");
        tbChooseLine.setOnAction(event -> updateTable());

        tbTable.getSelectionModel().selectedItemProperty().addListener(event -> {
            selectBlock(tbTable.getSelectionModel().getSelectedItem());
            trackModelManager.selectBlock(tbTable.getSelectionModel().getSelectedItem());
        });
    }

    public void selectBlock(TrackLayoutInfo newProperties){
        if(trackProperties != null) {
            // Unbind stuff here
            tbPassEmbarkedInput.textProperty().bindBidirectional(trackProperties.passEmbarkedProperty());
            tbPassDisembarkedInput.textProperty().bindBidirectional(trackProperties.passDisembarkedProperty());
            tbTicketSalesInput.textProperty().bindBidirectional(trackProperties.ticketSalesProperty());
            tbTempInput.textProperty().bindBidirectional(trackModelSubject.tempProperty());
        }

        trackProperties = tbTable.getSelectionModel().getSelectedItem();

        tbPassEmbarkedInput.textProperty().unbindBidirectional(trackProperties.passEmbarkedProperty());
        tbPassDisembarkedInput.textProperty().unbindBidirectional(trackProperties.passDisembarkedProperty());
        tbTicketSalesInput.textProperty().unbindBidirectional(trackProperties.ticketSalesProperty());
        tbTempInput.textProperty().unbindBidirectional(trackModelSubject.tempProperty());

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
    public void setTrackModelSubject(trackModelSubject trackModelSubject){
        this.trackModelSubject = trackModelSubject;
        tbTempInput.textProperty().bindBidirectional(trackModelSubject.tempProperty());
    }

    public void setTrackModelManager(trackModelManager trackModelManager){
        this.trackModelManager = trackModelManager;
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


