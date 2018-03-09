/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JDBC动作回调接口
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 */
public interface JdbcRowCallbackHandler {

    public void processRow(ResultSet rs) throws SQLException;
}
