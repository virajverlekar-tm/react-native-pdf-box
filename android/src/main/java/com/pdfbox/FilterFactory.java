package com.pdfbox;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory for Filter classes.
 */
public final class FilterFactory
{
    /**
     * Singleton instance.
     */
    public static final FilterFactory INSTANCE = new FilterFactory();

    private final Map<COSName, Filter> filters = new HashMap<COSName, Filter>();

    private FilterFactory()
    {
    }

    /**
     * Returns a filter instance given its name as a string.
     * @param filterName the name of the filter to retrieve
     * @return the filter that matches the name
     * @throws IOException if the filter name was invalid
     */
    public Filter getFilter(String filterName) throws IOException
    {
        return getFilter(COSName.getPDFName(filterName));
    }

    /**
     * Returns a filter instance given its COSName.
     * @param filterName the name of the filter to retrieve
     * @return the filter that matches the name
     * @throws IOException if the filter name was invalid
     */
    public Filter getFilter(COSName filterName) throws IOException
    {
        Filter filter = filters.get(filterName);
        if (filter == null)
        {
            throw new IOException("Invalid filter: " + filterName);
        }
        return filter;
    }

    // returns all available filters, for testing
    Collection<Filter> getAllFilters()
    {
        return filters.values();
    }
}
