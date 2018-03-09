/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.helper;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 批量执行相关的参数容器
 *
 * @author George <Georgeinfo@163.com>
 */
public class BatchAttributesContainer {

    private final AtomicInteger rowCounter;;
    private final HashMap<String, PreparedStatement> prestStmtMap;
    private final HashMap<String, ArrayList<int[]>> executedRowsMap;

    public BatchAttributesContainer(AtomicInteger rowCounter, HashMap<String, PreparedStatement> prestStmtMap, HashMap<String, ArrayList<int[]>> executedRowsMap) {
        this.rowCounter = rowCounter;
        this.prestStmtMap = prestStmtMap;
        this.executedRowsMap = executedRowsMap;
    }

    public AtomicInteger getRowCounter() {
        return rowCounter;
    }

    public HashMap<String, PreparedStatement> getPrestStmtMap() {
        return prestStmtMap;
    }

    public HashMap<String, ArrayList<int[]>> getExecutedRowsMap() {
        return executedRowsMap;
    }
}
