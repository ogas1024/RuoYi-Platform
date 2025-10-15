package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.manage.domain.Book;

/**
 * 图书列表Service接口
 * 
 * @author 曾辉
 * @date 2025-09-24
 */
public interface IBookService 
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
     * 批量删除图书列表
     * 
     * @param ids 需要删除的图书列表主键集合
     * @return 结果
     */
    public int deleteBookByIds(Long[] ids);

    /**
     * 删除图书列表信息
     * 
     * @param id 图书列表主键
     * @return 结果
     */
    public int deleteBookById(Long id);

    int batchAuditByIds(Long[] ids, Integer status);
}
