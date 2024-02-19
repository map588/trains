package Framework.GUI.Managers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;



public class CTCOfficeManager {
    public static final Boolean green = true;
    public static final Boolean red = false;
    public static final boolean straight = true;
    public static final boolean diverging = false;

    @FXML
    private VBox collapsingVBox;

    @FXML
    private Button ModeButton;


    @FXML
    private TableView<BlockInfo> blockTable;

    @FXML
    private TableColumn<BlockInfo, Number> blockNumberColumn;

    @FXML
    private TableColumn<BlockInfo, String> occupationLightColumn;
    /*
        @FXML
        private TableColumn<BlockInfo, Boolean> switchLightColumn;

        @FXML
        private TableColumn<BlockInfo, Boolean> switchStateColumn;
*/

    @FXML
    public void initialize() {
        blockNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
        occupationLightColumn.setCellValueFactory(new PropertyValueFactory<>("occupationLight"));
        blockTable.getItems().addAll(
                new BlockInfo(1, true),
                new BlockInfo(2, false),
                new BlockInfo(3, true),
                new BlockInfo(4, false),
                new BlockInfo(5, true),
                new BlockInfo(6, false)
        );


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
