package CTCOffice;

import Framework.Support.BlockIDs;
import Utilities.Enums.Lines;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static CTCOffice.Properties.BlockProperties.*;
import static CTCOffice.Properties.ScheduleProperties.*;
import static Utilities.TimeConvert.*;
import static CTCOffice.CTCOfficeImpl.*;

/**
 * This class manages the GUI for the Centralized Traffic Control (CTC) office.
 * It contains FXML annotated fields for the various GUI components and methods for handling user interactions.
 */
public class CTCOfficeManager {
    @FXML private SplitPane mainSplit;
    @FXML private AnchorPane mainAnchor;

    @FXML private TableView<CTCBlockSubject> blockTableGreen;
    @FXML private TableColumn<CTCBlockSubject, Integer> blockNumberColumnGreen;
    @FXML private TableColumn<CTCBlockSubject, Boolean> occupationLightColumnGreen;
    @FXML private TableColumn<CTCBlockSubject, Paint> switchLightColumnGreen;
    @FXML private TableColumn<CTCBlockSubject, String> switchStateColumnGreen;
    @FXML private TableColumn<CTCBlockSubject, Paint> crossingStateColumnGreen;
    @FXML private TableColumn<CTCBlockSubject, Paint> underMaintenanceColumnGreen;

    @FXML private TableView<CTCBlockSubject> blockTableRed;
    @FXML private TableColumn<CTCBlockSubject, Integer> blockNumberColumnRed;
    @FXML private TableColumn<CTCBlockSubject, Boolean> occupationLightColumnRed;
    @FXML private TableColumn<CTCBlockSubject, Paint> switchLightColumnRed;
    @FXML private TableColumn<CTCBlockSubject, String> switchStateColumnRed;
    @FXML private TableColumn<CTCBlockSubject, Paint> crossingStateColumnRed;
    @FXML private TableColumn<CTCBlockSubject, Paint> underMaintenanceColumnRed;

    @FXML private ComboBox<Integer> blockSelection;
    @FXML private ComboBox<String> lineSelection;
    @FXML private Button switchLightToggle;
    @FXML private Button switchStateToggle;
    @FXML private Button crossingStateToggle;
    @FXML private Button maintenanceToggle;

    @FXML private TableView<ScheduleFileSubject> scheduleTable;
    @FXML private TableColumn<ScheduleFileSubject, String> scheduleNameColumn;
    @FXML private TableColumn<ScheduleFileSubject, String> scheduleDateModColumn;
    @FXML private TableColumn<ScheduleFileSubject, Integer> trainNumColumn;

    @FXML private TableView<TrainScheduleSubject> trainSelectTable;
    @FXML private TableColumn<TrainScheduleSubject, Integer> scheduledTrainColumn;
    @FXML private TableColumn<TrainScheduleSubject, String> lineColumn;
    @FXML private TableColumn<TrainScheduleSubject, Integer> carNumberColumn;
    @FXML private TableColumn<TrainScheduleSubject, String> dispatchTimeColumn;

    @FXML private TableView<TrainStopSubject> scheduleEditTable;
    @FXML private TableColumn<TrainStopSubject, Integer> stopIndexColumn;
    @FXML private TableColumn<TrainStopSubject, Integer> stationBlockIDColumn;
    @FXML private TableColumn<TrainStopSubject, String> arrivalTimeColumn;
    @FXML private TableColumn<TrainStopSubject, String> departureTimeColumn;

    @FXML private ChoiceBox<String> lineTrainSelector;
    @FXML private ChoiceBox<Integer> trainIDSelector;
    @FXML private Spinner<Integer> carsSelector;
    @FXML private TextField dispatchTimeSelector;
    @FXML private Button AddTrain;
    @FXML private Button RemoveTrain;
    @FXML private Button saveTrainButton;

    @FXML private ComboBox<Integer> stopSelector;
    @FXML private TextField stationStopSelector;
    @FXML private TextField arrivalTimeSelector;
    @FXML private TextField departureTimeSelector;
    @FXML private Button AddStop;
    @FXML private Button RemoveStop;
    @FXML private Button saveStopButton;

