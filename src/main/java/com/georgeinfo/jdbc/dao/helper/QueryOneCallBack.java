/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

/**
 * queryOne(...)方法的原始数据库值处理回调接口
 *
 * @author George <Georgeinfo@163.com>
 */
public interface QueryOneCallBack extends QueryOneCallBackInner {

    /**
     * 当查询出的值转换成指定的返回值类型出错时调用
     *
     * @param originalValue 从数据库中查询出的原始值
     * @return 定期queryOne(...)方法的返回值
     */
    public Object whenValueClassCastException(Object originalValue);
}
