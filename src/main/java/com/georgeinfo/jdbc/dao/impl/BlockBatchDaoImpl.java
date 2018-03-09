/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.impl;

import com.georgeinfo.jdbc.dao.api.BlockBatchDao;
import com.georgeinfo.jdbc.dao.helper.BatchAttributesContainer;
import com.georgeinfo.jdbc.dao.helper.BatchCallback;
import com.georgeinfo.jdbc.dao.utils.DaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;

/**
 * 批量块状执行Dao（不支持跨线程事务，支持单例）
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 */
public class BlockBatchDaoImpl extends BlockDaoImpl implements BlockBatchDao {

    public BlockBatchDaoImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void executeBatch(BatchCallback call) throws DaoException {
        Connection conn = getConnection();
        try {
            //向回调类设置基本JDBC参数
            conn.setAutoCommit(false);
            call.setConn(conn);

            //设置批量处理相关属性 开始
            AtomicInteger rowCounter = new AtomicInteger(0);
            HashMap<String, PreparedStatement> prestStmtMap = new HashMap<String, PreparedStatement>();
            HashMap<String, ArrayList<int[]>> executedRowsMap = new HashMap<String, ArrayList<int[]>>();
            BatchAttributesContainer bac = new BatchAttributesContainer(rowCounter, prestStmtMap, executedRowsMap);
            call.setBatchAttributesContainer(bac);
            //设置批量处理相关属性 结束

            //执行应用层回调方法
            call.process();

            //将批量处理任务的残留数据执行，以及批量关闭预处理对象 开始
            //在最后提交事务之前，再把所有PreparedStatement的残留数据保存一次（因为数据行不是DEFAULT_BATCH_SIZE倍数而导致的残留数据）
            for (PreparedStatement prestStmt : prestStmtMap.values()) {
                prestStmt.executeBatch();
            }
            //将批量处理任务的残留数据执行，以及批量关闭预处理对象 结束

            //最后离开回调方法时，提交数据库事务
            conn.commit();

            //循环关闭所有PreparedStatement对象 开始
            for (PreparedStatement prestStmt : prestStmtMap.values()) {
                try {
                    prestStmt.close();
                } catch (SQLException ex) {
                    logger.error("## Exception when close jdbc PreparedStatement.", ex);
                }
            }
            //循环关闭所有PreparedStatement对象 结束
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
}
