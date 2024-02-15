package Framework.GUI.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class waysideControllerTB {

    @FXML
    private Label tbWaysideNumberLabel;
    @FXML
    private AnchorPane tbBlockMenu;
    @FXML
    private AnchorPane tbLightMenu;
    @FXML
    private AnchorPane tbSwitchMenu;



    @FXML
    private void initialize() {
        tbWaysideNumberLabel.setText("Wayside Controller #1");
    }
}
