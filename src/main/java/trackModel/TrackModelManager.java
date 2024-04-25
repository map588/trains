package trackModel;

import Utilities.HelperObjects.BooleanIconTableCell;
import Utilities.Enums.Lines;
import Utilities.HelperObjects.TrackLineMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import java.io.File;
import java.util.List;


public class TrackModelManager {

    @FXML
    public Label ticketSalesLabel, locationLabel, passDisembarkLabel, passEmbarkedLabel;
    @FXML
    public Label tempLabel, switchBlockNumbersLabel;
    @FXML
    public Label switchStateLabel, signalBlockNumberLabel, signalStateLabel, crossingLabel;
    @FXML
    public Label beaconInfoLabel, lineInfoLabel, degF, sectionsLabel, logo;
    @FXML
    public TitledPane sssSec, trackHeaterSec, murphySec, beaconInfoSec;
    @FXML
    public Tab switchTab, signalTab, stationTab;
    @FXML
    public TabPane sssTabs;

    //add line

    @FXML
    public TextField lineNameInput, trackFilePath;
    @FXML
    public Button chooseFile, trackUpload;

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
    private TableColumn<TrackBlockSubject, Integer> blockColumn;
    @FXML
    private TableColumn<TrackBlockSubject, Double> lengthColumn;
    @FXML
    private TableColumn<TrackBlockSubject, Boolean> occupiedColumn;
    @FXML
    private TableColumn<TrackBlockSubject, Double> gradeColumn, elevationColumn, speedLimitColumn;


    //subject list for table
    Lines line = Lines.GREEN;
    ObservableList<TrackBlockSubject> selectedTrackSubjectList = LineSubjectMap.getLineSubject(line);


    TrackBlockSubject currentBlockSubject;

    @FXML
    public void initialize() {
        //initialize buttons and user inputs
        murphyEnter.setOnAction(event -> murphyEnter());
        trackUpload.setOnAction(event -> uploadTrack());
        chooseFile.setOnAction(event -> fileOpen());

        //initialize combo boxes
        updateLineChoiceBox();

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

        outsideTemp.setText("-");

    }

    private void uploadTrack(){
        String trackFile = trackFilePath.getText();
        pickLine.getItems().add(trackFile);
        updateLineChoiceBox();
    }

    private void fileOpen() {
        System.out.println("File picker!");
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(chooseFile.getScene().getWindow());

        if (dir != null) {
            trackFilePath.setText(dir.getPath());
        }
    }

    private void updateLineChoiceBox() {
        List<String> lineNames = LineSubjectMap.getLineNames();
        pickLine.setItems(FXCollections.observableArrayList(lineNames));
    }

