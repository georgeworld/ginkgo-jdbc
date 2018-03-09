/*
 * Copyright 2007-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.georgeinfo.jdbc.dao.transaction;

import java.sql.Connection;
import java.sql.Savepoint;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库事务容器
 *
 * @author George <GeorgeNiceWorld@gmail.com>
 *
 */
public class TransactionContainer {

    /**
     * 存放当前线程所处理的数据库连接对象
     */
    private final static ThreadLocal<ConcurrentHashMap<Integer, Connection>> currentConn = new ThreadLocal<ConcurrentHashMap<Integer, Connection>>();
    /**
     * 存放当前线程所处理的保存掉，为部分回滚服务。
     */
    private final static ThreadLocal<ConcurrentHashMap<Integer, ConcurrentHashMap<String, Savepoint>>> currentSavepointMap = new ThreadLocal<ConcurrentHashMap<Integer, ConcurrentHashMap<String, Savepoint>>>();

    public static void bindConnToCurrentThread(Integer objHashCode, Connection conn) {
        if (conn != null) {
            ConcurrentHashMap<Integer, Connection> m = currentConn.get();
            if (m == null) {
                m = new ConcurrentHashMap<Integer, Connection>();
            }
            m.put(objHashCode, conn);
            currentConn.set(m);
        } else {
            unindConnFromCurrentThread(objHashCode);
        }
    }

    public static void unindConnFromCurrentThread(Integer objHashCode) {
        ConcurrentHashMap<Integer, Connection> m = currentConn.get();
        if (m != null) {
            m.remove(objHashCode);
        }
    }

    public static Connection getCurrentThreadConn(Integer objHashCode) {
        ConcurrentHashMap<Integer, Connection> m = currentConn.get();
        if (m == null) {
            return null;
        }
        return m.get(objHashCode);
    }

    private static ConcurrentHashMap<String, Savepoint> getCurrentSavepointMap(Integer objHashCode) {
        ConcurrentHashMap<Integer, ConcurrentHashMap<String, Savepoint>> m = currentSavepointMap.get();
        if (m == null) {
            m = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, Savepoint>>();
        }
        ConcurrentHashMap<String, Savepoint> spm = m.get(objHashCode);
        if (spm == null) {
            spm = new ConcurrentHashMap<String, Savepoint>();
            m.put(objHashCode, spm);
            currentSavepointMap.set(m);
        }

        return spm;
    }

    public static void bindSavepointToCurrentThread(Integer objHashCode, String key, Savepoint sp) {
        if (sp != null) {
            ConcurrentHashMap<String, Savepoint> spm = getCurrentSavepointMap(objHashCode);
            spm.put(key, sp);
            //currentSavepointMap.set(spm);
        } else {
            unindSavepointFromCurrentThread(objHashCode, key);
        }
    }

    public static void unindSavepointFromCurrentThread(Integer objHashCode, String key) {
        ConcurrentHashMap<String, Savepoint> spm = getCurrentSavepointMap(objHashCode);
        spm.remove(key);
    }

    public static Savepoint getCurrentThreadSavepoint(Integer objHashCode, String key) {
        ConcurrentHashMap<Integer, ConcurrentHashMap<String, Savepoint>> m = currentSavepointMap.get();
        if (m != null) {
            ConcurrentHashMap<String, Savepoint> savepointMap = m.get(objHashCode);
            if (savepointMap != null) {
                return savepointMap.get(key);
            }
        }
        return null;
    }
}
