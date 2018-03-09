/*
 * A George software product.
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.utils.sql;

/**
 * DELETE SQL语句提示类
 *
 * @author George <Georgeinfo@163.com>
 */
public class Delete {

    public Delete DELETE_FROM(String tableName) {
        return this;
    }

    public Delete WHERE(String whereClause) {
        return this;
    }
}
