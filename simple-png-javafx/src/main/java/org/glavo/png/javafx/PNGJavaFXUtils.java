package org.glavo.png.javafx;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import org.glavo.png.PNGType;
import org.glavo.png.PNGWriter;
import org.glavo.png.image.ArgbImageWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PNGJavaFXUtils {
    private PNGJavaFXUtils() {
    }

    public static ArgbImageWrapper<Image> asArgbImage(Image image) {
        PixelReader pixelReader = image.getPixelReader();

        return new ArgbImageWrapper<Image>(image, (int) image.getWidth(), (int) image.getHeight()) {
            @Override
            public int getArgb(int x, int y) {
                return pixelReader.getArgb(x, y);
            }
        };
    }

    public static void writeImage(Image image, Path file) throws IOException {
        writeImage(image, Files.newOutputStream(file));
    }

    public static void writeImage(Image image, Path file, PNGType type) throws IOException {
        writeImage(image, Files.newOutputStream(file), type);
    }

    public static void writeImage(Image image, Path file, PNGType type, int compressLevel) throws IOException {
        writeImage(image, Files.newOutputStream(file), type, compressLevel);
    }

    public static byte[] writeImageToArray(Image image) throws IOException {
        return writeImageToArray(image, PNGType.RGBA, PNGWriter.DEFAULT_COMPRESS_LEVEL);
    }

    public static byte[] writeImageToArray(Image image, PNGType type) throws IOException {
        return writeImageToArray(image, type, PNGWriter.DEFAULT_COMPRESS_LEVEL);
    }

    public static byte[] writeImageToArray(Image image, PNGType type, int compressLevel) throws IOException {
        int estimatedSize = (int) (
                image.getWidth() * image.getHeight()
                        * (type == PNGType.RGB ? 3 : 4)
                        + 32);

        if (compressLevel != 1) {
            estimatedSize /= 2;
        }

        ByteArrayOutputStream temp = new ByteArrayOutputStream(Integer.max(estimatedSize, 32));
        writeImage(image, temp, type, compressLevel);
        return temp.toByteArray();
    }

    private static void writeImage(Image image, OutputStream out) throws IOException {
        writeImage(image, out, PNGType.RGBA, PNGWriter.DEFAULT_COMPRESS_LEVEL);
    }

    private static void writeImage(Image image, OutputStream out, PNGType type) throws IOException {
        writeImage(image, out, type, PNGWriter.DEFAULT_COMPRESS_LEVEL);
    }

    private static void writeImage(Image image, OutputStream out, PNGType type, int compressLevel) throws IOException {
        new PNGWriter(out, type, compressLevel).write(asArgbImage(image));
    }
}
