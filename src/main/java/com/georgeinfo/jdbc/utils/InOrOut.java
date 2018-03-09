/*
 * Author: George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.utils;

/**
 *
 * @author George <Georgeinfo@163.com>
 */
public enum InOrOut {

    IN, OUT, INOUT;

    @Override
    public String toString() {
        return name();
    }
}
