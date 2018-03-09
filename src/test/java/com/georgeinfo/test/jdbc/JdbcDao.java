/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.test.jdbc;

import com.georgeinfo.jdbc.dao.impl.ProtoTypeBatchDaoImpl;

/**
 * 常规JDBC Dao
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 */
public class JdbcDao extends ProtoTypeBatchDaoImpl {

    public JdbcDao() {
        super(DBConnectionManager.getDataSource());
    }

}
