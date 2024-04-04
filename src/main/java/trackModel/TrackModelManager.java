package trackModel;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.DirectoryChooser;

import java.io.File;


public class TrackModelManager {

    @FXML
    public Label ticketSalesLabel,locationLabel, passDisembarkLabel, passEmbarkedLabel;
    @FXML
    public Label tempLabel, uploadLayoutLabel, pathLabel,simSpeedLabel, switchBlockNumbersLabel;
    @FXML
    public Label switchStateLabel, signalBlockNumberLabel, signalStateLabel, crossingLabel;
    @FXML
    public Label beaconInfoLabel, lineInfoLabel,degF, sectionsLabel, logo;
    @FXML
    public TitledPane sssSec, getTrackHeaterSec, murphySec, simulationInformationSec, beaconInfoSec;
    @FXML
    public Tab layoutTab, switchTab, signalTab, stationTab;
    @FXML
    public TabPane sssTabs;
    @FXML
    private Button trackUpload, chooseFile;
    @FXML
    private TextField trackFilePath, lineNameInput;
    @FXML
    private ComboBox<String> simSpeedInput;

    //murphy
    @FXML
    private ComboBox<String> murphyLine;
    @FXML
    private TextField murphyBlockInput;
    @FXML
    private ComboBox<String> chooseFailureMode;
    @FXML
    private Button murphyEnter;

    //TODO: track heater

    @FXML
    private TextField tempDisplay;
    @FXML
    private Label tempValueLabel;
    @FXML
    private Label statusLabel;

    //station signal switch
    @FXML
    private Label nameOfStationLabel, passEmbarkedValue,passDisembarkedValue;
    @FXML
    private Label ticketSalesValue,signalStateDisplay, signalBlockNumberDisplay;

    //beacon information
    @FXML
    private ComboBox<String> pickLine;
    @FXML
    private Label displayBeaconInfo, beaconBlockNumber;

    //switch information
    @FXML
    private Label switchStateDisplay, switchBlockNumbersDisplay, crossingState;

    //table
    @FXML
    private TableView<TrackLineSubject> lineTable;
    @FXML
    private TableColumn<TrackLineSubject, String> directionColumn, failureColumn, blockColumn;
    @FXML
    private TableColumn<TrackLineSubject, Integer> lengthColumn;
    @FXML
    private TableColumn<TrackLineSubject, Boolean> occupiedColumn;
    @FXML
    private TableColumn<TrackLineSubject, Double> gradeColumn, elevationColumn, speedLimitColumn;


    //subject
    private TrackLineSubject subject;

    //TODO: get TrackBlocks into table in the UI

    @FXML
    public void initialize(){
        //initialize buttons and user inputs

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

        //set up cell factories
        blockColumn.setCellValueFactory(block -> block.getValue().blockNumberProperty());
        lengthColumn.setCellValueFactory(block -> block.getValue().blockLengthProperty().asObject());
        gradeColumn.setCellValueFactory(block -> block.getValue().blockGradeProperty().asObject());

        speedLimitColumn.setCellValueFactory(block -> block.getValue().speedLimitProperty().asObject());
        failureColumn.setCellValueFactory(block -> block.getValue().failureProperty());

        occupiedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(occupiedColumn));
        occupiedColumn.setCellValueFactory(block -> block.getValue().isOccupiedProperty());

        //labels
        lineTable.getSelectionModel().selectedItemProperty().addListener(event -> {
            selectBlock(lineTable.getSelectionModel().getSelectedItem());

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
        //tempValueLabel.textProperty().bindBidirectional(subject.tempProperty());

        if(subject.isIsSwitch()){
            switchBlockNumbersDisplay.textProperty().bindBidirectional(subject.switchBlockIDProperty());
            switchStateDisplay.textProperty().bindBidirectional(subject.switchStateProperty());
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
        subject.setBrokenRail(false);
        subject.setTrackCircuitFailure(false);
        subject.setPowerFailure(false);

        String lineSelect = murphyLine.getValue();
        String blockSelect = murphyBlockInput.getText();
        String failure = chooseFailureMode.getValue();

        //send the failure to the track model
        //currTrackModel.setFailure(Integer.parseInt(blockSelect), failure);
        failureColumn.setCellValueFactory(block -> block.getValue().failureProperty());

        switch(failure) {
            case "Broken Rail" :
                subject.setBrokenRail(true);
                break;
            case "Track Circuit Failure" :
                subject.setTrackCircuitFailure(true);
                break;
            case "Power Failure" :
                subject.setPowerFailure(true);
                break;
            default:
                break;
        }

    }

    private void addLineName(String text) {
        pickLine.getItems().add(text);
        murphyLine.getItems().add(text);
    }

    private void uploadTrack() {
        //parse the csv file
        File file = new File(trackFilePath.getText());
        //send file to parser
        this.addLineName(lineNameInput.getText());
        //update table
        this.updateTable();
    }

    //user input for track layout
    private void chooseFolder() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(chooseFile.getScene().getWindow());
        if(dir != null){ trackFilePath.setText(dir.getPath()); }
    }
}
