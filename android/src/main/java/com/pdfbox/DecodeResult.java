package com.pdfbox;

/**
 * The result of a filter decode operation. Allows information such as color space to be
 * extracted from image streams, and for stream parameters to be repaired during reading.
 */
public final class DecodeResult
{
    /** Default decode result. */
    public static final DecodeResult DEFAULT = new DecodeResult(new COSDictionary());

    private final COSDictionary parameters;

    DecodeResult(COSDictionary parameters)
    {
        this.parameters = parameters;
    }

    /**
     * Returns the stream parameters, repaired using the embedded stream data.
     * @return the repaired stream parameters, or an empty dictionary
     */
    public COSDictionary getParameters()
    {
        return parameters;
    }
}
