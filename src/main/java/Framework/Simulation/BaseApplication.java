package Framework.Simulation;

import javafx.application.Platform;
public class BaseApplication {
        private static boolean jfxInitialized = false;

        public static void initializeJavaFX() {
            if (!jfxInitialized) {
                Platform.startup(() -> {});
                jfxInitialized = true;
            }
        }
    }
