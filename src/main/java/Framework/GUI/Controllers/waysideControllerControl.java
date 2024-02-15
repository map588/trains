package Framework.GUI.Controllers;

import Utilities.BlockInfo;
import Utilities.staticBlockInfo;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import waysideController.waysideControllerImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class waysideControllerControl {

    @FXML
    private TableView<BlockInfo> blockTable;
    @FXML
    private TableColumn<BlockInfo,Integer> blockTableIDColumn;
    @FXML
    private TableColumn<BlockInfo,Boolean> blockTableCircuitColumn;
    @FXML
    private TableColumn<BlockInfo,Integer> blockTableLightsColumn;
    @FXML
    private TableColumn<BlockInfo,Boolean> blockTableCrossingColumn;
    @FXML
    private TableView switchTable;
    @FXML
    private TableColumn switchTableIDColumn;
    @FXML
    private TableColumn switchTableBlockInColumn;
    @FXML
    private TableColumn switchTableStateColumn;
    @FXML
    private TableColumn switchTableBlockOutColumn;
    @FXML
    private CheckBox manualModeCheckbox;
    @FXML
    private TextField plcFolderTextField;
    @FXML
    private TableView<File> plcFileTable;
    @FXML
    public TableColumn<File,String> plcFileNameColumn;
    @FXML
    public TableColumn<File,String> plcFileDateModifiedColumn;
    @FXML
    public TableColumn<File,String> plcFileDateCreatedColumn;
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
    private ComboBox<String> changeControllerComboBox;
    @FXML
    private Label changeControllerLabel;

    private int controllerNum = 0;
    private waysideControllerImpl currentController = null;
    private final List<waysideControllerImpl> controllerList = new ArrayList<>();

    @FXML
    public void initialize() {
        // Set up event listeners
        plcFolderButton.setOnAction(event -> pickFolder());
        plcUploadButton.setOnAction(event ->  uploadPLC());
        manualModeCheckbox.setOnAction(event -> currentController.setManualMode(!currentController.isManualMode()));
        createNewControllerButton.setOnAction(event -> createNewController());
        changeControllerComboBox.setOnAction(event -> changeActiveController(changeControllerComboBox.getValue()));

        // Set up cell factories for table views
        blockTableIDColumn.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getStaticInfo().blockNumber));
        blockTableCircuitColumn.setCellValueFactory(new PropertyValueFactory<>("trackCircuitState"));
        blockTableLightsColumn.setCellValueFactory(new PropertyValueFactory<>("lightOutState"));
        blockTableCrossingColumn.setCellValueFactory(new PropertyValueFactory<>("crossingClosed"));

        plcFileNameColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(file.getValue().getName()));
        plcFileDateModifiedColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(file.getValue().lastModified()+""));
        plcFileDateCreatedColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(file.getValue().getName()));

        // Create initial controller and update values
        createNewController();
        staticBlockInfo staticInfo = new staticBlockInfo();
        staticInfo.blockNumber = 10;
        BlockInfo newBlock = new BlockInfo(staticInfo);
        newBlock.setLightOutState(1);
        newBlock.setCrossingClosed(true);
        newBlock.setTrackCircuitState(true);
        currentController.addBlock(newBlock);
        updateBlockList();
        updateSwitchList();
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
//            files[0].la;
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
            currentController.loadPLC(selectedFile);
            updateWaysideInfo();
            System.out.println(selectedFile.getName());
            uploadProgressBar.setProgress(1.0);
        }
        else {
            System.out.println("No file selected!");
        }
    }

    /**
     * Updates the wayside controller information on the GUI
     */
    private void updateWaysideInfo() {
        if(currentController.getPLC() != null) {
            plcCurrentFileLabel.setText("Current PLC File: " + currentController.getPLC().getName());
            plcActiveIndicator.setFill(Color.BLUE);
        }
        else {
            plcCurrentFileLabel.setText("Current PLC File: ");
            plcActiveIndicator.setFill(Color.GRAY);
        }

        manualModeCheckbox.setSelected(currentController.isManualMode());
    }

    /**
     * Updates the block list in the GUI with the information from the current wayside controller
     */
    private void updateBlockList() {
        ObservableList<BlockInfo> blocks = FXCollections.observableList(currentController.getBlockList());

        if(!blocks.isEmpty()) {
            blockTable.setItems(blocks);
        }
    }

    /**
     * Updates the switch list in the GUI with the information from the current wayside controller
     */
    private void updateSwitchList() {

    }

    /**
     * Creates a new wayside controller and adds it to the list of controllers
     */
    private void createNewController() {
        waysideControllerImpl newController = new waysideControllerImpl(++controllerNum);
        controllerList.add(newController);
        changeControllerComboBox.getItems().add("Wayside Controller #" + controllerNum);
        changeControllerComboBox.setValue("Wayside Controller #" + controllerNum);
        changeActiveController("Wayside Controller #" + controllerNum);
    }

    /**
     * Changes the active controller to the one selected in the combo box
     */
    private void changeActiveController(String controllerName) {
        changeControllerLabel.setText(controllerName);
        int id = Integer.parseInt(controllerName.substring(controllerName.lastIndexOf("#") + 1));
        currentController = controllerList.get(id - 1);
        // TODO: Change the active controller
        updateWaysideInfo();
        updateBlockList();
        updateSwitchList();
    }
}
