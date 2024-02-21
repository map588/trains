package Framework.GUI.Managers;

import Common.WaysideController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import waysideController.WaysideBlockInfo;

public class WaysideControllerTB {

    @FXML
    public Label tbWaysideNumberLabel;
    @FXML
    public TableView<WaysideBlockInfo> tbBlockTable;
    @FXML
    public TableView<WaysideBlockInfo> tbSwitchTable;
    @FXML
    public TableColumn<WaysideBlockInfo, Integer> tbBTID;
    @FXML
    public TableColumn<WaysideBlockInfo, Boolean> tbBTOccupied;
    @FXML
    public TableColumn<WaysideBlockInfo, Integer> tbSTID;
    @FXML
    public TableColumn<WaysideBlockInfo, Integer> tbSTSwitchTo;
    @FXML
    public TableColumn<WaysideBlockInfo, Boolean> tbSTEnable;
    @FXML
    public TableView tbSpeedAuthTable;
    @FXML
    public TableColumn tbSATID;
    @FXML
    public TableColumn tbSATSpeedIn;
    @FXML
    public TableColumn tbSATAuthIn;
    @FXML
    public TableColumn tbSATSpeedOut;
    @FXML
    public TableColumn tbSATAuthOut;

    private Object block;
    WaysideController controller;


    @FXML
    private void initialize() {
        tbBlockTable.setEditable(true);
        tbBTID.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        tbBTOccupied.setCellValueFactory(block -> block.getValue().occupationProperty());
        tbBTOccupied.setCellFactory(CheckBoxTableCell.forTableColumn(tbBTOccupied));

        tbSwitchTable.setEditable(true);
        tbSTID.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        tbSTSwitchTo.setCellValueFactory(block -> block.getValue().switchedBlockIDProperty().asObject());
        tbSTEnable.setCellValueFactory(block -> block.getValue().switchRequestedStateProperty());
        tbSTEnable.setCellFactory(CheckBoxTableCell.forTableColumn(tbSTEnable));
    }

    /**
     * Read the block info from the wayside controller
     * @param blocks The list of blocks to read
     */
    public void readBlockInfo(ObservableList<WaysideBlockInfo> blocks) {
        tbBlockTable.setItems(blocks);
        tbSwitchTable.getItems().clear();
        for(WaysideBlockInfo item : blocks) {
            if(item.isHasSwitch()) {
                tbSwitchTable.getItems().add(item);
            }
        }
    }
}
