/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.test.jdbc;

import com.georgeinfo.jdbc.dao.impl.BlockBatchDaoImpl;

/**
 * 纯JDBC dao
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 */
public class BDao extends  BlockBatchDaoImpl{

    public BDao() {
        super(DBConnectionManager.getDataSource());
    }

}