    private void initializeTable() {
        // Bind TableView columns to the properties of the TrackLineSubject object
        blockColumn.setCellValueFactory(cellData -> cellData.getValue().blockNumberProperty().asObject());
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
                return new BooleanIconTableCell<>("/Framework.GUI.Images/down_arrow.png", "/Framework.GUI.Images/up_arrow.png", 24, 24);
            }
        });

        // Set the items of the table to this subject
        lineTable.setItems(selectedTrackSubjectList);
        lineTable.refresh();
    }

    //change values based on selection in table
    public void selectBlock(TrackBlockSubject newSubject) {
        System.out.println("Selected block: " + newSubject.getBlockNumber());
        System.out.println("Signal: " + newSubject.isIsSignal());

        if (currentBlockSubject != null) {
            // Unbind stuff here
            if (currentBlockSubject.isIsStation()) {
                passEmbarkedValue.textProperty().unbindBidirectional(currentBlockSubject.passEmbarkedProperty());
                passDisembarkedValue.textProperty().unbindBidirectional(currentBlockSubject.passDisembarkedProperty());
                ticketSalesValue.textProperty().unbindBidirectional(currentBlockSubject.ticketSalesProperty());
                nameOfStationLabel.textProperty().unbindBidirectional(currentBlockSubject.nameOfStationProperty());
            }

            if (currentBlockSubject.isIsSwitch()) {
                switchBlockNumbersDisplay.textProperty().unbindBidirectional(currentBlockSubject.switchBlockIDProperty());
                switchStateDisplay.textProperty().unbindBidirectional(currentBlockSubject.switchStateProperty());
            }

            if (currentBlockSubject.isIsSignal()) {
                signalStateDisplay.textProperty().unbindBidirectional(currentBlockSubject.signalStateProperty());
                signalBlockNumberDisplay.textProperty().unbindBidirectional(currentBlockSubject.signalIDProperty());
            }

            if (currentBlockSubject.isIsCrossing()) {
                crossingState.textProperty().unbindBidirectional(currentBlockSubject.crossingStateProperty());
            }

            if (currentBlockSubject.isIsBeacon()) {
                displayBeaconInfo.textProperty().unbindBidirectional(currentBlockSubject.setBeaconProperty());
                beaconBlockNumber.textProperty().unbind();
            }

            //directionColumn.textProperty().unbindBidirectional(currentBlockSubject.directionProperty());

            elevationColumn.textProperty().unbindBidirectional(currentBlockSubject.blockElevationProperty().asObject());
            occupiedColumn.textProperty().unbindBidirectional(currentBlockSubject.isOccupiedProperty());
            gradeColumn.textProperty().unbindBidirectional(currentBlockSubject.blockGradeProperty().asObject());
            failureColumn.textProperty().unbindBidirectional(currentBlockSubject.failureProperty());
            outsideTemp.textProperty().unbindBidirectional(currentBlockSubject.outsideTempProperty());
        }

        currentBlockSubject = newSubject;

        // Bind stuff here
        if (currentBlockSubject.isIsStation()) {
            passEmbarkedValue.textProperty().bindBidirectional(currentBlockSubject.passEmbarkedProperty());
            passDisembarkedValue.textProperty().bindBidirectional(currentBlockSubject.passDisembarkedProperty());
            ticketSalesValue.textProperty().bindBidirectional(currentBlockSubject.ticketSalesProperty());
            nameOfStationLabel.textProperty().bindBidirectional(currentBlockSubject.nameOfStationProperty());
        } else {
            passEmbarkedValue.setText("");
            passDisembarkedValue.setText("");
            ticketSalesValue.setText("");
            nameOfStationLabel.setText("");
        }

        if (currentBlockSubject.isIsSwitch()) {
            switchBlockNumbersDisplay.setText("SWITCH BLOCK SELECTED");
            switchStateDisplay.textProperty().bindBidirectional(currentBlockSubject.switchStateProperty());
        } else {
            switchBlockNumbersDisplay.setText("NOT A SWITCH BLOCK");
            switchStateDisplay.setText("NONE");
        }

        if (currentBlockSubject.isIsSignal()) {
            signalStateDisplay.textProperty().bindBidirectional(currentBlockSubject.signalStateProperty());
            signalBlockNumberDisplay.setText("SIGNAL BLOCK");
        } else {
            signalStateDisplay.setText("NONE");
            signalBlockNumberDisplay.setText("NO SIGNAL");
        }

        if (currentBlockSubject.isIsCrossing()) {
            crossingState.textProperty().bindBidirectional(currentBlockSubject.crossingStateProperty());
        } else {
            crossingState.setText("NONE");
        }

        if (currentBlockSubject.isIsBeacon()) {
            displayBeaconInfo.textProperty().bindBidirectional(currentBlockSubject.setBeaconProperty());
            beaconBlockNumber.textProperty().bind(currentBlockSubject.blockNumberProperty().asString());
        } else {
            displayBeaconInfo.setText("");
        }

        if(currentBlockSubject.isIsOccupied()) {
            //directionColumn.textProperty().bindBidirectional(currentBlockSubject.directionProperty());
        } else {
            directionColumn.setText("");
        }

        outsideTemp.textProperty().bindBidirectional(currentBlockSubject.outsideTempProperty());

    }

    private void updateTable() {
        Lines line = Lines.valueOf(pickLine.getSelectionModel().getSelectedItem());
        System.out.println("Selected line: " + line);

        // Clear the selectedTrackLineSubject list and add the new TrackLineSubject object
        selectedTrackSubjectList = LineSubjectMap.getLineSubject(line);
        System.out.println("Block list: " + selectedTrackSubjectList.size());
        lineTable.setItems(selectedTrackSubjectList);

        // Update the table
        lineTable.refresh();
    }

    private void murphyEnter() {

        currentBlockSubject.setBrokenRail(false);
        currentBlockSubject.setTrackCircuitFailure(false);
        currentBlockSubject.setPowerFailure(false);

        String failure = chooseFailureMode.getValue();

        //send the failure to the track model
        //currTrackModel.setFailure(Integer.parseInt(blockSelect), failure);
        failureColumn.setCellValueFactory(block -> block.getValue().failureProperty());

        switch(failure) {
            case "Broken Rail" :
//                currentBlockSubject.setBrokenRail(true);
                TrackLineMap.getTrackLine(line).setBrokenRail(currentBlockSubject.getBlockNumber(), true);
                break;
            case "Track Circuit Failure" :
//                currentBlockSubject.setTrackCircuitFailure(true);
                TrackLineMap.getTrackLine(line).setTrackCircuitFailure(currentBlockSubject.getBlockNumber(), true);
                break;
            case "Power Failure" :
//                currentBlockSubject.setPowerFailure(true);
                TrackLineMap.getTrackLine(line).setPowerFailure(currentBlockSubject.getBlockNumber(), true);
                break;
            case "Fix Track Failure" :
//                currentBlockSubject.setBrokenRail(false);
//                currentBlockSubject.setTrackCircuitFailure(false);
//                currentBlockSubject.setPowerFailure(false);
                TrackLineMap.getTrackLine(line).fixTrackFailure(currentBlockSubject.getBlockNumber());
            default:
                break;
        }

    }
}