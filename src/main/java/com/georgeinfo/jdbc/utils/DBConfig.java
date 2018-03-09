/*
 * Programming by: George <GeorgeNiceWorld@gmail.com>
 * Copyright (C) George (www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author George
 */
public abstract class DBConfig {

    public static final String REMARKS_REPORTING_KEY = "remarksReporting";
    public static final String REMARKS_KEY = "remarks";
    protected String dbDriver = null;
    protected String url = null;
    protected String username = null;
    protected String password = null;
    protected String moreConnectionProperties = null;

    public abstract void init();

    /**
     * 得到当前数据库类型
     *
     * @return 当前数据库类型编码
     */
    public String getDatabaseType() {
        return DatabaseTypeDef.DbType.findDbCodeByJdbcDriver(dbDriver);
    }

    public String getDbDriver() {
        return dbDriver;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMoreConnectionPropertiesString() {
        return moreConnectionProperties;
    }

    public Properties getMoreConnectionProperties() {
        if (moreConnectionProperties == null) {
            return null;
        }

        String[] entries = moreConnectionProperties.split(";");
        Properties properties = new Properties();
        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i];
            if (entry.length() > 0) {
                int index = entry.indexOf('=');
                if (index > 0) {
                    String name = entry.substring(0, index);
                    String value = entry.substring(index + 1);
                    properties.setProperty(name, value);
                } else {
                    // no value is empty string which is how java.util.Properties works
                    properties.setProperty(entry, "");
                }
            }
        }
        return properties;
    }

    public Properties getConnectionProperties() {
        Properties moreProperties = getMoreConnectionProperties();
        Properties properties = null;

        if (moreProperties != null) {
            properties = new Properties(moreProperties);
        } else {
            properties = new Properties();
        }

        properties.put("user", username);
        properties.put("password", password);

        return properties;
    }

    public Connection getConnection(Properties properties) throws SQLException, ClassNotFoundException {
        Class.forName(dbDriver);
        Connection conn = DriverManager.getConnection(url, properties);

        return conn;
    }
}
