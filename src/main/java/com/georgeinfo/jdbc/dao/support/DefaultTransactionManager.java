/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.support;

import com.georgeinfo.jdbc.dao.utils.DaoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;

/**
 * 自带事务管理器的Dao接口
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 */
public interface DefaultTransactionManager {

    /**
     * 得到当前Connection对象
     *
     * @return 当前Connection对象
     */
    public Connection getCurrentConn();

    public void begin() throws SQLException;

    public void commit() throws SQLException;

    public void beginSavepoint(String savepointKey) throws SQLException;

    public void rollbackToSavepoint(String savepointKey) throws DaoException;

    public Savepoint createSavepoint() throws SQLException;

    public void rollbackToSavepoint(Savepoint savepoint) throws DaoException;

    /**
     * 释放一个保存点，如果不需要某个保存点了，就去掉它。
     *
     * @param savepoint 将被释放的保存点
     */
    public void releaseSavepoint(Savepoint savepoint) throws DaoException;

    public void rollback() throws DaoException;

    public void end();
}
