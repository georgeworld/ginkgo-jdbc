/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.namedparam;

import com.georgeinfo.jdbc.dao.utils.DaoException;
import com.georgeinfo.jdbc.dao.utils.SqlAndParams;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author George <Georgeinfo@163.com> | <GeorgeNiceWorld@gmail.com>
 */
public class NamedSqlUtils {

    /*
     public static void main(String[] args) {
     long startTime = System.currentTimeMillis();
     String sql = "select phone,batch from phone_no_reg where to_date(:begin,'yyyy-MM-dd') > sysdate and batchId=:batchId+1 and creation_time between to_date(:begin,'yyyy-MM-dd') and age < 20 and to_date(:end,'yyyy-MM-dd') and cust_id = :custId and valid = :valid and company_name != ':beginX' and team = ':team' and team2 = ':endX' order by phone desc :batchId";

     Map<String, Object> map = new HashMap<String, Object>();
     map.put("valid", "12345");
     map.put("custId", 1001);
     map.put("teaa", ":teaname");
     map.put("begin", "09:12:23");
     map.put("end", "09:12:24");
     map.put("batchId", "110");

     SqlAndParams sqlAndParams = doPreSql(sql, map);
     String preSql = sqlAndParams.getSql();
     ArrayList list = sqlAndParams.getParams();
     System.out.println(map);
     System.out.println(sql);
     System.out.println(preSql);
     System.out.println(list);
     long endTime = System.currentTimeMillis();
     System.out.println("��ʱ��" + (endTime - startTime));

     System.out.println(SqlParam.initParam("wjk", "王金魁") instanceof Map);
    
     Map<String, Object> param = new HashMap<String, Object>();
     param.put("fieldA", "aaa");
     param.put("fieldC", "ccc");

     SqlAndParams sap = getPreparedSqlAndSortedParamsMap("insert into table (fieldA,fieldB,fieldC) values (:fieldA,':fieldB',:fieldC)", param);

     HashMap<String, Integer> paramIndexMap = sap.getParamIndexMap();
     for (Map.Entry<String, Integer> entry : paramIndexMap.entrySet()) {
     System.out.println("参数" + entry.getKey() + "的索引是：" + entry.getValue());
     }

     }
     * */
    /**
     * 使用占位符sql语句和无序参数map组装出JDBC预查询sql语句和有序的JDBC参数list
     *
     * @param sql 使用了占位符的sql语句，类似：select * from table where a = :a and b = :b
     * @param paramsMap 无序的参数map
     * @return
     */
    public static SqlAndParams getPreparedSqlAndSortedParamsMap(String sql, Map<String, Object> paramsMap) {
        if (sql == null) {
            throw new DaoException("## 执行JDBC处理时出现异常，所执行的SQL语句不能为空！");
        }

        String[] sqlFragmentArray = sql.split("'(.*?)'");
        Pattern pattern = Pattern.compile("'(.*?)'", Pattern.DOTALL);
        Matcher match = pattern.matcher(sql);

        ArrayList paramList = null;
        HashMap<String, Integer> paramIndexMap = null;

        if (paramsMap != null && !paramsMap.isEmpty()) {
            paramList = new ArrayList();
            paramIndexMap = new HashMap<String, Integer>();
            SqlFragmentAndParamIndex sfpi;
            Integer paramIndex = 1;
            for (int i = 0; i < sqlFragmentArray.length; i++) {
                String sqlFragment = sqlFragmentArray[i];
                if (sqlFragment.indexOf(":") > -1) {
                    sfpi = parseSqlStatement(paramIndex, sqlFragment, paramsMap, paramList, paramIndexMap);
                    paramIndex = sfpi.getParamIndex();
                    sqlFragmentArray[i] = sfpi.getSqlFragment();
                }
            }
        }

        String prepareSQL = "";
        int i = 0;
        while (match.find()) {
            String ti = match.group();
            prepareSQL += sqlFragmentArray[i] + ti;
            i++;
        }
        if (i < sqlFragmentArray.length) {
            prepareSQL += sqlFragmentArray[i];
        }

        SqlAndParams result = new SqlAndParams(prepareSQL, paramList, paramIndexMap);

        return result;
    }

