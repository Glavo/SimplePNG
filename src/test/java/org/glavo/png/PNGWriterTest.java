package org.glavo.png;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.png.PngImageParser;
import org.glavo.png.imageio.PNGImageIOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.zip.Deflater;

import static org.junit.jupiter.api.Assertions.*;

public class PNGWriterTest {

    private record Argument(String file, PNGType pngType, int compressLevel) {
    }

    private static final String defaultTestFile = "minecraft.png";
    private static final String[] testFiles = {defaultTestFile, "skin.png", "background.jpg"};

    private static Stream<Argument> testFiles() {
        return Arrays.stream(testFiles)
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

    @ParameterizedTest
    @MethodSource("testFiles")
    public void testWriteArgbFile(Argument arg) throws Exception {
        BufferedImage sourceImage = ImageIO.read(PNGWriterTest.class.getResource(arg.file));

        BufferedImage targetImage;

        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        try (PNGWriter writer = new PNGWriter(temp, arg.pngType, arg.compressLevel)) {
            writer.write(PNGImageIOUtils.asArgbImage(sourceImage));
        }
        targetImage = ImageIO.read(new ByteArrayInputStream(temp.toByteArray()));

        int width = sourceImage.getWidth();
        int height = sourceImage.getHeight();

        assertEquals(width, targetImage.getWidth());
        assertEquals(height, targetImage.getHeight());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sourceColor = sourceImage.getRGB(x, y);
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

    @Test
    public void testMetadata() throws IOException, ImageReadException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PNGWriter writer = new PNGWriter(out)) {
            PNGMetadata metadata = new PNGMetadata()
                    .setCreationTime(LocalDateTime.of(2023, 1, 12, 12, 0))
                    .setAuthor("Glavo");
            writer.write(PNGImageIOUtils.asArgbImage(ImageIO.read(PNGWriterTest.class.getResource(defaultTestFile)))
                    .withMetadata(metadata));
        }

        byte[] image = out.toByteArray();
        ImageMetadata metadata = new PngImageParser().getMetadata(image);
        assertNotNull(metadata);
    }
}
