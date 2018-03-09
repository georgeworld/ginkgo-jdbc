package com.georgeinfo.jdbc.utils;

import java.util.Map;

/**
 * 处理sql字符串的拼接组装
 *
 * @author wjk<wjk33atp@yahoo.cn>
 *
 */
public class LiSqlIn {
    /*public static void main(String[] args) {
     String ids = "1001,1002,1003,1004";
     Map<String, Object> map = new HashMap<String, Object>();
     String preName = "accountId";
     StringBuffer whereClause = new StringBuffer();
     //我想得到   " and accountId in ( :accountId_1,:accountId_2,:accountId_3,:accountId_4 )"
     putSqlInParam(whereClause,map,preName,ids);
     System.out.println("whereClause:\t" + whereClause);
     System.out.println("map:\t\t" + map);
     }*/

    /**
     * 内部拼接字符串 如 coloumName='accountId'得到 " and accountId in (
     * :accountId_1,:accountId_2,:accountId_3,:accountId_4 )"
     *
     * @param whereClause	StringBuffer类型待拼接字符串
     * @param map	查询参数map
     * @param coloumName	数据库中此字段名字
     * @param ids	ID字符串 逗号隔开
     * @param pageParamName 页面参数名字
     */
    @SuppressWarnings("unused")
    public static void putSqlInParam(StringBuffer whereClause,
            Map<String, Object> map, String coloumName, String ids, String pageParamName) {
        if (ids == null || ids == "" || coloumName == null || coloumName == "") {
            return;
        }
        map.put(pageParamName, ids);//为了分页的正常显示
        String[] _ids = ids.split(",");
        whereClause.append(" and ").append(coloumName).append(" in ( ");
        String preNameTemp = coloumName + "_";
        for (int i = 0; i < _ids.length; i++) {
            map.put(preNameTemp + i, _ids[i]);
            whereClause.append(_ids[i]).append(",");
        }
        whereClause.deleteCharAt(whereClause.length() - 1);
        whereClause.append(" ) ");
    }
}
