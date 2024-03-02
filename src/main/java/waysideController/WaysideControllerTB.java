package waysideController;

import Common.WaysideController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

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

    WaysideController controller;


    private StringConverter<Double> doubleConverter = new StringConverter<Double>() {
        @Override
        public String toString(Double d) {
            return d.toString();
        }

        @Override
        public Double fromString(String s) {
            return Double.parseDouble(s);
        }
    };

    private StringConverter<Integer> intConverter = new StringConverter<Integer>() {
        @Override
        public String toString(Integer integer) {
            return integer.toString();
        }

        @Override
        public Integer fromString(String s) {
            return Integer.parseInt(s);
        }
    };


    @FXML
    private void initialize() {
        tbBlockTable.setEditable(true);
        tbBTID.setCellValueFactory(block -> block.getValue().getIntegerProperty(blockID_p).asObject());
        tbBTOccupied.setCellValueFactory(block -> block.getValue().getBooleanProperty(occupied_p));
//        tbBTOccupied.setCellFactory(CheckBoxTableCell.forTableColumn(tbBTOccupied));
        tbBTOccupied.setCellFactory(column -> {
            CheckBoxTableCell<WaysideBlockSubject, Boolean> cell = new CheckBoxTableCell<>();
            cell.itemProperty().addListener((obs, oldValue, newValue) -> {
                if (cell.getTableRow() != null && cell.getIndex() >= 0) {
                    WaysideBlockSubject block = cell.getTableView().getItems().get(cell.getIndex());
                    controller.trackModelSetOccupancy(block.getBlock().getBlockID(), newValue);
                }
            });
            return cell;
        });

        tbSwitchTable.setEditable(true);
        tbSTID.setCellValueFactory(block -> block.getValue().getIntegerProperty(blockID_p).asObject());
        tbSTSwitchTo.setCellValueFactory(block -> block.getValue().getIntegerProperty(switchedBlockID_p).asObject());
        tbSTEnable.setCellValueFactory(block -> block.getValue().getBooleanProperty(switchRequest_p));
//        tbSTEnable.setCellFactory(CheckBoxTableCell.forTableColumn(tbSTEnable));
        tbSTEnable.setCellFactory(column -> {
            CheckBoxTableCell<WaysideBlockSubject, Boolean> cell = new CheckBoxTableCell<>();
            cell.itemProperty().addListener((obs, oldValue, newValue) -> {
                if (cell.getTableRow() != null && cell.getIndex() >= 0) {
                    WaysideBlockSubject block = cell.getTableView().getItems().get(cell.getIndex());
                    controller.CTCRequestSwitchState(block.getBlock().getBlockID(), newValue);
                }
            });
            return cell;
        });
    }

    public void setController(WaysideController controller) {
        this.controller = controller;
        readBlockInfo(controller.getSubject().blockListProperty());
        if(controller instanceof WaysideControllerHWBridge) {
            tbHWPortLabel.setText("HW Port: " + ((WaysideControllerHWBridge) controller).getPort());
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
