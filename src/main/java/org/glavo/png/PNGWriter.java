package org.glavo.png;

import org.glavo.png.image.AWTArgbImageWrapper;
import org.glavo.png.image.ArgbImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public final class PNGWriter implements Closeable {
    private static final byte[] PNG_FILE_HEADER = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private static final byte[] UNCOMPRESSED_CHUNK_HEADER = {0x78, (byte) 0xDA, 0x01};
    private static final int MAX_BLOCK_SIZE = 32 * 1024;

    private final OutputStream out;
    private final PNGType type;
    private final int compressLevel;

    private final Deflater deflater = new Deflater();
    private final CRC32 crc32 = new CRC32();
    private final byte[] writeBuffer = new byte[8];

    public PNGWriter(OutputStream out) {
        this(out, PNGType.RGBA, 0);
    }

    public PNGWriter(OutputStream out, PNGType type) {
        this(out, type, 0);
    }

    public PNGWriter(OutputStream out, int compressLevel) {
        this(out, PNGType.RGBA, compressLevel);
    }

    public PNGWriter(OutputStream out, PNGType type, int compressLevel) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(out);

        if (compressLevel != Deflater.DEFAULT_COMPRESSION && (compressLevel < 0 || compressLevel > 9)) {
            throw new IllegalArgumentException();
        }

        if (type != PNGType.RGB && type != PNGType.RGBA) {
            throw new UnsupportedOperationException("SimplePNG currently only supports RGB or RGBA images");
        }

        this.type = type;
        this.compressLevel = compressLevel;
        this.out = out;

        this.deflater.setLevel(compressLevel);
    }

    public PNGType getType() {
        return type;
    }

    public int getCompressLevel() {
        return compressLevel;
    }

    private static int divRoundUp(int x, int y) {
        return (x + y - 1) / y;
    }

    private void writeByte(int b) throws IOException {
        out.write(b);
        crc32.update(b);
    }

    private void writeShort(int s) throws IOException {
        writeBuffer[0] = (byte) (s >>> 8);
        writeBuffer[1] = (byte) (s >>> 0);
        writeBytes(writeBuffer, 0, 2);
    }

    private void writeInt(int value) throws IOException {
        writeBuffer[0] = (byte) (value >>> 24);
        writeBuffer[1] = (byte) (value >>> 16);
        writeBuffer[2] = (byte) (value >>> 8);
        writeBuffer[3] = (byte) (value >>> 0);
        writeBytes(writeBuffer, 0, 4);
    }

    private void writeLong(long value) throws IOException {
        writeBuffer[0] = (byte) (value >>> 56);
        writeBuffer[1] = (byte) (value >>> 48);
        writeBuffer[2] = (byte) (value >>> 40);
        writeBuffer[3] = (byte) (value >>> 32);
        writeBuffer[4] = (byte) (value >>> 24);
        writeBuffer[5] = (byte) (value >>> 16);
        writeBuffer[6] = (byte) (value >>> 8);
        writeBuffer[7] = (byte) (value >>> 0);
        writeBytes(writeBuffer, 0, 8);
    }

    private void writeBytes(byte[] bytes) throws IOException {
        writeBytes(bytes, 0, bytes.length);
    }

    private void writeBytes(byte[] bytes, int off, int len) throws IOException {
        out.write(bytes, off, len);
        crc32.update(bytes, off, len);
    }

    private void beginChunk(String header, int length) throws IOException {
        writeBuffer[0] = (byte) (length >>> 24);
        writeBuffer[1] = (byte) (length >>> 16);
        writeBuffer[2] = (byte) (length >>> 8);
        writeBuffer[3] = (byte) (length >>> 0);
        writeBuffer[4] = (byte) header.charAt(0);
        writeBuffer[5] = (byte) header.charAt(1);
        writeBuffer[6] = (byte) header.charAt(2);
        writeBuffer[7] = (byte) header.charAt(3);
        out.write(writeBuffer, 0, 8);

        crc32.reset();
        crc32.update(writeBuffer, 4, 4);
    }

    private void endChunk() throws IOException {
        int crc = (int) crc32.getValue();
        writeBuffer[0] = (byte) (crc >>> 24);
        writeBuffer[1] = (byte) (crc >>> 16);
        writeBuffer[2] = (byte) (crc >>> 8);
        writeBuffer[3] = (byte) (crc >>> 0);
        out.write(writeBuffer, 0, 4);
    }

    public void write(ArgbImage image) throws IOException {
        final int width = image.getWidth();
        final int height = image.getHeight();

        out.write(PNG_FILE_HEADER);

        // IHDR Chunk
        beginChunk("IHDR", 13);
        writeInt(width);
        writeInt(height);
        writeByte(8); // bit depth
        writeByte(type.id);
        writeByte(0); // compression
        writeByte(0); // filter
        writeByte(0); // interlace method
        endChunk();

        // IDAT Chunk
        int colorPerPixel = type.cpp;
        int bytesPerLine = 1 + colorPerPixel * width;
        int outputRawSize = height * bytesPerLine;

        int blockSize = MAX_BLOCK_SIZE - (MAX_BLOCK_SIZE % bytesPerLine);
        int numBlocks = divRoundUp(outputRawSize, blockSize);

        System.out.println("blockSize=" + blockSize);
        System.out.println("numBlocks=" + numBlocks);

        if (compressLevel == 0) {
            Adler32 adler32 = new Adler32();
            byte[] lineBuffer = new byte[bytesPerLine];

            for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
                int len = Integer.min(outputRawSize - (blockIndex * blockSize), blockSize);
                int nlen = len ^ 0xFFFF;

                System.out.println("len=" + len);

                int beginLine = (blockIndex * blockSize) / bytesPerLine;
                int endLine = beginLine + (len / bytesPerLine);

                beginChunk("IDAT", len + 11);
                writeBytes(UNCOMPRESSED_CHUNK_HEADER);

                writeBuffer[0] = (byte) (len >>> 0);
                writeBuffer[1] = (byte) (len >>> 8);
                writeBuffer[2] = (byte) (nlen >>> 0);
                writeBuffer[3] = (byte) (nlen >>> 8);
                writeBytes(writeBuffer, 0, 4);

                adler32.reset();
                for (int y = beginLine; y < endLine; y++) {
                    for (int x = 0; x < width; x++) {
                        int color = image.getArgb(x, y);

                        int off = 1 + colorPerPixel * x;
                        lineBuffer[off + 0] = (byte) (color >>> 16);
                        lineBuffer[off + 1] = (byte) (color >>> 8);
                        lineBuffer[off + 2] = (byte) (color >>> 0);
                        if (colorPerPixel == 4)
                            lineBuffer[off + 3] = (byte) (color >>> 24);
                    }
                    writeBytes(lineBuffer, 0, bytesPerLine);
                    adler32.update(lineBuffer, 0, bytesPerLine);
                }

                writeInt((int) adler32.getValue());
                endChunk();
            }
        } else {
            throw new UnsupportedOperationException(); // TODO
        }


        // IEND Chunk
        beginChunk("IEND", 0);
        endChunk();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    public static void dumpImage(String input, String output) throws IOException {
        BufferedImage img = ImageIO.read(new File(input));
        try (PNGWriter writer = new PNGWriter(Files.newOutputStream(Paths.get(output)))) {
            writer.write(new AWTArgbImageWrapper(img));
        }
    }
}
