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
package org.glavo.png.example;

import org.glavo.png.PNGWriter;
import org.glavo.png.image.ArgbImageBuffer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Gradient {
    public static void main(String[] args) throws IOException {
        ArgbImageBuffer buffer = new ArgbImageBuffer(250, 250);
        for (int x = 0; x < 250; x++) {
            for (int y = 0; y < 250; y++) {
                buffer.setArgb(x, y, 255 - ((x / 2) & 255), x & 255, y & 255, 128);
            }
        }

        try (PNGWriter writer = new PNGWriter(Files.newOutputStream(Paths.get("gradient.png")))) {
            writer.write(buffer);
        }
    }
}
