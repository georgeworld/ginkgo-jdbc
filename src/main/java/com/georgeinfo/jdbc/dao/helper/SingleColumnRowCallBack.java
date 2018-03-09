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
public interface SingleColumnRowCallBack extends SingleRowCallBack {

    /**
     * 当查询出的值转换成指定的返回值类型出错时调用
     *
     * @param originalValue 从数据库中查询出的原始值
     * @param ex 当发生类型转换错误时抛出的异常
     * @return 应用层自己转换后的返回值
     */
    public Object whenTypeMismatch(Object originalValue, RuntimeException ex);
}
