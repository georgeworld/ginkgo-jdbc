/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.impl;

import com.georgeinfo.jdbc.dao.helper.DataRow;
import com.georgeinfo.jdbc.dao.helper.JdbcRowCallbackHandler;
import com.georgeinfo.jdbc.utils.ProcedureParam;
import com.georgeinfo.base.util.logger.GeorgeLogger;
import com.georgeinfo.jdbc.dao.api.GSDao;
import com.georgeinfo.jdbc.dao.helper.BasicType;
import com.georgeinfo.jdbc.dao.helper.SingleColumnRowCallBack;
import com.georgeinfo.jdbc.dao.transaction.TransactionContainer;
import com.georgeinfo.jdbc.dao.utils.DaoException;
import gbt.config.GeorgeLoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * 通用JDBC
 * Dao（在单线程中使用时，就是一个普通ProtoTypeDao，在多线程中使用时，每个子线程中，需要单独begin()、commit()和end()<br/>,
 * 也就是说，子线程中是一个独立的事务片段，本类不支持跨线程事务。本类可以被直接声明为单例，但是没有跨线程事务功能。）<br/>
 * 本类已过时，请使用{ProtoTypeDao}替代。
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 * @deprecated
 */
public class CommonDao implements GSDao {

    protected final GeorgeLogger logger = GeorgeLoggerFactory.getLogger(getClass());
    protected DataSource dataSource;

    public CommonDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public CommonDao(Connection connection) {
        Integer.parseInt("d");
        TransactionContainer.bindConnToCurrentThread(this.hashCode(), connection);
    }

    @Override
    public Connection getCurrentConn() {
        return TransactionContainer.getCurrentThreadConn(this.hashCode());
    }

    @Override
    public void begin() throws SQLException {
        Connection connection = getCurrentConn();
        if (connection == null || connection.isClosed()) {
            connection = this.dataSource.getConnection();

            //设置数据库连接为“不自动提交”
            connection.setAutoCommit(false);

            //将数据库连接绑定到当前线程上
            TransactionContainer.bindConnToCurrentThread(this.hashCode(), connection);
        }
    }

    @Override
    public void commit() throws SQLException {
        Connection conn = getCurrentConn();
        if ((conn != null) && (!conn.getAutoCommit())) {
            conn.commit();
        } else {
            if (conn == null) {
                throw new SQLException("connection not opened!");
            }
            throw new SQLException("first begin then commit please!");
        }
    }

    @Override
    public void beginSavepoint(String savepointKey) throws SQLException {
        Connection conn = getCurrentConn();
        if (conn != null) {
            TransactionContainer.bindSavepointToCurrentThread(this.hashCode(), savepointKey, conn.setSavepoint(savepointKey));
        } else {
            if (conn == null) {
                throw new SQLException("connection not opened!");
            }
            throw new SQLException("first begin the dao,and then begin savepoint please!");
        }
    }

    @Override
    public void rollbackToSavepoint(String savepointKey) throws DaoException {
        Connection conn = getCurrentConn();
        if (conn != null) {
            try {
                conn.rollback(TransactionContainer.getCurrentThreadSavepoint(this.hashCode(), savepointKey));//回滚到指定事务点上去
            } catch (SQLException ex) {
                throw new DaoException("## rollback to savepoint failure.", ex);
            }
            //不管成功与否，都将该事务点从ThreadLocal中移除
            TransactionContainer.unindSavepointFromCurrentThread(this.hashCode(), savepointKey); //将SavePoint从当前线程解除绑定
        } else {
            throw new DaoException("connection is null.");
        }
    }

    @Override
    public Savepoint createSavepoint() throws SQLException {
        Connection conn = getCurrentConn();
        if (conn != null) {
            return conn.setSavepoint();
        } else {
            if (conn == null) {
                throw new SQLException("connection not opened!");
            }
            throw new SQLException("first begin the dao,and then create savepoint please!");
        }
    }

