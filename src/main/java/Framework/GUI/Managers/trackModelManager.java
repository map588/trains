package Framework.GUI.Managers;

import Utilities.TrackLayoutInfo;
import Utilities.staticBlockInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import trackModel.trackModelImpl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.lang.String;


public class trackModelManager {

    //simulation information
    @FXML
    private Label logo;
    @FXML
    private Button trackUpload;
    @FXML
    private Button chooseFile;
    @FXML
    private TextField trackFilePath;
    @FXML
    private ComboBox<String> simSpeedInput;
    @FXML
    private TextField lineNameInput;

    //murphy
    @FXML
    private ComboBox<String> murphyLine;
    @FXML
    private ComboBox<String> murphyBlock;
    @FXML
    private ComboBox<String> chooseFailureMode;
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
    private TableView<TrackLayoutInfo> lineTable;
    @FXML
    private TableColumn<TrackLayoutInfo, Character> sectionsColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Integer> blockColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Integer> occupiedColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Integer> sizeColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Float> gradeColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> stationColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> signalColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> switchColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Integer> speedLimitColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> failureColumn;


    // potential variables to assist with control
    // private trackModelImpl currTrackModel = null;


    public void initialize(){
        //initialize buttons and user inputs
        logo.setOnMouseClicked(event -> launchTestBench());
        chooseFile.setOnAction(event -> chooseFolder());
        trackUpload.setOnAction(event -> uploadTrack());
        simSpeedInput.getItems().addAll("1x","2x","3x","4x","5x","6x","7x","8x","9x","10x");
        lineNameInput.setOnAction(event -> addLineName(lineNameInput.getText()));
        chooseFailureMode.getItems().addAll(
                "Broken Rail",
                "Track Circuit Failure",
                "Power Failure",
                "Fix Track Failure"
        );
        murphyEnter.setOnAction(event -> murphyEnter());
        // failureColumn.setCellValueFactory(block -> block.getValue().);

    }

    private void murphyEnter() {
        //get the line and block
        String lineSelect = murphyLine.getValue();
        String blockSelect = murphyBlock.getValue();
        String failure = chooseFailureMode.getValue();
        TrackLayoutInfo trackProperties = new TrackLayoutInfo();

        if(chooseFailureMode.getValue().equals("Fix Track Failure")){
            //set failure column in table at block and line to false
            trackProperties.setBlockNumber(Integer.parseInt(blockSelect));
            trackProperties.setHasFailure(false);
            failureColumn.setCellValueFactory(block -> block.getValue().hasFailureProperty());
        }
        else{
            //set failure column in table at block and line to true
            trackProperties.setBlockNumber(Integer.parseInt(blockSelect));
            trackProperties.setHasFailure(true);
        }

        //send the failure to the track model
        //currTrackModel.setFailure(line, block, failure);
    }

    private void addLineName(String text) {
        pickLine.getItems().add(text);
        murphyLine.getItems().add(text);
    }
    private ArrayList<String> csvParser(File file) {

        //parse the csv file
        //return the data
        //if there is an error, return null



        return null;
    }



    private void uploadTrack() {
        //parse the csv file
        File file = new File(trackFilePath.getText());
        ArrayList<String> csvData = csvParser(file);

        //create a new track model
        trackModelImpl currTrackModel = new trackModelImpl(csvData);

        //populate the table
        lineTable.setItems(currTrackModel.getTrackInfo());
    }

    //user input for track layout
    private void chooseFolder() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(chooseFile.getScene().getWindow());

        if(dir != null){ trackFilePath.setText(dir.getPath()); }
    }


    private trackModelTB launchTestBench() {
        try {
            String tbFile = "/Framework/GUI/FXML/trackModel_TB.fxml";
            URL url = getClass().getResource(tbFile);
            FXMLLoader loader = new FXMLLoader(url);
            Node content = loader.load();
            Stage newStage = new Stage();
            Scene newScene = new Scene(new VBox(content));
            newStage.setScene(newScene);
            newStage.setTitle("Track Model Test Bench");
            newStage.show();
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to launch test bench");
            throw new RuntimeException(e);
        }
    }

}
