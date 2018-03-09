/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.utils;

import com.georgeinfo.base.util.database.DbHelper;
import com.georgeinfo.base.util.database.SQLState;
import java.sql.SQLException;

/**
 * 通用数据库助手接口实现类（JDBC）
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 */
public class DbHelperJdbcImpl implements DbHelper {

    @Override
    public boolean isDuplicateKeyException(Exception exception) {
        if (exception instanceof SQLException) {
            SQLException sqle = (SQLException) exception;
            if (sqle.getSQLState().equals(SQLState.DuplicateKey.getCode())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isEmptyResultException(Exception exception) {
        return false;
    }

}
