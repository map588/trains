package waysideController;

import Common.WaysideController;
import Framework.Simulation.WaysideSystem;
import Framework.Support.ListenerReference;
import Utilities.Enums.Lines;
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
    private TableColumn<WaysideBlockSubject, Double> blockTableSpeedColumn;
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
    private TableColumn<File,String> plcFileNameColumn;
    @FXML
    private TableColumn<File,String> plcFileDateModifiedColumn;
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
        testBench.tbCreateNewControllerButton.setOnAction(event -> createNewController());
        changeControllerComboBox.setOnAction(event -> changeActiveController(changeControllerComboBox.getValue()));
        maintenanceModeCheckbox.setOnAction(event -> {
            currentSubject.setProperty(maintenanceMode_p, maintenanceModeCheckbox.isSelected());
            updateMaintenanceWriteable();
        });


        // Set up cell value factories for table views
        blockTableIDColumn.setCellValueFactory(block -> block.getValue().getIntegerProperty(blockID_p).asObject());
        blockTableCircuitColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty(occupied_p));
        blockTableLightsColumn.setCellValueFactory(block -> block.getValue().getTrafficLightColor());
        blockTableCrossingColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty(crossingState_p));
        blockTableAuthColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty(authority_p));
        blockTableSpeedColumn.setCellValueFactory(block -> block.getValue().getDoubleProperty(speed_p).asObject());

        switchTableIDColumn.setCellValueFactory(block -> block.getValue().getIntegerProperty(switchBlockParent_p).asObject());
        switchTableBlockOutColumn.setCellValueFactory(block -> block.getValue().getIntegerProperty(switchedBlockID_p).asObject());
        switchTableStateColumn.setCellValueFactory(block -> block.getValue().getBooleanProperty(switchState_p));

        plcFileNameColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(file.getValue().getName()));
        plcFileDateModifiedColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(dateFormat.format(new Date(file.getValue().lastModified()))));
        plcFileTable.setOnMousePressed(mouseEvent -> {
            if(mouseEvent.isPrimaryButtonDown() && mouseEvent.getClickCount() >= 2)
                uploadPLC();
        });


        // Set cell factories for editable columns
        setupTableCellFactories();


        // Set up editable columns
        blockTable.setEditable(true);
        blockTableCrossingColumn.setEditable(false);
        blockTableCircuitColumn.setEditable(false);

        switchTable.setEditable(true);
        switchTableStateColumn.setEditable(false);


        // Setup controller combo box
        changeControllerComboBox.itemsProperty().bindBidirectional(WaysideSystem.getControllerList());

        // Create initial controller and update values
//        createNewController();
        changeActiveController(WaysideSystem.getController(Lines.GREEN, 1));
        testBench.setController(currentSubject);

        // Set default folder for PLC:
        File dir = new File("src/main/antlr");
        plcFolderTextField.setText(dir.getPath());
        updatePLCTableView(dir);

        // Some testing of code triggered events:
