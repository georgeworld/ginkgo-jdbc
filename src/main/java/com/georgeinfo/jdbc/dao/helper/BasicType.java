/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 常用Java类型定义<br/>
 * 枚举元素后缀解释：p=primitive(Java8大原始类型)；w=wrap(Java8大原始类型对应的包装类型)；b=basic(常用的Java基本类型)
 *
 * @author George <Georgeinfo@163.com>
 */
public enum BasicType {

    //Java原始类型开始
    INT_PRIMITIVE(int.class),
    LONG_PRIMITIVE(long.class),
    FLOAT_PRIMITIVE(float.class),
    DOUBLE_PRIMITIVE(double.class),
    BOOLEAN_PRIMITIVE(boolean.class),
    CHAR_PRIMITIVE(char.class),
    SHORT_PRIMITIVE(short.class),
    BYTE_PRIMITIVE(byte.class),
    //Java原始类型的包装类型开始
    INTEGER_WRAP(Integer.class),
    LONG_WRAP(Long.class),
    FLOAT_WRAP(Float.class),
    DOUBLE_WRAP(Double.class),
    BOOLEAN_WRAP(Boolean.class),
    CHARACTER_WRAP(Character.class),
    SHORT_WRAP(Short.class),
    BYTE_WRAP(Byte.class),
    //常用基本类型开始
    STRING_BASIC(String.class),
    DATE_BASIC(Date.class),
    BIGDECIMAL_BASIC(BigDecimal.class),
    BIGINTEGER_BASIC(BigInteger.class),
    BYTE_ARRAY_BASIC(byte[].class),
    SQL_DATE_BASIC(java.sql.Date.class),
    SQL_TIME_BASIC(Time.class),
    SQL_TIMESTAMP_BASIC(Timestamp.class),
    SQL_BLOB_BASIC(Blob.class),
    SQL_CLOB_BASIC(Clob.class);

    private final Class typeCLass;

    private BasicType(Class typeCLass) {
        this.typeCLass = typeCLass;
    }

    public Class getTypeClass() {
        return typeCLass;
    }

}
