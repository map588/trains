package Integration;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    private static boolean jfxInitialized = false;

    @BeforeAll
    public static void initializeJavaFX() {
        if (!jfxInitialized) {
            Platform.startup(() -> {});
            jfxInitialized = true;
        }
    }
}