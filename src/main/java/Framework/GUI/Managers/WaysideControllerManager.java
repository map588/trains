package Framework.GUI.Managers;

import Common.WaysideController;
import Utilities.BlockInfo;
import Utilities.staticBlockInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import waysideController.WaysideControllerImpl;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WaysideControllerManager {

    @FXML
    private TableView<BlockInfo> blockTable;
    @FXML
    private TableColumn<BlockInfo,Integer> blockTableIDColumn;
    @FXML
    private TableColumn<BlockInfo,Boolean> blockTableCircuitColumn;
    @FXML
    private TableColumn<BlockInfo,Integer> blockTableLightInColumn;
    @FXML
    private TableColumn<BlockInfo,Integer> blockTableLightOutColumn;
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

    private WaysideController currentController = null;
    private static final ObjectProperty<ObservableList<WaysideController>> controllerList = new SimpleObjectProperty<>(FXCollections.observableArrayList(new ArrayList<>()));
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
        createNewControllerButton.setOnAction(event -> createNewController());
        changeControllerComboBox.setOnAction(event -> changeActiveController(changeControllerComboBox.getValue()));

        // Set up cell factories for table views
        blockTableIDColumn.setCellValueFactory(block -> new ReadOnlyObjectWrapper<>(block.getValue().getStaticInfo().blockNumber.getValue()));
        blockTableCircuitColumn.setCellValueFactory(block -> block.getValue().getTrackCircuitStateProperty());
        blockTableLightInColumn.setCellValueFactory(block -> block.getValue().lightInStateProperty().asObject());
        blockTableLightOutColumn.setCellValueFactory(block -> block.getValue().lightOutStateProperty().asObject());
        blockTableCrossingColumn.setCellValueFactory(block -> block.getValue().crossingClosedProperty());
//        blockTableLightInColumn.setCellValueFactory(new PropertyValueFactory<>("lightInState"));
//        blockTableLightOutColumn.setCellValueFactory(new PropertyValueFactory<>("lightOutState"));
//        blockTableCrossingColumn.setCellValueFactory(new PropertyValueFactory<>("crossingClosed"));

        plcFileNameColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(file.getValue().getName()));
        plcFileDateModifiedColumn.setCellValueFactory(file -> new ReadOnlyObjectWrapper<>(dateFormat.format(new Date(file.getValue().lastModified()))));

        changeControllerComboBox.itemsProperty().bindBidirectional(controllerList);
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
        staticBlockInfo staticInfo = new staticBlockInfo(new SimpleIntegerProperty(10));
        BlockInfo newBlock = new BlockInfo(staticInfo);
        newBlock.setLightInState(2);
        newBlock.setLightOutState(1);
        newBlock.setCrossingClosed(true);
        newBlock.setTrackCircuitState(true);

        staticBlockInfo staticInfo2 = new staticBlockInfo(new SimpleIntegerProperty(11));
        BlockInfo newBlock2 = new BlockInfo(staticInfo2);
        newBlock2.setLightInState(1);
        newBlock2.setLightOutState(0);
        newBlock2.setCrossingClosed(false);
        newBlock2.setTrackCircuitState(false);

        staticBlockInfo staticInfo3 = new staticBlockInfo(new SimpleIntegerProperty(12), newBlock, newBlock2);
        BlockInfo newBlock3 = new BlockInfo(staticInfo3);
        newBlock3.setLightInState(1);
        newBlock3.setLightOutState(0);
        newBlock3.setCrossingClosed(false);
        newBlock3.setTrackCircuitState(false);
        currentController.addBlock(newBlock);
        currentController.addBlock(newBlock2);
        currentController.addBlock(newBlock3);

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
        ObservableList<BlockInfo> blocks = FXCollections.observableList(currentController.getBlockList());
        blockTable.setItems(blocks);
        testBench.readBlockInfo(blocks);
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
        WaysideController newController = new WaysideControllerImpl(controllerList.get().size(), 0);
        controllerList.get().add(newController);
        changeActiveController(newController);
    }

    /**
     * Changes the active controller to the one selected in the combo box
     */
    private void changeActiveController(WaysideController controller) {
        // Unbind previous subject
        if(currentController != null) {
            manualModeCheckbox.selectedProperty().unbindBidirectional(currentController.getSubject().manualModeProperty());
            plcCurrentFileLabel.textProperty().unbindBidirectional(currentController.getSubject().PLCNameProperty());
            plcActiveIndicator.fillProperty().unbindBidirectional(currentController.getSubject().activePLCColorProperty());
        }

        // Update controller
        currentController = controller;
        changeControllerLabel.setText("Wayside Controller #" + (controller.getID()+1));
        testBench.tbWaysideNumberLabel.setText("Wayside Controller #" + (controller.getID()+1));

        // Bind new subject
        manualModeCheckbox.selectedProperty().bindBidirectional(currentController.getSubject().manualModeProperty());
        plcCurrentFileLabel.textProperty().bindBidirectional(currentController.getSubject().PLCNameProperty());
        plcActiveIndicator.fillProperty().bindBidirectional(currentController.getSubject().activePLCColorProperty());

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
