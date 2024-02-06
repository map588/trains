package Framework.GUI;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class mainMenu extends Application {

    String[] tabNames = {"CTC", "Track", "Track Controller", "Train", "Train Controller"};

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

            // Right-click menu for opening in a new window
            ContextMenu contextMenu = new ContextMenu();
            MenuItem openInNewWindow = new MenuItem("Open in new window");
            openInNewWindow.setOnAction(e -> openInNewWindow(tabNames[moduleId]));
            contextMenu.getItems().add(openInNewWindow);

            // Consolidate event handling for right and left clicks
            tabButton.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(tabButton, e.getScreenX(), e.getScreenY());
                } else if (e.getButton() == MouseButton.PRIMARY) {
                    openModuleTab(tabPane, tabNames[moduleId]);
                }
            });

            toolBar.getItems().add(tabButton);
        }

        VBox topContainer = new VBox(); // Use VBox to stack MenuBar and ToolBar
        MenuBar menuBar = new MenuBar(); // If you still want to use MenuBar for other purposes
        topContainer.getChildren().addAll(menuBar, toolBar);

        root.setTop(topContainer);

        Scene scene = new Scene(root, 1920, 1080);
        primaryStage.setScene(scene);
        primaryStage.setTitle("J.A.M.E.S - Train Management System");
        primaryStage.show();
    }

    private void openInNewWindow(String moduleName) {
        Stage newStage = new Stage();
        Node content = createModuleContent(moduleName); // Get the complex UI for the module
        Scene newScene = new Scene(new VBox(content), 1920, 1080); // Adjust size as needed
        newStage.setScene(newScene);
        newStage.setTitle(moduleName);
        newStage.show();
    }


    private void openModuleTab(TabPane tabPane, String moduleName) {
        Tab tab = new Tab(moduleName);
        Node content = createModuleContent(moduleName); // Get the complex UI for the module
        tab.setContent(content); // Set the complex UI as the content of the tab
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }

    private Node createModuleContent(String moduleName) {
        VBox content = new VBox();
        content.setSpacing(10); // Set spacing between children
        Label label = new Label("Content for " + moduleName);
        content.getChildren().add(label);
        return content; // Return the VBox as the content for the module
    }


    public static void main(String[] args) {
        launch(args);
    }
}
