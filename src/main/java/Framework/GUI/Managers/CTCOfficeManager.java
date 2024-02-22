package Framework.GUI.Managers;

import CTCOffice.*;
import Common.CTCOffice;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.lang.reflect.Array;
import java.util.Collection;


public class CTCOfficeManager {


    @FXML
    private VBox collapsingVBox;

    @FXML
    private Button ModeButton;


    @FXML
    private TableView<CTCBlockSubject> blockTable;

    @FXML
    private TableColumn<CTCBlockSubject, Integer> blockNumberColumn;

    @FXML
    private TableColumn<CTCBlockSubject, Boolean> occupationLightColumn;

    @FXML
    private TableColumn<CTCBlockSubject, Paint> switchLightColumn;


    @FXML
    private SplitPane mainSplit;

    @FXML
    private AnchorPane mainAnchor;

    @FXML
    private TableColumn<CTCBlockSubject, Boolean> switchStateColumn;


    CTCBlockSubjectFactory factory = CTCBlockSubjectFactory.getInstance();
    ScheduleSubjectFactory scheduleFactory = ScheduleSubjectFactory.getInstance();

    @FXML
    public void initialize() {
        CTCOffice one = new CTCOfficeImpl();

        blockTable.setEditable(true);
        Collection<CTCBlockSubject> blockList = factory.getSubjects().values();
        Collection<ScheduleSubject> scheduleList = scheduleFactory.getSubjects().values();

        //left side of split pane
        blockTable.getItems().addAll(blockList);
        blockNumberColumn.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getIntegerProperty("blockID").getValue()));
        blockNumberColumn.setStyle("-fx-alignment: CENTER_RIGHT;");
        occupationLightColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty("occupied"));
        occupationLightColumn.setCellFactory(CheckBoxTableCell.forTableColumn(occupationLightColumn));
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
            {
                graphic = new BorderPane();
                circle = new Circle(10);
                circle.setOnMouseClicked(event -> {
                        System.out.println("Color: " + getItem());
                });
                graphic.setCenter(circle);
            }
            @Override
            protected void updateItem(Paint item, boolean empty) {
                circle.setFill(item);
                setGraphic(graphic);
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
//split plane at 0.685
    @FXML
    private void toggle_VBox() {
        if (collapsingVBox.isVisible()) {
            collapsingVBox.setVisible(false);
            collapsingVBox.setManaged(false);
        } else {
            collapsingVBox.setVisible(true);
            collapsingVBox.setManaged(true);
        }
    }


}
