/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.support;

import com.georgeinfo.jdbc.dao.helper.Callback;
import com.georgeinfo.jdbc.dao.utils.DaoException;

/**
 * 块状事务管理器
 *
 * @author George <Georgeinfo@163.com>
 */
public interface BlockTransactionManager {

    /**
     * 事物块:这个事物块中不能够在内部开启线程后在线程中执行数据库操作，那样会报错(connection is closed)
     *
     * @param call 回调接口抽象类
     */
    public void withTransaction(Callback call) throws DaoException;
}
