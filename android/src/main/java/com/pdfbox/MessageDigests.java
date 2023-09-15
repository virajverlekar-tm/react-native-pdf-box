package com.pdfbox;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for creating MessageDigest instances.
 */
final class MessageDigests
{
    private MessageDigests()
    {
    }

    /**
     * @return MD5 message digest
     */
    static MessageDigest getMD5()
    {
        try
        {
            return MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            // should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * @return SHA-1 message digest
     */
    static MessageDigest getSHA1()
    {
        try
        {
            return MessageDigest.getInstance("SHA-1");
        }
        catch (NoSuchAlgorithmException e)
        {
            // should never happen
            throw new RuntimeException(e);
        }
    }

    /**
     * @return SHA-256 message digest
     */
    static MessageDigest getSHA256()
    {
        try
        {
            return MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e)
        {
            // should never happen
            throw new RuntimeException(e);
        }
    }
}