    @FXML private Button AddSchedule;
    @FXML private Button RemoveSchedule;
    @FXML private Button saveScheduleButton; // save to a file
    @FXML private Button checkScheduleButton; // check for errors

    @FXML private Button DispatchButton;
   // @FXML private Button DumbDispatchButton;

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    CTCBlockSubjectMap blockMap = CTCBlockSubjectMap.getInstance();
    ScheduleLibrary scheduleLibrary = ScheduleLibrary.getInstance();
    ScheduleFile selectedSchedule = null;
    TrainSchedule selectedTrain = null;

    //TODO Remove this
    int trainID = 1;

    /**
     * Because these color properties are only relevant to the GUI, they are not stored in the CTCBlockSubject.
     * They reflect boolean properties of the CTCBlockSubject, and are updated by listeners.
     */
    Map<CTCBlockSubject, ObjectProperty<Paint>> switchColors      = new ConcurrentHashMap<>();
    Map<CTCBlockSubject, ObjectProperty<Paint>> crossingColors    = new ConcurrentHashMap<>();
    Map<CTCBlockSubject, ObjectProperty<Paint>> maintenanceColors = new ConcurrentHashMap<>();
    /**
     * Initializes the GUI components.
     * This method is called after all the FXML annotated fields have been injected.
     */
    CTCOfficeImpl office = CTCOfficeImpl.OFFICE;

    public void initialize() {
        setupLineTables();
        setupScheduleTables();
        scheduleTable.getSelectionModel().select(0);
        trainSelectTable.getSelectionModel().select(0);
        setupStopTable();
        scheduleEditTable.getSelectionModel().select(0);
        setupScheduleButtons();
        setupDividers();
        checkScheduleButton.fire();
    }

