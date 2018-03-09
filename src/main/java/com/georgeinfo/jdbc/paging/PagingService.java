package com.georgeinfo.jdbc.paging;

import com.georgeinfo.pagination.context.GenericPagingContext;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface PagingService {

    public void setPagingDao(PagingDao pagingDao);

    /**
     * 执行分页查询
     *
     * @param <T> 查出的结果集，一行对应的Java类型，可以是实体类，也可以是Map<String,Object> 或DataRow
     * @param <PCT> 分页上下文对象的类型
     * @param request HttpServletRequest对象
     * @param pagingContext 分页上下文对象
     * @param rowType 结果集一行所对应的Java类型
     * @return 分页查出的一页结果集列表
     */
    public <T, PCT extends GenericPagingContext> List<T> doPaging(
            HttpServletRequest request,
            PCT pagingContext, Class<? extends T> rowType);
}
