package com.pdfbox;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.zip.Deflater;

/**
 * A filter for stream data.
 */
public abstract class Filter
{
    /**
     * Compression Level System Property. Set this to a value from 0 to 9 to change the zlib deflate
     * compression level used to compress /Flate streams. The default value is -1 which is
     * {@link Deflater#DEFAULT_COMPRESSION}. To set maximum compression, use
     * {@code System.setProperty(Filter.SYSPROP_DEFLATELEVEL, "9");}
     */
    public static final String SYSPROP_DEFLATELEVEL = "com.tom_roush.pdfbox.filter.deflatelevel";

    /**
     * Constructor.
     */
    protected Filter()
    {
    }

    /**
     * Decodes data, producing the original non-encoded data.
     * @param encoded the encoded byte stream
     * @param decoded the stream where decoded data will be written
     * @param parameters the parameters used for decoding
     * @param index the index to the filter being decoded
     * @return repaired parameters dictionary, or the original parameters dictionary
     * @throws IOException if the stream cannot be decoded
     */
    public abstract DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters,
                                        int index) throws IOException;

    /**
     * Decodes data, with optional DecodeOptions. Not all filters support all options, and so
     * callers should check the options' <code>honored</code> flag to test if they were applied.
     *
     * @param encoded the encoded byte stream
     * @param decoded the stream where decoded data will be written
     * @param parameters the parameters used for decoding
     * @param index the index to the filter being decoded
     * @param options additional options for decoding
     * @return repaired parameters dictionary, or the original parameters dictionary
     * @throws IOException if the stream cannot be decoded
     */
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters,
                               int index, DecodeOptions options) throws IOException
    {
        return decode(encoded, decoded, parameters, index);
    }

    // gets the decode params for a specific filter index, this is used to
    // normalise the DecodeParams entry so that it is always a dictionary
    protected COSDictionary getDecodeParams(COSDictionary dictionary, int index)
    {
        COSBase filter = dictionary.getDictionaryObject(COSName.F, COSName.FILTER);
        COSBase obj = dictionary.getDictionaryObject(COSName.DP, COSName.DECODE_PARMS);
        if (filter instanceof COSName && obj instanceof COSDictionary)
        {
            // PDFBOX-3932: The PDF specification requires "If there is only one filter and that
            // filter has parameters, DecodeParms shall be set to the filter’s parameter dictionary"
            // but tests show that Adobe means "one filter name object".
            return (COSDictionary)obj;
        }
        else if (filter instanceof COSArray && obj instanceof COSArray)
        {
            COSArray array = (COSArray)obj;
            if (index < array.size())
            {
                COSBase objAtIndex = array.getObject(index);
                if (objAtIndex instanceof COSDictionary)
                {
                    return (COSDictionary) objAtIndex;
                }
            }
        }
        else if (obj != null && !(filter instanceof COSArray || obj instanceof COSArray))
        {
            Log.e("PdfBox-Android", "Expected DecodeParams to be an Array or Dictionary but found " +
                    obj.getClass().getName());
        }
        return new COSDictionary();
    }

//    protected static ImageReader findImageReader(String formatName, String errorCause) throws MissingImageReaderException TODO: PdfBox-Android

    /**
     * @return the ZIP compression level configured for PDFBox
     */
    public static int getCompressionLevel()
    {
        int compressionLevel = Deflater.DEFAULT_COMPRESSION;
        try
        {
            compressionLevel = Integer.parseInt(System.getProperty(Filter.SYSPROP_DEFLATELEVEL, "-1"));
        }
        catch (NumberFormatException ex)
        {
            Log.w("PdfBox-Android", ex.getMessage(), ex);
        }
        return Math.max(-1, Math.min(Deflater.BEST_COMPRESSION, compressionLevel));
    }
}
