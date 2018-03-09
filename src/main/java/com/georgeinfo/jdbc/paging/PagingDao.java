package com.georgeinfo.jdbc.paging;

import com.georgeinfo.jdbc.dao.api.GSDao;
import com.georgeinfo.pagination.context.GenericPagingContext;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

public interface PagingDao extends GSDao {

    /*
     * 以下五个函数为预编译PreparedStatement中的sql占位符赋值
     */
    public void setString(int arg0, String arg1) throws SQLException;

    public void setLong(int arg0, long arg1) throws SQLException;

    public void setDouble(int arg0, double arg1) throws SQLException;

    public void setInt(int arg0, int arg1) throws SQLException;

    public void setDate(int arg0, Date arg1) throws SQLException;

    /**
     * 统计记录总数
     *
     * @param querySql 查询SQL
     * @param countSql 统计记录总数的SQL语句
     * @param params 查询参数map
     * @return 记录总数
     * @throws java.sql.SQLException
     */
    public long queryForCount(String querySql, String countSql, HashMap<String, Object> params) throws SQLException;

    /*
     * 传入页面模型对象，取得分页结果
     */
    public <T, PCT extends GenericPagingContext> PCT queryByPaging(PCT pagingContext, Class<T> rowType) throws SQLException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;

    /*
     * 以下两个函数已经封装，不需要显示调用
     */
    public void setFirstResult(long firstResult);

    public void setMaxResults(long maxResults);

    /*
     * 取完数据后，需要关闭
     */
    public void closePagingDao();
}
