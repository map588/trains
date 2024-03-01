package waysideController;

import Common.WaysideController;
import Framework.Support.ListenerReference;
import com.fazecast.jSerialComm.SerialPort;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static waysideController.Properties.*;

public class WaysideControllerManager {


    @FXML
    private TableView<WaysideBlockSubject> blockTable;
    @FXML
    private TableColumn<WaysideBlockSubject,Integer> blockTableIDColumn;
    @FXML
    private TableColumn<WaysideBlockSubject,Boolean> blockTableCircuitColumn;
    @FXML
    private TableColumn<WaysideBlockSubject, Paint> blockTableLightsColumn;
    @FXML
    private TableColumn<WaysideBlockSubject,Boolean> blockTableCrossingColumn;
    @FXML
    private TableColumn<WaysideBlockSubject, Boolean> blockTableAuthColumn;
    @FXML
    private TableView<WaysideBlockSubject> switchTable;
    @FXML
    private TableColumn<WaysideBlockSubject, Integer> switchTableIDColumn;
    @FXML
    private TableColumn<WaysideBlockSubject, Boolean> switchTableStateColumn;
    @FXML
    private TableColumn<WaysideBlockSubject, Integer> switchTableBlockOutColumn;
    @FXML
    private CheckBox maintenanceModeCheckbox;
    @FXML
    private TextField plcFolderTextField;
    @FXML
    private TableView<File> plcFileTable;
    @FXML
    public TableColumn<File,String> plcFileNameColumn;
    @FXML
    public TableColumn<File,String> plcFileDateModifiedColumn;
    @FXML
    private Button plcFolderButton;
    @FXML
    private Button plcUploadButton;

    @FXML
    private ProgressBar uploadProgressBar;
    @FXML
    private Circle plcActiveIndicator;
    @FXML
    private Label plcCurrentFileLabel;
    @FXML
    private ComboBox<WaysideController> changeControllerComboBox;
    @FXML
    private Label changeControllerLabel;

    private WaysideControllerSubject currentSubject = null;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");
    private WaysideControllerTB testBench;
    private final List<ListenerReference<?>> listenerReferences = new ArrayList<>();

    @FXML
    public void initialize() {
        // Launch the test bench
        testBench = launchTestBench();

        // Set up event listeners
        plcFolderButton.setOnAction(event -> pickFolder());
        plcFolderTextField.setOnAction(event -> updatePLCTableView(new File(plcFolderTextField.getText())));
        plcUploadButton.setOnAction(event ->  uploadPLC());
        switchTable.setEditable(true);
        blockTable.setEditable(true);
        testBench.tbCreateNewControllerButton.setOnAction(event -> createNewController());
        changeControllerComboBox.setOnAction(event -> changeActiveController(changeControllerComboBox.getValue()));
        maintenanceModeCheckbox.setOnAction(event -> {
            currentSubject.setProperty(maintenanceMode_p, maintenanceModeCheckbox.isSelected());
            updateMaintenanceWriteable();
        });

        testBench.tbHWPortComboBox.getItems().add("SW");
        SerialPort[] ports = SerialPort.getCommPorts();
        for(SerialPort port : ports) {
            testBench.tbHWPortComboBox.getItems().add(port.getSystemPortName());
        }
        testBench.tbHWPortComboBox.setValue("SW");

        // Set up cell factories for table views
        blockTableIDColumn.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        blockTableCircuitColumn.setCellValueFactory(block -> block.getValue().occupationProperty());
        blockTableLightsColumn.setCellValueFactory(block -> block.getValue().lightStateProperty().lightColorProperty());
        blockTableLightsColumn.setCellFactory(column -> new TableCell<WaysideBlockSubject, Paint>() {
            private BorderPane graphic;
            private Circle circle;

            {
                graphic = new BorderPane();
                circle = new Circle(8);
                graphic.setCenter(circle);
                setOnMouseClicked(event -> {
                    if(currentSubject.getBooleanProperty(maintenanceMode_p).get()) {
                        if(this.getTableRow().getItem().hasLight()) {
                            this.getTableRow().getItem().setLightState(!this.getTableRow().getItem().getLightState());
                        }
                    }
                });
            }

            @Override
            public void updateItem(Paint paint, boolean empty) {
                circle.setFill(paint);
                setGraphic(graphic);
            }
        });
        blockTableCrossingColumn.setCellValueFactory(block -> block.getValue().crossingStateProperty());

        blockTableCrossingColumn.setCellFactory(column -> new TableCell<WaysideBlockSubject, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                    return;
                } else {
                    WaysideBlockSubject blockInfo = getTableView().getItems().get(getIndex());
                    if(blockInfo.hasCrossing()) {
                        CheckBox checkBox;
                        {
                            checkBox = new CheckBox();
                            checkBox.setDisable(!currentSubject.getBooleanProperty(maintenanceMode_p).get());
                            checkBox.setSelected(item);
                            checkBox.setOnMouseClicked(event -> {
                                if(currentSubject.getBooleanProperty(maintenanceMode_p).get()) {
                                    this.getTableRow().getItem().setCrossingState(!this.getTableRow().getItem().isCrossingState());
                                }
                            });
                        }
                        setGraphic(checkBox);
                    } else {
                        setGraphic(null);
                    }
                }

            }
        });
        blockTableAuthColumn.setCellValueFactory(block -> block.getValue().authorityProperty());
        blockTableAuthColumn.setCellFactory(CheckBoxTableCell.forTableColumn(blockTableAuthColumn));
        blockTableCrossingColumn.setEditable(false);
        blockTableCircuitColumn.setEditable(false);

        switchTable.setEditable(true);
        switchTableIDColumn.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        switchTableBlockOutColumn.setCellValueFactory(block -> block.getValue().switchedBlockIDProperty().asObject());
        switchTableStateColumn.setCellValueFactory(block -> block.getValue().switchStateProperty());
        switchTableStateColumn.setCellFactory(CheckBoxTableCell.forTableColumn(switchTableStateColumn));
        switchTableStateColumn.setEditable(false);

        plcFileNameColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(file.getValue().getName()));
        plcFileDateModifiedColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(dateFormat.format(new Date(file.getValue().lastModified()))));

        changeControllerComboBox.itemsProperty().bindBidirectional(WaysideControllerSubjectFactory.getControllerList());

        // Create initial controller and update values
        createNewController();
        testBench.setController(currentSubject.getController());

        currentSubject.getController().setMaintenanceMode(true);
