package com.pdfbox;

import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the in-memory representation of the PDF document.  You need to call
 * close() on this object when you are done using it!!
 *
 */
public class COSDocument extends COSBase implements Closeable
{

    private float version = 1.4f;

    /**
     * Maps ObjectKeys to a COSObject. Note that references to these objects
     * are also stored in COSDictionary objects that map a name to a specific object.
     */
    private final Map<COSObjectKey, COSObject> objectPool =
            new HashMap<COSObjectKey, COSObject>();

    /**
     * Maps object and generation id to object byte offsets.
     */
    private final Map<COSObjectKey, Long> xrefTable =
            new HashMap<COSObjectKey, Long>();

    /**
     * List containing all streams which are created when creating a new pdf.
     */
    private final List<COSStream> streams = new ArrayList<COSStream>();

    /**
     * Document trailer dictionary.
     */
    private COSDictionary trailer;

    private boolean warnMissingClose = true;

    /**
     * Signal that document is already decrypted.
     */
    private boolean isDecrypted = false;

    private long startXref;

    private boolean closed = false;

    private boolean isXRefStream;

    private ScratchFile scratchFile;

    /**
     * Used for incremental saving, to avoid XRef object numbers from being reused.
     */
    private long highestXRefObjectNumber;

    /**
     * Constructor. Uses main memory to buffer PDF streams.
     */
    public COSDocument()
    {
        this(ScratchFile.getMainMemoryOnlyInstance());
    }

    /**
     * Constructor that will use the provide memory handler for storage of the
     * PDF streams.
     *
     * @param scratchFile memory handler for buffering of PDF streams
     *
     */
    public COSDocument(ScratchFile scratchFile)
    {
        this.scratchFile = scratchFile;
    }

    /**
     * Creates a new COSStream using the current configuration for scratch files.
     *
     * @return the new COSStream
     */
    public COSStream createCOSStream()
    {
        COSStream stream = new COSStream(scratchFile);
        // collect all COSStreams so that they can be closed when closing the COSDocument.
        // This is limited to newly created pdfs as all COSStreams of an existing pdf are
        // collected within the map objectPool
        streams.add(stream);
        return stream;
    }

    /**
     * Creates a new COSStream using the current configuration for scratch files.
     * Not for public use. Only COSParser should call this method.
     *
     * @param dictionary the corresponding dictionary
     * @return the new COSStream
     */
    public COSStream createCOSStream(COSDictionary dictionary)
    {
        COSStream stream = new COSStream(scratchFile);
        for (Map.Entry<COSName, COSBase> entry : dictionary.entrySet())
        {
            stream.setItem(entry.getKey(), entry.getValue());
        }
        return stream;
    }

    /**
     * This will set the header version of this PDF document.
     *
     * @param versionValue The version of the PDF document.
     */
    public void setVersion( float versionValue )
    {
        version = versionValue;
    }

    /**
     * This will get the version extracted from the header of this PDF document.
     *
     * @return The header version.
     */
    public float getVersion()
    {
        return version;
    }

    /**
     * Signals that the document is decrypted completely.
     */
    public void setDecrypted()
    {
        isDecrypted = true;
    }

    /**
     * Indicates if a encrypted pdf is already decrypted after parsing.
     *
     *  @return true indicates that the pdf is decrypted.
     */
    public boolean isDecrypted()
    {
        return isDecrypted;
    }

    /**
     * This will tell if this is an encrypted document.
     *
     * @return true If this document is encrypted.
     */
    public boolean isEncrypted()
    {
        boolean encrypted = false;
        if (trailer != null)
        {
            encrypted = trailer.getDictionaryObject(COSName.ENCRYPT) instanceof COSDictionary;
        }
        return encrypted;
    }

    /**
     * This will get the encryption dictionary if the document is encrypted or null if the document
     * is not encrypted.
     *
     * @return The encryption dictionary.
     */
    public COSDictionary getEncryptionDictionary()
    {
        return trailer.getCOSDictionary(COSName.ENCRYPT);
    }

    /**
     * This will set the encryption dictionary, this should only be called when
     * encrypting the document.
     *
     * @param encDictionary The encryption dictionary.
     */
    public void setEncryptionDictionary( COSDictionary encDictionary )
    {
        trailer.setItem( COSName.ENCRYPT, encDictionary );
    }

    /**
     * This will get the document ID.
     *
     * @return The document id.
     */
    public COSArray getDocumentID()
    {
        return getTrailer().getCOSArray(COSName.ID);
    }

    /**
     * This will set the document ID.
     *
     * @param id The document id.
     */
    public void setDocumentID( COSArray id )
    {
        getTrailer().setItem(COSName.ID, id);
    }

