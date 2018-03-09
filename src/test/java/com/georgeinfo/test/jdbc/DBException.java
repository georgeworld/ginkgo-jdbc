/*
 * Author: George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.test.jdbc;

/**
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public class DBException extends RuntimeException {

    public DBException() {
        super();
    }

    public DBException(String string) {
        super(string);
    }

    public DBException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public DBException(Throwable thrwbl) {
        super(thrwbl);
    }
}
