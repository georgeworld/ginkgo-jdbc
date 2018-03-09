/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.utils;

/**
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public class DaoException extends RuntimeException {

    public DaoException() {
    }

    public DaoException(String string) {
        super(string);
    }

    public DaoException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public DaoException(Throwable thrwbl) {
        super(thrwbl);
    }
}
