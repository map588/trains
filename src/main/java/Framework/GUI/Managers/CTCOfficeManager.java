package Framework.GUI.Managers;

import CTCOffice.CTCBlockSubject;
import CTCOffice.CTCBlockSubjectFactory;
import CTCOffice.CTCOfficeImpl;
import Common.CTCOffice;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;

import java.util.Collection;


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
    CTCBlockSubjectFactory factory = CTCBlockSubjectFactory.getInstance();
    @FXML
    public void initialize() {
        CTCOffice one = new CTCOfficeImpl();

        blockTable.setEditable(true);
        Collection<CTCBlockSubject> blockList = factory.getSubjects().values();
        blockTable.getItems().addAll(blockList);
        blockNumberColumn.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getIntegerProperty("blockID").getValue()));
        occupationLightColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty("occupied"));
        occupationLightColumn.setCellFactory(CheckBoxTableCell.forTableColumn(occupationLightColumn));
        switchLightColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty("hasSwitchCon"));
        //switchLightColumn.setCellFactory(

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
