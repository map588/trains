package Framework.GUI.Controllers;

import Framework.GUI.Utility.BooleanCell;
import Utilities.BlockInfo;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

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
        Callback<TableColumn<BlockInfo, Boolean>, TableCell<BlockInfo, Boolean>> booleanCellFactory = new Callback<TableColumn<BlockInfo, Boolean>, TableCell<BlockInfo, Boolean>>() {
            @Override
            public TableCell<BlockInfo, Boolean> call(TableColumn<BlockInfo, Boolean> param) {
                return new BooleanCell();
            }
        };
        tbBTRight.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().isOccupied()));
        tbBTRight.setCellFactory(booleanCellFactory);
    }

    /**
     * Read the block info from the wayside controller
     * @param blocks The list of blocks to read
     */
    public void readBlockInfo(ObservableList<BlockInfo> blocks) {
        tbBlockTable.setItems(blocks);
    }
}
