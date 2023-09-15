package com.pdfbox;


/**
 *
 * Represents the necessary information to decrypt a document protected by
 * the standard security handler (password protection).
 *
 * This is only composed of a password.
 */

public class StandardDecryptionMaterial extends DecryptionMaterial
{

    private final String password;

    /**
     * Create a new standard decryption material with the given password.
     *
     * @param pwd The password.
     */
    public StandardDecryptionMaterial(String pwd)
    {
        password = pwd;
    }

    /**
     * Returns the password.
     *
     * @return The password used to decrypt the document.
     */
    public String getPassword()
    {
        return password;
    }

}
