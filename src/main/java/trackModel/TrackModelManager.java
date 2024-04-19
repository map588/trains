package trackModel;

import Utilities.BooleanIconTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class TrackModelManager {

    @FXML
    public Label ticketSalesLabel, locationLabel, passDisembarkLabel, passEmbarkedLabel;
    @FXML
    public Label tempLabel, uploadLayoutLabel, pathLabel, simSpeedLabel, switchBlockNumbersLabel;
    @FXML
    public Label switchStateLabel, signalBlockNumberLabel, signalStateLabel, crossingLabel;
    @FXML
    public Label beaconInfoLabel, lineInfoLabel, degF, sectionsLabel, logo;
    @FXML
    public TitledPane sssSec, trackHeaterSec, murphySec, simulationInformationSec, beaconInfoSec;
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
    private ComboBox<String> chooseFailureMode;
    @FXML
    private Button murphyEnter;

    //track heater and environmental temperature
    @FXML
    private Label trackHeaterStatus;
    @FXML
    private Label outsideTemp;

    //station signal switch
    @FXML
    private Label nameOfStationLabel, passEmbarkedValue, passDisembarkedValue;
    @FXML
    private Label ticketSalesValue, signalStateDisplay, signalBlockNumberDisplay;

    @FXML
    private ChoiceBox<String> pickLine;


    //beacon information
    @FXML
    private Label displayBeaconInfo, beaconBlockNumber;

    //switch information
    @FXML
    private Label switchStateDisplay, switchBlockNumbersDisplay, crossingState;

    //table
    @FXML
    private TableView<TrackBlockSubject> lineTable;
    @FXML
    private TableColumn<TrackBlockSubject, Boolean> directionColumn;
    @FXML
    private TableColumn<TrackBlockSubject, String> failureColumn;
    @FXML
    private TableColumn<TrackBlockSubject, String> blockColumn;
    @FXML
    private TableColumn<TrackBlockSubject, Integer> lengthColumn;
    @FXML
    private TableColumn<TrackBlockSubject, Boolean> occupiedColumn;
    @FXML
    private TableColumn<TrackBlockSubject, Double> gradeColumn, elevationColumn, speedLimitColumn;



    //Subject Map
    private LineSubjectMap subjectMap = LineSubjectMap.getInstance();
    //subject
    private TrackBlockSubject subject;


    ObservableList<TrackBlockSubject> selectedTrackBlockSubject = FXCollections.observableArrayList(subjectMap.getLineSubject("GREEN"));

    @FXML
    public void initialize() {
        //initialize buttons and user inputs
        chooseFile.setOnAction(event -> chooseFolder());
        trackUpload.setOnAction(event -> uploadTrack());
        murphyEnter.setOnAction(event -> murphyEnter());
        simSpeedInput.setOnAction(event -> setSimSpeed(simSpeedInput.getValue()));
        lineNameInput.setOnAction(event -> addLineName(lineNameInput.getText()));

        //initialize combo boxes
        updateLineChoiceBox();

        simSpeedInput.getItems().addAll("1x", "2x", "3x", "4x", "5x", "6x", "7x", "8x", "9x", "10x");
        chooseFailureMode.getItems().addAll(
                "Broken Rail",
                "Track Circuit Failure",
                "Power Failure",
                "Fix Track Failure"
        );

        trackHeaterStatus.setText("STATUS - OFF");

        initializeTable();

        //table
        lineTable.getSelectionModel().selectedItemProperty().addListener(event -> {
            selectBlock(lineTable.getSelectionModel().getSelectedItem());
        });

        //Listen for a change of the selected line
        pickLine.getSelectionModel().selectedItemProperty().addListener(event -> {
            updateTable();
        });

        //pickLine.getSelectionModel().selectNext();
    }

    private void updateLineChoiceBox() {
        List<String> lineNames = new ArrayList<>(subjectMap.getLineSubjects().keySet());
        pickLine.setItems(FXCollections.observableArrayList(lineNames));
    }

    private void initializeTable() {
        // Bind TableView columns to the properties of the TrackLineSubject object
        blockColumn.setCellValueFactory(cellData -> cellData.getValue().blockNumberProperty());
        lengthColumn.setCellValueFactory(cellData -> cellData.getValue().blockLengthProperty().asObject());
        gradeColumn.setCellValueFactory(cellData -> cellData.getValue().blockGradeProperty().asObject());
        elevationColumn.setCellValueFactory(cellData -> cellData.getValue().blockElevationProperty().asObject());
        speedLimitColumn.setCellValueFactory(cellData -> cellData.getValue().speedLimitProperty().asObject());
        failureColumn.setCellValueFactory(cellData -> cellData.getValue().failureProperty());
        occupiedColumn.setCellValueFactory(cellData -> cellData.getValue().isOccupiedProperty());
        directionColumn.setCellValueFactory(cellData -> cellData.getValue().directionProperty());




        // set up factories for occupied column and direction column
        occupiedColumn.setCellFactory(new Callback<TableColumn<TrackBlockSubject, Boolean>, TableCell<TrackBlockSubject, Boolean>>() {
            @Override
            public TableCell<TrackBlockSubject, Boolean> call(TableColumn<TrackBlockSubject, Boolean> TrackLineSubjectBooleanTableColumn) {
                return new BooleanIconTableCell<>("/Framework.GUI.Images/train_rail_24.png", "/Framework.GUI.Images/train_24.png", 24, 24);
            }
        });

        directionColumn.setCellFactory(new Callback<TableColumn<TrackBlockSubject, Boolean>, TableCell<TrackBlockSubject, Boolean>>() {
            @Override
            public TableCell<TrackBlockSubject, Boolean> call(TableColumn<TrackBlockSubject, Boolean> TrackLineSubjectBooleanTableColumn) {
                return new BooleanIconTableCell<>(null, "/Framework.GUI.Images/Crossing_Down_24.png", 24, 24);
            }
        });

        // Set the items of the table to this subject
        lineTable.setItems(selectedTrackBlockSubject);

        lineTable.refresh();
    }

    //change values based on selection in table
    public void selectBlock(TrackBlockSubject newProperties) {
        System.out.println("Selected block");
        if (subject != null) {
            // Unbind stuff here
            if (subject.isIsStation()) {
                passEmbarkedValue.textProperty().unbindBidirectional(subject.passEmbarkedProperty());
                passDisembarkedValue.textProperty().unbindBidirectional(subject.passDisembarkedProperty());
                ticketSalesValue.textProperty().unbindBidirectional(subject.ticketSalesProperty());
                nameOfStationLabel.textProperty().unbindBidirectional(subject.nameOfStationProperty());
            }

            if (subject.isIsSwitch()) {
                switchBlockNumbersDisplay.textProperty().unbindBidirectional(subject.switchBlockIDProperty());
                switchStateDisplay.textProperty().unbindBidirectional(subject.switchStateProperty());
            }

            if (subject.isIsSignal()) {
                signalStateDisplay.textProperty().unbindBidirectional(subject.signalStateProperty());
                signalBlockNumberDisplay.textProperty().unbindBidirectional(subject.signalIDProperty());
            }

            if (subject.isIsCrossing()) {
                crossingState.textProperty().unbindBidirectional(subject.crossingStateProperty());
            }

            if (subject.isIsBeacon()) {
                displayBeaconInfo.textProperty().unbindBidirectional(subject.setBeaconProperty());
                beaconBlockNumber.textProperty().unbindBidirectional(subject.blockNumberProperty());
            }
            elevationColumn.textProperty().unbindBidirectional(subject.blockElevationProperty().asObject());
            occupiedColumn.textProperty().unbindBidirectional(subject.isOccupiedProperty());
            gradeColumn.textProperty().unbindBidirectional(subject.blockGradeProperty().asObject());
            failureColumn.textProperty().unbindBidirectional(subject.failureProperty());
        }


        // Bind stuff here
        if (subject.isIsStation()) {
            passEmbarkedValue.textProperty().bindBidirectional(subject.passEmbarkedProperty());
            passDisembarkedValue.textProperty().bindBidirectional(subject.passDisembarkedProperty());
            ticketSalesValue.textProperty().bindBidirectional(subject.ticketSalesProperty());
            nameOfStationLabel.textProperty().bindBidirectional(subject.nameOfStationProperty());
        } else {
            passEmbarkedValue.setText("0");
            passDisembarkedValue.setText("0");
            ticketSalesValue.setText("0");
        }

        if (subject.isIsSwitch()) {
            switchBlockNumbersDisplay.textProperty().bindBidirectional(subject.switchBlockIDProperty());
            switchStateDisplay.textProperty().bindBidirectional(subject.switchStateProperty());
        } else {
            switchBlockNumbersDisplay.setText("NOT A SWITCH BLOCK");
            switchStateDisplay.setText("NONE");
        }

        if (subject.isIsSignal()) {
            signalStateDisplay.textProperty().bindBidirectional(subject.signalStateProperty());
            signalBlockNumberDisplay.textProperty().bindBidirectional(subject.signalIDProperty());
        } else {
            signalStateDisplay.setText("NONE");
            signalBlockNumberDisplay.setText("NO SIGNAL");
        }

        if (subject.isIsCrossing()) {
            crossingState.textProperty().bindBidirectional(subject.crossingStateProperty());
        } else {
            crossingState.setText("NONE");
        }

        if (subject.isIsBeacon()) {
            displayBeaconInfo.textProperty().bindBidirectional(subject.setBeaconProperty());
            beaconBlockNumber.textProperty().bindBidirectional(subject.blockNumberProperty());
        } else {
            displayBeaconInfo.setText("NO CROSSING");
        }

        //update track heaters and temperature as new blocks are selected
        if (subject.getOutsideTemp() < 40) {
            trackHeaterStatus.setText("Status - ON");
        }
        outsideTemp.setText(subject.outsideTempProperty().toString());

    }



//TODO update table based on line selection
    private void updateTable() {
            String lineSelect = pickLine.getSelectionModel().getSelectedItem().toString().toUpperCase();

            if(lineSelect == null) {
                return;
            }

            TrackBlockSubject subject = subjectMap.getLineSubject(lineSelect);

            // Clear the selectedTrackLineSubject list and add the new TrackLineSubject object
            selectedTrackBlockSubject.clear();
            selectedTrackBlockSubject.add(subject);

            // Update the table
            lineTable.refresh();
    }



    private void murphyEnter() {
        //get the line and block
        subject.setBrokenRail(false);
        subject.setTrackCircuitFailure(false);
        subject.setPowerFailure(false);

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
            case "Fix Track Failure" :
                subject.setBrokenRail(false);
                subject.setTrackCircuitFailure(false);
                subject.setPowerFailure(false);
            default:
                break;
        }

    }

    private void addLineName(String text) {
        pickLine.getItems().add(text);
    }

    private void uploadTrack() {
        //parse the csv file
        File file = new File(trackFilePath.getText());
        //TODO: uploading csv from track model
        //send file to parser
        this.addLineName(lineNameInput.getText());
    }

    private void setSimSpeed(String value) {
        //set the simulation speed
        //TODO: figure out changing sim speed
        switch(value){
            case "1x":
                break;
            case "2x":
                break;
            case "3x":
                break;
            case "4x":
                break;
            case "5x":
                break;
            case "6x":
                break;
            case "7x":
                break;
            case "8x":
                break;
            case "9x":
                break;
            case "10x":
                break;
            default:
                break;
        }
    }

    //user input for track layout
    private void chooseFolder() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(chooseFile.getScene().getWindow());
        if(dir != null){ trackFilePath.setText(dir.getPath()); }
    }
}
