package Framework.GUI.Managers;

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

public class CTCOfficeManager {

    @FXML private TableView<CTCBlockSubject> blockTable;
    @FXML private TableColumn<CTCBlockSubject, Integer> blockNumberColumn;
    @FXML private TableColumn<CTCBlockSubject, Boolean> occupationLightColumn;
    @FXML private TableColumn<CTCBlockSubject, Paint> switchLightColumn;
    @FXML private SplitPane mainSplit;
    @FXML private AnchorPane mainAnchor;
    @FXML private TableColumn<CTCBlockSubject, String> switchStateColumn;
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


    CTCBlockSubjectFactory factory = CTCBlockSubjectFactory.getInstance();
    ScheduleSubjectFactory scheduleFactory = ScheduleSubjectFactory.getInstance();

    @FXML
    public void initialize() {
        CTCOffice one = new CTCOfficeImpl();

        blockTable.setEditable(true);
        Collection<CTCBlockSubject> blockList = factory.getSubjects().values();
        Collection<ScheduleSubject> scheduleList = scheduleFactory.getSubjects().values();

        //first lane table view
        blockTable.getItems().addAll(blockList);
        blockNumberColumn.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getIntegerProperty("blockID").getValue()));
        blockNumberColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
        occupationLightColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty("occupied"));
        occupationLightColumn.setCellFactory(CheckBoxTableCell.forTableColumn(occupationLightColumn));
        switchStateColumn.setCellValueFactory(block -> {
            boolean hasSwitchCon = block.getValue().getBooleanProperty("hasSwitchCon").getValue();
            boolean hasSwitchDiv = block.getValue().getBooleanProperty("hasSwitchDiv").getValue();
            boolean switchState = block.getValue().getBooleanProperty("switchState").getValue();
            int divergingBlockOneID = block.getValue().getIntegerProperty("divergingBlockOneID").getValue();
            int divergingBlockTwoID = block.getValue().getIntegerProperty("divergingBlockTwoID").getValue();
            int convergingBlockID = block.getValue().getIntegerProperty("convergingBlockID").getValue();
            int thisBlockID = block.getValue().getIntegerProperty("blockID").getValue();

            if(hasSwitchCon && !switchState) {
                return new ReadOnlyObjectWrapper<>( "( " + divergingBlockOneID + " == "  + convergingBlockID + " )  " + divergingBlockTwoID);
            }else if(hasSwitchCon && switchState) {
                return new ReadOnlyObjectWrapper<>( divergingBlockOneID +"  ( "   + convergingBlockID + " == " + divergingBlockTwoID + " )");
            }else if(hasSwitchDiv && !switchState) {
                if(divergingBlockOneID == thisBlockID) {
                    return new ReadOnlyObjectWrapper<>( divergingBlockOneID + " ==== " + convergingBlockID);
                }else {
                    return new ReadOnlyObjectWrapper<>( divergingBlockTwoID + "\t\t" + convergingBlockID);
                }
            }else if(hasSwitchDiv && switchState) {
                if(divergingBlockTwoID == thisBlockID) {
                    return new ReadOnlyObjectWrapper<>( divergingBlockTwoID + " ==== " + convergingBlockID);
                }else {
                    return new ReadOnlyObjectWrapper<>( divergingBlockOneID + "\t\t" + convergingBlockID);
                }
            }else {
                return null;
            }
        });
        switchStateColumn.setStyle("-fx-alignment: CENTER;");

        switchLightColumn.setCellValueFactory(block -> {
            if (block.getValue().getBooleanProperty("hasLight").getValue()) {
               return block.getValue().getObjectProperty("lightColor");
            } else {
                return null;
            }
        });
        switchLightColumn.setCellFactory(column -> new TableCell<CTCBlockSubject, Paint>() {
            private BorderPane graphic;
            private Circle circle;
            private ObjectProperty<Paint> paintProperty;
            {
                graphic = new BorderPane();
                circle = new Circle(10);
                graphic.setCenter(circle);
            }
            @Override
            protected void updateItem(Paint item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    if(paintProperty != null){
                        paintProperty.removeListener(this::onPaintChanged);
                        paintProperty = null;
                    }
                }else {
                    System.out.println("Setting graphic to " + item);
                    circle.setFill(item);
                    setGraphic(graphic);
                    CTCBlockSubject block = getTableView().getItems().get(getIndex());
                    if (block != null) {
                        paintProperty = block.getObjectProperty("lightColor");
                        if(paintProperty != null){
                            paintProperty.removeListener(this::onPaintChanged);
                        }
                        System.out.println("Adding listener to " + block);
                        block.getObjectProperty("lightColor").addListener(this::onPaintChanged);
                    }
                }
            }

            private void onPaintChanged(javafx.beans.Observable observable, Paint oldValue, Paint newValue) {
                circle.setFill(newValue);
            }
            @Override
            public void updateIndex(int i) {
                super.updateIndex(i);
                if(paintProperty != null){
                    paintProperty.removeListener(this::onPaintChanged);
                    paintProperty = null;
                }
            }
        });




        //Table editing bar
        blockSelection.getItems().addAll(factory.getSubjects().keySet());
        lineSelection.getItems().addAll(true, false);
        switchLightToggle.setOnAction(event -> {
            CTCBlockSubject block = factory.getSubjects().get(blockSelection.getValue());
            block.setProperty("lightState", !block.getBooleanProperty("lightState").getValue());
        });
        switchStateToggle.setOnAction(event -> {
            CTCBlockSubject block = factory.getSubjects().get(blockSelection.getValue());
            block.setProperty("switchState", !block.getBooleanProperty("switchState").getValue());
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
