/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.utils;

import com.georgeinfo.jdbc.utils.JdbcTypeConverter;
import java.sql.Date;

/**
 *
 * @author George <Georgeinfo@163.com>
 */
public class FieldValue {

    private final Object value;

    public FieldValue(Object value) {
        this.value = value;
    }

    public Integer toInteger() {
        return JdbcTypeConverter.toInteger(value);
    }

    public Long toLong() {
        return JdbcTypeConverter.toLong(value);
    }

    public Float toFloat() {
        return JdbcTypeConverter.toFloat(value);
    }

    public Double toDouble() {
        return JdbcTypeConverter.toDouble(value);
    }

    public Short toShort() {
        return JdbcTypeConverter.toShort(value);
    }

    @Override
    public String toString() {
        return JdbcTypeConverter.toString(value);
    }

    public Date toDate() {
        return JdbcTypeConverter.toDate(value);
    }

    public Object toObject() {
        return value;
    }
}
