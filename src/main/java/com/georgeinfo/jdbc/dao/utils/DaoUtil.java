/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.utils;

import com.georgeinfo.base.util.database.JdbcTypes;
import com.georgeinfo.jdbc.dao.helper.DataRow;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Dao工具类
 *
 * @author George <Georgeinfo@163.com>
 */
public class DaoUtil {

    /**
     * 将ResultSet转换成ArrayList<DataRow>对象
     *
     * @param rs jdbc ResultSet结果集对象
     * @return 结果集列表
     * @throws java.sql.SQLException
     */
    public static ArrayList<DataRow> convertToDataRowList(ResultSet rs) throws SQLException {
        ArrayList<DataRow> retList = new ArrayList<DataRow>();
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        while (rs.next()) {
            DataRow recordMap = new DataRow();
            for (int i = 1; i <= colCount; i++) {
                //如果使用meta.getColumnName(i)，则当SQL语句中有别名时，取得的还是字段名称，而不是别名
                //而使用meta.getColumnLabel(i)则可以实现：当SQL中有字段别名时，取到的是字段别名，没有别名时，取到的是字段名称
                String name = meta.getColumnLabel(i);
                //字段的JDBC TYPE类型，对应(java.sql.Types)类的定义
                int sqlType = meta.getColumnType(i);
                //字段的JDBC TYPE类型名称，如VARCHAR / INT / TINYINT等
                String columnTypeName = meta.getColumnTypeName(i);

                Object value = JdbcTypes.convertTypeOfResultSetField(rs, i, sqlType, columnTypeName);

                recordMap.put(name, value);
            }

            retList.add(recordMap);
        }

        return retList;
    }
}
