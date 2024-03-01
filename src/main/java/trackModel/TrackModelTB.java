package trackModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;


public class TrackModelTB {

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
    private TableColumn<TrackLayoutInfo, String> tbSwitchColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, String> tbSignalColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> tbOccupiedColumn;

    @FXML
    private Label comSpeedOutput;
    @FXML
    private TextField comSpeedInput;
    @FXML
    private Label trainAuthorityOutput;
    @FXML
    private TextField trainAuthorityInput;
    @FXML
    private TextField tbBeaconInput;
    @FXML
    private ComboBox<String> tbBeaconComboBox;

    private TrackLayoutInfo trackProperties = new TrackLayoutInfo();
    private TrackModelImpl trackModel;
    private TrackModelSubject trackModelSubject;
    private TrackModelManager trackModelManager;

    public int getTempInput(){
        return Integer.parseInt(tbTempInput.getText());
    }

   public void setTemp(int temp){
        tbTempInput.setText(Integer.toString(temp));
    }

    @FXML
    public void initialize() {
        System.out.println("LAUNCHED TESTBENCH");

        trackModelSubject = new TrackModelSubject(trackModel);
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

        tbTable.setEditable(true);
        tbOccupiedColumn.setEditable(true);
        tbSwitchColumn.setEditable(true);
        tbSignalColumn.setEditable(true);

        tbSectionColumn.setCellValueFactory(block -> block.getValue().sectionProperty());
        tbBlockColumn.setCellValueFactory(block -> block.getValue().blockNumberProperty());
        tbSwitchColumn.setCellValueFactory(block -> block.getValue().switchStateProperty());
        tbSignalColumn.setCellValueFactory(block -> block.getValue().signalStateProperty());
        tbOccupiedColumn.setCellValueFactory(block -> block.getValue().isOccupiedProperty());


        tbSectionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tbBlockColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tbSwitchColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tbSignalColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tbOccupiedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(tbOccupiedColumn));


        tbChooseLine.setOnAction(event -> updateTable());

        tbSwitchColumn.setText("Switch");
        tbSignalColumn.setText("Signal");

        comSpeedOutput.setText("NONE");
        trainAuthorityOutput.setText("NONE");

        // set labels by getting text
        trackProperties.setTicketSales(tbTicketSalesInput.getText());
        trackProperties.setPassEmbarked(tbPassEmbarkedInput.getText());
        trackProperties.setPassDisembarked(tbPassDisembarkedInput.getText());
    }

    public void selectBlock(TrackLayoutInfo newProperties){
        if(trackProperties != null) {
            // Unbind stuff here

            if(trackProperties.isIsStation()){
                tbPassEmbarkedInput.textProperty().unbindBidirectional(trackProperties.passEmbarkedProperty());
                tbPassDisembarkedInput.textProperty().unbindBidirectional(trackProperties.passDisembarkedProperty());
                tbTicketSalesInput.textProperty().unbindBidirectional(trackProperties.ticketSalesProperty());
                tbSwitchColumn.textProperty().unbindBidirectional(trackProperties.switchStateProperty());
                tbSwitchColumn.textProperty().unbindBidirectional(trackProperties.switchMainProperty());
                tbSwitchColumn.textProperty().unbindBidirectional(trackProperties.switchAltProperty());
            }

            if(trackProperties.isIsSignal()){
                tbSignalColumn.textProperty().unbindBidirectional(trackProperties.signalStateProperty());
            }

            //tbOccupiedColumn.textProperty().unbindBidirectional(trackProperties.isOccupiedProperty());
            if(trackProperties.isIsBeacon()){
                tbBeaconInput.textProperty().unbindBidirectional(trackProperties.setBeaconProperty());
            }

            tbSignalColumn.textProperty().unbindBidirectional(trackProperties.signalStateProperty());
            tbTempInput.textProperty().unbindBidirectional(trackModelSubject.tempProperty());

        }

//        trackProperties = tbTable.getSelectionModel().getSelectedItem();
        trackProperties = newProperties;
        tbTable.getSelectionModel().select(newProperties);

        if(trackProperties.isIsStation()){
            tbPassEmbarkedInput.textProperty().bindBidirectional(trackProperties.passEmbarkedProperty());
            tbPassDisembarkedInput.textProperty().bindBidirectional(trackProperties.passDisembarkedProperty());
            tbTicketSalesInput.textProperty().bindBidirectional(trackProperties.ticketSalesProperty());
            tbSwitchColumn.textProperty().bindBidirectional(trackProperties.switchStateProperty());
            tbSwitchColumn.textProperty().bindBidirectional(trackProperties.switchMainProperty());
            tbSwitchColumn.textProperty().bindBidirectional(trackProperties.switchAltProperty());
        }

        if(trackProperties.isIsSignal()){
            tbSignalColumn.textProperty().bindBidirectional(trackProperties.signalStateProperty());
        }

        if(trackProperties.isIsBeacon()){
            tbBeaconInput.textProperty().bindBidirectional(trackProperties.setBeaconProperty());
        }


        tbSignalColumn.textProperty().bindBidirectional(trackProperties.signalStateProperty());
        tbTempInput.textProperty().bindBidirectional(trackModelSubject.tempProperty());

    }

    public void updateTable() {
        ObservableList<TrackLayoutInfo> trackList = FXCollections.observableArrayList(trackModel.getTrackInfo());
        tbTable.setItems(trackList);
    }

    public void setLine(String line) {
        tbChooseLine.setValue(line);
    }

    public void setTrackModel(TrackModelImpl trackModel) {
        this.trackModel = trackModel;
    }

    public void setTrackModelManager(TrackModelManager trackModelManager) {
        this.trackModelManager = trackModelManager;
    }
    public void setTrackModelSubject(TrackModelSubject trackModelSubject){
        this.trackModelSubject = trackModelSubject;
        tbTempInput.textProperty().bindBidirectional(trackModelSubject.tempProperty());
        comSpeedInput.textProperty().bindBidirectional(trackModelSubject.comSpeedProperty());
        comSpeedOutput.textProperty().bindBidirectional(trackModelSubject.comSpeedProperty());
        trainAuthorityOutput.textProperty().bindBidirectional(trackModelSubject.trainAuthorityProperty());
        trainAuthorityInput.textProperty().bindBidirectional(trackModelSubject.trainAuthorityProperty());
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


