package com.georgeinfo.jdbc.paging;

import com.georgeinfo.base.util.database.JdbcTypes;
import com.georgeinfo.base.util.reflect.ReflectionTool;
import com.georgeinfo.base.util.string.StringTool;
import com.georgeinfo.jdbc.dao.impl.DaoExecutor;
import com.georgeinfo.jdbc.dao.helper.DataRow;
import com.georgeinfo.jdbc.dao.helper.QueryOneCallBack;
import com.georgeinfo.jdbc.dao.helper.SingleColumnRowCallBack;
import com.georgeinfo.jdbc.dao.impl.ProtoTypeBatchDaoImpl;
import com.georgeinfo.jdbc.dao.utils.DaoException;
import com.georgeinfo.jdbc.namedparam.NamedSqlUtils;
import com.georgeinfo.jdbc.dao.utils.SqlAndParams;
import com.georgeinfo.pagination.context.GenericPagingContext;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;
import javax.sql.DataSource;

public class MySQLPagingDaoImpl extends ProtoTypeBatchDaoImpl implements PagingDao {

    private String sql = null;
    private PreparedStatement pstmtOfPaging = null; //用于分页
    private long firstResultIndex = 0;
    private long maxResultsIndex = 0;

    public MySQLPagingDaoImpl(DataSource dataSource) throws SQLException {
        super(dataSource);
    }

    public MySQLPagingDaoImpl(Connection connection) throws SQLException {
        super(connection);
    }

    /*
     * == 以下五个函数为预编译PreparedStatement中的sql占位符赋值 开始 =========
     */
    @Override
    public void setString(int arg0, String arg1) throws SQLException {
        pstmtOfPaging.setString(arg0, arg1);

    }

    @Override
    public void setLong(int arg0, long arg1) throws SQLException {
        pstmtOfPaging.setLong(arg0, arg1);
    }

    @Override
    public void setDouble(int arg0, double arg1) throws SQLException {
        pstmtOfPaging.setDouble(arg0, arg1);
    }

    @Override
    public void setInt(int arg0, int arg1) throws SQLException {
        pstmtOfPaging.setInt(arg0, arg1);
    }

    @Override
    public void setDate(int arg0, Date arg1) throws SQLException {
        pstmtOfPaging.setDate(arg0, arg1);
    }
    /*
     * == 以下五个函数为预编译PreparedStatement中的sql占位符赋值 结束 =========
     */

    @Override
    public void setFirstResult(long firstResult) {
        this.firstResultIndex = firstResult;
    }

    @Override
    public void setMaxResults(long maxResults) {
        this.maxResultsIndex = maxResults;
    }

    /*
     * 取出分页数据
     */
    private <T, PCT extends GenericPagingContext> PCT buildPageData(ResultSet rs, PCT pagingContext, Class<T> rowType) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ArrayList<T> retList = new ArrayList<T>();
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            if (HashMap.class.isAssignableFrom(rowType)
                    || DataRow.class.isAssignableFrom(rowType)) {//结果集行是Map类型
                while (rs.next()) {
                    DataRow recordMap = new DataRow();
                    for (int i = 1; i <= columnCount; i++) {
                        String name = meta.getColumnLabel(i);
                        int sqlType = meta.getColumnType(i);
                        String columnTypeName = meta.getColumnTypeName(i);

                        Object value = JdbcTypes.convertTypeOfResultSetField(rs, i, sqlType,columnTypeName);

                        recordMap.put(name, value);
                    }

                    retList.add((T) recordMap);
                }
            } else {//结果集行是实体类类型
                while (rs.next()) {
                    DataRow recordMap = new DataRow();
                    for (int i = 1; i <= columnCount; i++) {
                        String name = meta.getColumnLabel(i);
                        int sqlType = meta.getColumnType(i);
                        String columnTypeName = meta.getColumnTypeName(i);

                        Object value = JdbcTypes.convertTypeOfResultSetField(rs, i, sqlType,columnTypeName);

                        //将数据库字段名称，转换成驼峰字符串，存入map
                        name = StringTool.dbField2AttributeName(name);
                        recordMap.put(name, value);
                    }

                    if (!recordMap.isEmpty()) {
                        T rowObject = rowType.newInstance();
                        ReflectionTool.populate(rowObject, recordMap);
                        retList.add(rowObject);
                    }
                }
            }

