package trainModel.GUI;

import eu.hansolo.medusa.Gauge;
import javafx.scene.control.*;
import javafx.scene.shape.Circle;
import trainController.GUI.trainControllerManager;
import trainModel.trainModelImpl;

public class trainModelManager {

    private trainModelImpl train;

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

    private int trainID;
    private trainModelSubject currentTrainSubject;
    private trainSubjectFactory controllerManager;

    public void initialize() {
        train = new trainModelImpl(0);

        //set event listeners
        eBrakeBtn.setOnAction(event -> train.setEmergencyBrake(false));
        brakeFailureBtn.setOnAction(event -> train.setBrakeFailure(false));
        powerFailureBtn.setOnAction(event -> train.setPowerFailure(false));
        signalFailureBtn.setOnAction(event -> train.setSignalFailure(false));
    }
}

