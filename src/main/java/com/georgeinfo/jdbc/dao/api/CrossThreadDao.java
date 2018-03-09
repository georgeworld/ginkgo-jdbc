/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.api;

import com.georgeinfo.jdbc.dao.support.ThreadConverger;

/**
 * 支持跨线程事务的Dao接口
 * @author George <Georgeinfo@163.com>
 */
public interface CrossThreadDao extends GSDao,ThreadConverger {
    
}
