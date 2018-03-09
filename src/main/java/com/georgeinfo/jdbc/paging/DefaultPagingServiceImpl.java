package com.georgeinfo.jdbc.paging;

import com.georgeinfo.base.util.logger.GeorgeLogger;
import com.georgeinfo.jdbc.dao.helper.DataRow;
import com.georgeinfo.pagination.context.GenericPagingContext;
import gbt.config.GeorgeLoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class DefaultPagingServiceImpl implements PagingService {

    private final GeorgeLogger logger = GeorgeLoggerFactory.getLogger(getClass());
    /*
     * 注入分页DAO
     */
    private PagingDao pagingDao = null;

    @Override
    public void setPagingDao(PagingDao pagingDao) {
        this.pagingDao = pagingDao;
    }

    @Override
    public <T, PCT extends GenericPagingContext> List<T> doPaging(
            HttpServletRequest request,
            PCT pagingContext, Class<? extends T> rowType) {

        //设置请求页数
        String pageNo = request.getParameter("pageNo");
        if (pageNo != null && !pageNo.trim().isEmpty()) {
            pagingContext.setCurrentPageNo(Long.parseLong(pageNo));
        } else {
            pagingContext.setCurrentPageNo(1L);
        }

        //设置每页显示的记录数
        String pageSizeString = request.getParameter("pageSize");
        if (pageSizeString != null && !pageSizeString.trim().isEmpty()) {
            pagingContext.setPageSize(Long.parseLong(pageSizeString));
        }
        //设置总记录数
        String totalRecordsString = request.getParameter("totalRecords");
        if (totalRecordsString != null && !totalRecordsString.trim().isEmpty()) {
            long totalRecords = Long.parseLong(totalRecordsString);
            if (totalRecords > 0) {
                pagingContext.setTotalRecords(totalRecords);
            }
        }

        try {
            //在此传入页面模型，返回结果为页面模型，整个请求与返回都为同一个页面模型对象
            pagingDao.begin();
            pagingContext = pagingDao.queryByPaging(pagingContext, rowType);
            pagingDao.end();
        } catch (SQLException ex) {
            logger.error("======执行分页查询时，出现DAO层SQL异常！======", ex);
        } catch (InstantiationException ex) {
            logger.error(ex);
        } catch (IllegalAccessException ex) {
            logger.error(ex);
        } catch (InvocationTargetException ex) {
            logger.error(ex);
        } catch (NoSuchMethodException ex) {
            logger.error(ex);
        }

        //将分页参数存储入attribute
        request.setAttribute("pageSize", pagingContext.getPageSize());
        request.setAttribute("totalRecords", pagingContext.getTotalRecords());
        request.setAttribute("totalPages", pagingContext.getTotalPages());
        request.setAttribute("pageNo", pagingContext.getCurrentPageNo());
        //将查询参数存储入attribute
        HashMap<String, Object> params = pagingContext.getParams();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> e : params.entrySet()) {
                request.setAttribute(e.getKey(), e.getValue());
            }
        }

        //从页面模型中取出数据列表
        List<T> recordsList = pagingContext.getRecordList();

        //封装页面模型到request
        request.setAttribute("pagingContext", pagingContext);
        //返回封装数据列表
        return recordsList;
    }

    /**
     * 执行分页查询
     *
     * @param request
     * @param pagingContext
     * @return
     */
    public List<DataRow> executeQuery(
            HttpServletRequest request,
            DefaultPagingContext<DataRow> pagingContext) {
        List<DataRow> dataList = doPaging(request, pagingContext, DataRow.class);

        return dataList;
    }
}
