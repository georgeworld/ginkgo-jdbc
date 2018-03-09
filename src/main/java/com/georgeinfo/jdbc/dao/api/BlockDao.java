/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.api;

import com.georgeinfo.jdbc.dao.support.BlockTransactionManager;

/**
 * 无事务单条自动提交和块状事务接口
 *
 * @author George <Georgeinfo@163.com>
 */
public interface BlockDao extends DaoApi, BlockTransactionManager {

}
