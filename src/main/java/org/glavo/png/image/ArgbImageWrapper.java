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

import java.util.Objects;

public abstract class ArgbImageWrapper<T> implements ArgbImage {
    protected final T image;
    protected final int width;
    protected final int height;

    protected ArgbImageWrapper(T image, int width, int height) {
        this.image = image;
        this.width = width;
        this.height = height;

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid picture size");
        }
    }

    public T getImage() {
        return image;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public static <T> ArgbImageWrapper<T> of(T image, int width, int height, ColorExtractor<T> extractor) {
        Objects.requireNonNull(extractor);
        return new ArgbImageWrapper<T>(image, width, height) {
            @Override
            public int getArgb(int x, int y) {
                return extractor.getArgb(super.image, x, y);
            }
        };
    }

    @FunctionalInterface
    public interface ColorExtractor<T> {
        int getArgb(T image, int x, int y);
    }
}
