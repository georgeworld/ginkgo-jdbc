/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 * @param <IT>
 */
public abstract class BatchRowCallbackHandler<IT> {

    public abstract String buildMainSql(IT item);

    public abstract Map<String, String> buildMemberSqlMap(IT itme);

    public abstract void processRow(HashMap<String, Object> mainSqlParamsMap, HashMap<String, HashMap<String, Object>> memberSqlParamsMap, IT item);
}
