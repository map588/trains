package CTCOffice;

import Utilities.CSVTokenizer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static CTCOffice.CTCOfficeImpl.scheduleLibrary;
import static CTCOffice.Properties.BlockProperties.*;
import static CTCOffice.Properties.ScheduleProperties.*;
/**
 * This class manages the GUI for the Centralized Traffic Control (CTC) office.
 * It contains FXML annotated fields for the various GUI components and methods for handling user interactions.
 */
public class CTCOfficeManager {

    @FXML private TableView<CTCBlockSubject> blockTable;
    @FXML private TableColumn<CTCBlockSubject, Integer> blockNumberColumn;
    @FXML private TableColumn<CTCBlockSubject, Boolean> occupationLightColumn;
    @FXML private TableColumn<CTCBlockSubject, Paint> switchLightColumn;
    @FXML private SplitPane mainSplit;
    @FXML private AnchorPane mainAnchor;
    @FXML private TableColumn<CTCBlockSubject, String> switchStateColumn;
    @FXML private TableColumn<CTCBlockSubject, Paint> crossingStateColumn;
    @FXML private TableColumn<CTCBlockSubject, Paint> underMaintenanceColumn;
    @FXML private TableView<FullScheduleFileSubject> scheduleTable;
    @FXML private TableColumn<FullScheduleFileSubject, String> scheduleNameColumn;
    @FXML private TableColumn<FullScheduleFileSubject, String> scheduleDateModColumn;
    @FXML private ComboBox<String> scheduleSelector;
    @FXML private Button selectScheduleButton;
    @FXML private TableColumn<SingleTrainScheduleSubject, Integer> dispatchTimeColumn;
    @FXML private TableColumn<SingleTrainScheduleSubject, Integer> stationBlockIDColumn;
    @FXML private TableColumn<SingleTrainScheduleSubject, Integer> arrivalTimeColumn;
    @FXML private TableColumn<SingleTrainScheduleSubject, Integer> departureTimeColumn;
    @FXML private TableView<SingleTrainScheduleSubject> scheduleEditTable;
    @FXML private TableColumn<SingleTrainScheduleSubject, Integer> lineColumn;
    @FXML private TableColumn<SingleTrainScheduleSubject, Integer> carNumberColumn;
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
    @FXML private TableColumn<SingleTrainScheduleSubject, Integer> scheduledTrainColumn;
    @FXML private ChoiceBox<Integer> lineTrainSelector;
    @FXML private ChoiceBox<Integer> trainIDSelector;
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


    CTCBlockSubjectFactory factory = CTCBlockSubjectFactory.getInstance();
   // ScheduleSubjectFactory scheduleFactory = ScheduleSubjectFactory.getInstance();

