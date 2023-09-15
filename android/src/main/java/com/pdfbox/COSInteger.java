package com.pdfbox;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class represents an integer number in a PDF document.
 */
public final class COSInteger extends COSNumber
{

    /**
     * The lowest integer to be kept in the {@link #STATIC} array.
     */
    private static final int LOW = -100;

    /**
     * The highest integer to be kept in the {@link #STATIC} array.
     */
    private static final int HIGH = 256;

    /**
     * Static instances of all COSIntegers in the range from {@link #LOW}
     * to {@link #HIGH}.
     */
    private static final COSInteger[] STATIC = new COSInteger[HIGH - LOW + 1];

    /**
     * Constant for the number zero.
     * @since Apache PDFBox 1.1.0
     */
    public static final COSInteger ZERO = get(0);

    /**
     * Constant for the number one.
     * @since Apache PDFBox 1.1.0
     */
    public static final COSInteger ONE = get(1);

    /**
     * Constant for the number two.
     * @since Apache PDFBox 1.1.0
     */
    public static final COSInteger TWO = get(2);

    /**
     * Constant for the number three.
     * @since Apache PDFBox 1.1.0
     */
    public static final COSInteger THREE = get(3);

    /**
     * Constant for an out of range value which is bigger than Log.MAX_VALUE.
     */
    protected static final COSInteger OUT_OF_RANGE_MAX = getInvalid(true);

    /**
     * Constant for an out of range value which is smaller than Log.MIN_VALUE.
     */
    protected static final COSInteger OUT_OF_RANGE_MIN = getInvalid(false);

    /**
     * Returns a COSInteger instance with the given value.
     *
     * @param val integer value
     * @return COSInteger instance
     */
    public static COSInteger get(long val)
    {
        if (LOW <= val && val <= HIGH)
        {
            int index = (int) val - LOW;
            // no synchronization needed
            if (STATIC[index] == null)
            {
                STATIC[index] = new COSInteger(val, true);
            }
            return STATIC[index];
        }
        return new COSInteger(val, true);
    }

    private static COSInteger getInvalid(boolean maxValue)
    {
        return maxValue ? new COSInteger(Long.MAX_VALUE, false)
                : new COSInteger(Long.MIN_VALUE, false);
    }

    private final long value;
    private final boolean isValid;

    /**
     * constructor.
     *
     * @param val The integer value of this object.
     */
    private COSInteger(long val, boolean valid)
    {
        value = val;
        isValid = valid;
    }

    /**
     * polymorphic access to value as float.
     *
     * @return The float value of this object.
     */
    @Override
    public float floatValue()
    {
        return value;
    }

    /**
     * polymorphic access to value as float.
     *
     * @return The double value of this object.
     *
     * @deprecated will be removed in a future release
     */
    @Override
    public double doubleValue()
    {
        return value;
    }

    /**
     * Polymorphic access to value as int
     * This will get the integer value of this object.
     *
     * @return The int value of this object,
     */
    @Override
    public int intValue()
    {
        return (int)value;
    }

    /**
     * Polymorphic access to value as int
     * This will get the integer value of this object.
     *
     * @return The int value of this object,
     */
    @Override
    public long longValue()
    {
        return value;
    }

    /**
     * Indicates whether this instance represents a valid value.
     *
     * @return true if the value is valid
     */
    public boolean isValid()
    {
        return isValid;
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
        return visitor.visitFromInt(this);
    }
}
