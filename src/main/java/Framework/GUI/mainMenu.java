package Framework.GUI;

import Framework.Simulation.Main;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;

public class mainMenu extends Application {

    String[] tabNames = {"CTC_Main_UI", "trackModel", "waysideController", "trainModel", "trainController"};

    public static Label timeLabel = new Label("Time: 6:00:00");
    public static Label timeScaleLabel = new Label("1.0x Speed");

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        TabPane tabPane = new TabPane();
        root.setCenter(tabPane);

        // ToolBar for module buttons, as a replacement for direct MenuBar usage for tabs
        ToolBar toolBar = new ToolBar();

        // Create buttons for each module
        for (int i = 0; i < tabNames.length; i++) {
            Button tabButton = new Button(tabNames[i]);
            final int moduleId = i;


            // Consolidate event handling for right and left clicks
            tabButton.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    openModuleTab(tabPane, tabNames[moduleId]);
                } else if (e.getButton() == MouseButton.PRIMARY) {
                    openInNewWindow(tabNames[moduleId]);
                }
            });

            toolBar.getItems().add(tabButton);
        }

        Slider speedSlider = new Slider(0.0, 10.0, 1);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(2.0);
        speedSlider.setOnMouseReleased(e -> Main.modifyTimeMultiplier(speedSlider.getValue()));
        toolBar.getItems().add( speedSlider);

        toolBar.getItems().add(timeLabel);
        toolBar.getItems().add(timeScaleLabel);

        VBox topContainer = new VBox(); // Use VBox to stack MenuBar and ToolBar
        MenuBar menuBar = new MenuBar(); // If you still want to use MenuBar for other purposes
        topContainer.getChildren().addAll(menuBar, toolBar);

        root.setTop(topContainer);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() * 0.65);
        int height = (int) (screenSize.getHeight() * 0.65);


        Scene scene = new Scene(root,width,height);
        primaryStage.setScene(scene);
        primaryStage.setTitle("J.A.M.E.S - Train Management System");
        primaryStage.show();
        mainMenu.timeScaleLabel.setText("Time Scale: " + Main.timeMultiplier + "x");
    }

    @Override
    public void stop() {
        Main.stopSimulation();
        System.exit(0);
    }

    private void openModuleTab(TabPane tabPane, String moduleName) {
        Tab tab = new Tab(moduleName);
        Node content = createModuleContent(moduleName); // Get the complex UI for the module
        tab.setContent(content); // Set the complex UI as the content of the tab
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private void openInNewWindow(String moduleName) {
        Stage newStage = new Stage();
        Node content = createModuleContent(moduleName); // This now loads from FXML
        Scene newScene;
        if(moduleName.equals("CTC_Main_UI")) {
            newScene = new Scene(new VBox(content) , 1200, 700);
            newStage.setScene(newScene);
            newStage.setMinWidth(780);
            newStage.setMinHeight(700);
        }else {
            newScene = new Scene((Parent) content); // Ensure the layout fits the loaded content
            newStage.setScene(newScene);
        }
        newStage.setTitle(moduleName);
        newStage.show();
    }


    private Node createModuleContent(String moduleName) {
        try {
            System.out.println(System.getProperty("java.class.path"));
            // Assuming the FXML files are in the same package as this class
            String fxmlFile = "/Framework/GUI/FXML/" + moduleName + ".fxml"; // Construct the path to the FXML
            URL url = getClass().getResource(fxmlFile);
            System.out.println(url);
            FXMLLoader loader = new FXMLLoader(url);
            return loader.load(); // Load the FXML and return the root node
        } catch (Exception e) {
            e.printStackTrace();
            return new Label("Failed to load: " + moduleName);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
