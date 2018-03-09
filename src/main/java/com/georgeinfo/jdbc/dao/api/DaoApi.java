/*
 * Author : George <GeorgeNiceWorld@gmail.com> | <Georgeinfo@163.com>
 * Copyright (C) George (http://www.georgeinfo.com), All Rights Reserved.
 */
package com.georgeinfo.jdbc.dao.api;

import com.georgeinfo.jdbc.dao.helper.BasicType;
import com.georgeinfo.jdbc.dao.helper.DataRow;
import com.georgeinfo.jdbc.dao.helper.JdbcRowCallbackHandler;
import com.georgeinfo.jdbc.dao.helper.SingleColumnRowCallBack;
import com.georgeinfo.jdbc.dao.utils.DaoException;
import com.georgeinfo.jdbc.utils.ProcedureParam;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础Dao方法定义接口，常用的增删改查+执行方法。
 *
 * @author George <Georgeinfo@163.com>
 */
public interface DaoApi {

    /**
     * 查询数据集（没有查询参数）
     *
     * @param sql 被执行的SQL语句
     * @return 一行数据是一个map，所有行数据组成一个List<DataRow>
     */
    public ArrayList<DataRow> queryList(String sql) throws DaoException;

    /**
     * 查询数据集（有查询参数）
     *
     * @param sql 被执行的 SQL语句
     * @param params SQL参数map
     * @return 查询出的数据集列表
     */
    public ArrayList<DataRow> queryList(String sql, Map<String, Object> params) throws DaoException;

    /**
     * 指定行类型查询数据集
     *
     * @param <T> 一行的类型
     * @param sql 被执行的SQL语句
     * @param params 被执行SQL语句的参数
     * @param rowType 一行的类型Class
     * @return
     * @throws DaoException
     */
    public <T> ArrayList<T> queryList(String sql, Map<String, Object> params, Class<T> rowType) throws DaoException;

    public void query(String sql, JdbcRowCallbackHandler rowCallbackHandler) throws DaoException;

    public void query(String sql, Map<String, Object> params, JdbcRowCallbackHandler rowCallbackHandler) throws DaoException;

    /**
     * 查询一行数据出来（基本类型，或DataRow或一个实体类）
     *
     * @param <T> 被查询的基本类型
     * @param sql 被执行的SQL语句
     * @param params 被执行的SQL语句的参数，可以为null
     * @param requiredType 被查询的基本类型的Class
     * @return 查询出的一行数据（或一行中的一字段的数据）
     */
    public <T> T queryOne(String sql, Map<String, Object> params, Class<T> requiredType) throws DaoException;

    public DataRow queryOneRow(String sql, Map<String, Object> params) throws DaoException;

    /**
     * 查询一个基本类型：8种Primitive类型，以及String/Date/BigDecimal/BigInteger/CLob/Blog等基本类型
     *
     * @param <T> 被查询的基本类型
     * @param sql 被执行的SQL语句
     * @param params 被执行的SQL语句的参数，可以为null
     * @param requiredTypeEnum 被查询的基本类型的Class定义枚举
     * @return 查询出的一行中的一个字段（基本类型）值
     *
     */
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, BasicType requiredTypeEnum) throws DaoException;

    /**
     * 查询一行中的一个字段的值，转换成基本类型（8种Primitive原始类型，或Date/BigInteger/Clob/String等常用基本类型），<br />
     * 通常用于select count(*)、select 序列.nextval()等场景。
     *
     * @param <T> 返回值的类型
     * @param sql 被执行的SQL语句
     * @param params 被执行SQL语句的参数
     * @param requiredType 应用层要求返回的类型的Class
     * @param singleColumnRowCallBack 单条查询回调接口
     * @return 查询出的基本类型值
     * @throws DaoException TODO: 当应用层要求的类型，与数据库中实际查出的类型不符时，报SQL异常，而没有进入回调
     */
    public <T> T queryOneBasicValue(String sql, Map<String, Object> params, Class<T> requiredType, SingleColumnRowCallBack singleColumnRowCallBack) throws DaoException;

    /**
     * 查询一行数据，转换成指定的实体类对象
     *
     * @param <T> 被查询的基本类型
     * @param sql 被执行的SQL语句
     * @param params 被执行的SQL语句的参数，可以为null
     * @param requiredType 被查询的基本类型的Class
     * @return 查询出的一行数据（转换成实体类形式返回）
     */
    public <T> T queryOneEntity(String sql, Map<String, Object> params, Class<T> requiredType) throws DaoException;

    public List[] queryBatch(String[] sqlArray, Map<String, Object>[] paramArray) throws DaoException;

    public int execute(String sql, Map<String, Object> params) throws SQLException;

    /**
     * @param sql 被执行的SQL语句
     * @param paramsMap SQL语句参数map
     * @return 刚才插入成功的一条记录的ID
     * @throws java.sql.SQLException
     */
    public List<Long> insertAndGetIds(String sql, Map<String, Object> paramsMap) throws SQLException;

    public Long insertAndGetId(String sql, Map<String, Object> paramsMap) throws SQLException;

    /**
     * 执行存储过程
     *
     * 传入参数时，可以使用LI工具来构造，如：<br/>
     * LI.toProcParams(InOrOut.IN, "pSeqCode", "pf-merchant-no",
     * java.sql.Types.VARCHAR, InOrOut.OUT, "returnValue", null,
     * java.sql.Types.BIGINT)
     *
     * @param procedureName 存储过程名称
     * @param params 存储过程 in输入参数列表
     * @return
     * @throws java.sql.SQLException
     */
    public HashMap<String, Object> executeProcedure(String procedureName, LinkedHashMap<String, ProcedureParam> params) throws SQLException;

    public int[] executeBatch(String[] sqlArray, Map<String, Object>[] paramArray) throws SQLException;

    /**
     * 此枚举已过期，目前仅为同样过期的{CommonDao}服务
     *
     * @deprecated
     */
    public enum LastInsertId {

        MYSQL("SELECT LAST_INSERT_ID()");
        private final String sql;

        private LastInsertId(String sql) {
            this.sql = sql;
        }

        public String getSql() {
            return sql;
        }
    }
}
