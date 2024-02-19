package Framework.GUI.Managers;

import eu.hansolo.medusa.Gauge;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.CheckBox;
import javafx.scene.shape.Circle;
import trainController.trainControllerImpl;


public class trainControllerControl {

    // Cabin Settings
    @FXML
    public CheckBox trainController_intLight_CheckBox;

    @FXML
    public CheckBox trainController_extLight_CheckBox;

    @FXML
    public CheckBox trainController_openDoorLeft_CheckBox;

    @FXML
    public CheckBox trainController_openDoorRight_CheckBox;

    @FXML
    public TextField trainController_setTemperature_TextField;

    @FXML
    public Button trainController_makeAnnouncements_Button;

    // Engineer's Input
    @FXML
    public TextField trainController_setKi_TextField;

    @FXML
    public TextField trainController_setKp_TextField;

    // Speed Controller
    @FXML
    public Slider trainController_setSpeed_Slider;
    @FXML
    public TextField trainController_setSpeed_TextField;

    @FXML
    public Gauge trainController_currentSpeed_Gauge;

    @FXML
    public Gauge trainController_speedLimit_Gauge;

    @FXML
    public Gauge trainController_commandSpeed_Gauge;

    // Authority
    @FXML
    public Gauge trainController_blocksToNextStation_Gauge;

    @FXML
    public Gauge trainController_Authority_Gauge;

    // Power/Brake
    @FXML
    public Button trainController_emergencyBrake_Button;

    @FXML
    public Circle trainController_eBrake_Status;

    @FXML
    public CheckBox trainController_toggleServiceBrake_CheckBox;

    @FXML
    public Gauge trainiController_powerOutput_Gauge;

    // Failure States
    @FXML
    public Circle trainController_powerFailure_Status;
    @FXML
    public Circle trainController_brakeFailure_Status;
    @FXML
    public Circle trainController_signalFailure_Status;

    // Train Selector
    @FXML
    public ChoiceBox trainController_trainNo_ChoiceBox;

    @FXML
    public CheckBox trainController_autoMode_CheckBox;


    private trainControllerImpl trainController = new trainControllerImpl(0); // Assuming constructor takes an ID

    /*  What needs to be Initialize
     *  - Cabin Settings
     *      - Doors: Closed
     *      - Lights: Closed
     *      - Temperature: 75 C
     *  - Speed Controller
     *      - SPD LIMIT: 0 MPH
     *      - Command Speed: 0 MPH
     *      - Current Speed: 0 MPH
     *      - Set Speed: 0 MPH
     *  - Engineer's Input
     *      - Ki = 1
     *      - Kp = 1
     *  - Brake/Power
     *      - Power: 0 HP
     *      - EBrake Status: OFF
     *  - Failure State
     *      - Power Failure: OFF
     *      - Brake Failure: OFF
     *      - Signal Failure: OFF
     *  - Train Selector
     *      -
     */
    @FXML
    public void initialize() {

    }

    private void setDefaultState(){

    }

    private void setDefaultState(int trainNo){

    }

}
