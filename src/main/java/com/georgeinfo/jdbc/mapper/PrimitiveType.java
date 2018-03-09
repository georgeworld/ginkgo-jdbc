/*
* Copyright (c) George software studio, All Rights Reserved.
* George <georgeinfo@qq.com> | http://www.georgeinfo.com 
*/
package com.georgeinfo.jdbc.mapper;

/**
 * 基本类型（原始类型）包装类，不包括void/Void
 * @author George<GeorgeWorld@qq.com>
 */
public class PrimitiveType {
    private Class<?> wrapperClazz;
    private Class<?> primitiveClazz;

    public PrimitiveType() {
    }

    public PrimitiveType(Class<?> wrapperClazz, Class<?> primitiveClazz) {
        this.wrapperClazz = wrapperClazz;
        this.primitiveClazz = primitiveClazz;
    }

    public Class<?> getWrapperClazz() {
        return wrapperClazz;
    }

    public void setWrapperClazz(Class<?> wrapperClazz) {
        this.wrapperClazz = wrapperClazz;
    }

    public Class<?> getPrimitiveClazz() {
        return primitiveClazz;
    }

    public void setPrimitiveClazz(Class<?> primitiveClazz) {
        this.primitiveClazz = primitiveClazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrimitiveType)) return false;

        PrimitiveType that = (PrimitiveType) o;

        if (wrapperClazz != null ? !wrapperClazz.equals(that.wrapperClazz) : that.wrapperClazz != null) return false;
        return primitiveClazz != null ? primitiveClazz.equals(that.primitiveClazz) : that.primitiveClazz == null;
    }

    @Override
    public int hashCode() {
        int result = wrapperClazz != null ? wrapperClazz.hashCode() : 0;
        result = 31 * result + (primitiveClazz != null ? primitiveClazz.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrimitiveType{" +
                "wrapperClazz=" + wrapperClazz +
                ", primitiveClazz=" + primitiveClazz +
                '}';
    }
}
