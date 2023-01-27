package org.glavo.png.imageio;

import org.glavo.png.image.ArgbImageWrapper;

import java.awt.image.BufferedImage;
import java.util.Objects;

public final class PNGImageIOUtils {
    private PNGImageIOUtils() {
    }

    public static ArgbImageWrapper<BufferedImage> asArgbImage(BufferedImage image) {
        Objects.requireNonNull(image);
        return new ArgbImageWrapper<BufferedImage>(image, image.getWidth(), image.getHeight()) {
            @Override
            public int getArgb(int x, int y) {
                return image.getRGB(x, y);
            }
        };
    }
}
