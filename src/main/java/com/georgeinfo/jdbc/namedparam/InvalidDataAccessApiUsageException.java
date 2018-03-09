/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.namedparam;

/**
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public class InvalidDataAccessApiUsageException extends RuntimeException {

    public InvalidDataAccessApiUsageException() {
    }

    public InvalidDataAccessApiUsageException(String string) {
        super(string);
    }

    public InvalidDataAccessApiUsageException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

    public InvalidDataAccessApiUsageException(Throwable thrwbl) {
        super(thrwbl);
    }
}
