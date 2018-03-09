/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.namedparam;

/**
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 */
public class SqlFragmentAndParamIndex implements java.io.Serializable {

    private Integer paramIndex;
    private String sqlFragment;

    public SqlFragmentAndParamIndex() {
    }

    public SqlFragmentAndParamIndex(Integer paramIndex, String sqlFragment) {
        this.paramIndex = paramIndex;
        this.sqlFragment = sqlFragment;
    }

    public Integer getParamIndex() {
        return paramIndex;
    }

    public void setParamIndex(Integer paramIndex) {
        this.paramIndex = paramIndex;
    }

    public String getSqlFragment() {
        return sqlFragment;
    }

    public void setSqlFragment(String sqlFragment) {
        this.sqlFragment = sqlFragment;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.paramIndex;
        hash = 41 * hash + (this.sqlFragment != null ? this.sqlFragment.hashCode() : 0);
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
        final SqlFragmentAndParamIndex other = (SqlFragmentAndParamIndex) obj;
        if (this.paramIndex != other.paramIndex) {
            return false;
        }
        if ((this.sqlFragment == null) ? (other.sqlFragment != null) : !this.sqlFragment.equals(other.sqlFragment)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SqlFragmentAndParamIndex{" + "paramIndex=" + paramIndex + ", sqlFragment=" + sqlFragment + '}';
    }
}