    /**
     * Initializes the GUI components.
     * This method is called after all the FXML annotated fields have been injected.
     */
    @FXML
    public void initialize() {
        CTCOfficeImpl office = CTCOfficeImpl.OFFICE;
        blockTable.setEditable(true);
        Collection<CTCBlockSubject> blockList = factory.getSubjects().values();
        Collection<FullScheduleFile> scheduleList = scheduleLibrary.getSubjects().values();

        //TODO: Make a data structure that sucks less for tables
        //first lane table view
        blockTable.getItems().addAll(blockList);
        blockNumberColumn.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getIntegerProperty(BLOCK_ID_PROPERTY).getValue()));
        blockNumberColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
        blockNumberColumn.setEditable(false);

        occupationLightColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty(OCCUPIED_PROPERTY));
        occupationLightColumn.setCellFactory(CheckBoxTableCell.forTableColumn(occupationLightColumn));
        occupationLightColumn.setEditable(false);

        switchStateColumn.setCellValueFactory(block -> {
            block.getValue().updateStringProperty(SWITCH_STATE_STRING_PROPERTY);
            boolean isConvergingSwitch = block.getValue().getBooleanProperty(HAS_SWITCH_CON_PROPERTY).getValue();
            boolean isDivergingSwitch = block.getValue().getBooleanProperty(HAS_SWITCH_DIV_PROPERTY).getValue();
            StringProperty stateString = block.getValue().getStringProperty(SWITCH_STATE_STRING_PROPERTY);

            return isConvergingSwitch || isDivergingSwitch ? stateString : null;
        });
        switchStateColumn.setStyle("-fx-alignment: CENTER;");


        switchLightColumn.setCellValueFactory(block -> {
            block.getValue().updatePaintProperty(SWITCH_LIGHT_COLOR_PROPERTY);
            boolean hasLight = block.getValue().getBooleanProperty(HAS_LIGHT_PROPERTY).getValue();
            ObjectProperty<Paint> lightColor = block.getValue().getPaintProperty(SWITCH_LIGHT_COLOR_PROPERTY);

            return hasLight ? lightColor : null;
        });
        switchLightColumn.setCellFactory(column -> createColoredCircleCell());

        crossingStateColumn.setCellValueFactory(block -> {
            block.getValue().updatePaintProperty(CROSSING_LIGHT_COLOR_PROPERTY);
            boolean hasCrossing = block.getValue().getBooleanProperty(HAS_CROSSING_PROPERTY).getValue();
            ObjectProperty<Paint> lightColor = block.getValue().getPaintProperty(CROSSING_LIGHT_COLOR_PROPERTY);

            return hasCrossing ? lightColor : null;
        });
        crossingStateColumn.setCellFactory(column -> createColoredCircleCell());

        underMaintenanceColumn.setCellValueFactory(block -> {
            block.getValue().updatePaintProperty(MAINTENANCE_LIGHT_COLOR_PROPERTY);
            return block.getValue().getPaintProperty(MAINTENANCE_LIGHT_COLOR_PROPERTY);
        });
        underMaintenanceColumn.setCellFactory(column -> createColoredCircleCell());

        //Table editing bar
        blockSelection.getItems().addAll(factory.getSubjects().keySet());
        lineSelection.getItems().addAll(CSVTokenizer.lineNames);

        switchLightToggle.setOnAction(event -> toggleProperty(SWITCH_LIGHT_STATE_PROPERTY));
        switchStateToggle.setOnAction(event -> toggleProperty(SWITCH_STATE_PROPERTY));
        crossingStateToggle.setOnAction(event -> toggleProperty(CROSSING_STATE_PROPERTY));
        maintenanceToggle.setOnAction(event -> toggleProperty(UNDER_MAINTENANCE_PROPERTY));

        blockTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                blockSelection.setValue(newValue.getIntegerProperty(BLOCK_ID_PROPERTY).getValue());
                lineSelection.setValue(newValue.getStringProperty(LINE_PROPERTY).getValue());
            }
        });

        blockSelection.setValue(1);
        lineSelection.setValue(CSVTokenizer.lineNames.get(0));


        //schedules table
        scheduleTable.setEditable(true);
        scheduleTable.getItems().addAll(scheduleList);
        scheduleNameColumn.setCellValueFactory(schedule ->

        selectScheduleButton.setOnAction(event -> {
            SingleTrainScheduleSubject schedule =
            scheduleEditTable.getItems().clear();
            scheduleEditTable.getItems().add(schedule);
            lineStopSelector.getItems().clear();
            lineStopSelector.getItems().addAll(CSVTokenizer.lineNames);
            lineStopSelector.setValue(schedule.getStringProperty(TRAIN_LINE_PROPERTY).getValue());
        });







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
    }

    /**
     * Toggles the value of a property of a block.
     * The property to be toggled and the block are specified by the user.
     *
     * @param propertyName The name of the property to be toggled.
     */
    private void toggleProperty(String propertyName) {
        System.out.println("\n ");
        CTCBlockSubject block = factory.getSubjects().get(blockSelection.getValue());
        block.setProperty(propertyName, !block.getBooleanProperty(propertyName).getValue());
    }

    private TableCell<CTCBlockSubject, Paint> createColoredCircleCell() {
        return new TableCell<CTCBlockSubject, Paint>() {
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


//    @FXML
//    private void toggle_VBox() {
//        if (collapsingVBox.isVisible()) {
//            collapsingVBox.setVisible(false);
//            collapsingVBox.setManaged(false);
//        } else {
//            collapsingVBox.setVisible(true);
//            collapsingVBox.setManaged(true);
//        }
//    }


}
