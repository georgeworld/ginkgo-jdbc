package com.georgeinfo.jdbc.paging;

import com.georgeinfo.base.util.clazz.ClazzTool;
import com.georgeinfo.base.util.string.StringTool;
import com.georgeinfo.pagination.context.GenericPagingContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultPagingContext<T> implements GenericPagingContext {

    /**
     * 所有的记录数
     */
    private Long totalRecords = 0L;
    /**
     * 每页多少条数据
     */
    private Long pageSize = DEFAULT_PAGE_SIZE;
    /**
     * 当前页第几页,用于接收页面参数
     */
    private Long currentPageNo = 1L;
    /**
     * 总页数
     */
    private Long totalPages = 0L;
    /**
     * 查询的结果集
     */
    private List<T> recordList;
    /**
     * 分页根URI
     */
    private String baseUri;
    /**
     * 查询参数
     */
    private HashMap<String, Object> params;
    /**
     * 查询SQL
     */
    private String sql;
    /**
     * 统计记录总数的SQL语句
     */
    private String countSql;
    /**
     * 追加了分页查询条件的SQL（数据库最终实际执行的SQL）
     */
    protected String finalSQL;
    /**
     * 追加了分页查询参数的SQL参数（数据库最终实际执行的SQL参数）
     */
    protected HashMap<String, Object> finalParams;

    public DefaultPagingContext(String querySql, HashMap<String, Object> params) {
        this.sql = querySql;
        this.params = params;
    }

    public DefaultPagingContext(String querySql, String countSql, HashMap<String, Object> params) {
        this.sql = querySql;
        this.countSql = countSql;
        this.params = params;
    }

    /**
     * 初始化分页上下文环境
     */
    @Override
    public void init() {
        //计算总页数
        this.totalPages = (totalRecords + pageSize - 1) / pageSize;
        //计算当前页码
        if (currentPageNo == 0) {
            this.currentPageNo = 1L;
        } else if (currentPageNo >= totalPages) {
            this.currentPageNo = totalPages;
        }
    }

    @Override
    public String getBaseUri() {
        return baseUri;
    }

    @Override
    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    @Override
    public HashMap<String, Object> getParams() {
        return params;
    }

    @Override
    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }

    private String getValueFromObject(Object obj) {
        String value = null;
        if (obj != null) {
            if (ClazzTool.isWrapperType(obj)) { //基本类型
                value = String.valueOf(obj);
            } else { //非基本类型
                if (obj instanceof Date) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    value = sdf.format(obj);
                } else {
                    value = String.valueOf(obj);
                }
            }
        }
        return value;
    }

    /**
     * 得到url的参数字符串部分，类似“paramA=123&viewType=hello&dateTime=2010-06-18 11:12:10”，
     * 返回值不是以&作为开始和结束字符
     *
     * @return URL查询参数串
     */
    @Override
    public String getParamsQueryString() {
        String returnValue;

        if (params != null && !params.isEmpty()) {
            StringBuffer queryString = new StringBuffer();
            String value;
            for (Map.Entry<String, Object> bean : params.entrySet()) {
                if (bean.getValue() != null) {
                    //判断参数值类型
                    value = getValueFromObject(bean.getValue());
                } else {
                    value = "";
                }

                queryString.append("&").append(bean.getKey()).append("=").append(value);
            }
            if (queryString.length() > 1) {
                returnValue = StringTool.removeStart(queryString.toString(), "&");
            } else {
                returnValue = "";
            }
        } else {
            returnValue = "";
        }

        return returnValue;
    }

    @Override
    public String getBaseUriWithQueryString() {
        String queryString = getParamsQueryString();
        if (queryString != null && !queryString.trim().isEmpty()) {
            return getBaseUri() + "?" + getParamsQueryString();
        } else {
            return getBaseUri();
        }
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String getCountSql() {
        return countSql;
    }

    @Override
    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }

    @Override
    public List<T> getRecordList() {
        return recordList;
    }

    @Override
    public void setRecordList(List recordList) {
        this.recordList = recordList;
    }

    @Override
    public Long getTotalRecords() {
        return this.totalRecords;
    }

    @Override
    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    @Override
    public Long getCurrentPageNo() {
        return this.currentPageNo;
    }

    @Override
    public void setCurrentPageNo(Long currentPageNo) {
        this.currentPageNo = currentPageNo;
    }

    @Override
    public Long getPageSize() {
        return this.pageSize;
    }

    @Override
    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public Long getTotalPages() {
        return this.totalPages;
    }

    @Override
    public String getFinalSQL() {
        return this.finalSQL;
    }

    @Override
    public void setFinalSQL(String finalSQL) {
        this.finalSQL = finalSQL;
    }

    @Override
    public HashMap<String, Object> getFinalParams() {
        return this.finalParams;
    }

    @Override
    public void setFinalParams(HashMap<String, Object> finalParams) {
        this.finalParams = finalParams;
    }
}
