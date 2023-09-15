package com.pdfbox;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * An InputStream which reads from a RandomAccessRead.
 */
public class RandomAccessInputStream extends InputStream
{
    private final RandomAccessRead input;
    private long position;

    /**
     * Creates a new RandomAccessInputStream, with a position of zero. The InputStream will maintain
     * its own position independent of the RandomAccessRead.
     *
     * @param randomAccessRead The RandomAccessRead to read from.
     */
    public RandomAccessInputStream(RandomAccessRead randomAccessRead)
    {
        input = randomAccessRead;
        position = 0;
    }

    void restorePosition() throws IOException
    {
        input.seek(position);
    }

    @Override
    public int available() throws IOException
    {
        restorePosition();
        long available = input.length() - input.getPosition();
        if (available > Integer.MAX_VALUE)
        {
            return Integer.MAX_VALUE;
        }
        return (int)available;
    }

    @Override
    public int read() throws IOException
    {
        restorePosition();
        if (input.isEOF())
        {
            return -1;
        }
        int b = input.read();
        if (b != -1)
        {
            position += 1;
        }
        else
        {
            // should never happen due to prior isEOF() check
            // unless there is an unsynchronized concurrent access
            Log.e("PdfBox-Android", "read() returns -1, assumed position: " +
                    position + ", actual position: " + input.getPosition());
        }
        return b;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException
    {
        restorePosition();
        if (input.isEOF())
        {
            return -1;
        }
        int n = input.read(b, off, len);
        if (n != -1)
        {
            position += n;
        }
        else
        {
            // should never happen due to prior isEOF() check
            // unless there is an unsynchronized concurrent access
            Log.e("PdfBox-Android", "read() returns -1, assumed position: " +
                    position + ", actual position: " + input.getPosition());
        }
        return n;
    }

    @Override
    public long skip(long n) throws IOException
    {
        restorePosition();
        input.seek(position + n);
        position += n;
        return n;
    }
}
