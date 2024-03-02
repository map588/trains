package trackModel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;


public class TrackModelManager {



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
    private TextField murphyBlockInput;
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
    @FXML
    private Label statusLabel;

    //station signal switch
    @FXML
    private Label nameOfStationLabel;
    @FXML
    private Label passEmbarkedValue;
    @FXML
    private Label passDisembarkedValue;
    @FXML
    private Label ticketSalesValue;
    @FXML
    private Label signalStateDisplay;
    @FXML
    private Label signalBlockNumberDisplay;

    //beacon information
    @FXML
    private ComboBox<String> pickLine;
    @FXML
    private Label displayBeaconInfo;
    @FXML
    private Label beaconBlockNumber;

    //switch information
    @FXML
    private Label switchStateDisplay;

    @FXML
    private Label switchBlockNumbersDisplay;
    @FXML
    private Label crossingState;


    //table
    @FXML
    private TableView<TrackLayoutInfo> lineTable;
    @FXML
    private TableColumn<TrackLayoutInfo, String> sectionsColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, String> blockColumn;
    @FXML
    private TableColumn<TrackLayoutInfo, Boolean> occupiedColumn;
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


    // potential variables to assist with control
    private TrackModelImpl currTrackModel = new TrackModelImpl();
    //subject
    private TrackModelSubject trackModelSubject = new TrackModelSubject(currTrackModel);

    // test bench object
    private TrackModelTB testBench;
    private TrackLayoutInfo trackProperties;

    @FXML
    public void initialize(){
        //initialize buttons and user inputs

        testBench = launchTestBench();
        System.out.println(testBench);

        testBench.setTrackModel(currTrackModel);
        testBench.setTrackModelSubject(trackModelSubject);
        testBench.setTrackModelManager(this);

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
        switchStateDisplay.setText("No Switch Present");

        occupiedColumn.setEditable(true);

        //set up cell factories
        sectionsColumn.setCellValueFactory(block -> block.getValue().sectionProperty());
        blockColumn.setCellValueFactory(block -> block.getValue().blockNumberProperty());
        sizeColumn.setCellValueFactory(block -> block.getValue().blockLengthProperty().asObject());
        gradeColumn.setCellValueFactory(block -> block.getValue().blockGradeProperty().asObject());
        stationColumn.setCellValueFactory(block -> block.getValue().isStationProperty());
        signalColumn.setCellValueFactory(block -> block.getValue().isSignalProperty());
        switchColumn.setCellValueFactory(block -> block.getValue().isSwitchProperty());
        speedLimitColumn.setCellValueFactory(block -> block.getValue().speedLimitProperty().asObject());
        failureColumn.setCellValueFactory(block -> block.getValue().hasFailureProperty());
//        occupiedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(occupiedColumn));
        occupiedColumn.setCellValueFactory(block -> block.getValue().isOccupiedProperty());

        crossingState.setText("false");


        //set track heater

        //labels
        lineTable.getSelectionModel().selectedItemProperty().addListener(event -> {
            selectBlock(lineTable.getSelectionModel().getSelectedItem());
            testBench.selectBlock(lineTable.getSelectionModel().getSelectedItem());
        });


    }

