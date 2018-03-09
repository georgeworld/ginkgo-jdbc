/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import java.util.ArrayList;

/**
 * queryOne(...)方法的原始数据库值处理回调接口
 *
 * @author George <Georgeinfo@163.com>
 */
public interface QueryOneCallBackInner {

    /**
     * 当查询结果是多个值时，调用此方法
     * @param dataRows 查询出的多条数据
     * @return 定期queryOne(...)方法的返回值
     */
    public Object whenMultipleValues(ArrayList<DataRow> dataRows);
}
