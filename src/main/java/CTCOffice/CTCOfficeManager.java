package CTCOffice;

import CTCOffice.*;
import Common.CTCOffice;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.lang.reflect.Array;
import java.util.Collection;

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
    @FXML private TableColumn<CTCBlockSubject, Boolean> crossingStateColumn;
    @FXML private TableColumn<CTCBlockSubject, Boolean> maintenanceColumn;
    @FXML private TableView<ScheduleSubject> scheduleTable;
    @FXML private TableColumn<ScheduleSubject, Integer> dispatchTimeColumn;
    @FXML private TableColumn<ScheduleSubject, Integer> stationBlockIDColumn;
    @FXML private TableColumn<ScheduleSubject, Integer> arrivalTimeColumn;
    @FXML private TableColumn<ScheduleSubject, Integer> departureTimeColumn;
    @FXML private TableView<ScheduleSubject> scheduleEditTable;
    @FXML private TableColumn<ScheduleSubject, Integer> lineColumn;
    @FXML private TableColumn<ScheduleSubject, Integer> carNumberColumn;
    @FXML private ChoiceBox<Integer> lineStopSelector;
    @FXML private ChoiceBox<Integer> trainStopSelector;
    @FXML private Button AddStop;
    @FXML private Button RemoveStop;
    @FXML private ComboBox<Integer> stopSelector;
    @FXML private ComboBox<Integer> arrivalTimeSelector;
    @FXML private ComboBox<Integer> departureTimeSelector;
    @FXML private ChoiceBox<Integer> stationStopSelector;
    @FXML private Button saveScheduleButton;
    @FXML private Button saveStopButton;
    @FXML private TableColumn<ScheduleSubject, Integer> scheduledTrainColumn;
    @FXML private ChoiceBox<Integer> lineTrainSelector;
    @FXML private ChoiceBox<Integer> trainIDSelector;
    @FXML private Button AddTrain;
    @FXML private Button RemoveTrain;
    @FXML private ComboBox<Integer> dispatchTimeSelector;
    @FXML private Spinner<Integer> carsSelector;
    @FXML private Button saveTrainButton;
    @FXML private Button DispatchButton;
    @FXML private TableColumn<ScheduleSubject, String> scheduleNameColumn;
    @FXML private TableColumn<ScheduleSubject, String> scheduleDateModColumn;
    @FXML private ComboBox<Integer> blockSelection;
    @FXML private ComboBox<Boolean> lineSelection;
    @FXML private Button switchLightToggle;
    @FXML private Button switchStateToggle;
    @FXML private Button crossingStateToggle;
    @FXML private Button maintenanceToggle;


    CTCBlockSubjectFactory factory = CTCBlockSubjectFactory.getInstance();
    ScheduleSubjectFactory scheduleFactory = ScheduleSubjectFactory.getInstance();

    /**
     * Initializes the GUI components.
     * This method is called after all the FXML annotated fields have been injected.
     */
    @FXML
    public void initialize() {
        CTCOffice one = new CTCOfficeImpl();
        blockTable.setEditable(true);
        Collection<CTCBlockSubject> blockList = factory.getSubjects().values();

        // Get all the ScheduleSubject instances from the scheduleFactory and store them in a Collection
        // Collection<ScheduleSubject> scheduleList = scheduleFactory.getSubjects().values();

        //first lane table view
        blockTable.getItems().addAll(blockList);
        blockNumberColumn.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getIntegerProperty("blockID").getValue()));
        blockNumberColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
        blockNumberColumn.setEditable(false);

        occupationLightColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty("occupied"));
        occupationLightColumn.setCellFactory(CheckBoxTableCell.forTableColumn(occupationLightColumn));
        occupationLightColumn.setEditable(false);

        switchStateColumn.setCellValueFactory(block -> {
                block.getValue().setStringProperty("switchStateString");
                return block.getValue().getBooleanProperty("hasSwitchCon").getValue() ||
                block.getValue().getBooleanProperty("hasSwitchDiv").getValue() ?
                block.getValue().getStringProperty("switchStateString") : null;
        });
        switchStateColumn.setStyle("-fx-alignment: CENTER;");



        switchLightColumn.setCellValueFactory(block ->
                block.getValue().getBooleanProperty("hasLight").getValue() ?
                block.getValue().getObjectProperty("lightColor") : null);

        switchLightColumn.setCellFactory(column -> new TableCell<CTCBlockSubject, Paint>() {
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
        });

        //Table editing bar
        blockSelection.getItems().addAll(factory.getSubjects().keySet());
        lineSelection.getItems().addAll(true, false);
        switchLightToggle.setOnAction(event -> toggleProperty("lightState"));
        switchStateToggle.setOnAction(event -> toggleProperty("switchState"));

        blockTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                blockSelection.setValue(newValue.getIntegerProperty("blockID").getValue());
                lineSelection.setValue(newValue.getBooleanProperty("line").getValue());
            }
        });

        double dividerPosition = 515.0;
        mainAnchor.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(mainAnchor.getWidth() > 0){
                if(Array.getDouble(mainSplit.getDividerPositions(), 0) * mainAnchor.getWidth() > dividerPosition){
                    mainSplit.setDividerPosition(0, dividerPosition / mainAnchor.getWidth());
                }
            }
        });
        mainSplit.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
            if( dividerPosition / mainAnchor.getWidth() < newValue.doubleValue()){
                mainSplit.setDividerPosition(0, dividerPosition / mainAnchor.getWidth());
            }
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
