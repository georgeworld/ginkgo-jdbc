/*
 * A George software product.
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.utils.sql;

/**
 * SELECT查询SQL语句的提示类
 *
 * @author George <Georgeinfo@163.com>
 */
public class Select {

    public Select SELECT(String selectedFields) {
        return this;
    }

    public Select FROM(String tableName) {
        return this;
    }

    public Select INNER_JOIN(String innerJoinAndOn) {
        return this;
    }

    public Select WHERE(String whereClause) {
        return this;
    }

    public Select OR() {
        return this;
    }

    public Select GROUP_BY(String groupByField) {
        return this;
    }

    public Select HAVING(String havingSql) {
        return this;
    }

    public Select ORDER_BY(String orderByField) {
        return this;
    }
}
