package Framework.GUI.Managers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;


public class CTCOfficeManager {
    @FXML
    private HBox collapsingHBox;

    @FXML
    private Button ModeButton;

    @FXML
    private void toggle_HBox() {
        if (collapsingHBox.isVisible()) {
            collapsingHBox.setVisible(false);
            collapsingHBox.setManaged(false);
        } else {
            collapsingHBox.setVisible(true);
            collapsingHBox.setManaged(true);
        }
    }
w
    void initialize() {
        collapsingHBox.setVisible(false);
        collapsingHBox.setManaged(false);
    }

}
