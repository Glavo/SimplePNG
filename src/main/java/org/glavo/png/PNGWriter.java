package org.glavo.png;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

public abstract class PNGWriter {
    final PNGType type;

    public PNGWriter(PNGType type) {
        Objects.requireNonNull(type);

        this.type = type;

        if (type != PNGType.PNG_RGB && type != PNGType.PNG_RGBA) {
            throw new UnsupportedOperationException();
        }
    }

    public final PNGType getType() {
        return type;
    }

    public void write(ArgbImage image, OutputStream outputStream) throws IOException {
        // TODO
    }

    public byte[] toBytes(ArgbImage image) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            write(image, bytes);
            return bytes.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
