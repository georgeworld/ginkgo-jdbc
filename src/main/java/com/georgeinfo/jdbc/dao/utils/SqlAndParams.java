/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * JDBC 预查询SQL语句与sql参数值（有序）的包装类
 *
 * @author George <Georgeinfo@163.com>
 */
public class SqlAndParams implements java.io.Serializable {

    private String sql;
    private ArrayList params;
    private HashMap<String, Integer> paramIndexMap;

    public SqlAndParams() {
    }

    public SqlAndParams(String sql, ArrayList params, HashMap<String, Integer> paramIndexMap) {
        this.sql = sql;
        this.params = params;
        this.paramIndexMap = paramIndexMap;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public ArrayList getParams() {
        return params;
    }

    public void setParams(ArrayList params) {
        this.params = params;
    }

    public HashMap<String, Integer> getParamIndexMap() {
        return paramIndexMap;
    }

    public void setParamIndexMap(HashMap<String, Integer> paramIndexMap) {
        this.paramIndexMap = paramIndexMap;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.sql != null ? this.sql.hashCode() : 0);
        hash = 29 * hash + (this.params != null ? this.params.hashCode() : 0);
        hash = 29 * hash + (this.paramIndexMap != null ? this.paramIndexMap.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SqlAndParams other = (SqlAndParams) obj;
        if ((this.sql == null) ? (other.sql != null) : !this.sql.equals(other.sql)) {
            return false;
        }
        if (this.params != other.params && (this.params == null || !this.params.equals(other.params))) {
            return false;
        }
        if (this.paramIndexMap != other.paramIndexMap && (this.paramIndexMap == null || !this.paramIndexMap.equals(other.paramIndexMap))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SqlAndParams{" + "sql=" + sql + ", params=" + params + ", paramIndexMap=" + paramIndexMap + '}';
    }
}