    public void selectBlock(TrackLayoutInfo newProperties){
        System.out.println("Selected block");
        if(trackProperties != null) {
            // Unbind stuff here
            if(trackProperties.isIsStation()){
                passEmbarkedValue.textProperty().unbindBidirectional(trackProperties.passEmbarkedProperty());
                passDisembarkedValue.textProperty().unbindBidirectional(trackProperties.passDisembarkedProperty());
                ticketSalesValue.textProperty().unbindBidirectional(trackProperties.ticketSalesProperty());
                nameOfStationLabel.textProperty().unbindBidirectional(trackProperties.nameOfStationProperty());
            }

            if(trackProperties.isIsSwitch()){
                switchBlockNumbersDisplay.textProperty().unbindBidirectional(trackProperties.switchBlockIDProperty());
                switchStateDisplay.textProperty().unbindBidirectional(trackProperties.switchStateProperty());
                switchColumn.textProperty().unbindBidirectional(trackProperties.switchStateProperty());
                switchStateDisplay.textProperty().unbindBidirectional(trackProperties.switchMainProperty());
                switchStateDisplay.textProperty().unbindBidirectional(trackProperties.switchAltProperty());
            }

            if(trackProperties.isIsSignal()){
                signalStateDisplay.textProperty().unbindBidirectional(trackProperties.signalStateProperty());
                signalBlockNumberDisplay.textProperty().unbindBidirectional(trackProperties.signalIDProperty());
            }

            if(trackProperties.isIsCrossing()){

                crossingState.textProperty().unbindBidirectional(trackProperties.crossingStateProperty());
            }

            if(trackProperties.isIsBeacon()){
                displayBeaconInfo.textProperty().unbindBidirectional(trackProperties.setBeaconProperty());
                beaconBlockNumber.textProperty().unbindBidirectional(trackProperties.blockNumberProperty());
            }


            statusLabel.textProperty().unbindBidirectional(trackProperties.trackHeaterProperty());
            tempValueLabel.textProperty().unbindBidirectional(trackModelSubject.tempProperty());

            //occupiedColumn.textProperty().unbindBidirectional(trackProperties.isOccupiedProperty());

        }

        trackProperties = newProperties;
        lineTable.getSelectionModel().select(newProperties);


        // Bind stuff here
        if(trackProperties.isIsStation()){

            passEmbarkedValue.textProperty().bindBidirectional(trackProperties.passEmbarkedProperty());
            passDisembarkedValue.textProperty().bindBidirectional(trackProperties.passDisembarkedProperty());
            ticketSalesValue.textProperty().bindBidirectional(trackProperties.ticketSalesProperty());
            nameOfStationLabel.textProperty().bindBidirectional(trackProperties.nameOfStationProperty());

        }
        else {
            passEmbarkedValue.setText("");
            passDisembarkedValue.setText("");
            ticketSalesValue.setText("");
        }

        statusLabel.textProperty().bindBidirectional(trackProperties.trackHeaterProperty());
        tempValueLabel.textProperty().bindBidirectional(trackModelSubject.tempProperty());

        //occupiedColumn.textProperty().bindBidirectional(trackProperties.isOccupiedProperty());

        if(trackProperties.isIsSwitch()){
            switchBlockNumbersDisplay.textProperty().bindBidirectional(trackProperties.switchBlockIDProperty());
            switchStateDisplay.textProperty().bindBidirectional(trackProperties.switchStateProperty());
            switchStateDisplay.textProperty().bindBidirectional(trackProperties.switchMainProperty());
            switchStateDisplay.textProperty().bindBidirectional(trackProperties.switchAltProperty());
            switchColumn.textProperty().bindBidirectional(trackProperties.switchStateProperty());

        }
        else {
            switchBlockNumbersDisplay.setText("NONE");
            switchStateDisplay.setText("NONE");
        }

        if(trackProperties.isIsSignal()){
            signalStateDisplay.textProperty().bindBidirectional(trackProperties.signalStateProperty());
            signalBlockNumberDisplay.textProperty().bindBidirectional(trackProperties.signalIDProperty());
        }
        else {
            signalStateDisplay.setText("NONE");
            signalBlockNumberDisplay.setText("NONE");
        }

        if(trackProperties.isIsCrossing()){
            crossingState.textProperty().bindBidirectional(trackProperties.crossingStateProperty());
        }
        else {
            crossingState.setText("NONE");
        }

        if(trackProperties.isIsBeacon()){
            displayBeaconInfo.textProperty().bindBidirectional(trackProperties.setBeaconProperty());
            beaconBlockNumber.textProperty().bindBidirectional(trackProperties.blockNumberProperty());
        }
        else {
            displayBeaconInfo.setText("NONE");
        }

    }
    private void updateTable() {
        //get the line
        //String lineSelect = pickLine.getValue();
        //send the line to the track model
        //currTrackModel.getLine(line);

       // ObservableList<TrackLayoutInfo> tableInfo = FXCollections.observableArrayList(currTrackModel.getTrackInfo());
        //lineTable.setItems(tableInfo);
    }

    private void murphyEnter() {
        //get the line and block
        String lineSelect = murphyLine.getValue();
        String blockSelect = murphyBlockInput.getText();
        String failure = chooseFailureMode.getValue();

        //send the failure to the track model
        currTrackModel.setFailure(Integer.parseInt(blockSelect), failure);
        failureColumn.setCellValueFactory(block -> block.getValue().hasFailureProperty());
        trackProperties.setHasFailure(!chooseFailureMode.getValue().equals("Fix Track Failure"));

    }

    private void addLineName(String text) {
        pickLine.getItems().add(text);
        murphyLine.getItems().add(text);
        testBench.setLine(text);
    }
    private ArrayList<String> csvParser(File file) {
        return null;
    }

    private void uploadTrack() {
        //parse the csv file
        File file = new File(trackFilePath.getText());
        ArrayList<String> csvData = csvParser(file);
        this.addLineName(lineNameInput.getText());
    }

    //user input for track layout
    private void chooseFolder() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(chooseFile.getScene().getWindow());

        if(dir != null){ trackFilePath.setText(dir.getPath()); }
    }


    private TrackModelTB launchTestBench() {
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
            System.out.println("Test bench launched");
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to launch test bench");
            throw new RuntimeException(e);
        }
    }

}
