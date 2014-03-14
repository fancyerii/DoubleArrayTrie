
/*
 * Copyright (c) 1994, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package com.antbrains.datrie;

import java.io.ObjectStreamField;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
 

public class MyString{
    /** The value is used for character storage. */
    private final char value[];
     
    public MyString() {
        this.value = new char[0];
    }
 

    /**
     * Allocates a new {@code String} so that it represents the sequence of
     * characters currently contained in the character array argument. The
     * contents of the character array are copied; subsequent modification of
     * the character array does not affect the newly created string.
     *
     * @param  value
     *         The initial value of the string
     */
    public MyString(char value[]) {
        this.value = Arrays.copyOf(value, value.length);
    }
 

    /**
     * Returns the length of this string.
     * The length is equal to the number of <a href="Character.html#unicode">Unicode
     * code units</a> in the string.
     *
     * @return  the length of the sequence of characters represented by this
     *          object.
     */
    public int length() {
        return value.length;
    }

    /**
     * Returns <tt>true</tt> if, and only if, {@link #length()} is <tt>0</tt>.
     *
     * @return <tt>true</tt> if {@link #length()} is <tt>0</tt>, otherwise
     * <tt>false</tt>
     *
     * @since 1.6
     */
    public boolean isEmpty() {
        return value.length == 0;
    }
  

    /**
     * Returns a hash code for this string. The hash code for a
     * <code>String</code> object is computed as
     * <blockquote><pre>
     * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
     * </pre></blockquote>
     * using <code>int</code> arithmetic, where <code>s[i]</code> is the
     * <i>i</i>th character of the string, <code>n</code> is the length of
     * the string, and <code>^</code> indicates exponentiation.
     * (The hash value of the empty string is zero.)
     *
     * @return  a hash code value for this object.
     */
    public int hashCode() {
        int h = 0;
        if (value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
            }
        }
        return h;
    }
    

}
