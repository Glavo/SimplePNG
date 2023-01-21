package org.glavo.png;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;

import java.nio.file.Path;

@DisabledIf("isDisabled")
public class JavaFXTest {

    private static boolean disabled = false;

    static {
        try {
            Platform.startup(() -> {
            });

            disabled = false;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static boolean isDisabled() {
        return disabled;
    }


}
