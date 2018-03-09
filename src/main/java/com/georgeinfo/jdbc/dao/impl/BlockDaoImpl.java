/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.impl;

import com.georgeinfo.jdbc.dao.helper.Callback;
import com.georgeinfo.base.util.logger.GeorgeLogger;
import com.georgeinfo.jdbc.dao.api.BlockDao;
import com.georgeinfo.jdbc.dao.helper.BasicType;
import com.georgeinfo.jdbc.dao.helper.DataRow;
import com.georgeinfo.jdbc.dao.helper.JdbcRowCallbackHandler;
import com.georgeinfo.jdbc.dao.helper.SingleColumnRowCallBack;
import com.georgeinfo.jdbc.dao.utils.DaoException;
import com.georgeinfo.jdbc.utils.ProcedureParam;
import gbt.config.GeorgeLoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 * 简单Dao/块状Dao实现类
 *
 * @author George <Georgeinfo@163.com>
 */
public class BlockDaoImpl implements BlockDao {

    protected final GeorgeLogger logger = GeorgeLoggerFactory.getLogger(getClass());
    private final DataSource dataSource;

    public BlockDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 从数据源中得到一个连接
     *
     * @return
     */
    public Connection getConnection() {
        if (dataSource != null) {
            try {
                Connection conn = dataSource.getConnection();
                return conn;
            } catch (SQLException ex) {
                logger.error("## Can't get jdbc connection object.", ex);
                throw new DaoException("Can't get jdbc connection object from datasource.", ex);
            }
        } else {
            throw new DaoException("#### datasource can't be null #####");
        }
    }

    /**
     * 事务块:这个事物块中不能够在内部开启线程后在线程中执行数据库操作，那样会报错(connection is closed)
     *
     * @param call 回调接口
     */
    @Override
    public void withTransaction(Callback call) throws DaoException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);
            call.setConn(conn);

            //执行应用层回调方法
            call.process();

            conn.commit();
        } catch (SQLException ex1) {
            try {
                conn.rollback();
            } catch (SQLException ex2) {
                logger.error("## Jdbc transaction can't rollback.", ex2);
                throw new DaoException("## Jdbc transaction can't rollback.", ex2);
            }
            throw new DaoException("## SQLException", ex1);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection ", ex);
            }
        }
    }

    @Override
    public ArrayList<DataRow> queryList(String sql) throws DaoException {
        Connection conn = getConnection();
        try {
            return DaoExecutor.queryList(conn, sql);
        } catch (SQLException ex) {
            logger.error("## SQLException", ex);
            throw new DaoException("## SQLException", ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public ArrayList<DataRow> queryList(String sql, Map<String, Object> params) throws DaoException {
        Connection conn = getConnection();
        try {
            return DaoExecutor.queryList(conn, sql, params);
        } catch (SQLException ex) {
            logger.error("## SQLException", ex);
            throw new DaoException("## SQLException", ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public <T> ArrayList<T> queryList(String sql, Map<String, Object> params, Class<T> rowType) throws DaoException {
        Connection conn = getConnection();
        try {
            return DaoExecutor.queryList(conn, sql, params, rowType);
        } catch (SQLException ex) {
            logger.error("## SQLException", ex);
            throw new DaoException("## SQLException", ex);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public void query(String sql, JdbcRowCallbackHandler rowCallbackHandler) throws DaoException {
        Connection conn = getConnection();
        try {
            DaoExecutor.query(conn, sql, rowCallbackHandler);
        } catch (SQLException ex) {
            logger.error("## SQLException", ex);
            throw new DaoException("## SQLException", ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public void query(String sql, Map<String, Object> params, JdbcRowCallbackHandler rowCallbackHandler) throws DaoException {
        Connection conn = getConnection();
        try {
            DaoExecutor.query(conn, sql, params, rowCallbackHandler);
        } catch (SQLException ex) {
            logger.error("## SQLException", ex);
            throw new DaoException("## SQLException", ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public <T> T queryOne(String sql, Map<String, Object> params, Class<T> requiredType) throws DaoException {
        Connection conn = getConnection();
        try {
            return DaoExecutor.queryOne(conn, sql, params, requiredType);
        } catch (SQLException ex) {
            logger.error("## SQLException", ex);
            throw new DaoException("## SQLException", ex);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        } catch (DaoException ex) {
            throw ex;
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, BasicType requiredTypeEnum) throws DaoException {
        Connection conn = getConnection();
        try {
            return DaoExecutor.queryOneBasicValue(conn, sql, params, requiredTypeEnum);
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        } catch (DaoException ex) {
            throw ex;
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, Class<T> requiredType, SingleColumnRowCallBack singleColumnRowCallBack) throws DaoException {
        Connection conn = getConnection();
        try {
            return DaoExecutor.queryOneBasicValue(conn, sql, params, requiredType, singleColumnRowCallBack);
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        } catch (DaoException ex) {
            throw ex;
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public DataRow queryOneRow(String sql, Map<String, Object> params) throws DaoException {
        Connection conn = getConnection();
        try {
            return DaoExecutor.queryOneRow(conn, sql, params);
        } catch (SQLException ex) {
            logger.error("## SQLException", ex);
            throw new DaoException("## SQLException", ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public <T> T queryOneEntity(String sql, Map<String, Object> params, Class<T> requiredType) throws DaoException {
        Connection conn = getConnection();
        try {
            return DaoExecutor.queryOneEntity(conn, sql, params, requiredType);
        } catch (SQLException ex) {
            logger.error("## SQLException", ex);
            throw new DaoException("## SQLException", ex);
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
            logger.error(ex.getMessage(), ex);
            throw new DaoException(ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public List[] queryBatch(String[] sqlArray, Map<String, Object>[] paramArray) throws DaoException {
        Connection conn = getConnection();
        try {
            return DaoExecutor.queryBatch(conn, sqlArray, paramArray);
        } catch (SQLException ex) {
            logger.error("## SQLException", ex);
            throw new DaoException("## SQLException", ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                logger.error("## Can't close jdbc connection object.", ex);
            }
        }
    }

    @Override
    public int execute(String sql, Map<String, Object> params) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(true);
            return DaoExecutor.execute(conn, sql, params);
        } finally {
            conn.setAutoCommit(false);
            conn.close();
        }
    }

    @Override
    public List<Long> insertAndGetIds(String sql, Map<String, Object> paramsMap) throws SQLException {
        Connection conn = getConnection();
        try {
            List<Long> result = DaoExecutor.insertAndGetIds(conn, sql, paramsMap);
            conn.commit();
            return result;
        } finally {
            conn.close();
        }
    }

    @Override
    public Long insertAndGetId(String sql, Map<String, Object> paramsMap) throws SQLException {
        Connection conn = getConnection();
        try {
            Long result = DaoExecutor.insertAndGetId(conn, sql, paramsMap);
            return result;
        } finally {
            conn.close();
        }
    }

    @Override
    public HashMap<String, Object> executeProcedure(String procedureName, LinkedHashMap<String, ProcedureParam> params) throws SQLException {
        Connection conn = getConnection();
        try {
            HashMap<String, Object> result = DaoExecutor.executeProcedure(conn, procedureName, params);
            return result;
        } finally {
            conn.close();
        }
    }

    @Override
    public int[] executeBatch(String[] sqlArray, Map<String, Object>[] paramArray) throws SQLException {
        Connection conn = getConnection();
        try {
            int[] result = DaoExecutor.executeBatch(conn, sqlArray, paramArray);
            return result;
        } finally {
            conn.close();
        }
    }

}
