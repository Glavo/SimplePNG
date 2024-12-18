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
package org.glavo.png;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.png.PngImageParser;
import org.glavo.png.image.ArgbImage;
import org.glavo.png.imageio.PNGImageIOUtils;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PNGWriterTest implements BasicTest {

    @Test
    public void testMetadata() throws IOException, ImageReadException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PNGWriter writer = new PNGWriter(out)) {
            PNGMetadata metadata = new PNGMetadata()
                    .setCreationTime(LocalDateTime.of(2023, 1, 12, 12, 0))
                    .setAuthor("Glavo");
            writer.write(PNGImageIOUtils.asArgbImage(ImageIO.read(PNGWriterTest.class.getResource(BasicTest.defaultTestFile)))
                    .withMetadata(metadata));
        }

        byte[] image = out.toByteArray();
        ImageMetadata metadata = new PngImageParser().getMetadata(image);
        assertNotNull(metadata);
    }

    @Override
    public ArgbImage readImage(InputStream input) throws IOException {
        return PNGImageIOUtils.asArgbImage(ImageIO.read(input));
    }
}
