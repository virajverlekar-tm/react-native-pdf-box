package com.pdfbox;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An OutputStream which writes to a RandomAccessWrite.
 */
public class RandomAccessOutputStream extends OutputStream
{
    private final RandomAccessWrite writer;

    /**
     * Constructor to create a new output stream which writes to the given RandomAccessWrite.
     *
     * @param writer The random access writer for output
     */
    public RandomAccessOutputStream(RandomAccessWrite writer)
    {
        this.writer = writer;
        // we don't have to maintain a position, as each COSStream can only have one writer.
    }

    @Override
    public void write(byte[] b, int offset, int length) throws IOException
    {
        writer.write(b, offset, length);
    }

    @Override
    public void write(byte[] b) throws IOException
    {
        writer.write(b);
    }

    @Override
    public void write(int b) throws IOException
    {
        writer.write(b);
    }
}
