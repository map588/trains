package Framework.GUI.Managers;


import CTCOffice.CTCBlockSubject;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;


public class CTCOfficeManager {


    @FXML
    private VBox collapsingVBox;

    @FXML
    private Button ModeButton;


    @FXML
    private TableView<CTCBlockSubject> blockTable;

    @FXML
    private TableColumn<CTCBlockSubject, Integer> blockNumberColumn;

    @FXML
    private TableColumn<CTCBlockSubject, Boolean> occupationLightColumn;

    @FXML
    private TableColumn<CTCBlockSubject, Boolean> switchLightColumn;

    @FXML
    private TableColumn<CTCBlockSubject, Boolean> switchStateColumn;

    @FXML
    public void initialize() {
        blockTable.setEditable(true);
        blockNumberColumn.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getIntegerProperty("blockID").getValue()));
        occupationLightColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty("occupied"));
        occupationLightColumn.setCellFactory(CheckBoxTableCell.forTableColumn(occupationLightColumn));
        



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
/* failure
@FXML
public void initialize() {
    blockNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blockNumber"));
    occupationLightColumn.setCellValueFactory(new PropertyValueFactory<>("occupationLight"));
    switchLightColumn.setCellValueFactory(new PropertyValueFactory<>("switchLightColor"));
    switchStateColumn.setCellValueFactory(new PropertyValueFactory<>("switchState"));
    blockTable.getItems().addAll(
            new CTCBlockSubject(1, true, red, straight),
            new CTCBlockSubject(2, false, green, diverging),
            new CTCBlockSubject(3, true, red, straight),
            new CTCBlockSubject(4, false, green, diverging),
            new CTCBlockSubject(5, true, red, straight),
            new CTCBlockSubject(6, false, green, diverging)
    );
}
    */