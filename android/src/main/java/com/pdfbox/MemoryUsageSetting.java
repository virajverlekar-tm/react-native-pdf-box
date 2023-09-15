package com.pdfbox;

import java.io.File;

/**
 * Controls how memory/temporary files are used for
 * buffering streams etc.
 */
public final class MemoryUsageSetting
{
    private final boolean useMainMemory;

    /** maximum number of main-memory bytes allowed to be used;
     *  <code>-1</code> means 'unrestricted' */
    private final long maxMainMemoryBytes;

    /** maximum number of bytes allowed for storage at all (main-memory+file);
     *  <code>-1</code> means 'unrestricted' */
    private final long maxStorageBytes;

    /** directory to be used for scratch file */
    private File tempDir;

    /**
     * Private constructor for setup buffering memory usage called by one of the setup methods.
     *
     * @param useMainMemory if <code>true</code> main memory usage is enabled;
     * @param maxMainMemoryBytes maximum number of main-memory to be used;
     *                           if <code>-1</code> means 'unrestricted';
     *                           otherwise main-memory usage will have restriction
     *                           defined by maxStorageBytes
     * @param maxStorageBytes maximum size the main-memory and temporary file(s) may have all together;
     *                        <code>0</code>  or less will be ignored; if it is less than
     *                        maxMainMemoryBytes we use maxMainMemoryBytes value instead
     */
    private MemoryUsageSetting(boolean useMainMemory,
                               long maxMainMemoryBytes, long maxStorageBytes)
    {
        // do some checks; adjust values as needed to get consistent setting
        boolean locUseMainMemory = useMainMemory;
        long    locMaxMainMemoryBytes = useMainMemory ? maxMainMemoryBytes : -1;
        long    locMaxStorageBytes = maxStorageBytes > 0 ? maxStorageBytes : -1;

        if (locMaxMainMemoryBytes < -1)
        {
            locMaxMainMemoryBytes = -1;
        }

        if (locUseMainMemory && (locMaxMainMemoryBytes == 0))
        {
            locMaxMainMemoryBytes = locMaxStorageBytes;
        }

        if (locUseMainMemory && (locMaxStorageBytes > -1) &&
                ((locMaxMainMemoryBytes == -1) || (locMaxMainMemoryBytes > locMaxStorageBytes)))
        {
            locMaxStorageBytes = locMaxMainMemoryBytes;
        }


        this.useMainMemory = locUseMainMemory;
        this.maxMainMemoryBytes = locMaxMainMemoryBytes;
        this.maxStorageBytes = locMaxStorageBytes;
    }

    /**
     * Setups buffering memory usage to only use main-memory (no temporary file)
     * which is not restricted in size.
     */
    public static MemoryUsageSetting setupMainMemoryOnly()
    {
        return setupMainMemoryOnly(-1);
    }

    /**
     * Setups buffering memory usage to only use main-memory with the defined maximum.
     *
     * @param maxMainMemoryBytes maximum number of main-memory to be used;
     *                           <code>-1</code> for no restriction;
     *                           <code>0</code> will also be interpreted here as no restriction
     */
    public static MemoryUsageSetting setupMainMemoryOnly(long maxMainMemoryBytes)
    {
        return new MemoryUsageSetting(true, maxMainMemoryBytes, maxMainMemoryBytes);
    }

    /**
     * Sets directory to be used for temporary files.
     *
     * @param tempDir directory for temporary files
     *
     * @return this instance
     */
    public MemoryUsageSetting setTempDir(File tempDir)
    {
        this.tempDir = tempDir;
        return this;
    }

    /**
     * Returns <code>true</code> if main-memory is to be used.
     */
    public boolean useMainMemory()
    {
        return useMainMemory;
    }

    /**
     * Returns <code>true</code> if maximum main memory is restricted to a specific
     * number of bytes.
     */
    public boolean isMainMemoryRestricted()
    {
        return maxMainMemoryBytes >= 0;
    }

    /**
     * Returns <code>true</code> if maximum amount of storage is restricted to a specific
     * number of bytes.
     */
    public boolean isStorageRestricted()
    {
        return maxStorageBytes > 0;
    }

    /**
     * Returns maximum size of main-memory in bytes to be used.
     */
    public long getMaxMainMemoryBytes()
    {
        return maxMainMemoryBytes;
    }

    /**
     * Returns maximum size of storage bytes to be used
     * (main-memory in temporary files all together).
     */
    public long getMaxStorageBytes()
    {
        return maxStorageBytes;
    }

    /**
     * Returns directory to be used for temporary files or <code>null</code>
     * if it was not set.
     */
    public File getTempDir()
    {
        return tempDir;
    }

    @Override
    public String toString()
    {
        return useMainMemory ?
                (isMainMemoryRestricted() ? "Main memory only with max. of " + maxMainMemoryBytes + " bytes" :
                        "Main memory only with no size restriction"):
                (isStorageRestricted() ? "Scratch file only with max. of " + maxStorageBytes + " bytes" :
                        "Scratch file only with no size restriction");
    }
}
