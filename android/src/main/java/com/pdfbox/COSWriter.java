package com.pdfbox;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * This class acts on a in-memory representation of a PDF document.
 */
public class COSWriter implements ICOSVisitor, Closeable
{
    // the stream where we create the pdf output
    private OutputStream output;

    // the stream used to write standard cos data
    private COSStandardOutputStream standardOutput;

    private PDDocument pdDocument = null;
    private boolean willEncrypt = false;

    /**
     * COSWriter constructor.
     *
     * @param outputStream The output stream to write the PDF. It will be closed when this object is
     * closed.
     */
    public COSWriter(OutputStream outputStream)
    {
        setOutput(outputStream);
        setStandardOutput(new COSStandardOutputStream(output));
    }

    /**
     * This will close the stream.
     *
     * @throws IOException If the underlying stream throws an exception.
     */
    @Override
    public void close() throws IOException
    {
        if (getStandardOutput() != null)
        {
            getStandardOutput().close();
        }
    }

    /**
     * This will get the standard output stream.
     *
     * @return The standard output stream.
     */
    protected COSStandardOutputStream getStandardOutput()
    {
        return standardOutput;
    }

    /**
     * This will set the output stream.
     *
     * @param newOutput The new output stream.
     */
    private void setOutput( OutputStream newOutput )
    {
        output = newOutput;
    }

    /**
     * This will set the standard output stream.
     *
     * @param newStandardOutput The new standard output stream.
     */
    private void setStandardOutput(COSStandardOutputStream newStandardOutput)
    {
        standardOutput = newStandardOutput;
    }

    @Override
    public Object visitFromArray( COSArray obj ) throws IOException
    {
        return null;
    }

    @Override
    public Object visitFromBoolean(COSBoolean obj) throws IOException
    {
        return null;
    }

    @Override
    public Object visitFromDictionary(COSDictionary obj) throws IOException
    {
        return null;
    }

    @Override
    public Object visitFromDocument(COSDocument doc) throws IOException
    {
        return null;
    }

    @Override
    public Object visitFromFloat(COSFloat obj) throws IOException
    {
        return null;
    }

    @Override
    public Object visitFromInt(COSInteger obj) throws IOException
    {
        return null;
    }

    @Override
    public Object visitFromName(COSName obj) throws IOException
    {
        return null;
    }

    @Override
    public Object visitFromNull(COSNull obj) throws IOException
    {
        return null;
    }

    @Override
    public Object visitFromStream(COSStream obj) throws IOException
    {
        return null;
    }

    @Override
    public Object visitFromString(COSString obj) throws IOException
    {
        return null;
    }

    /**
     * This will write the pdf document.
     *
     * @throws IOException If an error occurs while generating the data.
     * @param doc The document to write.
     */
    public void write(COSDocument doc) throws IOException
    {
        PDDocument pdDoc = new PDDocument( doc );
        write( pdDoc );
    }

    /**
     * This will write the pdf document.
     *
     * @param doc The document to write.
     *
     * @throws IOException If an error occurs while generating the data.
     * @throws IllegalStateException If the document has an encryption dictionary but no protection
     * policy.
     */
    public void write(PDDocument doc) throws IOException
    {
        Long idTime = System.currentTimeMillis();

        pdDocument = doc;

        // if the document says we should remove encryption, then we shouldn't encrypt
        if(doc.isAllSecurityToBeRemoved())
        {
            willEncrypt = false;
            // also need to get rid of the "Encrypt" in the trailer so readers
            // don't try to decrypt a document which is not encrypted
            COSDocument cosDoc = doc.getDocument();
            COSDictionary trailer = cosDoc.getTrailer();
            trailer.removeItem(COSName.ENCRYPT);
        }

        COSDocument cosDoc = pdDocument.getDocument();
        COSDictionary trailer = cosDoc.getTrailer();
        COSArray idArray = null;
        boolean missingID = true;
        COSBase base = trailer.getDictionaryObject(COSName.ID);
        if (base instanceof COSArray)
        {
            idArray = (COSArray) base;
            if (idArray.size() == 2)
            {
                missingID = false;
            }
        }
        // check for an existing documentID
        if (idArray != null && idArray.size() == 2)
        {
            missingID = false;
        }
        if( missingID )
        {
            MessageDigest md5;
            try
            {
                md5 = MessageDigest.getInstance("MD5");
            }
            catch (NoSuchAlgorithmException e)
            {
                // should never happen
                throw new RuntimeException(e);
            }

            // algorithm says to use time/path/size/values in doc to generate the id.
            // we don't have path or size, so do the best we can
            md5.update( Long.toString(idTime).getBytes(Charsets.ISO_8859_1) );

            COSDictionary info = trailer.getCOSDictionary(COSName.INFO);
            if( info != null )
            {
                for (COSBase cosBase : info.getValues())
                {
                    md5.update(cosBase.toString().getBytes(Charsets.ISO_8859_1));
                }
            }
            // reuse origin documentID if available as first value
            COSString firstID = missingID ? new COSString( md5.digest() ) : (COSString)idArray.get(0);
            // it's ok to use the same ID for the second part if the ID is created for the first time
            COSString secondID = missingID ? firstID : new COSString( md5.digest() );
            idArray = new COSArray();
            idArray.add( firstID );
            idArray.add( secondID );
            trailer.setItem(COSName.ID, idArray);
        }
        cosDoc.accept(this);
    }
}
