package Framework.GUI.Manangers;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import trainModel.trainModelImpl;
import trainModel.trainModelSubject;

public class trainModelManager {

    private trainModelImpl train;

    //Murphy Controls
    @FXML
    public ToggleButton brakeFailureBtn;
    @FXML
    public ToggleButton powerFailureBtn;
    @FXML
    public ToggleButton signalFailureBtn;
    @FXML
    public Button eBrakeBtn;
    @FXML
    public ChoiceBox trainDropDown;
    @FXML
    public TableView trainInfoTbl;
    @FXML
    public TableColumn trainInfoClm;
    @FXML
    public TableColumn trainInfoNum;
    @FXML
    public TableView passengerInfoTbl;
    @FXML
    public TableColumn passengerInfoClm;
    @FXML
    public TableColumn passengerInfoNum;
    @FXML
    public Gauge actualPowerDisp;
    @FXML
    public Gauge actualVelocityDisp;
    @FXML
    public Gauge actualAccelerationDisp;
    @FXML
    public Circle extLightsEn;
    @FXML
    public Circle intLightsEn;
    @FXML
    public Circle leftDoorsEn;
    @FXML
    public Circle rightDoorsEn;
    @FXML
    public Circle sBrakeEn;
    @FXML
    public Circle eBrakeEn;
    @FXML
    public Gauge cmdSpeedDisp;
    @FXML
    public Gauge authorityDisp;

    private trainModelSubject subject = new trainModelSubject();

    public void initialize() {

        //set event listeners
        eBrakeBtn.setOnAction(event -> subject.setEmergencyBrake(true));
        brakeFailureBtn.setOnAction(event -> subject.setBrakeFailure(false));
        powerFailureBtn.setOnAction(event -> subject.setPowerFailure(false));
        signalFailureBtn.setOnAction(event -> subject.setSignalFailure(false));
    }
}

