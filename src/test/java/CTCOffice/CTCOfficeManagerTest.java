package CTCOffice;

import CTCOffice.CTCOfficeManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;

public class CTCOfficeManagerTest extends ApplicationTest {

    CTCOfficeManager controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/path/to/CTCOfficeManager.fxml"));
        Scene scene = new Scene(loader.load());
        controller = loader.getController();

        stage.setScene(scene);
        stage.show();
    }

    //@Test
    public void testToggleProperty_lightState() {
        // Assuming there's a way to select a block before toggling its light state
        // For this test, let's say we simulate selecting the first row in the blockTable
        // blockTable.getSelectionModel().selectFirst();

        // Simulate clicking the "switchLightToggle" button to toggle the light state of the selected block
        clickOn("#switchLightToggle");

        // Verify the property has been toggled - this is highly dependent on your implementation details
        // For example, if toggling changes the color of a cell in the "switchLightColumn", you'd verify that
        //CTCBlockSubject selectedBlock = blockTable.getSelectionModel().getSelectedItem();
        //assertNotNull(selectedBlock);
        // Assuming there's a method or a way to check if the light state has been toggled
        // assertTrue(selectedBlock.isLightStateToggled());

        // Alternatively, if the toggle effects are visually represented, use TestFX to verify UI component states
        // Example: verifyThat("#someComponentId", hasProperty("someProperty", expectedValue));
    }

    // Additional tests for other functionalities like adding/removing stops, trains, etc.
}
