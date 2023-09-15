package com.pdfbox;

import java.nio.charset.Charset;

/**
 * Utility class providing common Charsets used in PDFBox.
 */
public final class Charsets
{
    private Charsets() {}

    /*** ASCII charset */
    public static final Charset US_ASCII = Charset.forName("US-ASCII");

    /*** UTF-16BE charset */
    public static final Charset UTF_16BE = Charset.forName("UTF-16BE");

    /*** UTF-16LE charset */
    public static final Charset UTF_16LE = Charset.forName("UTF-16LE");

    /*** ISO-8859-1 charset */
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    /*** Windows-1252 charset */
    public static final Charset WINDOWS_1252 = Charset.forName("Windows-1252");

    /*** UTF-8 charset */
    public static final Charset UTF_8 = Charset.forName("UTF-8");
}
