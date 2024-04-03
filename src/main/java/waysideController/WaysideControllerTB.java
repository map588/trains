package waysideController;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import static waysideController.Properties.*;

public class WaysideControllerTB {

    @FXML
    public Label tbWaysideNumberLabel;
    @FXML
    public TableView<WaysideBlockSubject> tbBlockTable;
    @FXML
    public TableView<WaysideBlockSubject> tbSwitchTable;
    @FXML
    public TableColumn<WaysideBlockSubject, Integer> tbBTID;
    @FXML
    public TableColumn<WaysideBlockSubject, Boolean> tbBTOccupied;
    @FXML
    public TableColumn<WaysideBlockSubject, Integer> tbSTID;
    @FXML
    public TableColumn<WaysideBlockSubject, Integer> tbSTSwitchTo;
    @FXML
    public TableColumn<WaysideBlockSubject, Boolean> tbSTEnable;
    @FXML
    public Button tbCreateNewControllerButton;
    @FXML
    public ComboBox<String> tbHWPortComboBox;
    @FXML
    public Label tbHWPortLabel;
    @FXML
    public Button runPLCButton;

    WaysideControllerSubject currentSubject;


    @FXML
    private void initialize() {
        // Set up cell value factories for table views
        tbBTID.setCellValueFactory(block -> block.getValue().getIntegerProperty(blockID_p).asObject());
        tbBTOccupied.setCellValueFactory(block -> block.getValue().getBooleanProperty(occupied_p));

        tbSTID.setCellValueFactory(block -> block.getValue().getIntegerProperty(blockID_p).asObject());
        tbSTSwitchTo.setCellValueFactory(block -> block.getValue().getIntegerProperty(switchedBlockID_p).asObject());

        runPLCButton.setOnAction(event -> currentSubject.getController().runPLC());

        // Set up cell factories for table views
        setupTableCellFactories();

        // Set up editable columns
        tbBlockTable.setEditable(true);
        tbSwitchTable.setEditable(true);

        // Set up the hardware port combo box
        tbHWPortComboBox.getItems().add("SW");
        SerialPort[] ports = SerialPort.getCommPorts();
        for(SerialPort port : ports) {
            tbHWPortComboBox.getItems().add(port.getSystemPortName());
        }
        tbHWPortComboBox.setValue("SW");
    }

    /**
     * Sets up the cell factories for the table views
     */
    private void setupTableCellFactories() {
        tbBTOccupied.setCellFactory(column -> new TableCell<WaysideBlockSubject, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    WaysideBlockSubject block = getTableView().getItems().get(getIndex());
                    CheckBox checkBox;
                    {
                        checkBox = new CheckBox();
                        checkBox.setSelected(item);
                        checkBox.setOnAction(event -> {
                            currentSubject.getController().trackModelSetOccupancy(block.getBlock().getBlockID(), checkBox.isSelected());
                        });
                    }
                    setGraphic(checkBox);
                }
            }
        });
        tbSTEnable.setCellFactory(column -> new TableCell<WaysideBlockSubject, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    WaysideBlockSubject block = getTableView().getItems().get(getIndex());
                    CheckBox checkBox;
                    {
                        checkBox = new CheckBox();
                        checkBox.setSelected(item);
                        checkBox.setOnAction(event -> {
//                            currentSubject.getController().CTCRequestSwitchState(block.getBlock().getBlockID(), checkBox.isSelected());
                        });
                    }
                    setGraphic(checkBox);
                }
            }
        });
    }

    public void setController(WaysideControllerSubject subject) {
        this.currentSubject = subject;
        readBlockInfo(subject.blockListProperty());
        if(subject.getController() instanceof WaysideControllerHWBridge) {
            tbHWPortLabel.setText("HW Port: " + ((WaysideControllerHWBridge) subject.getController()).getPort());
        }
        else {
            tbHWPortLabel.setText("HW Port: N/A");
        }
    }

    /**
     * Read the block info from the wayside controller
     * @param blocks The list of blocks to read
     */
    private void readBlockInfo(ObservableList<WaysideBlockSubject> blocks) {
        tbBlockTable.setItems(blocks);
        tbSwitchTable.getItems().clear();
        for(WaysideBlockSubject item : blocks) {
            if(item.getBlock().hasSwitch()) {
                tbSwitchTable.getItems().add(item);
            }
        }
    }
}
