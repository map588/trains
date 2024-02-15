package Framework.GUI.Controllers;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;

public class trainModelControl {

    //Murphy Controls
    public ToggleButton brakeFailureBtn;
    public ToggleButton powerFailureBtn;
    public ToggleButton signalFailureBtn;
    
    public Button eBrakeBtn;
    public ChoiceBox trainDropDown;
    public TableView trainInfoTbl;
    public TableColumn trainInfoClm;
    public TableColumn trainInfoNum;
    public TableView passengerInfoTbl;
    public TableColumn passengerInfoClm;
    public TableColumn passengerInfoNum;
    public Gauge actualPowerDisp;
    public Gauge actualVelocityDisp;
    public Gauge actualAccelerationDisp;
    public Circle extLightsEn;
    public Circle intLightsEn;
    public Circle leftDoorsEn;
    public Circle rightDoorsEn;
    public Circle sBrakeEn;
    public Circle eBrakeEn;
    public Gauge cmdSpeedDisp;
    public Gauge authorityDisp;


