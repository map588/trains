package Framework.GUI.Controllers;

import java.io.File;
import java.io.FilenameFilter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import waysideController.waysideControllerImpl;

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
    private ProgressBar uploadProgressBar;
    @FXML
    private Circle plcActiveIndicator;
    @FXML
    private Label plcCurrentFileLabel;
    @FXML
    private ComboBox changeControllerComboBox;
    @FXML
    private Label changeControllerLabel;

    private int controllerNum = -1;
    private String folderPath;
    private waysideControllerImpl waysideController = new waysideControllerImpl();

    @FXML
    public void initialize() {
        plcFolderButton.setOnAction(event -> pickFolder());
        plcUploadButton.setOnAction(event ->  uploadPLC());
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

    private void uploadPLC() {
        File selectedFile = (File) plcFileList.getSelectionModel().getSelectedItem();

        if(selectedFile != null) {
            waysideController.loadPLC(selectedFile);
            plcCurrentFileLabel.setText("Current PLC File: " + waysideController.getPLC().getName());
            plcActiveIndicator.setFill(Color.BLUE);
            System.out.println(selectedFile.getName());

            uploadProgressBar.setProgress(1.0);
        }
        else {
            System.out.println("No file selected!");
        }
    }
}