//        currentSubject.getController().setMaintenanceMode(true);
//        currentSubject.getController().maintenanceSetAuthority(1, false);
//        currentSubject.getController().maintenanceSetSwitch(5, true);
//        currentSubject.getController().maintenanceSetTrafficLight(6, false);
//        currentSubject.getController().maintenanceSetCrossing(3, false);
    }

    /**
     * Sets up the cell factories for the table views
     */
    private void setupTableCellFactories() {
        blockTableLightsColumn.setCellFactory(column -> new TableCell<WaysideBlockSubject, Paint>() {
            private final BorderPane graphic;
            private final Circle circle;

            {
                graphic = new BorderPane();
                circle = new Circle(8);
                graphic.setCenter(circle);
                setOnMouseClicked(event -> {
                    if(currentSubject.getBooleanProperty(maintenanceMode_p).get()) {
                        if(this.getTableRow().getItem().getBlock().hasLight()) {
                            currentSubject.getController().maintenanceSetTrafficLight(this.getTableRow().getItem().getBlock().getBlockID(), !this.getTableRow().getItem().getBlock().getLightState());
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

        blockTableCrossingColumn.setCellFactory(column -> new TableCell<WaysideBlockSubject, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    WaysideBlockSubject blockInfo = getTableView().getItems().get(getIndex());
                    if(blockInfo.getBlock().hasCrossing()) {
                        CheckBox checkBox;
                        {
                            checkBox = new CheckBox();
                            checkBox.setDisable(!currentSubject.getBooleanProperty(maintenanceMode_p).get());
                            checkBox.setOpacity(1.0);
                            checkBox.setSelected(item);
                            checkBox.setOnMouseClicked(event -> {
                                currentSubject.getController().maintenanceSetCrossing(blockInfo.getBlock().getBlockID(), checkBox.isSelected());
                            });
                        }
                        setGraphic(checkBox);
                    } else {
                        setGraphic(null);
                    }
                }

            }
        });
        blockTableAuthColumn.setCellFactory(column -> new TableCell<WaysideBlockSubject, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    WaysideBlockSubject block = getTableView().getItems().get(getIndex());
                    CheckBox checkBox;
                    {
                        checkBox = new CheckBox();
                        checkBox.setSelected(item);
                        checkBox.setDisable(!currentSubject.getBooleanProperty(maintenanceMode_p).get());
                        checkBox.setOpacity(1.0);
                        checkBox.setOnAction(event -> {
                            currentSubject.getController().maintenanceSetAuthority(block.getBlock().getBlockID(), checkBox.isSelected());
                        });
                    }
                    setGraphic(checkBox);
                }
            }
        });
        switchTableStateColumn.setCellFactory(column -> new TableCell<WaysideBlockSubject, Boolean>() {
            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if(empty || item == null) {
                    setGraphic(null);
                } else {
                    WaysideBlockSubject block = getTableView().getItems().get(getIndex());
                    CheckBox checkBox;
                    {
                        checkBox = new CheckBox();
                        checkBox.setSelected(item);
                        checkBox.setDisable(!currentSubject.getBooleanProperty(maintenanceMode_p).get());
                        checkBox.setOpacity(1.0);
                        checkBox.setOnAction(event -> {
                            currentSubject.getController().maintenanceSetSwitch(block.getBlock().getBlockID(), checkBox.isSelected());
                        });
                    }
                    setGraphic(checkBox);
                }
            }
        });
    }

    /**
     * Updates the maintenance mode checkbox and the editable state of the table columns
     */
    private void updateMaintenanceWriteable() {
        maintenanceModeCheckbox.setSelected(currentSubject.getBooleanProperty(maintenanceMode_p).get());
        switchTableStateColumn.setEditable(maintenanceModeCheckbox.isSelected());
        blockTableAuthColumn.setEditable(maintenanceModeCheckbox.isSelected());
        blockTable.refresh();
        switchTable.refresh();
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
     * Updates the block list and switch list in the GUI with the information from the current wayside controller
     */
    private void updateBlockList() {
        ObservableList<WaysideBlockSubject> blocks = currentSubject.blockListProperty();

        blockTable.setItems(blocks);
        switchTable.getItems().clear();
        for(WaysideBlockSubject item : blocks) {
            if(item.getBlock().hasSwitch()) {
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
            newController = new WaysideControllerImpl(WaysideSystem.size(),
                    Lines.GREEN,
                    new int[]{
                            1, 2, 3,
                            4, 5, 6,
                            7, 8, 9, 10, 11, 12,
                            13, 14, 15, 16,
                            17, 18, 19, 20,
                            21, 22, 23, 24, 25, 26, 27, 28,
                            29, 30, 31, 32,
                            33, 34, 35,
                            36, 37, 38, 39,
                            144, 145, 146,
                            147, 148, 149,
                            150},
                    null, null);
        } else {
            newController = new WaysideControllerHWBridge(WaysideSystem.size(),
                    Lines.GREEN,
                    new int[]{
                            1, 2, 3,
                            4, 5, 6,
                            7, 8, 9, 10, 11, 12,
                            13, 14, 15, 16,
                            17, 18, 19, 20,
                            21, 22, 23, 24, 25, 26, 27, 28,
                            29, 30, 31, 32,
                            33, 34, 35,
                            36, 37, 38, 39,
                            144, 145, 146,
                            147, 148, 149,
                            150},
                    testBench.tbHWPortComboBox.getValue());
        }

        WaysideSystem.addController(newController, Lines.GREEN);
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

        testBench.setController(controller.getSubject());

        // Update block lists
        updateMaintenanceWriteable();
        updateBlockList();
    }

    private <T> void appendListener(ObservableValue<T> observable, ChangeListener<T> listener) {
        observable.addListener(listener);
        listenerReferences.add(new ListenerReference<>(observable, listener));
    }

    /**
     * Launches the wayside controller test bench
     * @return The controller for the test bench
     */
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
