package com.pdfbox;

public interface COSUpdateInfo
{
    /**
     * Get the update state for the COSWriter. This indicates whether an object is to be written
     * when there is an incremental save.
     *
     * @return the update state.
     */
    boolean isNeedToBeUpdated();

    /**
     * Set the update state of the dictionary for the COSWriter. This indicates whether an object is
     * to be written when there is an incremental save.
     *
     * @param flag the update state.
     */
    void setNeedToBeUpdated(boolean flag);

}