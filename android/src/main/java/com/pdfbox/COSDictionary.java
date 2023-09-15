package com.pdfbox;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents a dictionary where name/value pairs reside.
 */
public class COSDictionary extends COSBase implements COSUpdateInfo
{
    private static final String PATH_SEPARATOR = "/";
    private static final int MAP_THRESHOLD = 1000;
    private boolean needToBeUpdated;

    /**
     * The name-value pairs of this dictionary. The pairs are kept in the order they were added to the dictionary.
     */
    protected Map<COSName, COSBase> items = new SmallMap<COSName, COSBase>();

    /**
     * Constructor.
     */
    public COSDictionary()
    {
        // default constructor
    }

    /**
     * This will clear all items in the map.
     */
    public void clear()
    {
        items.clear();
    }

    /**
     * This will get an object from this dictionary. If the object is a reference then it will dereference it and get it
     * from the document. If the object is COSNull then null will be returned.
     *
     * @param key The key to the object that we are getting.
     *
     * @return The object that matches the key.
     */
    public COSBase getDictionaryObject(String key)
    {
        return getDictionaryObject(COSName.getPDFName(key));
    }

    /**
     * This is a special case of getDictionaryObject that takes multiple keys, it will handle the situation where
     * multiple keys could get the same value, ie if either CS or ColorSpace is used to get the colorspace. This will
     * get an object from this dictionary. If the object is a reference then it will dereference it and get it from the
     * document. If the object is COSNull then null will be returned.
     *
     * @param firstKey The first key to try.
     * @param secondKey The second key to try.
     *
     * @return The object that matches the key.
     */
    public COSBase getDictionaryObject(COSName firstKey, COSName secondKey)
    {
        COSBase retval = getDictionaryObject(firstKey);
        if (retval == null && secondKey != null)
        {
            retval = getDictionaryObject(secondKey);
        }
        return retval;
    }

    /**
     * This is a special case of getDictionaryObject that takes multiple keys, it will handle the situation where
     * multiple keys could get the same value, ie if either CS or ColorSpace is used to get the colorspace. This will
     * get an object from this dictionary. If the object is a reference then it will dereference it and get it from the
     * document. If the object is COSNull then null will be returned.
     *
     * @param keyList The list of keys to find a value.
     *
     * @return The object that matches the key.
     *
     * @deprecated Will be removed in 3.0. A value may have to keys, the regular one and sometimes an additional
     * abbreviation. More than 2 values doesn't make sense.
     */
    public COSBase getDictionaryObject(String[] keyList)
    {
        COSBase retval = null;
        for (int i = 0; i < keyList.length && retval == null; i++)
        {
            retval = getDictionaryObject(COSName.getPDFName(keyList[i]));
        }
        return retval;
    }

    /**
     * This will get an object from this dictionary. If the object is a reference then it will dereference it and get it
     * from the document. If the object is COSNull then null will be returned.
     *
     * @param key The key to the object that we are getting.
     *
     * @return The object that matches the key.
     */
    public COSBase getDictionaryObject(COSName key)
    {
        COSBase retval = items.get(key);
        if (retval instanceof COSObject)
        {
            retval = ((COSObject) retval).getObject();
        }
        if (retval instanceof COSNull)
        {
            retval = null;
        }
        return retval;
    }

