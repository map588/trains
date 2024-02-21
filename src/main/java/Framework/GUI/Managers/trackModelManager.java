package Framework.GUI.Managers;

import Utilities.TrackLayoutInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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


public class trackModelManager {

    public trackModelManager() {
        this.trackProperties = new TrackLayoutInfo();

        this.sectionsColumn = new TableColumn<>();
        this.blockColumn = new TableColumn<>();
        this.occupiedColumn = new TableColumn<>();
        this.sizeColumn = new TableColumn<>();
        this.gradeColumn = new TableColumn<>();
        this.stationColumn = new TableColumn<>();
        this.signalColumn = new TableColumn<>();
        this.switchColumn = new TableColumn<>();
        this.speedLimitColumn = new TableColumn<>();
        this.failureColumn = new TableColumn<>();


    }

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
//    @FXML
//    private Label statusLabel;
    @FXML
    private TextField tempDisplay;
    @FXML
    private Label tempValueLabel;

    //station signal switch
    @FXML
    private TextField stationDisplay;
//    @FXML
//    private TextField passEmbarkDisplay;
//    @FXML
//    private TextField passDisembarkDisplay;
//    @FXML
//    private TextField ticketSalesDisplay;

    //beacon information
    @FXML
    private ComboBox<String> pickLine;
    @FXML
    private TextField beaconLabel;

    //switch information
    @FXML
    private Label switchStateDisplay;

//    @FXML
//    private Label switchBlockNumberDisplay;

    //table
    @FXML
    private TableView<TrackLayoutInfo> lineTable;
    @FXML
    private TableColumn<TrackLayoutInfo, String> sectionsColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Integer> blockColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Integer> occupiedColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Integer> sizeColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Double> gradeColumn;
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

    //current layout
    private TrackLayoutInfo trackProperties = new TrackLayoutInfo();

    // potential variables to assist with control
    private trackModelImpl currTrackModel = new trackModelImpl();

    // test bench object
    private trackModelTB testBench = new trackModelTB();


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
        pickLine.setOnAction(event -> updateTable());
//        passEmbarkDisplay.setText("0");
//        passDisembarkDisplay.setText("0");
//        ticketSalesDisplay.setText("0");
        tempValueLabel.setText("26");
//        statusLabel.setText("ON");
        switchStateDisplay.setText("No Switch Present");
//        switchBlockNumberDisplay.setText("NONE");

        //set up cell factories
        sectionsColumn.setCellValueFactory(block -> block.getValue().sectionProperty());
        blockColumn.setCellValueFactory(block -> block.getValue().blockNumberProperty().asObject());
        // occupiedColumn.setCellValueFactory(block -> block.getValue().blockLengthProperty().asObject());
        sizeColumn.setCellValueFactory(block -> block.getValue().blockLengthProperty().asObject());
        gradeColumn.setCellValueFactory(block -> block.getValue().blockGradeProperty().asObject());
        stationColumn.setCellValueFactory(block -> block.getValue().isStationProperty());
        signalColumn.setCellValueFactory(block -> block.getValue().isSignalProperty());
        switchColumn.setCellValueFactory(block -> block.getValue().isSwitchProperty());
        speedLimitColumn.setCellValueFactory(block -> block.getValue().speedLimitProperty().asObject());
        failureColumn.setCellValueFactory(block -> block.getValue().hasFailureProperty());

        //set beacon info
        //viewInfoBeacon.setOnAction(event -> getBeaconInfo());

    }

    private void updateTable() {
        //get the line
        //String lineSelect = pickLine.getValue();
        //send the line to the track model
        //currTrackModel.getLine(line);

        ObservableList<TrackLayoutInfo> tableInfo = FXCollections.observableArrayList(currTrackModel.getTrackInfo());
        lineTable.setItems(tableInfo);
    }

    private void murphyEnter() {
        //get the line and block
        String lineSelect = murphyLine.getValue();
        String blockSelect = murphyBlock.getValue();
        String failure = chooseFailureMode.getValue();

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
        currTrackModel.setLine(text);
    }
    private ArrayList<String> csvParser(File file) {
        return null;
    }

    private void uploadTrack() {
        //parse the csv file
        File file = new File(trackFilePath.getText());
        ArrayList<String> csvData = csvParser(file);
        this.updateTable();
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
