/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.impl;

import com.georgeinfo.base.util.clazz.TypeConverter;
import com.georgeinfo.jdbc.dao.helper.NotifyThread;
import com.georgeinfo.jdbc.dao.helper.DataRow;
import com.georgeinfo.jdbc.dao.helper.JdbcRowCallbackHandler;
import com.georgeinfo.jdbc.utils.ProcedureParam;
import com.georgeinfo.base.util.logger.GeorgeLogger;
import com.georgeinfo.jdbc.dao.api.CrossThreadDao;
import com.georgeinfo.jdbc.dao.helper.BasicType;
import com.georgeinfo.jdbc.dao.helper.SingleColumnRowCallBack;
import com.georgeinfo.jdbc.dao.utils.DaoException;
import gbt.config.GeorgeLoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;

/**
 * 通用JDBC Dao（支持跨线程事务，不支持单例，本类不能被声明为单例模式，需要ProtoType模式）<br/>
 * 此Dao整个生命周期内持有一个Connection<br/>
 * 在多线程模式下使用时，需要先调用public void regThread(NotifyThread thread)方法注册线程，<br/>
 * 在主线程commit()时，主线程会等待所有子线程汇聚完成才真正提交。
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public class ProtoTypeDaoImpl implements CrossThreadDao {

    protected final GeorgeLogger logger = GeorgeLoggerFactory.getLogger(getClass());
    /**
     * 存放当前线程所处理的保存点，为部分回滚服务。
     */
    protected final ConcurrentHashMap<String, Savepoint> currentSavepointMap = new ConcurrentHashMap<String, Savepoint>();
    protected Connection connection;
    protected DataSource dataSource;
    /**
     * 线程注册计数器
     */
    private final AtomicInteger threadRegisterCounter = new AtomicInteger(0);
    /**
     * 线程完成计数器
     */
    private final AtomicInteger threadCompletedCounter = new AtomicInteger(0);

    public ProtoTypeDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ProtoTypeDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getCurrentConn() {
        return connection;
    }

    @Override
    public void begin() throws SQLException {
        if (connection == null) {
            try {//如果connection为空，则尝试使用dataSource来创建connection
                if (dataSource != null) {
                    connection = this.dataSource.getConnection();
                } else {
                    logger.error("====== 无法创建数据库连接,Georgeinfo-JDBC Dao需要一个Connection对象或者一个DataSource对象,\n但是现在他们两个都为空！ ======");
                    throw new SQLException("#################### Georgeinfo-JDBC Dao需要一个Connection对象或者一个DataSource对象,\n但是现在他们两个都为空！");
                }
            } catch (SQLException ex) {
                this.logger.error("============初始化数据库连接时出现错误！", ex);
                throw new SQLException("============初始化数据库连接时出现错误！", ex);
            }
        }

        //设置数据库连接为“不自动提交”
        connection.setAutoCommit(false);
    }

    @Override
    public void commit() throws SQLException {
        Connection conn = getCurrentConn();
        if ((conn != null) && (!conn.getAutoCommit())) {
            while (true) {
                if (this.counterCompare(threadRegisterCounter, threadCompletedCounter)) {
                    conn.commit();
                    break;
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    logger.error("## 执行Dao提交前休眠时，出现异常。", ex);
                    break;
                }
            }
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
            currentSavepointMap.put(savepointKey, conn.setSavepoint(savepointKey));
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
                conn.rollback(currentSavepointMap.get(savepointKey));//回滚到指定事务点上去
            } catch (SQLException ex) {
                throw new DaoException("Jdbc rollback to savepoint failure.", ex);
            }
            currentSavepointMap.remove(savepointKey); //将SavePoint从当前线程解除绑定
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
                throw new DaoException("Jdbc rollback to savepoint failure.", ex);
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
            throw new DaoException("Can't release savepoint failure.", ex);
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
                throw new DaoException("connection can't be request autocommit status.");
            }
        } catch (SQLException ex) {
            throw new DaoException("Jdbc rollback failure.", ex);
        }
    }

    @Override
    public void end() {
        Connection conn = getCurrentConn();

        if (conn != null) {
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
    //-- 线程相关方法 开始 ------------------------------------------------------

    @Override
    public AtomicInteger getThreadRegisterCounter() {
        return threadRegisterCounter;
    }

    @Override
    public AtomicInteger getThreadCompletedCounter() {
        return threadCompletedCounter;
    }

    /**
     * 注册线程
     *
     * @param thread 被注册的线程，拥有执行完主动向主线程通知的功能
     */
    @Override
    public void regThread(NotifyThread thread) {
        threadRegisterCounter.incrementAndGet();
        thread.setThreadCompletedCounter(threadCompletedCounter);
    }

    /**
     * 线程注册计数器与线程完成计数器比较
     *
     * @param threadRegisterCounter 线程注册计数器
     * @param threadCompletedCounter 线程执行完成计数器
     * @return 线程注册计数器与线程执行完成计数器是否相等
     */
    @Override
    public boolean counterCompare(AtomicInteger threadRegisterCounter, AtomicInteger threadCompletedCounter) {
        return (threadRegisterCounter.get() == threadCompletedCounter.get());
    }
    //-- 线程相关方法 结束 ------------------------------------------------------

    @Override
    public ArrayList<DataRow> queryList(String sql) throws DaoException {
        try {
            return DaoExecutor.queryList(this.getCurrentConn(), sql);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public ArrayList<DataRow> queryList(String sql, Map<String, Object> params) throws DaoException {
        try {
            return DaoExecutor.queryList(this.getCurrentConn(), sql, params);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public <T> ArrayList<T> queryList(String sql, Map<String, Object> params, Class<T> rowType) throws DaoException {
        try {
            return DaoExecutor.queryList(this.getCurrentConn(), sql, params, rowType);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public void query(String sql, JdbcRowCallbackHandler rowCallbackHandler) throws DaoException {
        try {
            DaoExecutor.query(this.getCurrentConn(), sql, rowCallbackHandler);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public void query(String sql, Map<String, Object> params, JdbcRowCallbackHandler rowCallbackHandler) throws DaoException {
        try {
            DaoExecutor.query(this.getCurrentConn(), sql, params, rowCallbackHandler);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public DataRow queryOneRow(String sql, Map<String, Object> params) throws DaoException {
        try {
            return DaoExecutor.queryOneRow(this.getCurrentConn(), sql, params);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, BasicType requiredTypeEnum) throws DaoException {
        try {
            return DaoExecutor.queryOneBasicValue(this.getCurrentConn(), sql, params, requiredTypeEnum);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, Class<T> requiredType, SingleColumnRowCallBack singleColumnRowCallBack) throws DaoException {
        try {
            return DaoExecutor.queryOneBasicValue(this.getCurrentConn(), sql, params, requiredType, singleColumnRowCallBack);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } catch (DaoException ex) {
            throw ex;
        }
    }

    @Override
    public <T> T queryOneEntity(String sql, Map<String, Object> params, Class<T> requiredType) throws DaoException {
        try {
            return DaoExecutor.queryOneEntity(this.getCurrentConn(), sql, params, requiredType);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public <T> T queryOne(String sql, Map<String, Object> params, Class<T> rowType) throws DaoException {
        try {
            return DaoExecutor.queryOne(this.getCurrentConn(), sql, params, rowType);
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            throw new DaoException(ex);
        } catch (DaoException ex) {
            throw ex;
        }
    }

    @Override
    public List[] queryBatch(String[] sqlArray, Map<String, Object>[] paramArray) throws DaoException {
        try {
            return DaoExecutor.queryBatch(this.getCurrentConn(), sqlArray, paramArray);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public int execute(String sql, Map<String, Object> params) throws SQLException {
        try {
            return DaoExecutor.execute(this.getCurrentConn(), sql, params);
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    @Override
    public List<Long> insertAndGetIds(String sql, Map<String, Object> paramsMap) throws SQLException {
        return DaoExecutor.insertAndGetIds(this.getCurrentConn(), sql, paramsMap);
    }

    @Override
    public Long insertAndGetId(String sql, Map<String, Object> paramsMap) throws SQLException {
        return DaoExecutor.insertAndGetId(this.getCurrentConn(), sql, paramsMap);
    }

    /**
     * 一行数据集是一个map，返回值是由多个数据集map组合成的list
     *
     * private List<HashMap<String, Object>> calculateKeys(ResultSet keys)
     * throws SQLException { // Prepare a list to contain the auto-generated
     * column // values, and then fetch them from the statement.
     * List<HashMap<String, Object>> autoKeys = new
     * ArrayList<HashMap<String, Object>>(); int count =
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
     * @param params 存储过程 in输入参数列表
     * @return
     * @throws java.sql.SQLException
     */
    @Override
    public HashMap<String, Object> executeProcedure(String procedureName, LinkedHashMap<String, ProcedureParam> params) throws SQLException {
        return DaoExecutor.executeProcedure(this.getCurrentConn(), procedureName, params);
    }

    @Override
    public int[] executeBatch(String[] sqlArray, Map<String, Object>[] paramArray) throws SQLException {
        return DaoExecutor.executeBatch(this.getCurrentConn(), sqlArray, paramArray);
    }
}
