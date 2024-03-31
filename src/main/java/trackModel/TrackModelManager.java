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
    private TableView<TrackLineSubject> lineTable;
    @FXML
    private TableColumn<TrackLineSubject, String> sectionsColumn;
    @FXML
    private TableColumn<TrackLineSubject, String> blockColumn;
    @FXML
    private TableColumn<TrackLineSubject, Boolean> occupiedColumn;
    @FXML
    private TableColumn<TrackLineSubject, Integer> sizeColumn;
    @FXML
    private TableColumn<TrackLineSubject, Double> gradeColumn;
    @FXML
    private TableColumn<TrackLineSubject, Boolean> stationColumn;
    @FXML
    private TableColumn<TrackLineSubject, Boolean> signalColumn;
    @FXML
    private TableColumn<TrackLineSubject, Boolean> switchColumn;
    @FXML
    private TableColumn<TrackLineSubject, Integer> speedLimitColumn;
    @FXML
    private TableColumn<TrackLineSubject, Boolean> failureColumn;

    //current layout


    // potential variables to assist with control
    //subject
    private ArrayList <TrackLineSubject> trackModelSubject = new ArrayList<>();

    // test bench object
    private TrackModelTBManager testBench;
    private TrackLineSubject subject;

    @FXML
    public void initialize(){
        //initialize buttons and user inputs

        testBench = launchTestBench();
        System.out.println(testBench);

        testBench.setTrackModelSubject(subject);
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

    public void selectBlock(TrackLineSubject newProperties){
        System.out.println("Selected block");
        if(subject != null) {
            // Unbind stuff here
            if(subject.isIsStation()){
                passEmbarkedValue.textProperty().unbindBidirectional(subject.passEmbarkedProperty());
                passDisembarkedValue.textProperty().unbindBidirectional(subject.passDisembarkedProperty());
                ticketSalesValue.textProperty().unbindBidirectional(subject.ticketSalesProperty());
                nameOfStationLabel.textProperty().unbindBidirectional(subject.nameOfStationProperty());
            }

            if(subject.isIsSwitch()){
                switchBlockNumbersDisplay.textProperty().unbindBidirectional(subject.switchBlockIDProperty());
                switchStateDisplay.textProperty().unbindBidirectional(subject.switchStateProperty());
                switchColumn.textProperty().unbindBidirectional(subject.switchStateProperty());
                switchStateDisplay.textProperty().unbindBidirectional(subject.switchMainProperty());
                switchStateDisplay.textProperty().unbindBidirectional(subject.switchAltProperty());
            }

            if(subject.isIsSignal()){
                signalStateDisplay.textProperty().unbindBidirectional(subject.signalStateProperty());
                signalBlockNumberDisplay.textProperty().unbindBidirectional(subject.signalIDProperty());
            }

            if(subject.isIsCrossing()){

                crossingState.textProperty().unbindBidirectional(subject.crossingStateProperty());
            }

            if(subject.isIsBeacon()){
                displayBeaconInfo.textProperty().unbindBidirectional(subject.setBeaconProperty());
                beaconBlockNumber.textProperty().unbindBidirectional(subject.blockNumberProperty());
            }


            statusLabel.textProperty().unbindBidirectional(subject.trackHeaterProperty());
            //tempValueLabel.textProperty().unbindBidirectional(trackModelSubject.tempProperty());

            //occupiedColumn.textProperty().unbindBidirectional(trackProperties.isOccupiedProperty());

        }

        subject = newProperties;
        lineTable.getSelectionModel().select(newProperties);


        // Bind stuff here
        if(subject.isIsStation()){

            passEmbarkedValue.textProperty().bindBidirectional(subject.passEmbarkedProperty());
            passDisembarkedValue.textProperty().bindBidirectional(subject.passDisembarkedProperty());
            ticketSalesValue.textProperty().bindBidirectional(subject.ticketSalesProperty());
            nameOfStationLabel.textProperty().bindBidirectional(subject.nameOfStationProperty());

        }
        else {
            passEmbarkedValue.setText("");
            passDisembarkedValue.setText("");
            ticketSalesValue.setText("");
        }

        statusLabel.textProperty().bindBidirectional(subject.trackHeaterProperty());
        //tempValueLabel.textProperty().bindBidirectional(trackModelSubject.tempProperty());

        //occupiedColumn.textProperty().bindBidirectional(trackProperties.isOccupiedProperty());

        if(subject.isIsSwitch()){
            switchBlockNumbersDisplay.textProperty().bindBidirectional(subject.switchBlockIDProperty());
            switchStateDisplay.textProperty().bindBidirectional(subject.switchStateProperty());
            switchStateDisplay.textProperty().bindBidirectional(subject.switchMainProperty());
            switchStateDisplay.textProperty().bindBidirectional(subject.switchAltProperty());
            switchColumn.textProperty().bindBidirectional(subject.switchStateProperty());

        }
        else {
            switchBlockNumbersDisplay.setText("NONE");
            switchStateDisplay.setText("NONE");
        }

        if(subject.isIsSignal()){
            signalStateDisplay.textProperty().bindBidirectional(subject.signalStateProperty());
            signalBlockNumberDisplay.textProperty().bindBidirectional(subject.signalIDProperty());
        }
        else {
            signalStateDisplay.setText("NONE");
            signalBlockNumberDisplay.setText("NONE");
        }

        if(subject.isIsCrossing()){
            crossingState.textProperty().bindBidirectional(subject.crossingStateProperty());
        }
        else {
            crossingState.setText("NONE");
        }

        if(subject.isIsBeacon()){
            displayBeaconInfo.textProperty().bindBidirectional(subject.setBeaconProperty());
            beaconBlockNumber.textProperty().bindBidirectional(subject.blockNumberProperty());
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
        //currTrackModel.setFailure(Integer.parseInt(blockSelect), failure);
        failureColumn.setCellValueFactory(block -> block.getValue().hasFailureProperty());
        subject.setHasFailure(!chooseFailureMode.getValue().equals("Fix Track Failure"));

    }

    private void addLineName(String text) {
        pickLine.getItems().add(text);
        murphyLine.getItems().add(text);
        testBench.setLine(text);
    }

    private void uploadTrack() {
        //parse the csv file
        File file = new File(trackFilePath.getText());
        //send file to parser
        this.addLineName(lineNameInput.getText());
    }

    //user input for track layout
    private void chooseFolder() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(chooseFile.getScene().getWindow());

        if(dir != null){ trackFilePath.setText(dir.getPath()); }
    }


    private TrackModelTBManager launchTestBench() {
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
