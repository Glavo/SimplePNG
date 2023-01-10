package org.glavo.png;

public enum PNGType {
    PNG_GRAYSCALE(0, 1),
    PNG_RGB(2, 3),
    PNG_PALETTE(3, 1),
    PNG_GRAYSCALE_ALPHA(4, 2),
    PNG_RGBA(6, 4);

    private final int type;
    private final int bpp;

    PNGType(int type, int bpp) {
        this.type = type;
        this.bpp = bpp;
    }

    public int getType() {
        return type;
    }
}
