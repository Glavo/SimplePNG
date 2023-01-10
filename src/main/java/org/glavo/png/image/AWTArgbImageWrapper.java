package org.glavo.png.image;

import java.awt.image.BufferedImage;

public final class AWTArgbImageWrapper extends ArgbImageWrapper<BufferedImage> {
    public AWTArgbImageWrapper(BufferedImage image) {
        super(image, image.getWidth(), image.getHeight());
    }

    @Override
    public int getArgb(int x, int y) {
        return image.getRGB(x, y);
    }
}
