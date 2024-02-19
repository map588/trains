package Framework.GUI.Managers;

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
    public Button eBrakeBtn;
    @FXML
    public ChoiceBox trainDropDown;
    @FXML
    public ToggleButton brakeFailureBtn, powerFailureBtn, signalFailureBtn;
    @FXML
    public TableView passengerInfoTbl, getTrainInfoTbl;
    @FXML
    public TableColumn passengerInfoClm, passengerInfoNum, trainInfoNum, trainInfoClm;
    @FXML
    public Gauge actualPowerDisp, actualVelocityDisp, actualAccelerationDisp, cmdSpeedDisp, authorityDisp;
    @FXML
    public Circle extLightsEn, intLightEn, leftDoorsEn, rightDoorsEn, sBrakeEn, eBrakeEn;


    private trainModelSubject subject = new trainModelSubject(null, 0);

    public void initialize() {

        //set event listeners
        eBrakeBtn.setOnAction(event -> subject.updateProperty(subject.getBooleanProperty("emergencyBrake"), true));

        brakeFailureBtn.setOnAction(event -> {
            boolean newBrakeFailure = !subject.getBooleanProperty("brakeFailure").get();
            subject.updateProperty(subject.getBooleanProperty("brakeFailure"), newBrakeFailure);
        });

        powerFailureBtn.setOnAction(event -> {
            boolean newPowerFailure = !subject.getBooleanProperty("powerFailure").get();
            subject.updateProperty(subject.getBooleanProperty("powerFailure"), newPowerFailure);
        });

        signalFailureBtn.setOnAction(event -> {
            boolean newSignalFailure = !subject.getBooleanProperty("signalFailure").get();
            subject.updateProperty(subject.getBooleanProperty("signalFailure"), newSignalFailure);
        });
    }
}

