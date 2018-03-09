/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.impl;

import com.georgeinfo.base.util.clazz.TypeConverter;
import com.georgeinfo.jdbc.dao.helper.DataRow;
import com.georgeinfo.jdbc.dao.helper.JdbcRowCallbackHandler;
import com.georgeinfo.jdbc.dao.helper.BasicType;
import com.georgeinfo.jdbc.mapper.*;
import com.georgeinfo.jdbc.mapper.exception.MapperException;
import com.georgeinfo.jdbc.mapper.exception.TypeMismatchOrIndexInvalidException;
import com.georgeinfo.jdbc.utils.ProcedureParam;
import com.georgeinfo.jdbc.dao.utils.DaoException;
import com.georgeinfo.jdbc.dao.utils.SqlAndParams;
import com.georgeinfo.jdbc.namedparam.NamedSqlUtils;
import com.georgeinfo.base.util.database.JdbcTypes;
import com.georgeinfo.base.util.logger.GeorgeLogger;
import com.georgeinfo.base.util.reflect.ReflectionTool;
import com.georgeinfo.base.util.string.StringTool;

import static com.georgeinfo.jdbc.dao.api.BatchDaoFeature.DEFAULT_BATCH_SIZE;

import com.georgeinfo.jdbc.dao.api.DaoApi;
import com.georgeinfo.jdbc.dao.helper.SingleColumnRowCallBack;
import com.georgeinfo.jdbc.dao.utils.DaoUtil;
import com.georgeinfo.jdbc.namedparam.GeorgeNamedParameterUtils;
import com.georgeinfo.jdbc.namedparam.GeorgeParsedSql;
import com.georgeinfo.jdbc.namedparam.PreparedSqlAndParamsIndexMap;
import gbt.config.GeorgeLoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

/**
 * 基本的数据库操作，本身不支持事务，事务由外面调用本类者来维护
 *
 * @author George <Georgeinfo@163.com>
 */
public class DaoExecutor {

    protected static final GeorgeLogger LOG = GeorgeLoggerFactory.getLogger(DaoExecutor.class);

    /**
     * 将ResultSet数据集，转换成指定的ArrayList<实体类或DataRow/Map>列表，<br/>
     * 只能转换成引用类型（实体类或DataRow/Map），不能转换成基本类型
     */
    private static <T> ArrayList<T> convertToReferenceTypeObjList(ResultSet rs, Class<T> rowType) throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        ArrayList<T> retList = new ArrayList<T>();
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();
        while (rs.next()) {
            DataRow recordMap = new DataRow();

            for (int i = 1; i <= colCount; i++) {
                //如果使用meta.getColumnName(i)，则当SQL语句中有别名时，取得的还是字段名称，而不是别名
                //而使用meta.getColumnLabel(i)则可以实现：当SQL中有字段别名时，取到的是字段别名，没有别名时，取到的是字段名称
                String name = meta.getColumnLabel(i);
                int sqlType = meta.getColumnType(i);
                String columnTypeName = meta.getColumnTypeName(i);

                Object value = JdbcTypes.convertTypeOfResultSetField(rs, i, sqlType, columnTypeName);

                //将数据库字段名称，转换成驼峰字符串，存入map
                name = StringTool.dbField2AttributeName(name);
                recordMap.put(name, value);
            }

            if (!recordMap.isEmpty()) {
                //本方法不会处理应用层要求返回为基本类型的场景，本方法只处理返回类型为Map或引用类型(实体类对象)
                T rowObject = rowType.newInstance();
                ReflectionTool.populate(rowObject, recordMap);
                retList.add(rowObject);
            }
        }