    /**
     * This will get the document trailer.
     *
     * @return the document trailer dict
     */
    public COSDictionary getTrailer()
    {
        return trailer;
    }

    /**
     * // MIT added, maybe this should not be supported as trailer is a persistence construct.
     * This will set the document trailer.
     *
     * @param newTrailer the document trailer dictionary
     */
    public void setTrailer(COSDictionary newTrailer)
    {
        trailer = newTrailer;
    }

    /**
     * Internal PDFBox use only. Get the object number of the highest XRef stream. This is needed to
     * avoid reusing such a number in incremental saving.
     *
     * @return The object number of the highest XRef stream, or 0 if there was no XRef stream.
     */
    public long getHighestXRefObjectNumber()
    {
        return highestXRefObjectNumber;
    }

    /**
     * Internal PDFBox use only. Sets the object number of the highest XRef stream. This is needed
     * to avoid reusing such a number in incremental saving.
     *
     * @param highestXRefObjectNumber The object number of the highest XRef stream.
     */
    public void setHighestXRefObjectNumber(long highestXRefObjectNumber)
    {
        this.highestXRefObjectNumber = highestXRefObjectNumber;
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return any object, depending on the visitor implementation, or null
     * @throws IOException If an error occurs while visiting this object.
     */
    @Override
    public Object accept(ICOSVisitor visitor) throws IOException
    {
        return visitor.visitFromDocument( this );
    }

    /**
     * This will close all storage and delete the tmp files.
     *
     * @throws IOException If there is an error close resources.
     */
    @Override
    public void close() throws IOException
    {
        if (closed)
        {
            return;
        }

        // Make sure that:
        // - first Exception is kept
        // - all COSStreams are closed
        // - ScratchFile is closed
        // - there's a way to see which errors occurred

        IOException firstException = null;
        for (COSStream stream : streams)
        {
            firstException = IOUtils.closeAndLogException(stream, "COSStream", firstException);
        }
        if (scratchFile != null)
        {
            firstException = IOUtils.closeAndLogException(scratchFile, "ScratchFile", firstException);
        }
        closed = true;

        // rethrow first exception to keep method contract
        if (firstException != null)
        {
            throw firstException;
        }
    }

    /**
     * Returns true if this document has been closed.
     *
     * @return true if the document has been closed.
     */
    public boolean isClosed()
    {
        return closed;
    }

    /**
     * Warn the user in the finalizer if he didn't close the PDF document. The method also
     * closes the document just in case, to avoid abandoned temporary files. It's still a good
     * idea for the user to close the PDF document at the earliest possible to conserve resources.
     * @throws IOException if an error occurs while closing the temporary files
     */
    @Override
    protected void finalize() throws IOException
    {
        if (!closed)
        {
            if (warnMissingClose)
            {
                Log.w("PdfBox-Android", "Warning: You did not close a PDF Document" );
            }
            close();
        }
    }

    /**
     * This will get an object from the pool.
     *
     * @param key The object key.
     *
     * @return The object in the pool or a new one if it has not been parsed yet.
     *
     * @throws IOException If there is an error getting the proxy object.
     */
    public COSObject getObjectFromPool(COSObjectKey key) throws IOException
    {
        COSObject obj = null;
        if( key != null )
        {
            obj = objectPool.get(key);
        }
        if (obj == null)
        {
            // this was a forward reference, make "proxy" object
            obj = new COSObject(null);
            if( key != null )
            {
                obj.setObjectNumber(key.getNumber());
                obj.setGenerationNumber(key.getGeneration());
                objectPool.put(key, obj);
            }
        }
        return obj;
    }

    /**
     * Populate XRef HashMap with given values.
     * Each entry maps ObjectKeys to byte offsets in the file.
     * @param xrefTableValues  xref table entries to be added
     */
    public void addXRefTable( Map<COSObjectKey, Long> xrefTableValues )
    {
        xrefTable.putAll( xrefTableValues );
    }

    /**
     * Returns the xrefTable which is a mapping of ObjectKeys
     * to byte offsets in the file.
     * @return mapping of ObjectsKeys to byte offsets
     */
    public Map<COSObjectKey, Long> getXrefTable()
    {
        return xrefTable;
    }

    /**
     * This method set the startxref value of the document. This will only
     * be needed for incremental updates.
     *
     * @param startXrefValue the value for startXref
     */
    public void setStartXref(long startXrefValue)
    {
        startXref = startXrefValue;
    }

    /**
     * Sets isXRefStream to the given value. You need to take care that the version of your PDF is
     * 1.5 or higher.
     *
     * @param isXRefStreamValue the new value for isXRefStream
     */
    public void setIsXRefStream(boolean isXRefStreamValue)
    {
        isXRefStream = isXRefStreamValue;
    }
}
