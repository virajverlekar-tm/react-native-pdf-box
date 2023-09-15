package com.pdfbox;

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface allowing random access write operations.
 */
public interface RandomAccessWrite extends Closeable
{
    /**
     * Write a byte to the stream.
     *
     * @param b The byte to write.
     * @throws IOException If there is an IO error while writing.
     */
    void write(int b) throws IOException;

    /**
     * Write a buffer of data to the stream.
     *
     * @param b The buffer to get the data from.
     * @throws IOException If there is an error while writing the data.
     */
    void write(byte[] b) throws IOException;

    /**
     * Write a buffer of data to the stream.
     *
     * @param b The buffer to get the data from.
     * @param offset An offset into the buffer to get the data from.
     * @param length The length of data to write.
     * @throws IOException If there is an error while writing the data.
     */
    void write(byte[] b, int offset, int length) throws IOException;

    /**
     * Clears all data of the buffer.
     */
    void clear() throws IOException;
}
