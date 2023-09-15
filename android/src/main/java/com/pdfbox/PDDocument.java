package com.pdfbox;

import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PDDocument implements Closeable {

    private final COSDocument document;

    // holds a flag which tells us if we should remove all security from this documents.
    private boolean allSecurityToBeRemoved;

    /**
     * Creates an empty PDF document.
     * You need to add at least one page for the document to be valid.
     */
    public PDDocument()
    {
        this(MemoryUsageSetting.setupMainMemoryOnly());
    }

    /**
     * Creates an empty PDF document.
     * You need to add at least one page for the document to be valid.
     *
     * @param memUsageSetting defines how memory is used for buffering PDF streams
     */
    public PDDocument(MemoryUsageSetting memUsageSetting)
    {
        ScratchFile scratchFile = null;
        try
        {
            scratchFile = new ScratchFile(memUsageSetting);
        }
        catch (IOException ioe)
        {
            Log.w("PdfBox-Android", "Error initializing scratch file: " + ioe.getMessage() +
                    ". Fall back to main memory usage only.");
            try
            {
                scratchFile = new ScratchFile(MemoryUsageSetting.setupMainMemoryOnly());
            }
            catch (IOException ioe2) {}
        }

        document = new COSDocument(scratchFile);

        // First we need a trailer
        COSDictionary trailer = new COSDictionary();
        document.setTrailer(trailer);

        // Next we need the root dictionary.
        COSDictionary rootDictionary = new COSDictionary();
        trailer.setItem(COSName.ROOT, rootDictionary);
        rootDictionary.setItem(COSName.TYPE, COSName.CATALOG);
        rootDictionary.setItem(COSName.VERSION, COSName.getPDFName("1.4"));

        // next we need the pages tree structure
        COSDictionary pages = new COSDictionary();
        rootDictionary.setItem(COSName.PAGES, pages);
        pages.setItem(COSName.TYPE, COSName.PAGES);
        COSArray kidsArray = new COSArray();
        pages.setItem(COSName.KIDS, kidsArray);
        pages.setItem(COSName.COUNT, COSInteger.ZERO);
    }

    /**
     * Constructor that uses an existing document. The COSDocument that is passed in must be valid.
     *
     * @param doc The COSDocument that this document wraps.
     */
    public PDDocument(COSDocument doc)
    {
        this(doc, null, null);
    }

    /**
     * Constructor that uses an existing document. The COSDocument that is passed in must be valid.
     *
     * @param doc The COSDocument that this document wraps.
     * @param source the parser which is used to read the pdf
     * @param permission he access permissions of the pdf
     *
     */
    public PDDocument(COSDocument doc, RandomAccessRead source, AccessPermission permission)
    {
        document = doc;
    }

    /**
     * This will get the low level document.
     *
     * @return The document that this layer sits on top of.
     */
    public COSDocument getDocument()
    {
        return document;
    }

    /**
     * This will tell if this document is encrypted or not.
     *
     * @return true If this document is encrypted.
     */
    public boolean isEncrypted()
    {
        return document.isEncrypted();
    }

    /**
     * This will get the encryption dictionary for this document. This will still return the parameters if the document
     * was decrypted. As the encryption architecture in PDF documents is pluggable this returns an abstract class,
     * but the only supported subclass at this time is a
     * PDStandardEncryption object.
     *
     * @return The encryption dictionary(most likely a PDStandardEncryption object)
     */
    public PDEncryption getEncryption()
    {
        PDEncryption encryption = null;
        if (isEncrypted())
        {
            encryption = new PDEncryption(document.getEncryptionDictionary());
        }
        return encryption;
    }

    /**
     * This will set the encryption dictionary for this document.
     *
     * @param encryption The encryption dictionary(most likely a PDStandardEncryption object)
     *
     * @throws IOException If there is an error determining which security handler to use.
     */
    public void setEncryptionDictionary(PDEncryption encryption) throws IOException
    {
    }

    /**
     * Parses a PDF. Unrestricted main memory will be used for buffering PDF streams.
     *
     * @param file file to be loaded
     * @param password password to be used for decryption
     *
     * @return loaded document
     *
     * @throws InvalidPasswordException If the password is incorrect.
     * @throws IOException in case of a file reading or parsing error
     */
    public static PDDocument load(File file, String password)
            throws IOException
    {
        return load(file, password, null, null, MemoryUsageSetting.setupMainMemoryOnly());
    }

    /**
     * Parses a PDF.
     *
     * @param file file to be loaded
     * @param password password to be used for decryption
     * @param keyStore key store to be used for decryption when using public key security
     * @param alias alias to be used for decryption when using public key security
     * @param memUsageSetting defines how memory is used for buffering PDF streams
     *
     * @return loaded document
     *
     * @throws IOException in case of a file reading or parsing error
     */
    public static PDDocument load(File file, String password, InputStream keyStore, String alias,
                                  MemoryUsageSetting memUsageSetting) throws IOException
    {
        @SuppressWarnings({"squid:S2095"}) // raFile not closed here, may be needed for signing
        RandomAccessBufferedFileInputStream raFile = new RandomAccessBufferedFileInputStream(file);
        try
        {
            return load(raFile, password, keyStore, alias, memUsageSetting);
        }
        catch (IOException ioe)
        {
            IOUtils.closeQuietly(raFile);
            throw ioe;
        }
    }

    private static PDDocument load(RandomAccessBufferedFileInputStream raFile, String password,
                                   InputStream keyStore, String alias,
                                   MemoryUsageSetting memUsageSetting) throws IOException
    {
        ScratchFile scratchFile = new ScratchFile(memUsageSetting);
        try
        {
            PDFParser parser = new PDFParser(raFile, password, keyStore, alias, scratchFile);
            parser.parse();
            return parser.getPDDocument();
        }
        catch (IOException ioe)
        {
            IOUtils.closeQuietly(scratchFile);
            throw ioe;
        }
    }

    /**
     * Indicates if all security is removed or not when writing the pdf.
     *
     * @return returns true if all security shall be removed otherwise false
     */
    public boolean isAllSecurityToBeRemoved()
    {
        return allSecurityToBeRemoved;
    }

    /**
     * Activates/Deactivates the removal of all security when writing the pdf.
     *
     * @param removeAllSecurity remove all security if set to true
     */
    public void setAllSecurityToBeRemoved(boolean removeAllSecurity)
    {
        allSecurityToBeRemoved = removeAllSecurity;
    }

    /**
     * Save the document to a file.
     * <p>
     * @param file The file to save as.
     *
     * @throws IOException if the output could not be written
     */
    public void save(File file) throws IOException
    {
        save(new BufferedOutputStream(new FileOutputStream(file)));
    }

    /**
     * This will save the document to an output stream.
     * <p>
     * @param output The stream to write to. It will be closed when done. It is recommended to wrap
     * it in a {@link java.io.BufferedOutputStream}, unless it is already buffered.
     *
     * @throws IOException if the output could not be written
     */
    public void save(OutputStream output) throws IOException
    {
        if (document.isClosed())
        {
            throw new IOException("Cannot save a document which has been closed");
        }

        // save PDF
        COSWriter writer = new COSWriter(output);
        try
        {
            writer.write(this);
        }
        finally
        {
            writer.close();
        }
    }

    /**
     * This will close the underlying COSDocument object.
     *
     * @throws IOException If there is an error releasing resources.
     */
    @Override
    public void close() throws IOException
    {
        if (!document.isClosed())
        {
            // Make sure that:
            // - first Exception is kept
            // - all IO resources are closed
            // - there's a way to see which errors occurred

            IOException firstException = null;

            // close all intermediate I/O streams
            firstException = IOUtils.closeAndLogException(document, "COSDocument", firstException);

            // rethrow first exception to keep method contract
            if (firstException != null)
            {
                throw firstException;
            }
        }
    }
}
