/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import com.georgeinfo.jdbc.dao.api.DaoApi;
import com.georgeinfo.jdbc.dao.impl.DaoExecutor;
import com.georgeinfo.jdbc.dao.utils.DaoException;
import com.georgeinfo.jdbc.mapper.exception.TypeMismatchOrIndexInvalidException;
import com.georgeinfo.jdbc.utils.ProcedureParam;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author George <Georgeinfo@163.com>
 */
public abstract class Callback implements DaoApi {

    /**
     * 数据库连接对象，外部set进来
     */
    protected Connection conn;

    /**
     * 块状dao的一个抽象方法
     *
     * @throws java.sql.SQLException
     */
    public abstract void process() throws SQLException;

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    @Override
    public ArrayList<DataRow> queryList(String sql) throws DaoException {
        try {
            return DaoExecutor.queryList(conn, sql);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public ArrayList<DataRow> queryList(String sql, Map<String, Object> params) throws DaoException {
        try {
            return DaoExecutor.queryList(conn, sql, params);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public <T> ArrayList<T> queryList(String sql, Map<String, Object> params, Class<T> rowType) throws DaoException {
        try {
            return DaoExecutor.queryList(conn, sql, params, rowType);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public void query(String sql, JdbcRowCallbackHandler rowCallbackHandler) throws DaoException {
        try {
            DaoExecutor.query(conn, sql, rowCallbackHandler);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public void query(String sql, Map<String, Object> params, JdbcRowCallbackHandler rowCallbackHandler) throws DaoException {
        try {
            DaoExecutor.query(conn, sql, params, rowCallbackHandler);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public <T> T queryOne(String sql, Map<String, Object> params, Class<T> requiredType) throws DaoException {
        try {
            return DaoExecutor.queryOne(conn, sql, params, requiredType);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            throw new DaoException(ex);
        } catch (DaoException ex) {
            throw ex;
        }
    }

    @Override
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, BasicType requiredTypeEnum) throws DaoException {
        try {
            return DaoExecutor.queryOneBasicValue(conn, sql, params, requiredTypeEnum);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (DaoException ex) {
            throw ex;
        }
    }

    @Override
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, Class<T> requiredType,
                                    SingleColumnRowCallBack singleColumnRowCallBack)
            throws DaoException, TypeMismatchOrIndexInvalidException {
        try {
            return DaoExecutor.queryOneBasicValue(conn, sql, params, requiredType, singleColumnRowCallBack);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (DaoException ex) {
            throw ex;
        }
    }

    @Override
    public <T> T queryOneEntity(String sql, Map<String, Object> params, Class<T> requiredType) throws DaoException {
        try {
            return DaoExecutor.queryOneEntity(conn, sql, params, requiredType);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public DataRow queryOneRow(String sql, Map<String, Object> params) throws DaoException {
        try {
            return DaoExecutor.queryOneRow(conn, sql, params);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public List[] queryBatch(String[] sqlArray, Map<String, Object>[] paramArray) throws DaoException {
        try {
            return DaoExecutor.queryBatch(conn, sqlArray, paramArray);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public int execute(String sql, Map<String, Object> params) throws SQLException {
        return DaoExecutor.execute(conn, sql, params);
    }

    @Override
    public List<Long> insertAndGetIds(String sql, Map<String, Object> paramsMap) throws SQLException {
        return DaoExecutor.insertAndGetIds(conn, sql, paramsMap);
    }

    @Override
    public Long insertAndGetId(String sql, Map<String, Object> paramsMap) throws SQLException {
        return DaoExecutor.insertAndGetId(conn, sql, paramsMap);
    }

    @Override
    public HashMap<String, Object> executeProcedure(String procedureName, LinkedHashMap<String, ProcedureParam> params) throws SQLException {
        return DaoExecutor.executeProcedure(conn, procedureName, params);
    }

    @Override
    public int[] executeBatch(String[] sqlArray, Map<String, Object>[] paramArray) throws SQLException {
        return DaoExecutor.executeBatch(conn, sqlArray, paramArray);
    }

}
