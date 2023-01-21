package org.glavo.png.javafx;

import javafx.scene.image.PixelReader;

import java.io.IOException;
import java.io.InputStream;

final class ImageAsPngInputStream extends InputStream {

    private final PixelReader reader = null;

    public ImageAsPngInputStream() {

    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
