package com.pdfbox;

import java.io.IOException;

/**
 * A SequentialSource backed by a RandomAccessRead.
 */
final class RandomAccessSource implements SequentialSource
{
    private final RandomAccessRead reader;

    /**
     * Constructor.
     *
     * @param reader The random access reader to wrap.
     */
    RandomAccessSource(RandomAccessRead reader)
    {
        this.reader = reader;
    }

    @Override
    public int read() throws IOException
    {
        return reader.read();
    }

    @Override
    public int read(byte[] b) throws IOException
    {
        return reader.read(b);
    }

    @Override
    public int read(byte[] b, int offset, int length) throws IOException
    {
        return reader.read(b, offset, length);
    }

    @Override
    public long getPosition() throws IOException
    {
        return reader.getPosition();
    }

    @Override
    public int peek() throws IOException
    {
        return reader.peek();
    }

    @Override
    public void unread(int b) throws IOException
    {
        reader.rewind(1);
    }

    @Override
    public void unread(byte[] bytes) throws IOException
    {
        reader.rewind(bytes.length);
    }

    @Override
    public void unread(byte[] bytes, int start, int len) throws IOException
    {
        reader.rewind(len);
    }

    @Override
    public byte[] readFully(int length) throws IOException
    {
        return reader.readFully(length);
    }

    @Override
    public boolean isEOF() throws IOException
    {
        return reader.isEOF();
    }

    @Override
    public void close() throws IOException
    {
        reader.close();
    }
}
