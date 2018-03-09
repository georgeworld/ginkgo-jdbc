/*
 * Author: George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.test.jdbc;

import com.georgeinfo.base.util.io.PropertiesTool;
import com.georgeinfo.base.util.logger.GeorgeLogger;
import gbt.config.GeorgeLoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

/**
 * 数据库连接对象管理器，使用ThreadLocal方式保存当前线程内的数据库连接，实现数据库事务控制。
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public class DBConnectionManager {

    private static final GeorgeLogger logger = GeorgeLoggerFactory.getLogger(DBConnectionManager.class);
    private final static ThreadLocal<Connection> connectionsContainer = new ThreadLocal<Connection>();
    private final static ThreadLocal<Integer> transactionIndex = new ThreadLocal<Integer>();
    private static DataSource dataSource;
    private static boolean show_sql = false;

    static {
        initDataSource();
    }

    /**
     * 初始化连接池
     */
    private static void initDataSource() {
        InputStream in = null;
        try {
            Properties dbProperties = new Properties();
            
            in = DBConnectionManager.class.getClassLoader().getResourceAsStream(DatabaseConfig.getInstance().getJdbcConfigFileFullNamePath());
            dbProperties.load(in);
//            Properties dbProperties = DatabaseConfig.getInstance().getDbProperties();
            //将自定义表达式替换成真正的值
            dbProperties = PropertiesTool.replaceCustomExpressions(dbProperties);
            dataSource = BasicDataSourceFactory.createDataSource(dbProperties);

            logger.info("DBCP Pool is OK.");

            //判断是否要显示运行的SQL语句
            String isShowSql = DatabaseConfig.getInstance().getShowSql();
            show_sql = "true".equalsIgnoreCase(isShowSql);

            //打印数据库连接信息 开始
            logger.info("Using DataSource : " + dataSource.getClass().getName());

            Connection conn = getConnection();
            DatabaseMetaData mdm = conn.getMetaData();
            logger.info("Connected to " + mdm.getDatabaseProductName() + " " + mdm.getDatabaseProductVersion());
            closeConnection();
            //打印数据库连接信息 结束
        } catch (Exception e) {
            logger.error("## Exception when init datasource.", e);

            dataSource = null;
            throw new DBException("【无法连接数据库，请检查数据库是否运行正常，服务器网路是否稳定】", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    logger.error(ex);
                }
            }
        }

    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 断开连接池
     */
    public static void closeDataSource() {
        try {
            dataSource.getClass().getMethod("close").invoke(dataSource);
        } catch (NoSuchMethodException e) {
            logger.error("尝试关闭数据库数据源对象时，找不到数据源对象的close方法", e);
        } catch (SecurityException e) {
            logger.error("Unabled to destroy DataSource!!! ", e);
        } catch (IllegalAccessException e) {
            logger.error("Unabled to destroy DataSource!!! ", e);
        } catch (IllegalArgumentException e) {
            logger.error("Unabled to destroy DataSource!!! ", e);
        } catch (InvocationTargetException e) {
            logger.error("Unabled to destroy DataSource!!! ", e);
        }
    }

    public static Connection getConnection() {
        Connection conn = connectionsContainer.get();
        try {
            if (conn == null || conn.isClosed()) {
                conn = dataSource.getConnection();
                connectionsContainer.set(conn);
            }
        } catch (SQLException e) {
            logger.error("Unabled to get connection object from ThreadLocal.", e);
            throw new DBException("Unabled to get connection object from ThreadLocal!!!", e);
        }

        return (show_sql && !Proxy.isProxyClass(conn.getClass()))
                ? new _DebugConnection(conn).getConnection() : conn;
    }

    /**
     * 关闭连接
     */
    public static void closeConnection() {
        Connection conn = connectionsContainer.get();
        try {
            //count.get()==null的时候没有事务嵌套
            if (conn != null && !conn.isClosed() && transactionIndex.get() == null) {
                conn.setAutoCommit(true);
                conn.close();
                connectionsContainer.set(null);
            }
        } catch (SQLException e) {
            logger.error("Unabled to close connection!!! ", e);
            throw new RuntimeException("Unabled to close connection!!!", e);
        }

    }

    // 用于事务
    public static Connection getConn() {
        return connectionsContainer.get();
    }

    // 用于事务
    public static void setConn(Connection conn) {
        connectionsContainer.set(conn);
    }

    // 记录事务的序号
    public static void setTransactionIndex(Integer i) {
        transactionIndex.set(i);
    }

    // 记录事务的序号
    public static Integer getTransactionIndex() {
        return transactionIndex.get();
    }

    /**
     * 用于跟踪执行的SQL语句
     *
     * @author Winter Lau
     */
    static class _DebugConnection implements InvocationHandler {

        private static final GeorgeLogger logger = GeorgeLoggerFactory.getLogger(_DebugConnection.class);
        private Connection conn = null;

        public _DebugConnection(Connection conn) {
            this.conn = conn;
        }

        /**
         * Returns the conn.
         *
         * @return Connection
         */
        public Connection getConnection() {
            return (Connection) Proxy.newProxyInstance(conn.getClass().getClassLoader(), new Class[]{Connection.class}, this);
        }

        @Override
        public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
            try {
                String method = m.getName();
                if ("prepareStatement".equals(method) || "createStatement".equals(method)) {
                    logger.info("[SQL] >>> " + args[0]);
                }
                return m.invoke(conn, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }
}