    /**
     * This will set an item in the dictionary. If value is null then the result will be the same as removeItem( key ).
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem(COSName key, COSBase value)
    {
        if (value == null)
        {
            removeItem(key);
        }
        else
        {
            if (items instanceof SmallMap && items.size() >= MAP_THRESHOLD)
            {
                items = new LinkedHashMap<COSName, COSBase>(items);
            }
            items.put(key, value);
        }
    }

    /**
     * This will set an item in the dictionary. If value is null then the result will be the same as removeItem( key ).
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem(COSName key, COSObjectable value)
    {
        COSBase base = null;
        if (value != null)
        {
            base = value.getCOSObject();
        }
        setItem(key, base);
    }

    /**
     * This will set an item in the dictionary. If value is null then the result will be the same as removeItem( key ).
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem(String key, COSObjectable value)
    {
        setItem(COSName.getPDFName(key), value);
    }

    /**
     * This will set an item in the dictionary.
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setBoolean(String key, boolean value)
    {
        setItem(COSName.getPDFName(key), COSBoolean.getBoolean(value));
    }

    /**
     * This will set an item in the dictionary.
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setBoolean(COSName key, boolean value)
    {
        setItem(key, COSBoolean.getBoolean(value));
    }

    /**
     * This will set an item in the dictionary. If value is null then the result will be the same as removeItem( key ).
     *
     * @param key The key to the dictionary object.
     * @param value The value to the dictionary object.
     */
    public void setItem(String key, COSBase value)
    {
        setItem(COSName.getPDFName(key), value);
    }

    /**
     * This is a convenience method that will convert the value to a COSName object. If it is null then the object will
     * be removed.
     *
     * @param key The key to the object,
     * @param value The string value for the name.
     */
    public void setName(String key, String value)
    {
        setName(COSName.getPDFName(key), value);
    }

    /**
     * This is a convenience method that will convert the value to a COSName object. If it is null then the object will
     * be removed.
     *
     * @param key The key to the object,
     * @param value The string value for the name.
     */
    public void setName(COSName key, String value)
    {
        COSName name = null;
        if (value != null)
        {
            name = COSName.getPDFName(value);
        }
        setItem(key, name);
    }

    /**
     * This is a convenience method that will convert the value to a COSInteger object.
     *
     * @param key The key to the object,
     * @param value The int value for the name.
     */
    public void setInt(String key, int value)
    {
        setInt(COSName.getPDFName(key), value);
    }

    /**
     * This is a convenience method that will convert the value to a COSInteger object.
     *
     * @param key The key to the object,
     * @param value The int value for the name.
     */
    public void setInt(COSName key, int value)
    {
        setItem(key, COSInteger.get(value));
    }

    /**
     * This is a convenience method that will convert the value to a COSInteger object.
     *
     * @param key The key to the object,
     * @param value The int value for the name.
     */
    public void setLong(String key, long value)
    {
        setLong(COSName.getPDFName(key), value);
    }

