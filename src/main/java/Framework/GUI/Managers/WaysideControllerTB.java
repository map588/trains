package Framework.GUI.Managers;

import Common.WaysideController;
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
import waysideController.WaysideBlockInfo;

public class WaysideControllerTB {

    @FXML
    public Label tbWaysideNumberLabel;
    @FXML
    public TableView<WaysideBlockInfo> tbBlockTable;
    @FXML
    public TableView<WaysideBlockInfo> tbSwitchTable;
    @FXML
    public TableColumn<WaysideBlockInfo, Integer> tbBTLeft;
    @FXML
    public TableColumn<WaysideBlockInfo, Boolean> tbBTRight;
    @FXML
    public TableColumn<WaysideBlockInfo, Integer> tbSTLeft;
    @FXML
    public TableColumn<WaysideBlockInfo, Integer> tbSTRight;
    @FXML
    public TableColumn<WaysideBlockInfo, Boolean> tbSTEnable;
    private Object block;
    WaysideController controller;


    @FXML
    private void initialize() {
        tbBlockTable.setEditable(true);
        tbBTLeft.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        tbBTRight.setCellValueFactory(block -> block.getValue().occupationProperty());
        tbBTRight.setCellFactory(CheckBoxTableCell.forTableColumn(tbBTRight));

        tbSwitchTable.setEditable(true);
        tbSTLeft.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        tbSTRight.setCellValueFactory(block -> block.getValue().switchedBlockIDProperty().asObject());
        tbSTEnable.setCellValueFactory(block -> block.getValue().switchRequestedStateProperty());
        tbSTEnable.setCellFactory(CheckBoxTableCell.forTableColumn(tbSTEnable));

        /**
        tbLightTable.setEditable(true);
        tbLTLeft.setCellValueFactory(block -> block.getValue().lightInStateProperty().asObject());
        tbLTLeft.setCellFactory(ComboBoxTableCell.forTableColumn(0, 1, 2));
        tbLTRight.setCellValueFactory(block -> block.getValue().lightOutStateProperty().asObject());
        tbLTRight.setCellFactory(ComboBoxTableCell.forTableColumn(0, 1, 2));
         */
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
