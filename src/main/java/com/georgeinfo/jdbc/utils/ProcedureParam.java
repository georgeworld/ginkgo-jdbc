/*
 * Author: George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.utils;

/**
 *
 * @author George <Georgeinfo@163.com>
 */
public class ProcedureParam implements java.io.Serializable {

    private InOrOut inOrOut;
    private String name;
    private Object value;
    private int sqlType;

    public ProcedureParam() {
    }

    public ProcedureParam(InOrOut inOrOut, String name, Object value, int sqlType) {
        this.inOrOut = inOrOut;
        this.name = name;
        this.value = value;
        this.sqlType = sqlType;
    }

    public InOrOut getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(InOrOut inOrOut) {
        this.inOrOut = inOrOut;
    }

    public boolean isInParam() {
        return (inOrOut == InOrOut.IN);
    }

    public boolean isOutParam() {
        return (inOrOut == InOrOut.OUT);
    }

    public boolean isInOutParam() {
        return (inOrOut == InOrOut.INOUT);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getSqlType() {
        return sqlType;
    }

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + (this.inOrOut != null ? this.inOrOut.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
        hash = 97 * hash + this.sqlType;
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
        final ProcedureParam other = (ProcedureParam) obj;
        if (this.inOrOut != other.inOrOut) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
            return false;
        }
        if (this.sqlType != other.sqlType) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProcedureParam{" + "inOrOut=" + inOrOut + ", name=" + name + ", value=" + value + ", sqlType=" + sqlType + '}';
    }
}
