package Framework.GUI.Managers;

import Common.WaysideController;
import waysideController.TrainSpeedAuth;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
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
    public TableView<TrainSpeedAuth> tbSpeedAuthTable;
    @FXML
    public TableColumn<TrainSpeedAuth, Integer> tbSATID;
    @FXML
    public TableColumn<TrainSpeedAuth, Double> tbSATSpeedIn;
    @FXML
    public TableColumn<TrainSpeedAuth, Integer> tbSATAuthIn;
    @FXML
    public TableColumn<TrainSpeedAuth, Double> tbSATSpeedOut;
    @FXML
    public TableColumn<TrainSpeedAuth, Integer> tbSATAuthOut;

    private Object block;
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
        tbBTID.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        tbBTOccupied.setCellValueFactory(block -> block.getValue().occupationProperty());
        tbBTOccupied.setCellFactory(CheckBoxTableCell.forTableColumn(tbBTOccupied));

        tbSwitchTable.setEditable(true);
        tbSTID.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        tbSTSwitchTo.setCellValueFactory(block -> block.getValue().switchedBlockIDProperty().asObject());
        tbSTEnable.setCellValueFactory(block -> block.getValue().switchRequestedStateProperty());
        tbSTEnable.setCellFactory(CheckBoxTableCell.forTableColumn(tbSTEnable));

        tbSpeedAuthTable.setEditable(true);
        tbSATID.setCellValueFactory(speedAuth -> speedAuth.getValue().trainIDProperty().asObject());
        tbSATSpeedIn.setCellValueFactory(speedAuth -> speedAuth.getValue().speedInProperty().asObject());
        tbSATAuthIn.setCellValueFactory(speedAuth -> speedAuth.getValue().authorityInProperty().asObject());
        tbSATSpeedOut.setCellValueFactory(speedAuth -> speedAuth.getValue().speedOutProperty().asObject());
        tbSATAuthOut.setCellValueFactory(speedAuth -> speedAuth.getValue().authorityOutProperty().asObject());
        tbSATSpeedIn.setCellFactory(TextFieldTableCell.forTableColumn(doubleConverter));
        tbSATAuthIn.setCellFactory(TextFieldTableCell.forTableColumn(intConverter));
    }

    public void setController(WaysideController controller) {
        this.controller = controller;
        readBlockInfo(controller.getSubject().blockListProperty());
        tbSpeedAuthTable.setItems(controller.getSubject().getSpeedAuthList());
    }

    /**
     * Read the block info from the wayside controller
     * @param blocks The list of blocks to read
     */
    private void readBlockInfo(ObservableList<WaysideBlockInfo> blocks) {
        tbBlockTable.setItems(blocks);
        tbSwitchTable.getItems().clear();
        for(WaysideBlockInfo item : blocks) {
            if(item.isHasSwitch()) {
                tbSwitchTable.getItems().add(item);
            }
        }
    }
}
