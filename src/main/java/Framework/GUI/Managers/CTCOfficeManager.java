package Framework.GUI.Managers;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;



public class CTCOfficeManager {
    public static final boolean green = true;
    public static final boolean red = false;
    public static final boolean straight = true;
    public static final boolean diverging = false;
    public static final boolean occupied = true;
    public static final boolean unoccupied = false;

    @FXML
    private VBox collapsingVBox;

    @FXML
    private Button ModeButton;


    @FXML
    private TableView<BlockInfo> blockTable;

    @FXML
    private TableColumn<BlockInfo, Number> blockNumberColumn;
    /*
        @FXML
        private TableColumn<BlockInfo, Boolean> occupationLightlumn;
    /*
        @FXML
        private TableColumn<BlockInfo, Boolean> switchLightColumn;

        @FXML
        private TableColumn<BlockInfo, Boolean> switchStateColumn;
    */
    @FXML
    public void initialize() {
        blockNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
       // occupationLightColumn.setCellValueFactory(cellData -> cellData.getValue().getOccupationLight());

        ObservableList<BlockInfo> lineOne = FXCollections.observableArrayList(
                new BlockInfo(1),
                new BlockInfo(2),
                new BlockInfo(3),
                new BlockInfo(4)
        );

        blockTable.setItems(lineOne);
    }

    @FXML
    private void toggle_VBox() {
        if (collapsingVBox.isVisible()) {
            collapsingVBox.setVisible(false);
            collapsingVBox.setManaged(false);
        } else {
            collapsingVBox.setVisible(true);
            collapsingVBox.setManaged(true);
        }
    }


}
