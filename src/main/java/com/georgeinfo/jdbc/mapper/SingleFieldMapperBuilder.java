/*
* Copyright (c) George software studio, All Rights Reserved.
* George <GeorgeWorld@qq.com> | QQ:178069108 | www.georgeinfo.com 
*/
package com.georgeinfo.jdbc.mapper;

import com.georgeinfo.jdbc.dao.helper.SingleColumnRowCallBack;


/**
 * 单字段值映射器构造者
 *
 * @author George<GeorgeWorld@qq.com>
 */
public class SingleFieldMapperBuilder<T> {
    private Class<T> targetType;
    private SingleColumnRowCallBack singleColumnRowCallBack;

    /**
     * 默认构造函数，使用此构造函数创建了本类实例之后，给必要的类成员属性赋值，
     * 然后调用build()方法，得到最终工作的mapper对象，本类本身只是一个构建器，
     * 用来分布构建Mapper对象的。
     */
    public SingleFieldMapperBuilder() {
    }

    public SingleFieldMapperBuilder<T> setTargetType(Class<T> targetType) {
        this.targetType = targetType;
        return this;
    }

    public SingleFieldMapperBuilder<T> setSingleColumnRowCallBack(SingleColumnRowCallBack singleColumnRowCallBack) {
        this.singleColumnRowCallBack = singleColumnRowCallBack;
        return this;
    }

    public SingleFieldMapper<T> build() {
        SingleFieldMapper<T> mapper = new SingleFieldMapper<T>();
        mapper.setTargetType(this.targetType);
        mapper.setSingleColumnRowCallBack(this.singleColumnRowCallBack);

        return mapper;
    }
}

