package Framework.GUI.Controllers;

import Utilities.BlockInfo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;


public class trackModelControl {

    @FXML
    private Button trackUpload;
    @FXML
    private Button chooseFile;
    @FXML
    private ComboBox<String> simSpeedInput;
    @FXML
    private TextField trackFilePath;
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
    @FXML
    private ToggleButton trackHeaterIndicator;
    @FXML
    private TextField tempDisplay;
    @FXML
    private TextField stationDisplay;
    @FXML
    private TextField passEmbarkDisplay;
    @FXML
    private TextField passDisembarkDisplay;
    @FXML
    private TextField ticketSalesDisplay;
    @FXML
    private TextField beaconInput;
    @FXML
    private Button beaconUpload;
    @FXML
    private ComboBox<String> pickLine;
    @FXML
    private TableView<BlockInfo> sectionTable;
    @FXML
    private TableColumn<BlockInfo, String> sectionsColumn;
    @FXML
    private TableColumn<BlockInfo, Integer> blockColumn;
    @FXML
    private TableColumn<BlockInfo, Integer> sizeColumn;
    @FXML
    private TableColumn<BlockInfo, Float> gradeColumn;
    @FXML
    private TableColumn<BlockInfo, Boolean> stationColumn;
    @FXML
    private TableColumn<BlockInfo, Boolean> signalColumn;
    @FXML
    private TableColumn<BlockInfo, Boolean> switchColumn;
    @FXML
    private TableColumn<BlockInfo, Integer> speedLimitColumn;
    @FXML
    private TableColumn<BlockInfo, Boolean> failureColumn;


    // potential variables to assist with control
    // private trackModelImpl currTrackModel = null;


    public void initialize(){
        //initialize buttons and user inputs
        chooseFile.setOnAction(event -> chooseFolder());
        trackUpload.setOnAction(event -> uploadTrack());
        simSpeedInput.setOnAction(event -> listSimSpeedOptions());
        murphyLine.setOnAction(event -> listLineOptions());
        murphyBlock.setOnAction(event -> listBlockOptions());
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
