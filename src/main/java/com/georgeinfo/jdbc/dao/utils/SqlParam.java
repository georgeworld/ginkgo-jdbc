/*
 * Programming by: George <GeorgeNiceWorld@gmail.com>
 * Copyright (C) George And George Companies to Work For, All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public class SqlParam extends HashMap<String, Object> {

    public SqlParam(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial capacity
     * and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public SqlParam(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public SqlParam() {
        super();
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the specified
     * <tt>Map</tt>. The <tt>HashMap</tt> is created with default load factor
     * (0.75) and an initial capacity sufficient to hold the mappings in the
     * specified <tt>Map</tt>.
     *
     * @param m the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     */
    public SqlParam(Map<? extends String, ? extends Object> m) {
        super(m);
    }

    public static SqlParam initParam(String key, Object value) {
        SqlParam sp = new SqlParam();
        sp.addParam(key, value);
        return sp;
    }

    public SqlParam addParam(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public SqlParam addParamIfNotNull(String key, Object value) {
        if (value != null) {
            this.put(key, value);
        }
        return this;
    }

    public SqlParam addStringParamIfNotNullNotEmpty(String key, Object value) {
        if (value != null) {
            if (!String.valueOf(value).isEmpty()) {
                this.put(key, value);
            }
        }
        return this;
    }
}
