/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author George <georgeinfo@163.com>
 */
public class LI {

    public static List toList(Object... params) {
        List list = Arrays.asList(params);
        return list;
    }

    /**
     * 用法：
     * LI.toProcParams(InOrOut.IN,"参数A名字","参数A值",java.sql.Types类型的一种，如java.sql.Types.VARCHAR,
     * InOrOut.IN,"参数B名字","参数B值",java.sql.Types类型的一种，如java.sql.Types.VARCHAR,
     * InOrOut.OUT,"存储过程返回值的名字","存储过程返回值的值",java.sql.Types类型的一种，如java.sql.Types.VARCHAR,
     * );
     */
    public static LinkedHashMap<String, ProcedureParam> toProcParams(Object... params) {
        if (params != null) {
            LinkedHashMap<String, ProcedureParam> map = new LinkedHashMap<String, ProcedureParam>();

            ProcedureParam p = new ProcedureParam();
            int mark = 1;
            String name = null;
            for (Object o : params) {
                switch (mark) {
                    case 1:
                        p.setInOrOut((InOrOut) o);
                        break;
                    case 2:
                        name = String.valueOf(o);
                        p.setName(name);
                        break;
                    case 3:
                        p.setValue(o);
                        break;
                    case 4:
                        p.setSqlType(Integer.valueOf(String.valueOf(o)));
                        map.put(name, p);
                        p = new ProcedureParam();
                        mark = 0;
                        break;
                    default:
                        break;
                }
                mark++;
            }

            return map;
        } else {
            return null;
        }
    }
}
