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
package org.glavo.png.imageio;

import org.glavo.png.image.ArgbImageWrapper;

import java.awt.image.BufferedImage;
import java.util.Objects;

public final class PNGImageIOUtils {
    private PNGImageIOUtils() {
    }

    public static ArgbImageWrapper<BufferedImage> asArgbImage(BufferedImage image) {
        Objects.requireNonNull(image);
        return new ArgbImageWrapper<BufferedImage>(image, image.getWidth(), image.getHeight()) {
            @Override
            public int getArgb(int x, int y) {
                return image.getRGB(x, y);
            }
        };
    }
}
