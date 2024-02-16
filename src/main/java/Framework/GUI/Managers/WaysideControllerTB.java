package Framework.GUI.Managers;

import Utilities.BlockInfo;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

public class WaysideControllerTB {

    @FXML
    public Label tbWaysideNumberLabel;
    @FXML
    public TableView<BlockInfo> tbBlockTable;
    @FXML
    public TableView tbSwitchTable;
    @FXML
    public TableView tbLightTable;
    @FXML
    public TableColumn<BlockInfo, Integer> tbBTLeft;
    @FXML
    public TableColumn<BlockInfo, Boolean> tbBTRight;
    @FXML
    public TableColumn tbSTLeft;
    @FXML
    public TableColumn tbSTRight;
    @FXML
    public TableColumn tbLTLeft;
    @FXML
    public TableColumn tbLTRight;


    @FXML
    private void initialize() {
        tbBlockTable.setEditable(true);
        tbBTLeft.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getStaticInfo().getBlockNumber()));

         // Create the Cell Factory for the Boolean columns
//        Callback<TableColumn<BlockInfo, Boolean>, TableCell<BlockInfo, Boolean>> booleanCellFactory = new Callback<TableColumn<BlockInfo, Boolean>, TableCell<BlockInfo, Boolean>>() {
//            @Override
//            public TableCell<BlockInfo, Boolean> call(TableColumn<BlockInfo, Boolean> param) {
//                return new BooleanCell();
//            }
//        };
//
//        tbBTRight.setCellValueFactory(param -> param.getValue().getTrackCircuitStateProperty());
//        tbBTRight.setCellFactory(booleanCellFactory);

        // New version of the above code block, uses a standard checkbox cell and updates the properties correctly
        tbBTRight.setCellValueFactory(block -> block.getValue().getTrackCircuitStateProperty());
        tbBTRight.setCellFactory(CheckBoxTableCell.forTableColumn(tbBTRight));
    }

    /**
     * Read the block info from the wayside controller
     * @param blocks The list of blocks to read
     */
    public void readBlockInfo(ObservableList<BlockInfo> blocks) {
        tbBlockTable.setItems(blocks);
    }
}
