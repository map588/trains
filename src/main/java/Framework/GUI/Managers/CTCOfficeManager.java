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
    private TableView<CTCBlockInfoManager> blockTable;

    @FXML
    private TableColumn<CTCBlockInfoManager, Number> blockNumberColumn;

    @FXML
    private TableColumn<CTCBlockInfoManager, String> occupationLightColumn;

    @FXML
    private TableColumn<CTCBlockInfoManager, Boolean> switchLightColumn;

    @FXML
    private TableColumn<CTCBlockInfoManager, Boolean> switchStateColumn;


    @FXML
    public void initialize() {
        blockNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
        occupationLightColumn.setCellValueFactory(new PropertyValueFactory<>("occupationLight"));
        switchLightColumn.setCellValueFactory(new PropertyValueFactory<>("switchLightColor"));
        switchStateColumn.setCellValueFactory(new PropertyValueFactory<>("switchState"));
        blockTable.getItems().addAll(
                new CTCBlockInfoManager(1, true, red, straight),
                new CTCBlockInfoManager(2, false, green, diverging),
                new CTCBlockInfoManager(3, true, red, straight),
                new CTCBlockInfoManager(4, false, green, diverging),
                new CTCBlockInfoManager(5, true, red, straight),
                new CTCBlockInfoManager(6, false, green, diverging)
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
