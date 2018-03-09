/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.mapper.exception;

/**
 * 类型不匹配，或者字段索引值错误的异常
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public class TypeMismatchOrIndexInvalidException extends IllegalArgumentException {

    public TypeMismatchOrIndexInvalidException() {
    }

    public TypeMismatchOrIndexInvalidException(String string) {
        super(string);
    }

    public TypeMismatchOrIndexInvalidException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public TypeMismatchOrIndexInvalidException(Throwable thrwbl) {
        super(thrwbl);
    }
}
