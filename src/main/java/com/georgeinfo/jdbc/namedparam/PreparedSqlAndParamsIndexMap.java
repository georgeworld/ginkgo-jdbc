/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.namedparam;

import java.util.HashMap;

/**
 * 预处理SQL以及SQL语句的参数与参数索引值的map
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 */
public class PreparedSqlAndParamsIndexMap implements java.io.Serializable {

    private String preparedSql;
    private HashMap<String, Integer> paramIndexMap;

    public PreparedSqlAndParamsIndexMap() {
    }

    public PreparedSqlAndParamsIndexMap(String preparedSql, HashMap<String, Integer> paramIndexMap) {
        this.preparedSql = preparedSql;
        this.paramIndexMap = paramIndexMap;
    }

    public String getPreparedSql() {
        return preparedSql;
    }

    public void setPreparedSql(String preparedSql) {
        this.preparedSql = preparedSql;
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
        hash = 71 * hash + (this.preparedSql != null ? this.preparedSql.hashCode() : 0);
        hash = 71 * hash + (this.paramIndexMap != null ? this.paramIndexMap.hashCode() : 0);
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
        final PreparedSqlAndParamsIndexMap other = (PreparedSqlAndParamsIndexMap) obj;
        if ((this.preparedSql == null) ? (other.preparedSql != null) : !this.preparedSql.equals(other.preparedSql)) {
            return false;
        }
        if (this.paramIndexMap != other.paramIndexMap && (this.paramIndexMap == null || !this.paramIndexMap.equals(other.paramIndexMap))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PreparedSqlAndParamsIndexMap{" + "preparedSql=" + preparedSql + ", paramIndexMap=" + paramIndexMap + '}';
    }
}
