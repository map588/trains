package Framework.GUI.Managers;

import Common.WaysideController;
import Utilities.BlockInfo;
import Utilities.TrainSpeedAuth;
import Utilities.staticBlockInfo;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import waysideController.WaysideBlockInfo;
import waysideController.WaysideControllerImpl;
import waysideController.WaysideControllerSubject;
import waysideController.WaysideControllerSubjectFactory;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WaysideControllerManager {

    @FXML
    private TableView<WaysideBlockInfo> blockTable;
    @FXML
    private TableColumn<WaysideBlockInfo,Integer> blockTableIDColumn;
    @FXML
    private TableColumn<WaysideBlockInfo,Boolean> blockTableCircuitColumn;
    @FXML
    private TableColumn<WaysideBlockInfo, Paint> blockTableLightsColumn;
    @FXML
    private TableColumn<WaysideBlockInfo,Boolean> blockTableCrossingColumn;
    @FXML
    private TableView<WaysideBlockInfo> switchTable;
    @FXML
    private TableColumn<WaysideBlockInfo, Integer> switchTableIDColumn;
    @FXML
    private TableColumn<WaysideBlockInfo, Boolean> switchTableStateColumn;
    @FXML
    private TableColumn<WaysideBlockInfo, Integer> switchTableBlockOutColumn;
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
    private Button createNewControllerButton;
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

    @FXML
    public void initialize() {
        // Launch the test bench
        testBench = launchTestBench();

        // Set up event listeners
        plcFolderButton.setOnAction(event -> pickFolder());
        plcFolderTextField.setOnAction(event -> updatePLCTableView(new File(plcFolderTextField.getText())));
        plcUploadButton.setOnAction(event ->  uploadPLC());
        switchTable.setEditable(true);
        createNewControllerButton.setOnAction(event -> createNewController());
        changeControllerComboBox.setOnAction(event -> changeActiveController(changeControllerComboBox.getValue()));
        maintenanceModeCheckbox.setOnAction(event -> switchTableStateColumn.setEditable(maintenanceModeCheckbox.isSelected()));

        // Set up cell factories for table views
        blockTableIDColumn.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        blockTableCircuitColumn.setCellValueFactory(block -> block.getValue().occupationProperty());
        blockTableLightsColumn.setCellValueFactory(block -> block.getValue().lightStateProperty().lightColorProperty());
        blockTableLightsColumn.setCellFactory(column -> new TableCell<WaysideBlockInfo, Paint>() {
            private BorderPane graphic;
            private Circle circle;

            {
                graphic = new BorderPane();
                circle = new Circle(8);
                graphic.setCenter(circle);
            }

            @Override
            public void updateItem(Paint paint, boolean empty) {
                circle.setFill(paint);
                setGraphic(graphic);
            }
        });
        blockTableCrossingColumn.setCellValueFactory(block -> block.getValue().crossingStateProperty());

        switchTableIDColumn.setCellValueFactory(block -> block.getValue().blockIDProperty().asObject());
        switchTableBlockOutColumn.setCellValueFactory(block -> block.getValue().switchedBlockIDProperty().asObject());
        switchTableStateColumn.setCellValueFactory(block -> block.getValue().switchStateProperty());
        switchTableStateColumn.setCellFactory(CheckBoxTableCell.forTableColumn(switchTableStateColumn));
        switchTableStateColumn.setEditable(false);

        plcFileNameColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(file.getValue().getName()));
        plcFileDateModifiedColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(dateFormat.format(new Date(file.getValue().lastModified()))));

        changeControllerComboBox.itemsProperty().bindBidirectional(WaysideControllerSubjectFactory.getControllerList());
        changeControllerComboBox.setCellFactory(listViews -> new ListCell<>() {
            @Override
            protected void updateItem(WaysideController item, boolean b) {
                super.updateItem(item, b);
                if (item != null) {
                    setText("Wayside Controller #" + (item.getID() + 1));
                }
            }
        });
        changeControllerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(WaysideController waysideController) {
                if (waysideController != null)
                    return "Wayside Controller #" + (waysideController.getID() + 1);
                else
                    return null;
            }

            @Override
            public WaysideController fromString(String s) {
                return null;
            }
        });

        // Create initial controller and update values
        createNewController();
        currentSubject.getController().addBlock((new WaysideBlockInfo(1, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(2, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(3, false, false, true)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(4, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(5, true, false, false, 6, 11)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(6, false, true, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(7, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(8, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(9, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(10, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(11, false, true, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(12, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(13, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(14, false, false, false)));
        currentSubject.getController().addBlock((new WaysideBlockInfo(15, false, false, false)));
        currentSubject.getController().runPLC();

        updateBlockList();

        currentSubject.getController().CTCSetSpeedAuth(new TrainSpeedAuth(1, 0, 0));

        // IDK why but there's some bug with loading in the switches and this fixes it
        changeActiveController(currentSubject.getController());
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
//        testBench.readBlockInfo(currentSubject.blockListProperty());
        updateSwitchList();
    }

    /**
     * Updates the switch list in the GUI with the information from the current wayside controller
     */
    private void updateSwitchList() {
        switchTable.getItems().clear();
        ObservableList<WaysideBlockInfo> blocks = FXCollections.observableList(currentSubject.getController().getBlockList());
        for(WaysideBlockInfo item : blocks) {
            if(item.isHasSwitch()) {
                switchTable.getItems().add(item);
            }
        }
    }

    /**
     * Creates a new wayside controller and adds it to the list of controllers
     */
    private void createNewController() {
        WaysideController newController = new WaysideControllerImpl(WaysideControllerSubjectFactory.size(), 0);
        WaysideControllerSubjectFactory.addController(newController);
        changeActiveController(newController);
    }

    /**
     * Changes the active controller to the one selected in the combo box
     */
    private void changeActiveController(WaysideController controller) {
        // Unbind previous subject
        if(currentSubject != null) {
            maintenanceModeCheckbox.selectedProperty().unbindBidirectional(currentSubject.maintenanceModeProperty());
            plcCurrentFileLabel.textProperty().unbindBidirectional(currentSubject.PLCNameProperty());
            plcActiveIndicator.fillProperty().unbindBidirectional(currentSubject.activePLCColorProperty());
        }

        // Update controller
        currentSubject = controller.getSubject();
        changeControllerLabel.setText("Wayside Controller #" + (controller.getID()+1));
        testBench.tbWaysideNumberLabel.setText("Wayside Controller #" + (controller.getID()+1));

        // Bind new subject
        maintenanceModeCheckbox.selectedProperty().bindBidirectional(currentSubject.maintenanceModeProperty());
        plcCurrentFileLabel.textProperty().bindBidirectional(currentSubject.PLCNameProperty());
        plcActiveIndicator.fillProperty().bindBidirectional(currentSubject.activePLCColorProperty());

        testBench.setController(controller);

        updateBlockList();
        updateSwitchList();
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
