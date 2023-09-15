package com.pdfbox;

public class PDFBoxConfig
{
    private static boolean debugLoggingEnabled = false;

    /**
     * @param debugLoggingEnabled sets whether debug logging is enabled for PdfBox-Android
     */
    public static void setDebugLoggingEnabled(boolean debugLoggingEnabled)
    {
        PDFBoxConfig.debugLoggingEnabled = debugLoggingEnabled;
    }

    /**
     * @return whether debug logging is enabled for PdfBox-Android
     */
    public static boolean isDebugEnabled()
    {
        return debugLoggingEnabled;
    }
}
