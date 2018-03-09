/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.test.jdbc;

import com.georgeinfo.base.injection.InitializingBean;
import com.georgeinfo.base.util.logger.GeorgeLogger;
import com.georgeinfo.jdbc.paging.DefaultPagingServiceImpl;
import com.georgeinfo.jdbc.paging.MySQLPagingDaoImpl;
import gbt.config.GeorgeLoggerFactory;
import java.sql.SQLException;

/**
 *
 * @author George <Georgeinfo@163.com>
 */
public class JdbcPagingService extends DefaultPagingServiceImpl implements InitializingBean {

    private static final GeorgeLogger logger = GeorgeLoggerFactory.getLogger(JdbcPagingService.class);

    public JdbcPagingService() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            super.setPagingDao(new MySQLPagingDaoImpl(DBConnectionManager.getDataSource()));
        } catch (SQLException ex) {
            logger.error("## Can't setting paging dao to PagingService", ex);
        }
    }
}
