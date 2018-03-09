/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.api;

import com.georgeinfo.jdbc.dao.helper.BatchCallback;
import com.georgeinfo.jdbc.dao.utils.DaoException;

/**
 * 批量操作Dao
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 */
public interface BlockBatchDao extends BlockDao {
     /**
     * 批量执行
     *
     * @param call 回调接口抽象类
     */
    public void executeBatch(BatchCallback call) throws DaoException;
}
