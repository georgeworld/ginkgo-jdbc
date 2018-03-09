/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.mapper.exception;

/**
 * 数据记录集映射模块相关异常
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public class MapperException extends RuntimeException {

    public MapperException() {
    }

    public MapperException(String string) {
        super(string);
    }

    public MapperException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public MapperException(Throwable thrwbl) {
        super(thrwbl);
    }
}
