package org.glavo.png.image;

import org.glavo.png.PNGMetadata;

public interface ArgbImage {

    static int argb(int a, int r, int g, int b) {
        return ((r << 16) | ((g) << 8) | ((b)) | ((a) << 24));
    }

    static int rgb(int r, int g, int b) {
        return argb(-1, r, g, b);
    }

    int getWidth();

    int getHeight();

    int getArgb(int x, int y);

    default PNGMetadata getMetadata() {
        return null;
    }

    default ArgbImage withMetadata(PNGMetadata metadata) {
        return new ArgbImageWithMetadata(this, metadata);
    }

    default ArgbImage withDefaultMetadata() {
        return withMetadata(new PNGMetadata()
                .setAuthor()
                .setCreationTime());
    }
}
