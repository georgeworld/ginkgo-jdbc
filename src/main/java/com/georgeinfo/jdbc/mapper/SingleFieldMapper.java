/*
* Copyright (c) George software studio, All Rights Reserved.
* George <georgeinfo@qq.com> | http://www.georgeinfo.com 
*/
package com.georgeinfo.jdbc.mapper;

import com.georgeinfo.jdbc.dao.helper.SingleColumnRowCallBack;
import com.georgeinfo.jdbc.dao.utils.DaoUtil;
import com.georgeinfo.jdbc.mapper.exception.MapperException;
import com.georgeinfo.jdbc.mapper.exception.TypeMismatchOrIndexInvalidException;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 单字段值映射器（提取一行中的一个字段的值，并映射到指定类型的对象上）
 *
 * @author George<GeorgeWorld@qq.com>
 */
public class SingleFieldMapper<T> {
    /**
     * 字段值最终被转换成的目标类型
     */
    private Class<?> targetType;
    /**
     * 当出现异常时的回调处理对象
     */
    private SingleColumnRowCallBack singleColumnRowCallBack;

    /**
     * 默认构造函数，如果使用此构造函数初始化对象，则必须后面给对象的targetType属性赋值后，才能正常使用。
     */
    public SingleFieldMapper() {
    }

    /**
     * 构造函数，同时指定字段值的目标类型
     *
     * @param targetType 字段值将被转换成的目标类型
     */
    public SingleFieldMapper(Class<T> targetType) {
        setTargetType(targetType);
    }

    /**
     * 构造函数，同时指定字段值的目标类型，指定当异常时的回调处理对象
     *
     * @param targetType              字段值将被转换成的目标类型
     * @param singleColumnRowCallBack 异常时回调接口
     */
    public SingleFieldMapper(Class<T> targetType, SingleColumnRowCallBack singleColumnRowCallBack) {
        this(targetType);
        this.singleColumnRowCallBack = singleColumnRowCallBack;
    }

    /**
     * 目标类型属性setter
     */
    public void setTargetType(Class<T> targetType) throws MapperException {
        this.targetType = MapperUtils.getPrimitiveType(targetType);
    }

    /**
     * 当出现异常时的回调处理对象setter
     **/
    public void setSingleColumnRowCallBack(SingleColumnRowCallBack singleColumnRowCallBack) {
        this.singleColumnRowCallBack = singleColumnRowCallBack;
    }

    /**
     * 将整个结果集，每一行的第一个字段值，分别映射到指定类型的对象上去，然后将映射后的对象组装成列表<br>
     * 这个是外部调用本类实例的时候，实际工作的主入口方法，本类其他的方法都是辅助方法。
     * ## 【主入口方法】
     */
    public List<T> extractData(ResultSet rs) throws SQLException, TypeMismatchOrIndexInvalidException {
        List<T> results = new ArrayList<T>();
        int rowNum = 0;
        while (rs.next()) {
            results.add(mapRowData(rs, rowNum++));
        }
        return results;
    }

    /**
     * 将数据集中的指定行，映射到指定类型的对象上
     *
     * @param rs     从数据库中查询出的数据集对象
     * @param rowNum 待被处理的数据集的行号（第几行数据）
     * @return 转换到指定类型的数据载体对象
     */
    public T mapRowData(ResultSet rs, int rowNum) throws SQLException, TypeMismatchOrIndexInvalidException {
        if (singleColumnRowCallBack == null) {
            return doMapRow(rs, rowNum);
        } else {
            return doMapRowWithCallback(rs, rowNum);
        }
    }

    private T doMapRow(ResultSet rs, int rowNum) throws SQLException, MapperException, TypeMismatchOrIndexInvalidException {
        // 判断记录集的字段数量是否是1
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        if (columnCount != 1) {
            MapperException exception = new MapperException("#Incorrect column count of result set: column count must be 1,but found " + columnCount);
            throw exception;
        }

        // 从数据集中提取字段数据，并转换成指定目标类型
        Object result = getColumnValueAndConvertToTargetType(rs, 1, this.targetType);
        // 如果调用方指定了字段的目标类型，但是获得的字段值类型，不是指定的目标类型，则尝试进行类型转换
        if (result != null && this.targetType != null && !this.targetType.isInstance(result)) {
            try {
                return (T) convertValueToTargetType(result, this.targetType);
            } catch (IllegalArgumentException ex) {
                MapperException exception = new MapperException("#Field actual type mismatch: the column[1] type of row [" + rowNum + "] is " + rsmd.getColumnTypeName(1) ,ex);
                throw exception;
            }
        }
        return (T) result;
    }

    private T doMapRowWithCallback(ResultSet rs, int rowNum) throws SQLException {
        // 判断记录集的字段数量是否是1
        ResultSetMetaData rsmd = rs.getMetaData();
        int nrOfColumns = rsmd.getColumnCount();
        if (nrOfColumns != 1) {
            MapperException exception = new MapperException("#Incorrect column count of result set: column count must be 1,but found " + nrOfColumns);
            return (T) singleColumnRowCallBack.whenIncorrectResultSetColumnCount(DaoUtil.convertToDataRowList(rs), exception);
        }

        // 从数据集中提取字段数据，并转换成指定目标类型
        Object result = null;
        try {
            result = getColumnValueAndConvertToTargetType(rs, 1, this.targetType);
        } catch (MapperException ex) {
            //如果发生异常，则调用异常处理的回调方法
            return (T) singleColumnRowCallBack.whenTypeMismatch(rs.getObject(1), ex);
        }

        // 如果调用方指定了字段的目标类型，但是获得的字段值类型，不是指定的目标类型，则尝试进行类型转换
        if (result != null && this.targetType != null && !this.targetType.isInstance(result)) {
            try {
                return (T) convertValueToTargetType(result, this.targetType);
            } catch (IllegalArgumentException ex) {
                MapperException exception = new MapperException("#Field actual type mismatch: the column[1] type of row [" + rowNum + "] is " + rsmd.getColumnTypeName(1) + "," + ex.getMessage());

                return (T) singleColumnRowCallBack.whenTypeMismatch(result, exception);
            }
        }
        return (T) result;
    }

    /**
     * 从数据集指定的字段中，取得字段值，并转换成指定类型
     **/
    protected Object getColumnValueAndConvertToTargetType(ResultSet rs, int index, Class<?> targetType)
            throws SQLException, TypeMismatchOrIndexInvalidException {
        if (targetType != null) {
            return MapperUtils.getResultSetValue(rs, index, targetType);
        } else {
            // 如果没有指定目标类型，则按照默认类型处理
            return getColumnValueAndConvertToTargetType(rs, index);
        }
    }

    protected Object getColumnValueAndConvertToTargetType(ResultSet rs, int index) throws SQLException {
        return MapperUtils.getResultSetValue(rs, index);
    }

    /**
     * 将一个对象，转换成指定的目标类型的对象
     */
    protected Object convertValueToTargetType(Object value, Class<?> targetType) {
        if (String.class == targetType) {
            return value.toString();
        } else if (Number.class.isAssignableFrom(targetType)) {
            if (value instanceof Number) {
                // Convert original Number to target Number class.
                return MapperUtils.convertNumberToTargetClass(((Number) value), (Class<Number>) targetType);
            } else {
                // Convert stringified value to target Number class.
                return MapperUtils.parseNumber(value.toString(), (Class<Number>) targetType);
            }
        } else {
            throw new IllegalArgumentException(
                    "Value [" + value + "] is of type [" + value.getClass().getName()
                            + "] and cannot be converted to required type [" + targetType.getName() + "]");
        }
    }
}

