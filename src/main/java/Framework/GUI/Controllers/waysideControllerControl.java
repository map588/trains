package Framework.GUI.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import waysideController.waysideControllerImpl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class waysideControllerControl {

    @FXML
    private ListView blockList;
    @FXML
    private ListView switchList;
    @FXML
    private CheckBox manualModeCheckbox;
    @FXML
    private TextField plcFolderTextField;
    @FXML
    private ListView plcFileList;
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
    private ComboBox changeControllerComboBox;
    @FXML
    private Label changeControllerLabel;

    private int controllerNum = 0;
    private waysideControllerImpl currentController = null;
    private List<waysideControllerImpl> controllerList = new ArrayList<waysideControllerImpl>();

    @FXML
    public void initialize() {
        createNewController();

        currentController.addBlock(1);
        currentController.addBlock(2);
        currentController.addBlock(3);

        updateBlockList();
        updateSwitchList();

        plcFolderButton.setOnAction(event -> pickFolder());
        plcUploadButton.setOnAction(event ->  uploadPLC());
        manualModeCheckbox.setOnAction(event -> currentController.setManualMode(!currentController.isManualMode()));
        createNewControllerButton.setOnAction(event -> createNewController());
        changeControllerComboBox.setOnAction(event -> changeActiveController(changeControllerComboBox.getValue().toString()));
        plcActiveIndicator.setFill(Color.GRAY);
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
            updatePLCListView(dir);
        }
    }

    /**
     * Updates the PLC File list with all valid files in the selected directory
     * @param dir File object with the directory of files
     */
    private void updatePLCListView(File dir) {
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".plc");
            }
        });

        if (files != null) {
            ObservableList<File> items = FXCollections.observableArrayList(files);
            plcFileList.setItems(items);
        }
    }

    /**
     * Uploads the selected PLC file to the wayside controller
     */
    private void uploadPLC() {
        File selectedFile = (File) plcFileList.getSelectionModel().getSelectedItem();

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
        ObservableList<Integer> blocks = FXCollections.observableList(currentController.getBlockList());

        if(blocks != null) {
            blockList.setItems(blocks);
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
