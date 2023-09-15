package com.pdfbox;

/**
 * This class is a specialized view of the crypt filter dictionary of a PDF document.
 * It contains a low level dictionary (COSDictionary) and provides the methods to
 * manage its fields.
 *
 */
public class PDCryptFilterDictionary implements COSObjectable
{

    /**
     * COS crypt filter dictionary.
     */
    protected COSDictionary cryptFilterDictionary = null;

    /**
     * creates a new empty crypt filter dictionary.
     */
    public PDCryptFilterDictionary()
    {
        cryptFilterDictionary = new COSDictionary();
    }

    /**
     * creates a new crypt filter dictionary from the low level dictionary provided.
     * @param d the low level dictionary that will be managed by the newly created object
     */
    public PDCryptFilterDictionary(COSDictionary d)
    {
        cryptFilterDictionary = d;
    }

    /**
     * This will get the dictionary associated with this crypt filter dictionary.
     *
     * @return The COS dictionary that this object wraps.
     * @deprecated use {@link #getCOSObject()}
     */
    @Deprecated
    public COSDictionary getCOSDictionary()
    {
        return cryptFilterDictionary;
    }

    /**
     * This will get the dictionary associated with this crypt filter dictionary.
     *
     * @return The COS dictionary that this object wraps.
     */
    @Override
    public COSDictionary getCOSObject()
    {
        return cryptFilterDictionary;
    }

    /**
     * This will set the number of bits to use for the crypt filter algorithm.
     *
     * @param length The new key length.
     */
    public void setLength(int length)
    {
        cryptFilterDictionary.setInt(COSName.LENGTH, length);
    }

    /**
     * This will return the Length entry of the crypt filter dictionary.<br><br>
     * The length in <b>bits</b> for the crypt filter algorithm. This will return a multiple of 8.
     *
     * @return The length in bits for the encryption algorithm
     */
    public int getLength()
    {
        return cryptFilterDictionary.getInt( COSName.LENGTH, 40 );
    }

    /**
     * This will set the crypt filter method.
     * Allowed values are: NONE, V2, AESV2, AESV3
     *
     * @param cfm name of the crypt filter method.
     *
     */
    public void setCryptFilterMethod(COSName cfm)
    {
        cryptFilterDictionary.setItem( COSName.CFM, cfm );
    }

    /**
     * This will return the crypt filter method.
     * Allowed values are: NONE, V2, AESV2, AESV3
     *
     * @return the name of the crypt filter method.
     */
    public COSName getCryptFilterMethod()
    {
        return (COSName)cryptFilterDictionary.getDictionaryObject( COSName.CFM );
    }

    /**
     * Will get the EncryptMetaData dictionary info.
     *
     * @return true if EncryptMetaData is explicitly set (the default is true)
     */
    public boolean isEncryptMetaData()
    {
        COSBase value = getCOSObject().getDictionaryObject(COSName.ENCRYPT_META_DATA);
        if (value instanceof COSBoolean)
        {
            return ((COSBoolean) value).getValue();
        }

        // default is true (see 7.6.3.2 Standard Encryption Dictionary PDF 32000-1:2008)
        return true;
    }
}
