/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.png;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import org.glavo.png.image.ArgbImage;
import org.glavo.png.javafx.PNGJavaFXUtils;
import org.junit.jupiter.api.condition.DisabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisabledIf("isDisabled")
public class JavaFXTest implements BasicTest {

    private static boolean disabled = true;

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

    @ParameterizedTest
    @MethodSource("testFiles")
    public void writeImageToArrayTest(Argument arg) throws IOException {
        Image sourceImage;
        try (InputStream input = Files.newInputStream(arg.file())) {
            sourceImage = new Image(input);
        }

        byte[] data = PNGJavaFXUtils.writeImageToArray(sourceImage, arg.pngType(), arg.compressLevel());
        Image targetImage = new Image(new ByteArrayInputStream(data));


        assertEquals(sourceImage.getWidth(), targetImage.getWidth());
        assertEquals(sourceImage.getHeight(), targetImage.getHeight());

        int width = (int) sourceImage.getWidth();
        int height = (int) sourceImage.getHeight();

        PixelReader sourceReader = sourceImage.getPixelReader();
        PixelReader targetReader = targetImage.getPixelReader();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sourceColor = sourceReader.getArgb(x, y);
                int targetColor = targetReader.getArgb(x, y);

                assertEquals((byte) (sourceColor >>> 16), (byte) (targetColor >>> 16));     // Red
                assertEquals((byte) (sourceColor >>> 8), (byte) (targetColor >>> 8));       // Green
                assertEquals((byte) (sourceColor >>> 0), (byte) (targetColor >>> 0));       // Blue

                // Alpha
                if (arg.pngType() != PNGType.RGB) {
                    assertEquals((byte) (sourceColor >>> 24), (byte) (targetColor >>> 24));
                } else {
                    assertEquals((byte) 0xFF, (byte) (targetColor >>> 24));
                }
            }
        }
    }
}
