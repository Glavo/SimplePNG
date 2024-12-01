/*
 * Copyright 2024 Glavo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glavo.png.image;

public final class ArgbImageBuffer implements ArgbImage {
    private final int width;
    private final int height;
    private final int[] colors;

    public ArgbImageBuffer(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }

        this.width = width;
        this.height = height;
        this.colors = new int[width * height];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getArgb(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }

        return colors[x + y * width];
    }

    public void setArgb(int x, int y, int color) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IllegalArgumentException();
        }

        colors[x + y * width] = color;
    }

    public void setArgb(int x, int y, int a, int r, int g, int b) {
        setArgb(x, y, (a << 24) | (r << 16) | (g << 8) | b);
    }

    public void setRgb(int x, int y, int r, int g, int b) {
        setArgb(x, y, 0xff, r, g, b);
    }
}
