package org.glavo.png;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.glavo.png.image.ArgbImage;
import org.glavo.png.javafx.PNGJavaFXUtils;
import org.junit.jupiter.api.condition.DisabledIf;

import java.io.IOException;
import java.io.InputStream;


@DisabledIf("isDisabled")
public class JavaFXTest implements BasicTest {

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


    @Override
    public ArgbImage readImage(InputStream input) throws IOException {
        return PNGJavaFXUtils.asArgbImage(new Image(input));
    }
}
