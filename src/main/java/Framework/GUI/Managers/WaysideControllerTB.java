package Framework.GUI.Managers;

import Utilities.BlockInfo;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;

public class WaysideControllerTB {

    @FXML
    public Label tbWaysideNumberLabel;
    @FXML
    public TableView<BlockInfo> tbBlockTable;
    @FXML
    public TableView<BlockInfo> tbSwitchTable;
    @FXML
    public TableColumn<BlockInfo, Integer> tbBTLeft;
    @FXML
    public TableColumn<BlockInfo, Boolean> tbBTRight;
    @FXML
    public TableColumn<BlockInfo, Integer> tbSTLeft;
    @FXML
    public TableColumn<BlockInfo, Integer> tbSTRight;
    @FXML
    public TableColumn<BlockInfo, Boolean> tbSTEnable;
    private Object block;


    @FXML
    private void initialize() {
        tbBlockTable.setEditable(true);
        tbBTLeft.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getStaticInfo().getBlockNumber().getValue()));
        tbBTRight.setCellValueFactory(block -> block.getValue().getTrackCircuitStateProperty());
        tbBTRight.setCellFactory(CheckBoxTableCell.forTableColumn(tbBTRight));

        tbSwitchTable.setEditable(true);
        tbSTLeft.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getStaticInfo().getBlockNumber().getValue()));
        tbSTRight.setCellValueFactory(block -> block.getValue().getStaticInfo().switchedBlockNumber.asObject());
        tbSTEnable.setCellValueFactory(block -> block.getValue().getStaticInfo().isSwitched);
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
    public void readBlockInfo(ObservableList<BlockInfo> blocks) {
        tbBlockTable.setItems(blocks);
        //tbLightTable.setItems(blocks);
        for(BlockInfo item : blocks) {
            if(item.getStaticInfo().isSwitch()) {
                tbSwitchTable.getItems().add(item);
            }
        }
    }
}
