package com.pdfbox;

import android.util.Log;

import java.io.Closeable;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a stream object in a PDF document.
 */
public class COSStream extends COSDictionary implements Closeable
{
    private RandomAccess randomAccess;      // backing store, in-memory or on-disk
    private final ScratchFile scratchFile;  // used as a temp buffer during decoding
    private boolean isWriting;              // true if there's an open OutputStream

    /**
     * Creates a new stream with an empty dictionary.
     */
    public COSStream()
    {
        this(ScratchFile.getMainMemoryOnlyInstance());
    }

    /**
     * Creates a new stream with an empty dictionary. Data is stored in the given scratch file.
     *
     * @param scratchFile Scratch file for writing stream data.
     */
    public COSStream(ScratchFile scratchFile)
    {
        setInt(COSName.LENGTH, 0);
        this.scratchFile = scratchFile != null ? scratchFile : ScratchFile.getMainMemoryOnlyInstance();
    }

    /**
     * Throws if the random access backing store has been closed. Helpful for catching cases where
     * a user tries to use a COSStream which has outlived its COSDocument.
     */
    private void checkClosed() throws IOException
    {
        if (randomAccess != null && randomAccess.isClosed())
        {
            throw new IOException("COSStream has been closed and cannot be read. " +
                    "Perhaps its enclosing PDDocument has been closed?");
            // Tip for debugging: look at the destination file with an editor, you'll see an
            // incomplete stream at the bottom.
        }
    }

    /**
     * Ensures {@link #randomAccess} is not <code>null</code> by creating a
     * buffer from {@link #scratchFile} if needed.
     *
     * @param forInputStream  if <code>true</code> and {@link #randomAccess} is <code>null</code>
     *                        a debug message is logged - input stream should be retrieved after
     *                        data being written to stream
     * @throws IOException
     */
    private void ensureRandomAccessExists(boolean forInputStream) throws IOException
    {
        if (randomAccess == null)
        {
            if (forInputStream && PDFBoxConfig.isDebugEnabled())
            {
                // no data written to stream - maybe this should be an exception
                Log.d("PdfBox-Android", "Create InputStream called without data being written before to stream.");
            }
            randomAccess = scratchFile.createBuffer();
        }
    }

    /**
     * Returns a new InputStream which reads the encoded PDF stream data. Experts only!
     *
     * @return InputStream containing raw, encoded PDF stream data.
     * @throws IOException If the stream could not be read.
     */
    public InputStream createRawInputStream() throws IOException
    {
        checkClosed();
        if (isWriting)
        {
            throw new IllegalStateException("Cannot read while there is an open stream writer");
        }
        ensureRandomAccessExists(true);
        return new RandomAccessInputStream(randomAccess);
    }

    /**
     * Returns a new InputStream which reads the decoded stream data.
     *
     * @return InputStream containing decoded stream data.
     * @throws IOException If the stream could not be read.
     */
    public COSInputStream createInputStream() throws IOException
    {
        return createInputStream(DecodeOptions.DEFAULT);
    }

    public COSInputStream createInputStream(DecodeOptions options) throws IOException
    {
        checkClosed();
        if (isWriting)
        {
            throw new IllegalStateException("Cannot read while there is an open stream writer");
        }
        ensureRandomAccessExists(true);
        InputStream input = new RandomAccessInputStream(randomAccess);
        return COSInputStream.create(getFilterList(), this, input, scratchFile, options);
    }

    /**
     * Returns a new OutputStream for writing encoded PDF data. Experts only!
     *
     * @return OutputStream for raw PDF stream data.
     * @throws IOException If the output stream could not be created.
     */
    public OutputStream createRawOutputStream() throws IOException
    {
        checkClosed();
        if (isWriting)
        {
            throw new IllegalStateException("Cannot have more than one open stream writer.");
        }
        IOUtils.closeQuietly(randomAccess);
        randomAccess = scratchFile.createBuffer();
        OutputStream out = new RandomAccessOutputStream(randomAccess);
        isWriting = true;
        return new FilterOutputStream(out)
        {
            @Override
            public void write(byte[] b, int off, int len) throws IOException
            {
                this.out.write(b, off, len);
            }

            @Override
            public void close() throws IOException
            {
                super.close();
                setInt(COSName.LENGTH, (int)randomAccess.length());
                isWriting = false;
            }
        };
    }

    /**
     * Returns the list of filters.
     */
    private List<Filter> getFilterList() throws IOException
    {
        List<Filter> filterList;
        COSBase filters = getFilters();
        if (filters instanceof COSName)
        {
            filterList = new ArrayList<Filter>(1);
            filterList.add(FilterFactory.INSTANCE.getFilter((COSName)filters));
        }
        else if (filters instanceof COSArray)
        {
            COSArray filterArray = (COSArray)filters;
            filterList = new ArrayList<Filter>(filterArray.size());
            for (int i = 0; i < filterArray.size(); i++)
            {
                COSBase base = filterArray.get(i);
                if (!(base instanceof COSName))
                {
                    throw new IOException("Forbidden type in filter array: " +
                            (base == null ? "null" : base.getClass().getName()));
                }
                filterList.add(FilterFactory.INSTANCE.getFilter((COSName) base));
            }
        }
        else
        {
            filterList = new ArrayList<Filter>();
        }
        return filterList;
    }

    /**
     * This will return the filters to apply to the byte stream.
     * The method will return
     * <ul>
     * <li>null if no filters are to be applied
     * <li>a COSName if one filter is to be applied
     * <li>a COSArray containing COSNames if multiple filters are to be applied
     * </ul>
     *
     * @return the COSBase object representing the filters
     */
    public COSBase getFilters()
    {
        return getDictionaryObject(COSName.FILTER);
    }

    /**
     * {@inheritDoc}
     *
     * Called by PDFBox when the PDDocument is closed, this closes the stream and removes the data.
     * You will usually not need this.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException
    {
        // marks the scratch file pages as free
        if (randomAccess != null)
        {
            randomAccess.close();
        }
    }
}
