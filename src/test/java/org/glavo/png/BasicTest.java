package org.glavo.png;

import org.glavo.png.image.ArgbImage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.zip.Deflater;

import static org.junit.jupiter.api.Assertions.assertEquals;

public interface BasicTest {
    record Argument(Path file, PNGType pngType, int compressLevel) {
    }

    String defaultTestFile = "minecraft.png";
    String[] testFiles = {defaultTestFile, "skin.png", "background.jpg", "rgba.png"};

    static Stream<Argument> testFiles() {
        return Arrays.stream(testFiles)
                .map(path -> {
                    try {
                        return Path.of(BasicTest.class.getResource(path).toURI());
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(file -> Stream.of(
                        new Argument(file, PNGType.RGBA, Deflater.DEFAULT_COMPRESSION),
                        new Argument(file, PNGType.RGBA, 0),
                        new Argument(file, PNGType.RGBA, 1),
                        new Argument(file, PNGType.RGBA, 9),

                        new Argument(file, PNGType.RGB, Deflater.DEFAULT_COMPRESSION),
                        new Argument(file, PNGType.RGB, 0),
                        new Argument(file, PNGType.RGB, 1),
                        new Argument(file, PNGType.RGB, 9)
                ));
    }

    ArgbImage readImage(InputStream input) throws IOException;

    @ParameterizedTest
    @MethodSource("testFiles")
    default void testWriteArgbFile(Argument arg) throws Exception {
        ArgbImage sourceImage;
        try (InputStream input = Files.newInputStream(arg.file)) {
            sourceImage = readImage(input);
        }
        BufferedImage targetImage;

        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        try (PNGWriter writer = new PNGWriter(temp, arg.pngType, arg.compressLevel)) {
            writer.write(sourceImage);
        }
        targetImage = ImageIO.read(new ByteArrayInputStream(temp.toByteArray()));

        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();

        assertEquals(width, targetImage.getWidth());
        assertEquals(height, targetImage.getHeight());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sourceColor = sourceImage.getArgb(x, y);
                int targetColor = targetImage.getRGB(x, y);

                assertEquals((byte) (sourceColor >>> 16), (byte) (targetColor >>> 16));     // Red
                assertEquals((byte) (sourceColor >>> 8), (byte) (targetColor >>> 8));       // Green
                assertEquals((byte) (sourceColor >>> 0), (byte) (targetColor >>> 0));       // Blue

                // Alpha
                if (arg.pngType != PNGType.RGB) {
                    assertEquals((byte) (sourceColor >>> 24), (byte) (targetColor >>> 24));
                } else {
                    assertEquals((byte) 0xFF, (byte) (targetColor >>> 24));
                }
            }
        }
    }

}