            pagingContext.setRecordList(retList);
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                rs.close();
            } catch (SQLException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        closePagingDao();

        return pagingContext;
    }

    /*
     * 传入页面模型对象，取得分页结果
     */
    @Override
    public <T, PCT extends GenericPagingContext> PCT queryByPaging(PCT pagingContext, Class<T> rowType) throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        //处理总记录数
        if (pagingContext.getTotalRecords() <= 0) {
            pagingContext.setTotalRecords(getTotalRecords(pagingContext));
        }

        //初始化分页上下文环境
        pagingContext.init();

        //从分页上下文中获取查询所需信息
        SqlAndParams sqlAndParams = NamedSqlUtils.getPreparedSqlAndSortedParamsMap(pagingContext.getSql(), pagingContext.getParams());

        //设置分页游标位置     
        if (pagingContext.getCurrentPageNo() <= 0) {
            pagingContext.setCurrentPageNo(1L);
        }
        setFirstResult((pagingContext.getCurrentPageNo() - 1) * pagingContext.getPageSize());
        setMaxResults(pagingContext.getPageSize());

        //组装分页查询SQL及JDBC预查询对象
        this.sql = sqlAndParams.getSql();
        String pagingSQL = this.sql + " limit ?,?";

        //创建查询系列对象
        Connection conn = getCurrentConn();
        pstmtOfPaging = conn.prepareStatement(pagingSQL);

        //处理查询参数
        int paramsCount = 2;
        ArrayList params = sqlAndParams.getParams();
        if (params != null && !params.isEmpty()) {
            paramsCount = paramsCount + params.size();
            int index = 1;
            for (Object v : params) {
                if (v == null) {
                    pstmtOfPaging.setObject(index, "");
                } else {
                    pstmtOfPaging.setObject(index, v);
                }
                index++;
            }
        }

        //处理分页参数
        long beginIndex = (firstResultIndex < 0 ? 0 : firstResultIndex);
        long endIndex = maxResultsIndex;
        pstmtOfPaging.setLong(paramsCount - 1, beginIndex);
        pstmtOfPaging.setLong(paramsCount, endIndex);

        //打印SQL
        HashMap<String, Object> allParams = pagingContext.getParams();
        if (allParams == null) {
            allParams = new LinkedHashMap<String, Object>();
        }
        allParams.put("beginIndex", beginIndex);
        allParams.put("endIndex", endIndex);
        DaoExecutor.printSQLWithParams(pagingSQL, allParams);

        ResultSet rs = pstmtOfPaging.executeQuery();
        pagingContext = buildPageData(rs, pagingContext, rowType);

        return pagingContext;
    }

    @Override
    public long queryForCount(String querySql, String countSql, HashMap<String, Object> params) throws SQLException {
        String actualSQL;
        if (countSql != null && !countSql.trim().isEmpty()) {
            actualSQL = countSql;
        } else {
            //生成随机数，用于MySQL子查询的表名
            Random rand = new Random();
            int MIN = 100;
            int MAX = 99999;
            int randNumber = rand.nextInt(MAX - MIN + 1) + MIN; // randNumber 将被赋值为一个 MIN 和 MAX 范围内的随机数

            //组装统计记录总数的SQL
            actualSQL = "select count(*) from ( " + querySql + ") as georgemysqlpdi" + randNumber;
        }

        Long r = this.queryOneBasicValue(actualSQL, params, Long.class, new SingleColumnRowCallBack() {

            @Override
            public Object whenTypeMismatch(Object originalValue, RuntimeException ex) {
                try {
                    return Long.valueOf(originalValue.toString());
                } catch (Exception ex2) {
                    throw new DaoException("## Can't case value to long." + ex.getMessage(), ex2);
                }
            }

            @Override
            public Object whenIncorrectResultSize(Collection results, RuntimeException ex) {
                throw ex;
            }

            @Override
            public Object whenIncorrectResultSetColumnCount(ArrayList<DataRow> dataRow, RuntimeException ex) {
                throw ex;
            }
        });
        return r;
    }

    /*
     * 取得总记录数
     */
    private <PCT extends GenericPagingContext> long getTotalRecords(PCT pagingContext) throws SQLException {
        String actualSQL = pagingContext.getSql();
        HashMap<String, Object> params = pagingContext.getParams();

        return queryForCount(actualSQL, pagingContext.getCountSql(), params);
    }

    /*
     * 取完数据后，需要关闭
     */
    @Override
    public void closePagingDao() {
        try {
            if (!pstmtOfPaging.isClosed()) {
                pstmtOfPaging.close();
            }
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
