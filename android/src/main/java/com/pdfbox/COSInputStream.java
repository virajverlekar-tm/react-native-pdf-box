package com.pdfbox;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An InputStream which reads from an encoded COS stream.
 */
public final class COSInputStream extends FilterInputStream
{
    /**
     * Creates a new COSInputStream from an encoded input stream.
     *
     * @param filters Filters to be applied.
     * @param parameters Filter parameters.
     * @param in Encoded input stream.
     * @param scratchFile Scratch file to use, or null.
     * @return Decoded stream.
     * @throws IOException If the stream could not be read.
     */
    static COSInputStream create(List<Filter> filters, COSDictionary parameters, InputStream in,
                                 ScratchFile scratchFile) throws IOException
    {
        return create(filters, parameters, in, scratchFile, DecodeOptions.DEFAULT);
    }

    static COSInputStream create(List<Filter> filters, COSDictionary parameters, InputStream in,
                                 ScratchFile scratchFile, DecodeOptions options) throws IOException
    {
        InputStream input = in;
        if (filters.isEmpty())
        {
            return new COSInputStream(in, Collections.<DecodeResult>emptyList());
        }

        List<DecodeResult> results = new ArrayList<DecodeResult>(filters.size());
        if (filters.size() > 1)
        {
            Set<Filter> filterSet = new HashSet<Filter>(filters);
            if (filterSet.size() != filters.size())
            {
                throw new IOException("Duplicate");
            }
        }
        // apply filters
        for (int i = 0; i < filters.size(); i++)
        {
            if (scratchFile != null)
            {
                // scratch file
                final RandomAccess buffer = scratchFile.createBuffer();
                DecodeResult result = filters.get(i).decode(input, new RandomAccessOutputStream(buffer), parameters, i, options);
                results.add(result);
                input = new RandomAccessInputStream(buffer)
                {
                    @Override
                    public void close() throws IOException
                    {
                        buffer.close();
                    }
                };
            }
            else
            {
                // in-memory
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                DecodeResult result = filters.get(i).decode(input, output, parameters, i, options);
                results.add(result);
                input = new ByteArrayInputStream(output.toByteArray());
            }
        }
        return new COSInputStream(input, results);
    }

    private final List<DecodeResult> decodeResults;

    /**
     * Constructor.
     *
     * @param input decoded stream
     * @param decodeResults results of decoding
     */
    private COSInputStream(InputStream input, List<DecodeResult> decodeResults)
    {
        super(input);
        this.decodeResults = decodeResults;
    }

    /**
     * Returns the result of the last filter, for use by repair mechanisms.
     *
     * @return the result of the decoding.
     */
    public DecodeResult getDecodeResult()
    {
        if (decodeResults.isEmpty())
        {
            return DecodeResult.DEFAULT;
        }
        else
        {
            return decodeResults.get(decodeResults.size() - 1);
        }
    }
}
