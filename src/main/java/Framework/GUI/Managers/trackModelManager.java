package Framework.GUI.Managers;

import Utilities.staticBlockInfo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;


public class trackModelManager {

    //simulation information
    @FXML
    private Button trackUpload;
    @FXML
    private Button chooseFile;
    @FXML
    private TextField trackFilePath;
    @FXML
    private ComboBox<String> simSpeedInput;

    //murphy
    @FXML
    private ComboBox<String> murphyLine;
    @FXML
    private ComboBox<String> murphyBlock;
    @FXML
    private CheckBox fixTrackFailure;
    @FXML
    private CheckBox trackCircuitFailure;
    @FXML
    private CheckBox breakRail;
    @FXML
    private CheckBox powerFailure;
    @FXML
    private Button murphyEnter;

    //track heater
    @FXML
    private ToggleButton trackHeaterIndicator;
    @FXML
    private TextField tempDisplay;

    //station signal switch
    @FXML
    private TextField stationDisplay;
    @FXML
    private TextField passEmbarkDisplay;
    @FXML
    private TextField passDisembarkDisplay;
    @FXML
    private TextField ticketSalesDisplay;

    //beacon information
    @FXML
    private TextField beaconInput;
    @FXML
    private Button beaconUpload;
    @FXML
    private ComboBox<String> pickLine;

    //table
    @FXML
    private TableView<staticBlockInfo> sectionTable;
    @FXML
    private TableColumn<staticBlockInfo, String> sectionsColumn;
    @FXML
    private TableColumn<staticBlockInfo, Integer> blockColumn;
    @FXML
    private TableColumn<staticBlockInfo, Integer> sizeColumn;
    @FXML
    private TableColumn<staticBlockInfo, Float> gradeColumn;
    @FXML
    private TableColumn<staticBlockInfo, Boolean> stationColumn;
    @FXML
    private TableColumn<staticBlockInfo, Boolean> signalColumn;
    @FXML
    private TableColumn<staticBlockInfo, Boolean> switchColumn;
    @FXML
    private TableColumn<staticBlockInfo, Integer> speedLimitColumn;
    @FXML
    private TableColumn<staticBlockInfo, Boolean> failureColumn;


    // potential variables to assist with control
    // private trackModelImpl currTrackModel = null;


    public void initialize(){
        //initialize buttons and user inputs
        chooseFile.setOnAction(event -> chooseFolder());
        trackUpload.setOnAction(event -> uploadTrack());
        simSpeedInput.getItems().addAll("10x", "9x", "8x", "7x", "6x", "5x", "4x", "3x", "2x","1x");
        murphyLine.getItems().add("Blue");
        pickLine.getItems().add("Blue");


    }

    private void listBlockOptions() {
    }

    private void listSimSpeedOptions() {
    }

    private void listLineOptions() {
    }

    private ArrayList<String> csvParser(File file) {
        return null;
    }

    private void uploadTrack() {

    }

    //user input for track layout
    private void chooseFolder() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(chooseFile.getScene().getWindow());

        if(dir != null){ trackFilePath.setText(dir.getPath()); }
    }


}
