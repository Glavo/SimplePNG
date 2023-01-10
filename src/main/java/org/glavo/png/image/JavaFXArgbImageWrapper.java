package org.glavo.png.image;

import javafx.scene.image.PixelReader;

public final class JavaFXArgbImageWrapper extends ArgbImageWrapper<javafx.scene.image.Image> {
    private final PixelReader pixelReader;

    public JavaFXArgbImageWrapper(javafx.scene.image.Image image) {
        super(image, (int) image.getWidth(), (int) image.getHeight());
        this.pixelReader = image.getPixelReader();
    }

    @Override
    public int getArgb(int x, int y) {
        return pixelReader.getArgb(x, y);
    }
}
