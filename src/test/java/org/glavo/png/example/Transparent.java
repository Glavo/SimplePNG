package org.glavo.png.example;

import org.glavo.png.PNGWriter;
import org.glavo.png.image.ArgbImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Transparent {
    public static void main(String[] args) throws IOException {
        try (PNGWriter writer = new PNGWriter(Files.newOutputStream(Paths.get("transparent.png")))) {
            writer.write(new ArgbImage() {
                @Override
                public int getWidth() {
                    return 960;
                }

                @Override
                public int getHeight() {
                    return 540;
                }

                @Override
                public int getArgb(int x, int y) {
                    return 0;
                }
            });
        }
    }
}
