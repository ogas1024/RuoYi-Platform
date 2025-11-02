package com.ruoyi.manage.mapper;

import java.util.List;
import com.ruoyi.manage.domain.Book;
import org.apache.ibatis.annotations.Param;

/**
 * 图书列表Mapper接口
 * 
 * @author 曾辉
 * @date 2025-09-24
 */
public interface BookMapper 
{
    /**
     * 查询图书列表
     * 
     * @param id 图书列表主键
     * @return 图书列表
     */
    public Book selectBookById(Long id);

    /**
     * 查询图书列表列表
     * 
     * @param book 图书列表
     * @return 图书列表集合
     */
    public List<Book> selectBookList(Book book);

    /**
     * 新增图书列表
     * 
     * @param book 图书列表
     * @return 结果
     */
    public int insertBook(Book book);

    /**
     * 修改图书列表
     * 
     * @param book 图书列表
     * @return 结果
     */
    public int updateBook(Book book);

    /**
     * 删除图书列表
     * 
     * @param id 图书列表主键
     * @return 结果
     */
    public int deleteBookById(Long id);

    /**
     * 批量删除图书列表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBookByIds(Long[] ids);

    public int countBookDataByCategoryId(Long categoryId);

    public int batchAuditByIds(@Param("ids") Long[] ids,@Param("status") Integer status);
}
