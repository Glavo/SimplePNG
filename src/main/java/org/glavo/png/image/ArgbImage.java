package org.glavo.png.image;

import org.glavo.png.PNGMetadata;

public interface ArgbImage {

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
