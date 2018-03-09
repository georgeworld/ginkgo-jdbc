/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import com.georgeinfo.jdbc.dao.utils.FieldValue;
import com.georgeinfo.jdbc.utils.JdbcTypeConverter;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据行
 *
 * @author George <Georgeinfo@163.com>
 */
public class DataRow extends HashMap<String, Object> {

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial capacity
     * and load factor.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor the load factor
     * @throws IllegalArgumentException if the initial capacity is negative or
     * the load factor is nonpositive
     */
    public DataRow(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial capacity
     * and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public DataRow(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public DataRow() {
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
    public DataRow(Map<? extends String, ? extends Object> m) {
        super(m);
    }

    public FieldValue getValue(String key) {
        Object v = super.get(key);
        if (v != null) {
            return new FieldValue(v);
        } else {
            return null;
        }
    }

    public Integer getInteger(String key) {
        Object v = super.get(key);
        return JdbcTypeConverter.toInteger(v);
    }

    public Long getLong(String key) {
        Object v = super.get(key);
        return JdbcTypeConverter.toLong(v);
    }

    public Float getFloat(String key) {
        Object v = super.get(key);
        return JdbcTypeConverter.toFloat(v);
    }

    public Double getDouble(String key) {
        Object v = super.get(key);
        return JdbcTypeConverter.toDouble(v);
    }

    public Short getShort(String key) {
        Object v = super.get(key);
        return JdbcTypeConverter.toShort(v);
    }

    public String getString(String key) {
        Object v = super.get(key);
        return JdbcTypeConverter.toString(v);
    }

    public Date getDate(String key) {
        Object v = super.get(key);
        return JdbcTypeConverter.toDate(v);
    }

    public Object getObject(String key) {
        return super.get(key);
    }
}