        return retList;
    }

    private static LinkedHashMap<String, Integer> applyParamsForProcedure(CallableStatement cstmt, LinkedHashMap<String, ProcedureParam> params) throws SQLException {
        LinkedHashMap<String, Integer> outParamsMap = new LinkedHashMap<String, Integer>();
        if ((params != null) && (!params.isEmpty())) {
            int index = 1;
            for (Map.Entry<String, ProcedureParam> entry : params.entrySet()) {
                //如果是out输出参数，则注册之
                if (entry.getValue().isOutParam()) {
                    cstmt.registerOutParameter(index, entry.getValue().getSqlType());
                    outParamsMap.put(entry.getKey(), index);
                } else {
                    //填充参数值
                    if (entry.getValue().getValue() == null) {
                        cstmt.setObject(index, "");
                    } else {
                        cstmt.setObject(index, entry.getValue().getValue());
                    }
                }

                index++;
            }
        }

        return outParamsMap;
    }

    private static void setValueToPreparedStatement(int index, PreparedStatement pstmt, Object paramValue) throws SQLException {
        if (paramValue == null) {
            pstmt.setObject(index, null);
        } else {
            if (paramValue instanceof java.util.Date) {
                pstmt.setTimestamp(index, new java.sql.Timestamp(((java.util.Date) paramValue).getTime()));
            } else if (paramValue instanceof java.sql.Timestamp) {
                pstmt.setTimestamp(index, (java.sql.Timestamp) paramValue);
            } else {
                pstmt.setObject(index, paramValue);
            }
        }
    }

    private static void apply(PreparedStatement pstmt, List params) throws SQLException {
        if ((params != null) && (!params.isEmpty())) {
            int index = 1;
            for (Object paramValue : params) {

                setValueToPreparedStatement(index, pstmt, paramValue);

                index++;
            }
        }
    }

    /**
     * 查询数据集（没有查询参数）
     *
     * @param conn 数据库连接对象，由事务控制层Daoc传递过来
     * @param sql  被执行的SQL语句
     * @return 一行数据是一个map，所有行数据组成一个List<DataRow>
     * @throws java.sql.SQLException
     */
    public static ArrayList<DataRow> queryList(Connection conn, String sql) throws SQLException {
        return doQueryDataRowListOrCallback(conn, sql, new ArrayList(), null);
    }

    /**
     * 查询数据集（有查询参数）
     *
     * @param conn   数据库连接对象，由事务控制层Daoc传递过来
     * @param sql    被执行的 SQL语句
     * @param params SQL参数map
     * @return 查询出的数据集列表
     * @throws java.sql.SQLException
     */
    public static ArrayList<DataRow> queryList(Connection conn, String sql, Map<String, Object> params) throws SQLException {
        SqlAndParams sqlAndParams = NamedSqlUtils.getPreparedSqlAndSortedParamsMap(sql, params);
        sql = sqlAndParams.getSql();
        ArrayList list = sqlAndParams.getParams();

        return doQueryDataRowListOrCallback(conn, sql, list, null);
    }

    /**
     * 查询数据集（有查询参数） 指定行类型（基本类型）查询数据集
     *
     * @param conn    数据库连接对象，由事务控制层Daoc传递过来
     * @param sql     被执行的 SQL语句
     * @param params  SQL参数map
     * @param rowType 返回值类型
     * @return 查询出的数据集列表
     */
    public static <T> ArrayList<T> queryListBasicValue(Connection conn, String sql,
                                                       Map<String, Object> params, Class<T> rowType)
            throws SQLException,TypeMismatchOrIndexInvalidException {
        SingleFieldMapper<T> row2ObjectMapper = new SingleFieldMapperBuilder<T>()
                .setTargetType(rowType)
                .build();

        List<T> results = queryListByExtractor(conn, sql, params, row2ObjectMapper);
        return new ArrayList<T>(results);
    }

    /**
     * 指定行类型查询数据集
     *
     * @param <T>     一行的类型
     * @param conn    数据库连接对象
     * @param sql     被执行的SQL语句
     * @param params  被执行SQL语句的参数
     * @param rowType 一行的类型Class
     * @return
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.reflect.InvocationTargetException
     * @throws java.lang.NoSuchMethodException
     * @throws java.lang.InstantiationException
     */
    public static <T> ArrayList<T> queryList(Connection conn, String sql, Map<String, Object> params, Class<T> rowType) throws SQLException, ClassNotFoundException,
            IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException,TypeMismatchOrIndexInvalidException {
        if (TypeConverter.isPrimitiveClass(rowType)
                || rowType == String.class
                || rowType == Date.class
                || rowType == BigDecimal.class
                || rowType == BigInteger.class
                || java.sql.Date.class == rowType
                || java.sql.Time.class == rowType
                || java.sql.Timestamp.class == rowType
                || byte[].class == rowType
                || Blob.class == rowType
                || Clob.class == rowType) {//如果要求传回的是Java原始类型或者常用的几种基本引用类型
            return queryListBasicValue(conn, sql, params, rowType);
        } else {//如果要求传回的是Map或者实体类列表
            SqlAndParams sqlAndParams = NamedSqlUtils.getPreparedSqlAndSortedParamsMap(sql, params);
            sql = sqlAndParams.getSql();
            ArrayList list = sqlAndParams.getParams();

            return doQueryListSpecifiedRowTypeOrCallback(conn, sql, list, rowType, null);
        }
    }

    public static void query(Connection conn, String sql, JdbcRowCallbackHandler rowCallbackHandler) throws SQLException {
        doQueryDataRowListOrCallback(conn, sql, new ArrayList(), rowCallbackHandler);
    }

    public static void query(Connection conn, String sql, Map<String, Object> params, JdbcRowCallbackHandler rowCallbackHandler) throws SQLException {
        SqlAndParams sqlAndParams = NamedSqlUtils.getPreparedSqlAndSortedParamsMap(sql, params);
        sql = sqlAndParams.getSql();
        ArrayList list = sqlAndParams.getParams();

        doQueryDataRowListOrCallback(conn, sql, list, rowCallbackHandler);
    }

    /**
     * 指定行类型（实体类或DataRow/Map）进行查询（查询出的是数据集）
     */
    private static <T> ArrayList<T> doQueryListSpecifiedRowTypeOrCallback(Connection conn, String sql, List params, Class<T> rowType, JdbcRowCallbackHandler rowCallbackHandler)
            throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        ArrayList<T> rowList = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            printSQL(sql, params);
            pstmt = conn.prepareStatement(sql);
            apply(pstmt, params);
            rs = pstmt.executeQuery();
            //如果没有行回调处理器，
            //则把每行的结果集组装成一个ArrayList<HashMap<String, Object>>，最后返回
            if (rowCallbackHandler == null) {
                rowList = convertToReferenceTypeObjList(rs, rowType);
            } else {//如果有行回调处理器，则使用行回调处理器处理每行的ResultSet
                while (rs.next()) {
                    rowCallbackHandler.processRow(rs);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        }
        return rowList;
    }

    /**
     * 重要的内部方法，几乎所有的查询都是由这个方法来实现的
     */
    private static ArrayList<DataRow> doQueryDataRowListOrCallback(Connection conn, String sql, List params, JdbcRowCallbackHandler rowCallbackHandler) throws SQLException {
        ArrayList<DataRow> result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            printSQL(sql, params);
            pstmt = conn.prepareStatement(sql);
            apply(pstmt, params);
            rs = pstmt.executeQuery();
            //如果没有行回调处理器，
            //则把每行的结果集组装成一个ArrayList<HashMap<String, Object>>，最后返回
            if (rowCallbackHandler == null) {
                result = DaoUtil.convertToDataRowList(rs);
            } else {//如果有行回调处理器，则使用行回调处理器处理每行的ResultSet
                while (rs.next()) {
                    rowCallbackHandler.processRow(rs);
                }
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        }
        return result;
    }

    public static DataRow queryOneRow(Connection conn, String sql, Map<String, Object> params) throws SQLException {
        ArrayList<DataRow> list = queryList(conn, sql, params);

        if (list != null && !list.isEmpty()) {
            DataRow record = list.get(0);
            if (record != null && !record.isEmpty()) {
                return record;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private static <LT> List<LT> queryListByExtractor(Connection conn, String sql, Map<String, Object> params, SingleFieldMapper<LT> sfm)
            throws SQLException,TypeMismatchOrIndexInvalidException {
        if (sql == null || sql.trim().isEmpty()) {
            throw new MapperException("SQL must not be null");
        }
        if (sfm == null) {
            throw new MapperException("#ResultSetExtractor must not be null");
        }

        SqlAndParams sqlAndParams = NamedSqlUtils.getPreparedSqlAndSortedParamsMap(sql, params);
        sql = sqlAndParams.getSql();
        ArrayList list = sqlAndParams.getParams();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        printSQL(sql, params);
        pstmt = conn.prepareStatement(sql);
        apply(pstmt, list);
        rs = pstmt.executeQuery();

        List<LT> result = sfm.extractData(rs);
        rs.close();
        pstmt.close();

        return result;
    }

    private static <T> T queryOneBasicValue(Connection conn, String sql, Map<String, Object> params, Class<T> requiredType)
            throws SQLException, DaoException {
        return queryOneBasicValue(conn, sql, params, requiredType, null);
    }

    public static <T> T queryOneBasicValue(Connection conn, String sql, Map<String, Object> params, BasicType requiredTypeEnum)
            throws SQLException, DaoException {
        return queryOneBasicValue(conn, sql, params, requiredTypeEnum.getTypeClass(), null);
    }

    /**
     * 查询一行中的一个字段的值，转换成基本类型（8种Primitive原始类型，或Date/BigInteger/Clob/String等常用基本类型），<br />
     * 通常用于select count(*)、select 序列.nextval()等场景。
     *
     * @param <T>                     返回值的类型
     * @param conn                    数据库连接对象
     * @param sql                     被执行的SQL语句
     * @param params                  被执行SQL语句的参数
     * @param targetType              应用层要求返回的类型的Class
     * @param singleColumnRowCallBack 单条查询回调接口
     * @return 查询出的基本类型值
     * @throws java.sql.SQLException
     */
    public static <T> T queryOneBasicValue(Connection conn, String sql,
                                           Map<String, Object> params,
                                           Class targetType,
                                           SingleColumnRowCallBack singleColumnRowCallBack)
            throws SQLException, TypeMismatchOrIndexInvalidException {
        SingleFieldMapper<T> row2ObjectMapper = null;
        if (singleColumnRowCallBack == null) {
            row2ObjectMapper = new SingleFieldMapperBuilder<T>().setTargetType(targetType).build();
        } else {
            row2ObjectMapper = new SingleFieldMapperBuilder<T>()
                    .setTargetType(targetType)
                    .setSingleColumnRowCallBack(singleColumnRowCallBack)
                    .build();
        }

        List<T> results = queryListByExtractor(conn, sql, params, row2ObjectMapper);
        if (results != null && !results.isEmpty()) {
            try {
                if (singleColumnRowCallBack != null) {
                    return MapperUtils.requiredSingleResult(results, singleColumnRowCallBack);
                } else {
                    return MapperUtils.requiredSingleResult(results);
                }
            } catch (MapperException ex) { //查处了空记录
                LOG.error("## An exception occurs when a record is extracted.", ex);
                return null;
            }
        } else {
            return null;
        }
    }

    public static <T> T queryOneEntity(Connection conn, String sql, Map<String, Object> params, Class<T> requiredType)
            throws SQLException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        SqlAndParams sqlAndParams = NamedSqlUtils.getPreparedSqlAndSortedParamsMap(sql, params);
        sql = sqlAndParams.getSql();
        ArrayList list = sqlAndParams.getParams();

        ArrayList<T> objectRows = doQueryListSpecifiedRowTypeOrCallback(conn, sql, list, requiredType, null);

        if (objectRows != null && !objectRows.isEmpty()) {
            return objectRows.get(0);
        } else {
            return null;
        }
    }

    public static <T> T queryOne(Connection conn, String sql, Map<String, Object> params, Class<T> requiredType) throws SQLException,
            ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException,
            DaoException, MapperException {
        if (TypeConverter.isPrimitiveClass(requiredType)
                || requiredType == String.class
                || requiredType == Date.class
                || requiredType == BigDecimal.class
                || requiredType == BigInteger.class
                || java.sql.Date.class == requiredType
                || java.sql.Time.class == requiredType
                || java.sql.Timestamp.class == requiredType
                || byte[].class == requiredType
                || Blob.class == requiredType
                || Clob.class == requiredType) {//如果要求传回的是基本类型或者常用的几种引用类型
            return queryOneBasicValue(conn, sql, params, requiredType);
        } else {//如果要求传回的是引用类型(Map或其他Java实体类对象)
            return queryOneEntity(conn, sql, params, requiredType);
        }
    }

    public static int execute(Connection conn, String sql, Map<String, Object> params) throws SQLException {
        SqlAndParams sqlAndParams = NamedSqlUtils.getPreparedSqlAndSortedParamsMap(sql, params);
        sql = sqlAndParams.getSql();
        ArrayList paramsLinked = sqlAndParams.getParams();

        return doExecute(conn, sql, paramsLinked);
    }

    private static int doExecute(Connection conn, String sql, List params) throws SQLException {
        int ret = 0;
        PreparedStatement pstmt = null;
        try {
            printSQL(sql, params);
            pstmt = conn.prepareStatement(sql);
            apply(pstmt, params);
            ret = pstmt.executeUpdate();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
        }
        return ret;
    }

    /**
     * @param conn                  数据库连接对象，由事务控制层Daoc传递过来
     * @param sql                   被执行的SQL语句
     * @param paramsMap             SQL语句参数map
     * @param sqlForGetLastInsertId 执行ID查询的枚举对象
     * @return 刚才插入成功的一条记录的ID
     * @throws java.sql.SQLException
     * @deprecated
     */
    public static int insertAndGetId(Connection conn, String sql, Map<String, Object> paramsMap, DaoApi.LastInsertId sqlForGetLastInsertId) throws SQLException {
        return insertAndGetId(conn, sql, paramsMap).intValue();
    }

    /**
     * @param conn      数据库连接对象，由事务控制层Daoc传递过来
     * @param sql       被执行的SQL语句
     * @param paramsMap SQL语句参数map
     * @return 刚才插入成功的一条记录的ID
     * @throws java.sql.SQLException
     */
    public static List<Long> insertAndGetIds(Connection conn, String sql, Map<String, Object> paramsMap) throws SQLException {
        List<Long> generatedIds = null;
        //int result = -1;
        int ret = 0;

        SqlAndParams sqlAndParams = NamedSqlUtils.getPreparedSqlAndSortedParamsMap(sql, paramsMap);
        sql = sqlAndParams.getSql();
        ArrayList paramsLinked = sqlAndParams.getParams();

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            printSQL(sql, paramsLinked);
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            apply(pstmt, paramsLinked);
            ret = pstmt.executeUpdate();

            generatedIds = new ArrayList<Long>();
            //得到刚刚插入记录的主键值
            rs = pstmt.getGeneratedKeys();
            while (rs.next()) {
                generatedIds.add(rs.getLong(1));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }
        }
        return generatedIds;
    }

    public static Long insertAndGetId(Connection conn, String sql, Map<String, Object> paramsMap) throws SQLException {
        List<Long> ids = insertAndGetIds(conn, sql, paramsMap);
        if (ids != null && !ids.isEmpty()) {
            return ids.get(0);
        }

        return null;
    }

    /**
     * 一行数据集是一个map，返回值是由多个数据集map组合成的list
     *
     * private List<HashMap<String, Object>> calculateKeys(ResultSet keys)
     * throws SQLException { // Prepare a list to contain the auto-generated
     * column // values, and then fetch them from the statement.
     * List<HashMap<String, Object>> autoKeys = new
     * ArrayList<HashMap<String, Object>>(); int count =
     * keys.getMetaData().getColumnCount();
     *
     * // Copy the column values into a list of a list. while (keys.next()) {
     * HashMap<String, Object> row = new HashMap<String, Object>(); for (int i =
     * 1; i <= count; i++) { row.put(keys.getMetaData().getColumnLabel(i),
     * keys.getObject(i)); }
     *
     * autoKeys.add(row); } return autoKeys; }
     */
    /**
     * 执行存储过程
     * <p>
     * 传入参数时，可以使用LI工具来构造，如：<br/>
     * LI.toProcParams(InOrOut.IN, "pSeqCode", "pf-merchant-no",
     * java.sql.Types.VARCHAR, InOrOut.OUT, "returnValue", null,
     * java.sql.Types.BIGINT)
     *
     * @param conn          数据库连接对象，由事务控制层Daoc传递过来
     * @param procedureName 存储过程名称
     * @param params        存储过程 in输入参数列表
     * @return
     * @throws java.sql.SQLException
     */
    public static HashMap<String, Object> executeProcedure(Connection conn, String procedureName, LinkedHashMap<String, ProcedureParam> params) throws SQLException {
        HashMap<String, Object> resultMap = new HashMap<String, Object>();
        StringBuffer paramsPlaceholders = new StringBuffer("");
        String paramsPlaceholdersValue = "";
        if (params != null && !params.isEmpty()) {
            int paramsSize = params.size();
            for (int i = 0; i < paramsSize; i++) {
                paramsPlaceholders.append("?").append(",");
            }
            paramsPlaceholdersValue = StringUtils.removeEnd(paramsPlaceholders.toString(), ",");
        }
        CallableStatement cstmt = null;
        try {
            printSQL(procedureName, paramsPlaceholdersValue);
            cstmt = conn.prepareCall("{call " + procedureName + "(" + paramsPlaceholdersValue + ")}");
            LinkedHashMap<String, Integer> nameListOfOutParams = applyParamsForProcedure(cstmt, params);
            cstmt.execute();

            if (nameListOfOutParams != null && !nameListOfOutParams.isEmpty()) {
                for (Map.Entry<String, Integer> entry : nameListOfOutParams.entrySet()) {
                    Object o = cstmt.getObject(entry.getValue().intValue());
                    //将存储过程返回值放入返回值map中
                    resultMap.put(entry.getKey(), o);

                    //将存储过程返回值放入到方法入参map中
                    if (params != null) {
                        ProcedureParam procParam = params.get(entry.getKey());
                        if (procParam != null) {
                            procParam.setValue(o);
                        }
                    }
                }
            }
        } finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }

        return resultMap;
    }

    public static List[] queryBatch(Connection conn, String[] sqlArray, Map<String, Object>[] paramArray) throws SQLException {
        List rets = new ArrayList();
        if (sqlArray.length != paramArray.length) {
            throw new DaoException("sql size not equal parameter size");
        }
        for (int i = 0; i < sqlArray.length; i++) {
            String sql = sqlArray[i];
            Map<String, Object> param = paramArray[i];
            ArrayList<DataRow> ret = queryList(conn, sql, param);
            rets.add(ret);
        }
        return (List[]) (List[]) rets.toArray();
    }

    public static int[] executeBatch(Connection conn, String[] sqlArray, Map<String, Object>[] paramArray) throws SQLException {
        List rets = new ArrayList();
        if (sqlArray.length != paramArray.length) {
            throw new DaoException("sql size not equal parameter size");
        }
        for (int i = 0; i < sqlArray.length; i++) {
            int ret = execute(conn, sqlArray[i], paramArray[i]);
            rets.add(ret);
        }

        int[] retArray = new int[rets.size()];
        for (int i = 0; i < retArray.length; i++) {
            retArray[i] = ((Integer) rets.get(i)).intValue();
        }

        return retArray;
    }

    //## 批量执行 方法 开始 #####################################################

    /**
     * 正式的批量操作方法，经过实际检验，很好用！ 本批量执行方法应该在业务层的循环中被调用，循环的迭代器是待被批量保存的数据集合。
     * 可以在循环中使用多种的SQL语句来调用本方法，每一【种】sql语句，都会被自动创建一个与之对应的 PreparedStatement
     * 对象，然后该【种】 sql语句的数据会被批量执行。<br />
     * 也就是：该批量执行方法，可以在业务层的一个循环内，执行多种SQL语句，比如在循环内，你需要
     * 保存一种数据，同时需要更新一种数据，这两种SQL操作都是与循环迭代器相关的批量操作，则你可以在
     * 循环中调用两次本方法，用于执行两种SQL语句，本方法完全支持这种复杂的逻辑。<br />
     * 【需要注意的是】：每种SQL语句在批量执行中，批量执行的批次大小，并不一定等于 本类接口中 DEFAULT_BATCH_SIZE
     * 定义的值，当然，如果 在业务层迭代器循环中，只执行一种SQL语句的话，批量执行的批次大小是等于DEFAULT_BATCH_SIZE的。
     * 因为在本类中，rowIndex这个计数器统计的是所有namedSql的执行行数，而不是单一一种namedSql的执行行数计数,这就意味着，
     * 如果业务层在迭代器循环中调用本方法执行了多于一种的sql语句，则批量保存的批次大小是对具体一种sql来说并不是等于DEFAULT_BATCH_SIZE的值，
     * 因为这个rowIndex计数是多个sql的执行行数计数。
     *
     * @param conn            java.sql.Connection 数据库连接对象
     * @param prestStmtMap    JDBC预处理对象map，key=namedSql，value=PreparedStatement
     * @param executedRowsMap 执行批量任务时，所影响的行数map，key=namedSql,value=这个sql语句所影响的数据行数
     * @param rowCounter      行记录器
     * @param namedSql        被执行的SQL语句，格式如：update table set a = :a,b = :b where c = :c
     * @param parameterMap    SQL语句对应的参数map
     * @throws java.sql.SQLException
     */
    public static void executeBatch(Connection conn, HashMap<String, PreparedStatement> prestStmtMap,
                                    HashMap<String, ArrayList<int[]>> executedRowsMap, AtomicInteger rowCounter,
                                    String namedSql, Map<String, Object> parameterMap) throws SQLException {
        if (parameterMap == null) {
            parameterMap = new HashMap<String, Object>();
        }
        //打印日志
        DaoExecutor.printSQL(namedSql, parameterMap);
        //正式处理
        HashMap<String, Integer> paramIndexMap = null;
        PreparedSqlAndParamsIndexMap preparedSqlAndParamsIndexMap = getPreparedSql(namedSql);
        paramIndexMap = preparedSqlAndParamsIndexMap.getParamIndexMap();

        PreparedStatement preparedStatement = prestStmtMap.get(namedSql);
        if (preparedStatement == null) {//批量任务之第一次执行
            //创建该SQL对应的PreparedStatement对象，并以业务key为key存入map
            String preparedSql = preparedSqlAndParamsIndexMap.getPreparedSql();
            preparedStatement = conn.prepareStatement(preparedSql);
            prestStmtMap.put(namedSql, preparedStatement);
        }

        //使用行数据回调后，填充入实际数据的参数map，给主SQL的PreparedStatement赋值
        Object[] sortedParamArray = getParamIndexArrayList(paramIndexMap, parameterMap);
        int i = 1;
        boolean rowOk = true;
        for (Object paramValue : sortedParamArray) {
            try {
                setValueToPreparedStatement(i, preparedStatement, paramValue);
            } catch (SQLException ex) {
                rowOk = false;
                LOG.error("### 某行数据出现错误，本行数据将被跳过！", ex);
            }
            i++;
        }

        if (rowOk) {
            preparedStatement.addBatch();
        } else {
            LOG.error("### 某行数据出现错误，本行数据将被跳过！");
        }

        //如果迭代器计数达到了默认批次大小的倍数，则保存这批数据(【注意】：这个rowIndex计数器统计的是所有namedSql的执行行数，
        //而不是单一一种namedSql的执行行数计数,这就意味着，如果业务层在迭代器循环中调用本方法执行了多于一种的sql语句，则批量
        //保存的批次大小是对具体一种sql来说并不是等于DEFAULT_BATCH_SIZE的值，因为这个rowIndex计数是多个sql的执行行数计数。)
        if ((rowCounter.get() + 1) % DEFAULT_BATCH_SIZE == 0) {
            //批量保存主SQL的数据
            int[] rows = preparedStatement.executeBatch();

            //将批量执行后的执行行数存入返回值计数map
            ArrayList<int[]> executedRowsOfNameSql = executedRowsMap.get(namedSql);
            if (executedRowsOfNameSql == null) {
                executedRowsOfNameSql = new ArrayList<int[]>();
            }
            executedRowsOfNameSql.add(rows);
            executedRowsMap.put(namedSql, executedRowsOfNameSql);
        }

        //行计数器递增
        rowCounter.incrementAndGet();
    }

    /**
     * 根据namedSql，得到其对应的预处理sql，也就是，根据形如： insert into table(a,b,c) values
     * (:a,:b,:c) 这样的sql语句，得到形如：insert into table(a,b,c) values (?,?,?)
     * 这样的预处理SQL语句。
     * 同时得到每个SQL参数名所对应的该参数在PreparedStatement中的索引值(PreparedStatement的索引值是从1开始的)。
     */
    private static PreparedSqlAndParamsIndexMap getPreparedSql(String namedSql) {
        PreparedSqlAndParamsIndexMap result = new PreparedSqlAndParamsIndexMap();
        GeorgeParsedSql gps = GeorgeNamedParameterUtils.parseSqlStatement(namedSql);
        List<String> parameterNames = gps.getParameterNames();
        if (parameterNames != null && !parameterNames.isEmpty()) {
            HashMap<String, Object> virtualParamMap = new HashMap<String, Object>();
            for (String paramName : parameterNames) {
                virtualParamMap.put(paramName, "temp");
            }

            SqlAndParams sap = NamedSqlUtils.getPreparedSqlAndSortedParamsMap(namedSql, virtualParamMap);

            result.setPreparedSql(sap.getSql());
            result.setParamIndexMap(sap.getParamIndexMap());
        } else {
            result.setPreparedSql(namedSql);
        }

        return result;
    }

    /**
     * 根据SQL参数名PreparedStatement索引值map和sql参数名--参数值map，得到sql参数值与PreparedStatement参数索引顺序相同的参数值数组。
     */
    private static Object[] getParamIndexArrayList(HashMap<String, Integer> paramIndexMap, Map<String, Object> paramNameAndValueMap) {
        if (paramIndexMap != null && !paramIndexMap.isEmpty() && paramNameAndValueMap != null && !paramNameAndValueMap.isEmpty()) {
            Object[] result = new Object[paramIndexMap.size()];

            for (Map.Entry<String, Integer> entry : paramIndexMap.entrySet()) {
                result[entry.getValue() - 1] = paramNameAndValueMap.get(entry.getKey());
            }

            return result;
        } else {
            return null;
        }
    }
    //## 批量执行 方法 结束 #####################################################

    public static void printSQLWithParams(String sql, Map<String, Object> params) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("==SQL BEGIN ===================================");
            LOG.debug("SQL:{" + sql + "}");

            if (params != null) {
                StringBuffer pString = new StringBuffer();
                for (Map.Entry<String, Object> p : params.entrySet()) {
                    pString.append(p.getKey()).append(":").append(p.getValue()).append(",");
                }
                LOG.debug("Params:{" + StringUtils.removeEnd(pString.toString(), ",") + "}");
            }
            LOG.debug("==SQL END =====================================");
        }

