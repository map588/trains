package Framework.GUI.Managers;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.shape.Circle;

public class trainModelTB {
    @FXML
    public ToggleButton tbEBrake, tbSBrake, tbIntLights, tbExtLights, tbLeftDoors, tbRightDoors;
    @FXML
    public TextField tbPower, tbSpeed, tbAuthority, tbGrade, tbTemp;
    @FXML
    public Circle tbBrakeFailure, tbPowerFailure, tbSignalFailure;

    @FXML
    public void initialize() {

    }
}