    private void LineTableSet(TableColumn<CTCBlockSubject, Integer> blockNumberColumn, TableView<CTCBlockSubject> blockTable, TableColumn<CTCBlockSubject, Boolean> occupationLightColumn,
                              TableColumn<CTCBlockSubject, String> switchStateColumn, TableColumn<CTCBlockSubject, Paint> switchLightColumn, TableColumn<CTCBlockSubject, Paint> crossingStateColumn,
                              TableColumn<CTCBlockSubject, Paint> underMaintenanceColumn) {
        blockNumberColumn.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getIntegerProperty(BLOCK_ID_PROPERTY).getValue()));
        blockNumberColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
        blockNumberColumn.setEditable(false);
        blockNumberColumn.setSortType(TableColumn.SortType.ASCENDING);
        blockTable.getSortOrder().add(blockNumberColumn);
        blockTable.sort();

        occupationLightColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty(OCCUPIED_PROPERTY));
        occupationLightColumn.setCellFactory(CheckBoxTableCell.forTableColumn(occupationLightColumn));
        occupationLightColumn.setEditable(false);

        switchStateColumn.setCellValueFactory(block -> {
            block.getValue().updateStringProperty(SWITCH_STATE_STRING_PROPERTY);
            return block.getValue().getStringProperty(SWITCH_STATE_STRING_PROPERTY);
        });
        switchStateColumn.setStyle("-fx-alignment: CENTER;");


        switchLightColumn.setCellFactory(column -> createColoredCircleCell());
        crossingStateColumn.setCellFactory(column -> createColoredCircleCell());
        underMaintenanceColumn.setCellFactory(column -> createColoredCircleCell());

        crossingStateColumn.setCellValueFactory(block -> crossingColors.get(block.getValue()));
        underMaintenanceColumn.setCellValueFactory(block -> maintenanceColors.get(block.getValue()));
        switchLightColumn.setCellValueFactory(block -> switchColors.get(block.getValue()));
    }


    private TableCell<CTCBlockSubject, Paint> createColoredCircleCell() {
        return new TableCell<>() {
            private final BorderPane graphic = new BorderPane();
            private final Circle circle = new Circle(10);
            {
                graphic.setCenter(circle);
            }
            @Override
            protected void updateItem(Paint item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    circle.setFill(item);
                    setGraphic(graphic);
                }
            }
        };
    }

    private void switchColorListener(CTCBlockSubject block){
        if(block.hasLight()) {switchColors.computeIfAbsent(block, k -> new SimpleObjectProperty<>()).setValue(Color.RED);}
        else {switchColors.computeIfAbsent(block, k -> new SimpleObjectProperty<>());}

        block.getBooleanProperty(SWITCH_LIGHT_STATE_PROPERTY).addListener((observable, oldValue, newValue) -> {
            boolean hasLight = block.hasLight();
            if(newValue && hasLight)
                switchColors.get(block).setValue(Color.GREEN);
            else if(hasLight) {
                switchColors.get(block).setValue(Color.RED);
            }
            else{
                switchColors.get(block).setValue(Color.TRANSPARENT);
            }
        });
    }

    private void crossingColorListener(CTCBlockSubject block){

        if(block.hasCrossing()) {crossingColors.computeIfAbsent(block, k -> new SimpleObjectProperty<>()).setValue(Color.DARKGRAY);}
        else {crossingColors.computeIfAbsent(block, k -> new SimpleObjectProperty<>());}

        block.getBooleanProperty(CROSSING_STATE_PROPERTY).addListener((observable, oldValue, newValue) -> {
            boolean hasCrossing = block.hasCrossing();
            if(newValue && hasCrossing)
                crossingColors.get(block).setValue(Color.RED);
            else if(hasCrossing) {
                crossingColors.get(block).setValue(Color.DARKGRAY);
            }
            else{
                crossingColors.get(block).setValue(Color.TRANSPARENT);
            }
        });
    }

    private void maintenanceColorListener(CTCBlockSubject block){
        maintenanceColors.computeIfAbsent(block, k -> new SimpleObjectProperty<>());
        block.getBooleanProperty(UNDER_MAINTENANCE_PROPERTY).addListener((observable, oldValue, newValue) -> {
            if(newValue)
                maintenanceColors.get(block).setValue(Color.RED);
            else {
                maintenanceColors.get(block).setValue(Color.TRANSPARENT);
            }
        });
    }

    private void setupLineTables(){
        blockTableGreen.setEditable(true);
        blockTableRed.setEditable(true);
        lineSelection.getItems().addAll("GREEN", "RED");

        for(CTCBlockSubject block : blockMap.getSubjects().values()) {
            if(block.getStringProperty(LINE_PROPERTY).getValue().equals("GREEN")) {
                blockTableGreen.getItems().add(block);
            }else{
                blockTableRed.getItems().add(block);
            }
            if(!blockSelection.getItems().contains(block.getIntegerProperty(BLOCK_ID_PROPERTY).getValue())) {
                blockSelection.getItems().add(block.getIntegerProperty(BLOCK_ID_PROPERTY).getValue());
            }
            switchColorListener(block);
            crossingColorListener(block);
            maintenanceColorListener(block);
        }

        LineTableSet(blockNumberColumnGreen, blockTableGreen, occupationLightColumnGreen, switchStateColumnGreen, switchLightColumnGreen, crossingStateColumnGreen, underMaintenanceColumnGreen);
        LineTableSet(blockNumberColumnRed, blockTableRed, occupationLightColumnRed, switchStateColumnRed, switchLightColumnRed, crossingStateColumnRed, underMaintenanceColumnRed);

        //Table editing bar
        switchLightToggle.setOnAction(event -> {
           // System.out.println("toggled traffic light for block " + blockSelection.getValue() + " on line " + lineSelection.getValue() + "\n");
            CTCBlockSubject block = blockMap.getSubject(BlockIDs.of(blockSelection.getValue(), Enum.valueOf(Lines.class, lineSelection.getValue())));
            block.getBlockInfo().setSwitchLightState(true, !block.getBlockInfo().getSwitchLightState());
        });
        switchStateToggle.setOnAction(event -> {
           // System.out.println("toggled switch for block " + blockSelection.getValue() + " on line " + lineSelection.getValue() + "\n");
            CTCBlockSubject block = blockMap.getSubject(BlockIDs.of(blockSelection.getValue(), Enum.valueOf(Lines.class, lineSelection.getValue())));
            block.getBlockInfo().setSwitchState(true, !block.getBlockInfo().getSwitchState());
        });
        crossingStateToggle.setOnAction(event -> {
            //System.out.println("toggled crossing for block " + blockSelection.getValue() + " on line " + lineSelection.getValue() + "\n");
            CTCBlockSubject block = blockMap.getSubject(BlockIDs.of(blockSelection.getValue(), Enum.valueOf(Lines.class, lineSelection.getValue())));
            block.getBlockInfo().setCrossingState(true, !block.getBlockInfo().getCrossingState());
        });
        maintenanceToggle.setOnAction(event -> {
           // System.out.println("toggled maintenance for block " + blockSelection.getValue() + " on line " + lineSelection.getValue() + "\n");
            CTCBlockSubject block = blockMap.getSubject(BlockIDs.of(blockSelection.getValue(), Enum.valueOf(Lines.class, lineSelection.getValue())));
            block.getBlockInfo().setUnderMaintenance(true, !block.getBlockInfo().getUnderMaintenance());
        });

        blockTableGreen.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                blockSelection.setValue(newValue.getIntegerProperty(BLOCK_ID_PROPERTY).getValue());
                lineSelection.setValue(newValue.getStringProperty(LINE_PROPERTY).getValue());
            }
        });
        blockTableRed.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                blockSelection.setValue(newValue.getIntegerProperty(BLOCK_ID_PROPERTY).getValue());
                lineSelection.setValue(newValue.getStringProperty(LINE_PROPERTY).getValue());
            }
        });

        blockSelection.setValue(1);
        lineSelection.setValue("GREEN");
    }

    private void setupScheduleTables(){
        //schedules table
        scheduleTable.setEditable(true);
        scheduleTable.getItems().addAll(scheduleLibrary.getSubjects().values());
        scheduleNameColumn.setCellValueFactory(schedule -> schedule.getValue().getStringProperty(SCHEDULE_FILE_NAME_PROPERTY));
        scheduleDateModColumn.setCellValueFactory(schedule -> schedule.getValue().getStringProperty(LAST_MODIFIED_PROPERTY));
        trainNumColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(NUM_TRAINS_PROPERTY).getValue()));

        scheduleTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedSchedule = newValue.getSchedule();
               // System.out.println("selected schedule " + newValue.getProperty(SCHEDULE_FILE_NAME_PROPERTY).getValue() + "\n");
                trainSelectTable.getItems().clear();
                for(int i = 1; i <= newValue.getSchedule().getNumTrains(); i++) {
                    trainSelectTable.getItems().add(newValue.getSchedule().getTrainSchedule(i).getSubject());
                    trainIDSelector.getItems().add(i);
                }
            }
        });

        trainSelectTable.setEditable(true);
        scheduledTrainColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(TRAIN_ID_PROPERTY).getValue()));
        lineColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getStringProperty(LINE_PROPERTY).getValue()));
        dispatchTimeColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getStringProperty(DISPATCH_TIME_PROPERTY).getValue()));
        carNumberColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(CAR_COUNT_PROPERTY).getValue()));

        lineTrainSelector.getItems().add(Lines.GREEN.toString()); lineTrainSelector.getItems().add(Lines.RED.toString());
        lineTrainSelector.setValue(Lines.GREEN.toString());
        carsSelector.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 2, 1));

        dispatchTimeSelector.setText(convertDoubleToClockTime(0.0));
        dispatchTimeSelector.setOnAction(event -> {
            if (!dispatchTimeSelector.getText().matches("\\d{0,2}:\\d{0,2}")) {
                dispatchTimeSelector.setText(convertDoubleToClockTime(
                        scheduleEditTable.getItems().get(trainSelectTable.getSelectionModel().selectedItemProperty().get().getSchedule()
                                .getStopCount() -1).getIntegerProperty(DEPARTURE_TIME_PROPERTY).getValue() + 5.0));
            }
        });
        AddTrain.setOnAction(event -> {
          //  System.out.println("added train to schedule " + scheduleTable.getSelectionModel().selectedItemProperty().getValue().getProperty(SCHEDULE_FILE_NAME_PROPERTY).getValue() + "\n");
            ScheduleFile schedule = scheduleTable.getSelectionModel().getSelectedItem().getSchedule();
            schedule.putTrainSchedule(schedule.getMultipleTrainSchedules().size() + 1, new TrainSchedule(schedule.getMultipleTrainSchedules().size() + 1,
                    lineTrainSelector.getValue(), (int)convertClockTimeToDouble(dispatchTimeSelector.getText()), carsSelector.getValue(), new ArrayList<>()));
            trainSelectTable.getItems().add(schedule.getMultipleTrainScheduleSubjects().get(schedule.getMultipleTrainSchedules().size()));
            trainIDSelector.getItems().add(schedule.getMultipleTrainSchedules().size());
        });
        RemoveTrain.setOnAction(event -> {
           // System.out.println("removed train from schedule " + scheduleTable.getSelectionModel().selectedItemProperty().getValue().getProperty(SCHEDULE_FILE_NAME_PROPERTY).getValue() + "\n");
            ScheduleFile schedule = scheduleTable.getSelectionModel().getSelectedItem().getSchedule();
            schedule.removeTrainSchedule(trainIDSelector.getValue());
            trainSelectTable.getItems().remove(trainSelectTable.getSelectionModel().getSelectedItem());
            trainIDSelector.getItems().remove(trainIDSelector.getValue());
        });
        saveTrainButton.setOnAction(event -> {
           // System.out.println("saved train to schedule " + selectedSchedule.getSubject().getProperty(SCHEDULE_FILE_NAME_PROPERTY).getValue() + "\n");
            TrainScheduleSubject train = selectedSchedule.getTrainSchedule(trainIDSelector.getValue()).getSubject();
            train.setProperty(DISPATCH_TIME_PROPERTY, dispatchTimeSelector.getText());
            train.setProperty(CAR_COUNT_PROPERTY, carsSelector.getValue());
            trainSelectTable.refresh();
        });

        trainSelectTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                scheduleEditTable.getItems().clear();
                selectedTrain = newValue.getSchedule();
                stopSelector.getItems().clear();
                trainIDSelector.setValue(newValue.getSchedule().getTrainID());
                lineTrainSelector.setValue(newValue.getSchedule().getLine());
                scheduleEditTable.getItems().addAll(newValue.getSchedule().stopList);
                stopSelector.getItems().addAll(newValue.getSchedule().stopIndices);
            }
        });

    }

    private void setupStopTable(){
        scheduleEditTable.setEditable(true);
        stopIndexColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(STOP_INDEX_PROPERTY).getValue()));
        stationBlockIDColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(DESTINATION_PROPERTY).getValue()));
        arrivalTimeColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getStringProperty(ARRIVAL_TIME_PROPERTY).getValue()));
        departureTimeColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getStringProperty(DEPARTURE_TIME_PROPERTY).getValue()));
        ArrayList<Integer> StopIndices = (selectedTrain.getLine().equals("GREEN") ? GreenTrackLayout : RedTrackLayout);
        stationStopSelector.setText(StopIndices.get(0).toString());
        scheduleEditTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                stopSelector.setValue(newValue.getStop().getStopIndex());
                stationStopSelector.setText("" + newValue.getStop().getStationBlockID());
                arrivalTimeSelector.setText(convertDoubleToClockTime(newValue.getStop().getArrivalTime()));
                departureTimeSelector.setText(convertDoubleToClockTime(newValue.getStop().getDepartureTime()));
            }
        });

        TableView<TrainStopSubject> tableView = scheduleEditTable;
        tableView.setRowFactory( obj -> {
            TableRow<TrainStopSubject> row = new TableRow<>();
            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Integer index = row.getIndex();
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    db.setDragView(row.snapshot(null, null));
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(SERIALIZED_MIME_TYPE, index);
                    db.setContent(cc);
                    event.consume();
                }
            });
            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) db.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });
            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedIndex = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                    TrainStopSubject draggedStop = tableView.getItems().remove(draggedIndex);
                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = tableView.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }
                    tableView.getItems().add(dropIndex, draggedStop);
                    event.setDropCompleted(true);
                    tableView.getSelectionModel().select(dropIndex);
                    event.consume();
                    TrainSchedule train = selectedSchedule.getTrainSchedule(trainIDSelector.getValue());
                    train.moveStop(draggedIndex + 1, dropIndex + 1);
                }
            });
            return row;
        });

        AddStop.setOnAction(event -> {
            if(selectedSchedule == null) {
                return;
            }
            TrainSchedule train = selectedSchedule.getTrainSchedule(trainIDSelector.getValue());
            if(stationStopSelector.getText().isEmpty()) {
                stationStopSelector.setText("0");
            }
            if(arrivalTimeSelector.getText().isEmpty()) {
                arrivalTimeSelector.setText(convertDoubleToClockTime((60)));
            }
            if(departureTimeSelector.getText().isEmpty()) {
                departureTimeSelector.setText(convertDoubleToClockTime(120));
            }
            if(train.getStopCount() == 0) {
                arrivalTimeSelector.setText(convertDoubleToClockTime(START_TIME + (300)));
                departureTimeSelector.setText(convertDoubleToClockTime(START_TIME + 60));
            }else{
                if(arrivalTimeSelector.getText().isEmpty() || (convertClockTimeToDouble(arrivalTimeSelector.getText()) <= train.getStop(train.getStopCount() - 1).getArrivalTime())) {
                    arrivalTimeSelector.setText(convertDoubleToClockTime(train.getStop(train.getStopCount() - 1).getDepartureTime() + 300));
                }
                if (departureTimeSelector.getText().isEmpty() || (convertClockTimeToDouble(departureTimeSelector.getText()) <= train.getStop(train.getStopCount() - 1).getDepartureTime())) {
                    departureTimeSelector.setText(convertDoubleToClockTime(convertClockTimeToDouble(arrivalTimeSelector.getText()) + 60));
                }
            }
            train.addStop(Integer.parseInt(stationStopSelector.getText()),  convertClockTimeToDouble(arrivalTimeSelector.getText()), convertClockTimeToDouble(departureTimeSelector.getText()));
            scheduleEditTable.getItems().add(train.getStop(train.getStopCount() - 1).getSubject());
            stopSelector.getItems().add(train.getStopCount() - 1);
        });
        RemoveStop.setOnAction(event -> {
            if(selectedSchedule == null) {
                return;
            }
            TrainSchedule train = selectedSchedule.getTrainSchedule(trainIDSelector.getValue());
            train.removeStop(stopSelector.getValue());
            scheduleEditTable.getItems().remove(scheduleEditTable.getItems().get(stopSelector.getValue() - 1));
            stopSelector.getItems().remove(train.getStops().size() - 1);
        });
        saveStopButton.setOnAction(event -> {
            if(selectedSchedule == null || selectedTrain == null) {
                return;
            }
            TrainSchedule train = selectedSchedule.getTrainSchedule(trainIDSelector.getValue());
            TrainStopSubject stop = train.getStop(stopSelector.getValue()).getSubject();
            stop.getStop().setStationBlockID(Integer.parseInt(stationStopSelector.getText()));
            stop.getStop().setArrivalTime((int)convertClockTimeToDouble(arrivalTimeSelector.getText()));
            stop.getStop().setDepartureTime((int)convertClockTimeToDouble(departureTimeSelector.getText()));
            scheduleEditTable.refresh();
        });
        /*
                @FXML private Button saveScheduleButton;
         */


    }

    private void setupScheduleButtons() {
        AddSchedule.setOnAction(event -> {
           // System.out.println("added schedule\n");
            new ScheduleFile("Schedule " + (scheduleLibrary.getSubjects().size() + 1), "4/25/2021");
            scheduleTable.getItems().add(scheduleLibrary.getSubjects().get(scheduleLibrary.getSubjects().size()));
        });

        RemoveSchedule.setOnAction(event -> {
           // System.out.println("removed schedule\n");
            scheduleLibrary.removeScheduleFile(scheduleTable.getSelectionModel().getSelectedItem().getStringProperty(SCHEDULE_FILE_NAME_PROPERTY).getValue());
            scheduleTable.getItems().remove(scheduleTable.getSelectionModel().getSelectedItem());
        });

        checkScheduleButton.setOnAction(event -> {
            for (TrainScheduleSubject train : trainSelectTable.getItems()) {
                train.getSchedule().fixSchedule();
            }
            scheduleEditTable.getItems().clear();
            stopSelector.getItems().clear();
            scheduleEditTable.getItems().addAll(selectedTrain.stopList);
            stopSelector.getItems().addAll(selectedTrain.stopIndices);
        });
    }

    private void setupDividers(){
        double minDividerPosition = 400.0;
        double maxDividerPosition = 500.0;
        double tableWidthAdjustment = 8.0;
        mainAnchor.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (mainAnchor.getWidth() > 0) {
                if (Array.getDouble(mainSplit.getDividerPositions(), 0) * mainAnchor.getWidth() < minDividerPosition) {
                    mainSplit.setDividerPosition(0, minDividerPosition / mainAnchor.getWidth());
                } else if ((1 - Array.getDouble(mainSplit.getDividerPositions(), 0)) * mainAnchor.getWidth() > maxDividerPosition) {
                    mainSplit.setDividerPosition(0, 1 - (maxDividerPosition / mainAnchor.getWidth()));
                }

                scheduleNameColumn.setMaxWidth(((newValue.doubleValue() * (1 - mainSplit.getDividerPositions()[0])) - tableWidthAdjustment) * 0.7);
                scheduleDateModColumn.setMaxWidth(((newValue.doubleValue() * (1 - mainSplit.getDividerPositions()[0])) - tableWidthAdjustment) * 0.3);
                scheduleNameColumn.setMinWidth(((newValue.doubleValue() * (1 - mainSplit.getDividerPositions()[0])) - tableWidthAdjustment) * 0.7);
                scheduleDateModColumn.setMinWidth(((newValue.doubleValue() * (1 - mainSplit.getDividerPositions()[0])) - tableWidthAdjustment) * 0.3);
            }
        });
        mainSplit.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
            if (minDividerPosition / mainAnchor.getWidth() > newValue.doubleValue()) {
                mainSplit.setDividerPosition(0, minDividerPosition / mainAnchor.getWidth());
            } else if (1 - (maxDividerPosition / mainAnchor.getWidth()) < newValue.doubleValue()) {
                mainSplit.setDividerPosition(0, 1 - (maxDividerPosition / mainAnchor.getWidth()));
            }
            scheduleNameColumn.setMaxWidth(((mainAnchor.getWidth() * (1 - newValue.doubleValue())) - tableWidthAdjustment) * 0.7);
            scheduleDateModColumn.setMaxWidth(((mainAnchor.getWidth() * (1 - newValue.doubleValue())) - tableWidthAdjustment) * 0.3);
            scheduleNameColumn.setMinWidth(((mainAnchor.getWidth() * (1 - newValue.doubleValue())) - tableWidthAdjustment) * 0.7);
            scheduleDateModColumn.setMinWidth(((mainAnchor.getWidth() * (1 - newValue.doubleValue())) - tableWidthAdjustment) * 0.3);
        });

        DispatchButton.setOnAction(event -> {
            checkScheduleButton.fire();
            office.runSchedule(selectedSchedule.getScheduleFileName());
        });


//        DumbDispatchButton.setOnAction(event -> {
//            Lines line = lineTrainSelector.getValue().equals("GREEN") ? Lines.GREEN : Lines.RED;
//            office.dispatchDumbTrain(trainID++, line);
//            office.sendDumbAuthority(trainID, line, line == Lines.GREEN ? 63 : 10, 50000);
//            office.sendDumbSpeed(trainID, line, line == Lines.GREEN ? 63 : 10, 19.6);
//        });
    }
}