//        printSQLWithParamsUseSystem(sql, params);
    }

    protected static void printSQLWithParamsUseSystem(String sql, Map<String, Object> params) {
        System.out.println("==SQL BEGIN ===================================");
        System.out.println("SQL:{" + sql + "}");

        if (params != null) {
            StringBuffer pString = new StringBuffer();
            for (Map.Entry<String, Object> p : params.entrySet()) {
                pString.append(p.getKey()).append(":").append(p.getValue()).append(",");
            }
            System.out.println("Params:{" + StringUtils.removeEnd(pString.toString(), ",") + "}");
        }
        System.out.println("==SQL END =====================================");
    }

    protected static void printSQL(String sql, List params) {
        printSQLWithListParams(sql, params);
    }

    protected static void printSQLWithListParams(String sql, List params) {
        LOG.debug("==SQL BEGIN ===================================");
        LOG.debug("SQL:{" + sql + "}");

        if (params != null) {
            StringBuffer pString = new StringBuffer();
            for (Object p : params) {
                pString.append(p).append(",");
            }
            LOG.debug("Params:{" + StringUtils.removeEnd(pString.toString(), ",") + "}");
        }
        LOG.debug("==SQL END =====================================");
    }

    protected static void printSQL(String sql, Map<String, Object> params) {
        LOG.debug("==SQL BEGIN ===================================");
        LOG.debug("SQL:{" + sql + "}");

        if (params != null) {
            StringBuffer pString = new StringBuffer();
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                pString.append(entry.getKey()).append(":").append(entry.getValue()).append(",");
            }
            LOG.debug("Params:{" + StringUtils.removeEnd(pString.toString(), ",") + "}");
        }
        LOG.debug("==SQL END =====================================");
    }

    protected static void printSQLWithSystem(String sql, List params) {
        System.out.println("==SQL BEGIN ===================================");
        System.out.println("SQL:{" + sql + "}");

        if (params != null) {
            StringBuffer pString = new StringBuffer();
            for (Object p : params) {
                pString.append(p).append(",");
            }
            System.out.println("Params:{" + StringUtils.removeEnd(pString.toString(), ",") + "}");
        }
        System.out.println("==SQL END =====================================");
    }

    protected static void printSQL(String procedureName, String procedureParams) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("==SQL BEGIN ===================================");
            LOG.debug("{call " + procedureName + "(" + procedureParams + ")}");
            LOG.debug("==SQL END =====================================");
        }
//        printSQLWithSystem(procedureName, procedureParams);
    }

    protected static void printSQLWithSystem(String procedureName, String procedureParams) {
        System.out.println("==SQL BEGIN ===================================");
        System.out.println("{call " + procedureName + "(" + procedureParams + ")}");
        System.out.println("==SQL END =====================================");
    }
}