    @Override
    public void rollbackToSavepoint(Savepoint savepoint) throws DaoException {
        Connection conn = getCurrentConn();
        if (conn != null) {
            try {
                conn.rollback(savepoint);//回滚到指定事务点上去
            } catch (SQLException ex) {
                throw new DaoException("## jdbc rollback failure.", ex);
            }
        } else {
            throw new DaoException("connection is null.");
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws DaoException {
        try {
            getCurrentConn().releaseSavepoint(savepoint);
        } catch (SQLException ex) {
            throw new DaoException("## jdbc release savepoint failure.", ex);
        }
    }

    @Override
    public void rollback() throws DaoException {
        Connection conn = getCurrentConn();
        try {
            if ((conn != null) && (!conn.getAutoCommit())) {
                conn.rollback();
            } else {
                if (conn == null) {
                    throw new DaoException("connection is null.");
                }
                throw new DaoException("Can't reqeust jdbc connection autocommit status.");
            }
        } catch (SQLException ex) {
            throw new DaoException("## jdbc connection rollback failure.", ex);
        }
    }

    @Override
    public void end() {
        Connection conn = getCurrentConn();

        if (conn != null) {
            //首先，将数据库连接从当前线程容器中移除
            TransactionContainer.unindConnFromCurrentThread(this.hashCode());

            try {
                //然后，将数据库连接设置为“自动提交”（看起来是多此一举，但是为了保险起见，还是这样做吧。）
                //conn.setAutoCommit(true);
                //最后，关闭数据库连接  
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.error("## Exception when close jdbc connection.", ex);
            }
        }
    }

    @Override
    public ArrayList<DataRow> queryList(String sql) {
        try {
            return DaoExecutor.queryList(this.getCurrentConn(), sql);
        } catch (SQLException ex) {
            logger.error(ex);
            throw new DaoException("## SQLException", ex);
        }
    }

    @Override
    public ArrayList<DataRow> queryList(String sql, Map<String, Object> params) {
        try {
            return DaoExecutor.queryList(this.getCurrentConn(), sql, params);
        } catch (SQLException ex) {
            logger.error(ex);
            throw new DaoException("## SQLException", ex);
        }
    }

    @Override
    public <T> ArrayList<T> queryList(String sql, Map<String, Object> params, Class<T> rowType) throws DaoException {
        try {
            return DaoExecutor.queryList(this.getCurrentConn(), sql, params, rowType);
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException("## SQLException", ex);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }
    }

    @Override
    public void query(String sql, JdbcRowCallbackHandler rowCallbackHandler) {
        try {
            DaoExecutor.query(this.getCurrentConn(), sql, rowCallbackHandler);
        } catch (SQLException ex) {
            logger.error(ex);
            throw new DaoException("## SQLException", ex);
        }
    }

    @Override
    public void query(String sql, Map<String, Object> params, JdbcRowCallbackHandler rowCallbackHandler) {
        try {
            DaoExecutor.query(this.getCurrentConn(), sql, params, rowCallbackHandler);
        } catch (SQLException ex) {
            logger.error(ex);
            throw new DaoException("## SQLException", ex);
        }
    }

    @Override
    public <T> T queryOne(String sql, Map<String, Object> params, Class<T> requiredType) throws DaoException {
        try {
            return DaoExecutor.queryOne(this.getCurrentConn(), sql, params, requiredType);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        } catch (DaoException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, BasicType requiredTypeEnum) throws DaoException {
        try {
            return DaoExecutor.queryOneBasicValue(this.getCurrentConn(), sql, params, requiredTypeEnum);
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        } catch (DaoException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, Class<T> requiredType, SingleColumnRowCallBack singleColumnRowCallBack) throws DaoException {
        try {
            return DaoExecutor.queryOneBasicValue(this.getCurrentConn(), sql, params, requiredType, singleColumnRowCallBack);
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        } catch (DaoException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public <T> T queryOneEntity(String sql, Map<String, Object> params, Class<T> requiredType) throws DaoException {
        try {
            return DaoExecutor.queryOneEntity(this.getCurrentConn(), sql, params, requiredType);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        }
    }

    @Override
    public DataRow queryOneRow(String sql, Map<String, Object> params) {
        try {
            return DaoExecutor.queryOneRow(this.getCurrentConn(), sql, params);
        } catch (SQLException ex) {
            logger.error(ex);
            throw new DaoException("## SQLException", ex);
        }
    }

    @Override
    public List[] queryBatch(String[] sqlArray, Map<String, Object>[] paramArray) {
        try {
            return DaoExecutor.queryBatch(this.getCurrentConn(), sqlArray, paramArray);
        } catch (SQLException ex) {
            logger.error(ex);
            throw new DaoException("## SQLException", ex);
        }
    }

    @Override
    public int execute(String sql, Map<String, Object> params) throws SQLException {
        return DaoExecutor.execute(this.getCurrentConn(), sql, params);
    }

    /**
     * 请使用public List<Integer> insertAndGetId(String sql,
     * HashMap<String, Object> paramsMap)代替
     *
     * @param sql
     * @param paramsMap
     * @param sqlForGetLastInsertId
     * @return
     * @throws java.sql.SQLException
     * @deprecated
     */
    public int insertAndGetId(String sql, Map<String, Object> paramsMap, LastInsertId sqlForGetLastInsertId) throws SQLException {
        return DaoExecutor.insertAndGetId(this.getCurrentConn(), sql, paramsMap, sqlForGetLastInsertId);
    }

    @Override
    public List<Long> insertAndGetIds(String sql, Map<String, Object> paramsMap) throws SQLException {
        return DaoExecutor.insertAndGetIds(this.getCurrentConn(), sql, paramsMap);
    }

    @Override
    public Long insertAndGetId(String sql, Map<String, Object> paramsMap) throws SQLException {
        return DaoExecutor.insertAndGetId(this.getCurrentConn(), sql, paramsMap);
    }

    @Override
    public int[] executeBatch(String[] sqlArray, Map<String, Object>[] paramArray) {
        try {
            return DaoExecutor.executeBatch(this.getCurrentConn(), sqlArray, paramArray);
        } catch (SQLException ex) {
            logger.error(ex);
            throw new DaoException("## SQLException", ex);
        }
    }

    /**
     * 一行数据集是一个map，返回值是由多个数据集map组合成的list
     *
     * private List<HashMap<String, Object>> calculateKeys(ResultSet keys) { //
     * Prepare a list to contain the auto-generated column // values, and then
     * fetch them from the statement. List<HashMap<String, Object>> autoKeys =
     * new ArrayList<HashMap<String, Object>>(); int count =
     * keys.getMetaData().getColumnCount();
     *
     * // Copy the column values into a list of a list. while (keys.next()) {
     * HashMap<String, Object> row = new HashMap<String, Object>(); for (int i =
     * 1; i <= count; i++) { row.put(keys.getMetaData().getColumnLabel(i),
     * keys.getObject(i)); }
     *
     * autoKeys.add(row); } return autoKeys; }
     */
    /**
     * 执行存储过程
     *
     * @param procedureName 存储过程名称
     * @param params        存储过程 in输入参数列表
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public HashMap<String, Object> executeProcedure(String procedureName, LinkedHashMap<String, ProcedureParam> params) throws SQLException {
        return DaoExecutor.executeProcedure(this.getCurrentConn(), procedureName, params);
    }

}