    /**
     * This is a convenience method that will convert the value to a COSInteger object.
     *
     * @param key The key to the object,
     * @param value The int value for the name.
     */
    public void setLong(COSName key, long value)
    {
        COSInteger intVal = COSInteger.get(value);
        setItem(key, intVal);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be a COSArray. Null is
     * returned if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The COSArray.
     */
    public COSArray getCOSArray(COSName key)
    {
        COSBase array = getDictionaryObject(key);
        if (array instanceof COSArray)
        {
            return (COSArray) array;
        }
        return null;
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be a name. Null is returned
     * if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The COS name.
     */
    public COSName getCOSName(COSName key)
    {
        COSBase name = getDictionaryObject(key);
        if (name instanceof COSName)
        {
            return (COSName) name;
        }
        return null;
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be a name and convert it to
     * a string. Null is returned if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The name converted to a string.
     */
    public String getNameAsString(String key)
    {
        return getNameAsString(COSName.getPDFName(key));
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be a name and convert it to
     * a string. Null is returned if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The name converted to a string.
     */
    public String getNameAsString(COSName key)
    {
        String retval = null;
        COSBase name = getDictionaryObject(key);
        if (name instanceof COSName)
        {
            retval = ((COSName) name).getName();
        }
        else if (name instanceof COSString)
        {
            retval = ((COSString) name).getString();
        }
        return retval;
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be a name and convert it to
     * a string.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The name converted to a string.
     */
    public String getNameAsString(String key, String defaultValue)
    {
        return getNameAsString(COSName.getPDFName(key), defaultValue);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be a name and convert it to
     * a string.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The name converted to a string.
     */
    public String getNameAsString(COSName key, String defaultValue)
    {
        String retval = getNameAsString(key);
        if (retval == null)
        {
            retval = defaultValue;
        }
        return retval;
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be a COSObject. Null is
     * returned if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The COSObject.
     */
    public COSObject getCOSObject(COSName key)
    {
        COSBase object = getItem(key);
        if (object instanceof COSObject)
        {
            return (COSObject) object;
        }
        return null;
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be a COSDictionary. Null is
     * returned if the entry does not exist in the dictionary.
     *
     * @param key The key to the item in the dictionary.
     * @return The COSDictionary.
     */
    public COSDictionary getCOSDictionary(COSName key)
    {
        COSBase dictionary = getDictionaryObject(key);
        if (dictionary instanceof COSDictionary)
        {
            return (COSDictionary) dictionary;
        }
        return null;
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an int. -1 is returned if
     * there is no value.
     *
     * @param key The key to the item in the dictionary.
     * @return The integer value.
     */
    public int getInt(String key)
    {
        return getInt(COSName.getPDFName(key), -1);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an int. -1 is returned if
     * there is no value.
     *
     * @param key The key to the item in the dictionary.
     * @return The integer value..
     */
    public int getInt(COSName key)
    {
        return getInt(key, -1);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an integer. If the
     * dictionary value is null then the default value will be returned.
     *
     * @param keyList The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The integer value.
     *
     * @deprecated Will be removed in 3.0. A value may have to keys, the regular one and sometimes an additional
     * abbreviation. More than 2 values doesn't make sense.
     */
    public int getInt(String[] keyList, int defaultValue)
    {
        int retval = defaultValue;
        COSBase obj = getDictionaryObject(keyList);
        if (obj instanceof COSNumber)
        {
            retval = ((COSNumber) obj).intValue();
        }
        return retval;
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an integer. If the
     * dictionary value is null then the default value will be returned.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The integer value.
     */
    public int getInt(String key, int defaultValue)
    {
        return getInt(COSName.getPDFName(key), defaultValue);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an integer. If the
     * dictionary value is null then the default value will be returned.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The integer value.
     */
    public int getInt(COSName key, int defaultValue)
    {
        return getInt(key, null, defaultValue);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an integer. If the
     * dictionary value is null then the default value -1 will be returned.
     *
     * @param firstKey The first key to the item in the dictionary.
     * @param secondKey The second key to the item in the dictionary.
     * @return The integer value.
     */
    public int getInt(COSName firstKey, COSName secondKey)
    {
        return getInt(firstKey, secondKey, -1);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an integer. If the
     * dictionary value is null then the default value will be returned.
     *
     * @param firstKey The first key to the item in the dictionary.
     * @param secondKey The second key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The integer value.
     */
    public int getInt(COSName firstKey, COSName secondKey, int defaultValue)
    {
        int retval = defaultValue;
        COSBase obj = getDictionaryObject(firstKey, secondKey);
        if (obj instanceof COSNumber)
        {
            retval = ((COSNumber) obj).intValue();
        }
        return retval;
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an long. -1 is returned
     * if there is no value.
     *
     * @param key The key to the item in the dictionary.
     *
     * @return The long value.
     */
    public long getLong(String key)
    {
        return getLong(COSName.getPDFName(key), -1L);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an long. -1 is returned
     * if there is no value.
     *
     * @param key The key to the item in the dictionary.
     * @return The long value.
     */
    public long getLong(COSName key)
    {
        return getLong(key, -1L);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an long. If the
     * dictionary value is null then the default value will be returned.
     *
     * @param keyList The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The long value.
     *
     * @deprecated Will be removed in 3.0. A value may have to keys, the regular one and sometimes an additional
     * abbreviation. More than 2 values doesn't make sense.
     */
    public long getLong(String[] keyList, long defaultValue)
    {
        long retval = defaultValue;
        COSBase obj = getDictionaryObject(keyList);
        if (obj instanceof COSNumber)
        {
            retval = ((COSNumber) obj).longValue();
        }
        return retval;
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an integer. If the
     * dictionary value is null then the default value will be returned.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The integer value.
     */
    public long getLong(String key, long defaultValue)
    {
        return getLong(COSName.getPDFName(key), defaultValue);
    }

    /**
     * This is a convenience method that will get the dictionary object that is expected to be an integer. If the
     * dictionary value is null then the default value will be returned.
     *
     * @param key The key to the item in the dictionary.
     * @param defaultValue The value to return if the dictionary item is null.
     * @return The integer value.
     */
    public long getLong(COSName key, long defaultValue)
    {
        long retval = defaultValue;
        COSBase obj = getDictionaryObject(key);
        if (obj instanceof COSNumber)
        {
            retval = ((COSNumber) obj).longValue();
        }
        return retval;
    }

    /**
     * This will remove an item for the dictionary. This will do nothing of the object does not exist.
     *
     * @param key The key to the item to remove from the dictionary.
     */
    public void removeItem(COSName key)
    {
        items.remove(key);
    }

    /**
     * This will do a lookup into the dictionary.
     *
     * @param key The key to the object.
     *
     * @return The item that matches the key.
     */
    public COSBase getItem(COSName key)
    {
        return items.get(key);
    }

    /**
     * This will do a lookup into the dictionary.
     *
     * @param key The key to the object.
     *
     * @return The item that matches the key.
     */
    public COSBase getItem(String key)
    {
        return getItem(COSName.getPDFName(key));
    }

    /**
     * This is a special case of getItem that takes multiple keys, it will handle the situation
     * where multiple keys could get the same value, ie if either CS or ColorSpace is used to get
     * the colorspace. This will get an object from this dictionary.
     *
     * @param firstKey The first key to try.
     * @param secondKey The second key to try.
     *
     * @return The object that matches the key.
     */
    public COSBase getItem(COSName firstKey, COSName secondKey)
    {
        COSBase retval = getItem(firstKey);
        if (retval == null && secondKey != null)
        {
            retval = getItem(secondKey);
        }
        return retval;
    }

    /**
     * Returns the name-value entries in this dictionary. The returned set is in the order the entries were added to the
     * dictionary.
     *
     * @since Apache PDFBox 1.1.0
     * @return name-value entries in this dictionary
     */
    public Set<Map.Entry<COSName, COSBase>> entrySet()
    {
        return items.entrySet();
    }

    /**
     * This will get all of the values for the dictionary.
     *
     * @return All the values for the dictionary.
     */
    public Collection<COSBase> getValues()
    {
        return items.values();
    }

    /**
     * visitor pattern double dispatch method.
     *
     * @param visitor The object to notify when visiting this object.
     * @return The object that the visitor returns.
     *
     * @throws IOException If there is an error visiting this object.
     */
    @Override
    public Object accept(ICOSVisitor visitor) throws IOException
    {
        return visitor.visitFromDictionary(this);
    }

    @Override
    public boolean isNeedToBeUpdated()
    {
        return needToBeUpdated;
    }

    @Override
    public void setNeedToBeUpdated(boolean flag)
    {
        needToBeUpdated = flag;
    }

    /**
     * This will add all of the dictionaries keys/values to this dictionary. Existing key/value pairs will be
     * overwritten.
     *
     * @param dict The dictionaries to get the key/value pairs from.
     */
    public void addAll(COSDictionary dict)
    {
        if (items instanceof SmallMap && items.size() + dict.items.size() >= MAP_THRESHOLD)
        {
            items = new LinkedHashMap<COSName, COSBase>(items);
        }
        items.putAll(dict.items);
    }

    /**
     * @see java.util.Map#containsKey(Object)
     *
     * @param name The key to find in the map.
     * @return true if the map contains this key.
     */
    public boolean containsKey(COSName name)
    {
        return this.items.containsKey(name);
    }

    /**
     * @see java.util.Map#containsKey(Object)
     *
     * @param name The key to find in the map.
     * @return true if the map contains this key.
     */
    public boolean containsKey(String name)
    {
        return containsKey(COSName.getPDFName(name));
    }
}
