package org.glavo.png.image;

public interface ArgbImage {
    int getWidth();

    int getHeight();

    int getArgb(int x, int y);
}