//        currentSubject.getController().mai
    }

    private void updateMaintenanceWriteable() {
        maintenanceModeCheckbox.setSelected(currentSubject.getBooleanProperty(maintenanceMode_p).get());
        switchTableStateColumn.setEditable(maintenanceModeCheckbox.isSelected());
        blockTableAuthColumn.setEditable(maintenanceModeCheckbox.isSelected());
        blockTable.refresh();
    }

    /**
     * Loads a system dialog to select the folder for viewing PLC files in
     */
    private void pickFolder() {
        System.out.println("File picker!");
        DirectoryChooser dirChooser = new DirectoryChooser();
        File dir = dirChooser.showDialog(plcFolderButton.getScene().getWindow());

        if (dir != null) {
            plcFolderTextField.setText(dir.getPath());
            updatePLCTableView(dir);
        }
    }

    /**
     * Updates the PLC File list with all valid files in the selected directory
     * @param dir File object with the directory of files
     */
    private void updatePLCTableView(File dir) {
        File[] files = dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".plc"));

        if (files != null) {
            ObservableList<File> items = FXCollections.observableArrayList(files);
            plcFileTable.setItems(items);
        }
    }

    /**
     * Uploads the selected PLC file to the wayside controller
     */
    private void uploadPLC() {
        File selectedFile = plcFileTable.getSelectionModel().getSelectedItem();

        if(selectedFile != null) {
            currentSubject.getController().loadPLC(selectedFile);
            System.out.println(selectedFile.getName());
            uploadProgressBar.setProgress(1.0);
        }
        else {
            System.out.println("No file selected!");
        }
    }

    /**
     * Updates the block list in the GUI with the information from the current wayside controller
     */
    private void updateBlockList() {
        blockTable.setItems(currentSubject.blockListProperty());
        updateSwitchList();
    }

    /**
     * Updates the switch list in the GUI with the information from the current wayside controller
     */
    private void updateSwitchList() {
        switchTable.getItems().clear();
        ObservableList<WaysideBlockSubject> blocks = currentSubject.blockListProperty();
        for(WaysideBlockSubject item : blocks) {
            if(item.isHasSwitch()) {
                switchTable.getItems().add(item);
            }
        }
    }

    /**
     * Creates a new wayside controller and adds it to the list of controllers
     */
    private void createNewController() {
        WaysideController newController;
        if(testBench.tbHWPortComboBox.getValue().equals("SW")) {
            newController = new WaysideControllerImpl(WaysideControllerSubjectFactory.size(), 0);
        } else {
            newController = new WaysideControllerHWBridge(WaysideControllerSubjectFactory.size(), 0, testBench.tbHWPortComboBox.getValue());
        }
        WaysideControllerSubjectFactory.addController(newController);
        changeActiveController(newController);
    }

    /**
     * Changes the active controller to the one selected in the combo box
     */
    private void changeActiveController(WaysideController controller) {
        // Unbind previous subject
        if(currentSubject != null) {
            listenerReferences.forEach(ListenerReference::detach);
            listenerReferences.clear();
            plcCurrentFileLabel.textProperty().unbindBidirectional(currentSubject.getStringProperty(PLCName_p));
            plcActiveIndicator.fillProperty().unbindBidirectional(currentSubject.getPaintProperty(activePLCColor_p));
            currentSubject.getBooleanProperty(maintenanceMode_p).removeListener((observable, oldValue, newValue) -> updateMaintenanceWriteable());
        }

        // Update controller
        currentSubject = controller.getSubject();
        changeControllerComboBox.getSelectionModel().select(controller);
        changeControllerLabel.setText(controller.toString());
        testBench.tbWaysideNumberLabel.setText(controller.toString());

        // Bind new subject
        appendListener(currentSubject.getBooleanProperty(maintenanceMode_p), (observable, oldValue, newValue) -> updateMaintenanceWriteable());
        plcCurrentFileLabel.textProperty().bindBidirectional(currentSubject.getStringProperty(PLCName_p));
        plcActiveIndicator.fillProperty().bindBidirectional(currentSubject.getPaintProperty(activePLCColor_p));

        testBench.setController(controller);

        currentSubject.getController().runPLC();
        updateMaintenanceWriteable();
        updateBlockList();
    }

    private <T> void appendListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        listenerReferences.add(new ListenerReference<>(observable, listener));
    }

    private WaysideControllerTB launchTestBench() {
        System.out.println(System.getProperty("Preparing to launch test bench"));
        try {
            String tbFile = "/Framework/GUI/FXML/waysideController_TB.fxml";
            URL url = getClass().getResource(tbFile);
            FXMLLoader loader = new FXMLLoader(url);
            Node content = loader.load();
            Stage newStage = new Stage();
            Scene newScene = new Scene(new VBox(content));
            newStage.setScene(newScene);
            newStage.setTitle("Wayside Controller Test Bench");
            newStage.show();
            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to launch test bench");
            throw new RuntimeException(e);
        }
    }
}
