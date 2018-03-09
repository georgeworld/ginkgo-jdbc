/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.test.jdbc;

import java.util.ResourceBundle;
import com.georgeinfo.jdbc.utils.BasicDBConfig;

/**
 * 数据库连接参数持有对象
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public final class DatabaseConfig extends BasicDBConfig {

//    private static final GeorgeLogger logger = GeorgeLoggerFactory.getLogger(DatabaseConfig.class);
//    private final String jdbcConfigFilePath = System.getProperty("user.dir")
//            + System.getProperty("file.separator")
//            + "deploy" + System.getProperty("file.separator")
//            + "config.properties";
    private final String jdbcConfigFilePath = "resources/jdbc";
    private String showSql = "false";
//    private Properties dbProperties;

    private DatabaseConfig() {
        init();
    }

    // 线程安全的惰性加载单例模式 开始
    private static class DatabaseConfigHolder {

        private static final DatabaseConfig instance = new DatabaseConfig();
    }

    public static DatabaseConfig getInstance() {
        return DatabaseConfigHolder.instance;
    }
    // 线程安全的惰性加载单例模式 结束

    @Override
    public void init() {
//        InputStream is = null;
//        try {
//            is = new BufferedInputStream(new FileInputStream(jdbcConfigFilePath));
        ResourceBundle bundle = ResourceBundle.getBundle(jdbcConfigFilePath);
        //dbProperties = new Properties();
        //dbProperties.load(is);

        dbDriver = bundle.getString("jdbc.driver"); //dbProperties.getProperty("jdbc.driver");
        url = bundle.getString("jdbc.url");
        username = bundle.getString("jdbc.username");
        password = bundle.getString("jdbc.password");
        moreConnectionProperties = bundle.getString("jdbc.connectionProperties");
        showSql = bundle.getString("jdbc.show_sql");
//        } catch (FileNotFoundException ex) {
//            logger.error("## Can't found config file in dir:" + jdbcConfigFilePath, ex);
//        } catch (IOException ex) {
//            logger.error("## IOException when loading config file.", ex);
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException ex) {
//                    logger.error("## IOException when close inputstream of config file.", ex);
//                }
//            }
//        }

    }

    public String getSchema() {
        return getUsername();
    }

    /**
     * 返回jdbc.properties的相对路径
     *
     * @return
     */
    public String getJdbcConfigFileFullNamePath() {
        return jdbcConfigFilePath + ".properties";
    }

    public String getShowSql() {
        return showSql;
    }

//    public Properties getDbProperties() {
//        return dbProperties;
//    }
}
