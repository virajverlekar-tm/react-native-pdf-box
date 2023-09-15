package com.pdfbox;

/**
 * An interface to allow data to be stored completely in memory or
 * to use a scratch file on the disk.
 */
public interface RandomAccess extends RandomAccessRead, RandomAccessWrite
{
    // super interface for both read and write
}
