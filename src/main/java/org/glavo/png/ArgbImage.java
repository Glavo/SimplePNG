package org.glavo.png;

import java.util.Objects;

public interface ArgbImage {
    int getWidth();

    int getHeight();

    int getColor(int x, int y);

    static <T> ArgbImage of(T image, int width, int height, ColorExtractor<T> extractor) {
        Objects.requireNonNull(extractor);
        return new ArgbImage() {
            @Override
            public int getWidth() {
                return width;
            }

            @Override
            public int getHeight() {
                return height;
            }

            @Override
            public int getColor(int x, int y) {
                return extractor.getColor(image, x, y);
            }
        };
    }

    interface ColorExtractor<T> {
        int getColor(T image, int x, int y);
    }
}