    public static String getPreparedSql(String sql) {
        if (sql == null) {
            throw new DaoException("## 执行JDBC处理时出现异常，所执行的SQL语句不能为空！");
        }

        String[] sqlFragmentArray = sql.split("'(.*?)'");
        Pattern pattern = Pattern.compile("'(.*?)'", Pattern.DOTALL);
        Matcher match = pattern.matcher(sql);

//        ArrayList paramList = null;
//
//            paramList = new ArrayList();
//            for (int i = 0; i < sqlFragmentArray.length; i++) {
//                String sqlFragment = sqlFragmentArray[i];
//                if (sqlFragment.indexOf(":") > -1) {
//                    sqlFragmentArray[i] = replaceWord(sqlFragment, paramsMap, paramList);
//                }
//            }
        String prepareSQL = "";
        int i = 0;
        while (match.find()) {
            System.out.println("找到了");
            String ti = match.group();
            prepareSQL += sqlFragmentArray[i] + ti;
            i++;
        }
        if (i < sqlFragmentArray.length) {
            prepareSQL += sqlFragmentArray[i];
        }

//        SqlAndParams result = new SqlAndParams(prepareSQL, paramList);
        return prepareSQL;
    }

    /**
     *
     * @param sqlFragment SQL片段
     * @param paramsMap 应用层传递过来的SQL参数map���
     * @param paramList 实际使用的SQL参数列表���
     * @return
     */
    private static SqlFragmentAndParamIndex parseSqlStatement(Integer paramIndex, String sqlFragment, Map<String, Object> paramsMap, ArrayList paramList, HashMap<String, Integer> paramIndexMap) {
        SqlFragmentAndParamIndex result = new SqlFragmentAndParamIndex();
        String[] keyArrayOfParamsMap = paramsMap.keySet().toArray(new String[paramsMap.keySet().size()]);
        if (sqlFragment == null || sqlFragment.trim().length() == 0) {
            result.setSqlFragment(sqlFragment);
            result.setParamIndex(paramIndex);
            return result;
        } else {
            sortStrArray(keyArrayOfParamsMap);//排序
            String patt = makePatt(keyArrayOfParamsMap);
            Pattern p = Pattern.compile(patt);
            Matcher match = p.matcher(sqlFragment);
            int tt = 0;
            String paramName;
            while (match.find()) {
                String ti = match.group();
                int start = match.start() + tt;
                int end = match.end() + tt;

                paramName = ti.substring(1);
                //将找到的参数名放入paramIndexMap
                paramIndexMap.put(paramName, paramIndex);
                //将找到的参数名放入paramIndexMap后，将参数索引值paramIndex加1
                paramIndex++;
                Object param = paramsMap.get(paramName);
                paramList.add(param);
                String temp = "?";
                tt += temp.length() - ti.length();
                int[] dd = {start, end};
                sqlFragment = doReplaceStr(sqlFragment, dd, temp);
            }

            result.setParamIndex(paramIndex);
            result.setSqlFragment(sqlFragment);
        }
        return result;
    }

    /**
     * ƴ��ƥ������
     *
     * @param key
     * @return
     */
    private static String makePatt(String[] key) {
        if (key == null || key.length == 0) {
            return "";
        }
        StringBuffer patt = new StringBuffer();
        for (String str : key) {
            patt.append("(:").append(str).append(")").append("|");
        }
        patt.deleteCharAt(patt.lastIndexOf("|"));

        return patt.toString();
    }

    /**
     * �õ��ɴ�С������
     *
     * @param sss
     */
    private static void sortStrArray(String[] sss) {
        int len = sss.length;
        if (len == 0) {
            return;
        }
        Arrays.sort(sss);
        int i = len / 2;
        for (int j = 0; j < i; j++) {
            String temp = sss[j];
            sss[j] = sss[len - 1 - j];
            sss[len - 1 - j] = temp;
        }

    }

    /**
     * �滻ָ��λ�õ��ַ�Ϊ�µ��ַ�
     *
     * @param oldStr
     * @param index 0��ʼ 1����
     * @param newStr
     * @return
     */
    private static String doReplaceStr(String oldStr, int[] index, String newStr) {
        if (index.length != 2) {
            return "";
        }
        int start = index[0];
        int end = index[1];
        String firstStr = oldStr.substring(0, start);
        String secondStr = oldStr.substring(end);

        return firstStr + newStr + secondStr;
    }
}
