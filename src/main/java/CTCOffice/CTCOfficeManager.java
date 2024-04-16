package CTCOffice;

import CTCOffice.ScheduleInfo.ScheduleFileSubject;
import CTCOffice.ScheduleInfo.ScheduleLibrary;
import CTCOffice.ScheduleInfo.TrainScheduleSubject;
import Framework.Support.BlockIDs;
import Utilities.Enums.Lines;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static CTCOffice.Properties.BlockProperties.*;
import static CTCOffice.Properties.ScheduleProperties.*;
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



    @FXML private TableView<CTCBlockSubject> blockTableRed;
    @FXML private TableColumn<CTCBlockSubject, Integer> blockNumberColumnRed;
    @FXML private TableColumn<CTCBlockSubject, Boolean> occupationLightColumnRed;
    @FXML private TableColumn<CTCBlockSubject, Paint> switchLightColumnRed;






    @FXML private TableColumn<CTCBlockSubject, String> switchStateColumnGreen;
    @FXML private TableColumn<CTCBlockSubject, Paint> crossingStateColumnGreen;
    @FXML private TableColumn<CTCBlockSubject, Paint> underMaintenanceColumnGreen;
    @FXML private TableColumn<CTCBlockSubject, String> switchStateColumnRed;
    @FXML private TableColumn<CTCBlockSubject, Paint> crossingStateColumnRed;
    @FXML private TableColumn<CTCBlockSubject, Paint> underMaintenanceColumnRed;
    @FXML private TableView<ScheduleFileSubject> scheduleTable;
    @FXML private TableColumn<ScheduleFileSubject, String> scheduleNameColumn;
    @FXML private TableColumn<ScheduleFileSubject, String> scheduleDateModColumn;
    @FXML private ComboBox<String> scheduleSelector;
    @FXML private Button selectScheduleButton;
    @FXML private TableView<TrainScheduleSubject> trainSelectTable;
    @FXML private ChoiceBox<Integer> lineTrainSelector;
    @FXML private ChoiceBox<Integer> trainIDSelector;
    @FXML private TableColumn<TrainScheduleSubject, String> lineColumn;
    @FXML private TableColumn<TrainScheduleSubject, Integer> dispatchTimeColumn;
    @FXML private TableColumn<TrainScheduleSubject, Integer> stationBlockIDColumn;
    @FXML private TableColumn<TrainScheduleSubject, Integer> arrivalTimeColumn;
    @FXML private TableColumn<TrainScheduleSubject, Integer> departureTimeColumn;
    @FXML private TableView<TrainScheduleSubject> scheduleEditTable;
    @FXML private TableColumn<TrainScheduleSubject, Integer> scheduledTrainColumn;


    @FXML private TableColumn<TrainScheduleSubject, Integer> carNumberColumn;
    @FXML private ChoiceBox<String> lineStopSelector;
    @FXML private ChoiceBox<Integer> trainStopSelector;
    @FXML private Button AddStop;
    @FXML private Button RemoveStop;
    @FXML private ComboBox<Integer> stopSelector;
    @FXML private ComboBox<Integer> arrivalTimeSelector;
    @FXML private ComboBox<Integer> departureTimeSelector;
    @FXML private ChoiceBox<Integer> stationStopSelector;
    @FXML private Button saveScheduleButton;
    @FXML private Button saveStopButton;


    @FXML private Button AddTrain;
    @FXML private Button RemoveTrain;
    @FXML private ComboBox<Integer> dispatchTimeSelector;
    @FXML private Spinner<Integer> carsSelector;
    @FXML private Button saveTrainButton;
    @FXML private Button DispatchButton;
    @FXML private ComboBox<Integer> blockSelection;
    @FXML private ComboBox<String> lineSelection;
    @FXML private Button switchLightToggle;
    @FXML private Button switchStateToggle;
    @FXML private Button crossingStateToggle;
    @FXML private Button maintenanceToggle;


    CTCBlockSubjectMap blockMap = CTCBlockSubjectMap.getInstance();
    ScheduleLibrary scheduleLibrary = ScheduleLibrary.getInstance();

    /**
     * Because these color properties are only relevant to the GUI, they are not stored in the CTCBlockSubject.
     * They reflect boolean properties of the CTCBlockSubject, and are updated by listeners.
     */
    Map<CTCBlockSubject, ObjectProperty<Paint>> switchColors      = new ConcurrentHashMap<>();
    Map<CTCBlockSubject, ObjectProperty<Paint>> crossingColors    = new ConcurrentHashMap<>();
    Map<CTCBlockSubject, ObjectProperty<Paint>> maintenanceColors = new ConcurrentHashMap<>();
    int numOfTrains = 0;
    /**
     * Initializes the GUI components.
     * This method is called after all the FXML annotated fields have been injected.
     */
    @FXML
    public void initialize() {
        CTCOfficeImpl office = CTCOfficeImpl.OFFICE;
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
            System.out.println("toggled traffic light for block " + blockSelection.getValue() + " on line " + lineSelection.getValue() + "\n");
            CTCBlockSubject block = blockMap.getSubject(BlockIDs.of(blockSelection.getValue(), Enum.valueOf(Lines.class, lineSelection.getValue())));
            block.getBlockInfo().setSwitchLightState(true, !block.getBlockInfo().getSwitchLightState());
        });
        switchStateToggle.setOnAction(event -> {
            System.out.println("toggled switch for block " + blockSelection.getValue() + " on line " + lineSelection.getValue() + "\n");
            CTCBlockSubject block = blockMap.getSubject(BlockIDs.of(blockSelection.getValue(), Enum.valueOf(Lines.class, lineSelection.getValue())));
            block.getBlockInfo().setSwitchState(true, !block.getBlockInfo().getSwitchState());
        });
        crossingStateToggle.setOnAction(event -> {
            System.out.println("toggled crossing for block " + blockSelection.getValue() + " on line " + lineSelection.getValue() + "\n");
            CTCBlockSubject block = blockMap.getSubject(BlockIDs.of(blockSelection.getValue(), Enum.valueOf(Lines.class, lineSelection.getValue())));
            block.getBlockInfo().setCrossingState(true, !block.getBlockInfo().getCrossingState());
        });
        maintenanceToggle.setOnAction(event -> {
            System.out.println("toggled maintenance for block " + blockSelection.getValue() + " on line " + lineSelection.getValue() + "\n");
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

        //schedules table
        scheduleTable.setEditable(true);
        scheduleTable.getItems().addAll(scheduleLibrary.getSubjects().values());
        scheduleNameColumn.setCellValueFactory(schedule -> schedule.getValue().getStringProperty(SCHEDULE_FILE_NAME_PROPERTY));

        scheduleSelector.getItems().addAll(scheduleLibrary.getSubjects().keySet());

        selectScheduleButton.setOnAction(event -> {
            ScheduleFileSubject selectedSchedule = scheduleLibrary.getSubject(scheduleSelector.getValue());
            scheduleEditTable.getItems().clear();
            for(int i = 0; i < selectedSchedule.getSchedule().getTrainSchedule(1).getStops().size(); i++) {
                scheduleEditTable.getItems().add(selectedSchedule.getSchedule().getTrainSchedule(1).getSubject());
            }
        });

        scheduledTrainColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(TRAIN_ID_PROPERTY).getValue()));
        lineColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getStringProperty(LINE_PROPERTY).getValue()));
        dispatchTimeColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(DISPATCH_TIME_PROPERTY).getValue()));
        stationBlockIDColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(DESTINATION_PROPERTY, 0).getValue()));
        arrivalTimeColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(ARRIVAL_TIME_PROPERTY, 0).getValue()));
        departureTimeColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(DEPARTURE_TIME_PROPERTY, 0).getValue()));
        carNumberColumn.setCellValueFactory(schedule -> new ReadOnlyObjectWrapper<>(schedule.getValue().getIntegerProperty(CAR_COUNT_PROPERTY).getValue()));

        // divider position listeners for the main split pane
        double minDividerPosition = 460.0;
        double maxDividerPosition = 300.0;
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
            office.DispatchTrain(Lines.GREEN, ++numOfTrains);
            System.out.println("Dispatched Train : ID " + numOfTrains + " on Line " + "GREEN");
            office.sendAuthority(Lines.GREEN, 0, 9);
            office.sendSpeed(Lines.GREEN, 0, 40);
        });
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

    /**
     * Toggles the value of a property of a block.
     * The property to be toggled and the block are specified by the user.
     *
     * @param propertyName The name of the property to be toggled.
     */
    private void toggleProperty(String propertyName) {
        System.out.println("toggled property " + propertyName + " for block " + blockSelection.getValue() + " on line " + lineSelection.getValue() + "\n");
        CTCBlockSubject block;
        block = blockMap.getSubject(BlockIDs.of(blockSelection.getValue(), Enum.valueOf(Lines.class, lineSelection.getValue())));
        block.setProperty(propertyName, !block.getBooleanProperty(propertyName).getValue());
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


    //TODO Implement a Create Train Code
    public void createTrain(){

    }

}
