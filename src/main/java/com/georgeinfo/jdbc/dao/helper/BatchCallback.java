/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import com.georgeinfo.jdbc.dao.api.BatchDaoFeature;
import com.georgeinfo.jdbc.dao.impl.DaoExecutor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author George <Georgeinfo@163.com>
 */
public abstract class BatchCallback extends Callback implements BatchDaoFeature {

    private BatchAttributesContainer batchAttributesContainer;

    public void setBatchAttributesContainer(BatchAttributesContainer batchAttributesContainer) {
        this.batchAttributesContainer = batchAttributesContainer;
    }

    /**
     * 正式的批量操作方法，经过实际检验，很好用！ 本批量执行方法应该在业务层的循环中被调用，循环的迭代器是待被批量保存的数据集合。
     * 可以在循环中使用多种的SQL语句来调用本方法，每一【种】sql语句，都会被自动创建一个与之对应的 PreparedStatement
     * 对象，然后该【种】 sql语句的数据会被批量执行。<br />
     * 也就是：该批量执行方法，可以在业务层的一个循环内，执行多种SQL语句，比如在循环内，你需要
     * 保存一种数据，同时需要更新一种数据，这两种SQL操作都是与循环迭代器相关的批量操作，则你可以在
     * 循环中调用两次本方法，用于执行两种SQL语句，本方法完全支持这种复杂的逻辑。<br />
     * 【需要注意的是】：每种SQL语句在批量执行中，批量执行的批次大小，并不一定等于 本类接口中 DEFAULT_BATCH_SIZE
     * 定义的值，当然，如果 在业务层迭代器循环中，只执行一种SQL语句的话，批量执行的批次大小是等于DEFAULT_BATCH_SIZE的。
     * 因为在本类中，rowIndex这个计数器统计的是所有namedSql的执行行数，而不是单一一种namedSql的执行行数计数,这就意味着，
     * 如果业务层在迭代器循环中调用本方法执行了多于一种的sql语句，则批量保存的批次大小是对具体一种sql来说并不是等于DEFAULT_BATCH_SIZE的值，
     * 因为这个rowIndex计数是多个sql的执行行数计数。
     *
     * @param namedSql 被执行的SQL语句，格式如：update table set a = :a,b = :b where c = :c
     * @param parameterMap SQL语句对应的参数map
     * @throws java.sql.SQLException
     */
    @Override
    public void executeBatch(String namedSql, Map<String, Object> parameterMap) throws SQLException {
        DaoExecutor.executeBatch(this.conn,
                batchAttributesContainer.getPrestStmtMap(),
                batchAttributesContainer.getExecutedRowsMap(),
                batchAttributesContainer.getRowCounter(),
                namedSql,
                parameterMap);
    }

    /**
     * 得到各个SQL语句成功执行的次数。
     *
     * @return key=namedSql语句，value等于该namedSql每一个批次执行的成功次数。
     */
    @Override
    public HashMap<String, ArrayList<int[]>> getExecutedRows() {
        return batchAttributesContainer.getExecutedRowsMap();
    }
}
