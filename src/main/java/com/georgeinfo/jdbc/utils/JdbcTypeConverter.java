/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.utils;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Jdbc类型转换器
 *
 * @author George <Georgeinfo@163.com>
 */
public class JdbcTypeConverter {

    public static Integer toInteger(Object value) {
        if (value != null) {
            return Integer.valueOf(value.toString());
        } else {
            return null;
        }
    }

    public static Long toLong(Object value) {
        if (value != null) {
            return Long.valueOf(value.toString());
        } else {
            return null;
        }
    }

    public static Float toFloat(Object value) {
        if (value != null) {
            return Float.valueOf(value.toString());
        } else {
            return null;
        }
    }

    public static Double toDouble(Object value) {
        if (value != null) {
            return Double.valueOf(value.toString());
        } else {
            return null;
        }
    }

    public static Short toShort(Object value) {
        if (value != null) {
//            if (value instanceof String && (value.toString().equals("true") || value.toString().equals("false"))) {
//                return Short.valueOf(value.toString().equals("true") ? "1" : "0");
//            } else {
            return Short.valueOf(value.toString());
//            }
        } else {
            return null;
        }
    }

    public static String toString(Object value) {
        if (value != null) {
            return String.valueOf(value);
        } else {
            return null;
        }
    }

    public static Date toDate(Object value) {
        if (value != null) {
            Timestamp timestamp = (Timestamp) value;
            Date date = new Date(timestamp.getTime());
            return date;
        } else {
            return null;
        }
    }
}
