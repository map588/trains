package Framework.GUI.Controllers;

import java.io.File;
import java.io.FilenameFilter;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
    }
}
