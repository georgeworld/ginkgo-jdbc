/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 单独一行查询回调接口
 *
 * @author George <Georgeinfo@163.com>
 */
public interface SingleRowCallBack {

    /**
     * 当查询结果是多行结果集时，调用本方法。
     *
     * @param results 查询出的多行结果集
     * @param ex 当遇到多个记录行返回值时，抛出的异常
     * @return 经过应用层转换后的指定类型返回值
     */
    public Object whenIncorrectResultSize(Collection results, RuntimeException ex);

    /**
     * 当查询结果是一行，但是是多列时，调用本方法。
     *
     * @param dataRow 一行结果集
     * @param ex 当遇到多列返回值时，抛出的异常
     * @return 经过应用层转换后的指定类型返回值
     */
    public Object whenIncorrectResultSetColumnCount(ArrayList<DataRow> dataRow, RuntimeException ex);
}
