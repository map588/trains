package Framework.GUI.Managers;

import Utilities.BlockInfo;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;

public class WaysideControllerTB {

    @FXML
    public Label tbWaysideNumberLabel;
    @FXML
    public TableView<BlockInfo> tbBlockTable;
    @FXML
    public TableView tbSwitchTable;
    @FXML
    public TableView<BlockInfo> tbLightTable;
    @FXML
    public TableColumn<BlockInfo, Integer> tbBTLeft;
    @FXML
    public TableColumn<BlockInfo, Boolean> tbBTRight;
    @FXML
    public TableColumn tbSTLeft;
    @FXML
    public TableColumn tbSTRight;
    @FXML
    public TableColumn<BlockInfo, Integer> tbLTLeft;
    @FXML
    public TableColumn<BlockInfo, Integer> tbLTRight;


    @FXML
    private void initialize() {
        tbBlockTable.setEditable(true);
        tbBTLeft.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getStaticInfo().getBlockNumber()));
        tbBTRight.setCellValueFactory(block -> block.getValue().getTrackCircuitStateProperty());
        tbBTRight.setCellFactory(CheckBoxTableCell.forTableColumn(tbBTRight));

        tbLightTable.setEditable(true);
        tbLTLeft.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().lightInStateProperty().getValue()));
        tbLTRight.setCellValueFactory(block -> block.getValue().lightOutStateProperty().asObject());
        tbLTRight.setCellFactory(ComboBoxTableCell.forTableColumn(0, 1, 2));
    }

    /**
     * Read the block info from the wayside controller
     * @param blocks The list of blocks to read
     */
    public void readBlockInfo(ObservableList<BlockInfo> blocks) {
        tbBlockTable.setItems(blocks);
        tbLightTable.setItems(blocks);
    }
}
