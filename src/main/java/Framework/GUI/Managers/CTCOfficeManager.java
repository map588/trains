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
    private TableView<BlockInfoManager> blockTable;

    @FXML
    private TableColumn<BlockInfoManager, Number> blockNumberColumn;

    @FXML
    private TableColumn<BlockInfoManager, String> occupationLightColumn;

    @FXML
    private TableColumn<BlockInfoManager, Boolean> switchLightColumn;

    @FXML
    private TableColumn<BlockInfoManager, Boolean> switchStateColumn;


    @FXML
    public void initialize() {
        blockNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
        occupationLightColumn.setCellValueFactory(new PropertyValueFactory<>("occupationLight"));
        switchLightColumn.setCellValueFactory(new PropertyValueFactory<>("switchLightColor"));
        switchStateColumn.setCellValueFactory(new PropertyValueFactory<>("switchState"));
        blockTable.getItems().addAll(
                new BlockInfoManager(1, true, red, straight),
                new BlockInfoManager(2, false, green, diverging),
                new BlockInfoManager(3, true, red, straight),
                new BlockInfoManager(4, false, green, diverging),
                new BlockInfoManager(5, true, red, straight),
                new BlockInfoManager(6, false, green, diverging)
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
