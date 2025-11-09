package com.ruoyi.manage.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.mapper.BookMapper;
import com.ruoyi.manage.domain.Book;
import com.ruoyi.manage.service.IBookService;

/**
 * 图书列表Service业务层处理
 *
 * @author 曾辉
 * @date 2025-09-24
 */
/**
 * 图书（样例模块） 服务实现
 */
@Service
public class BookServiceImpl implements IBookService {
    @Autowired
    private BookMapper bookMapper;

    /**
     * 查询图书列表
     *
     * @param id 图书列表主键
     * @return 图书列表
     */
    @Override
    public Book selectBookById(Long id) {
        return bookMapper.selectBookById(id);
    }

    /**
     * 查询图书列表列表
     *
     * @param book 图书列表
     * @return 图书列表
     */
    @Override
    public List<Book> selectBookList(Book book) {
        return bookMapper.selectBookList(book);
    }

    /**
     * 新增图书列表
     *
     * @param book 图书列表
     * @return 结果
     */
    @Override
    public int insertBook(Book book) {
        book.setCreateTime(DateUtils.getNowDate());
        return bookMapper.insertBook(book);
    }

    /**
     * 修改图书列表
     *
     * @param book 图书列表
     * @return 结果
     */
    @Override
    public int updateBook(Book book) {

        return bookMapper.updateBook(book);
    }

    /**
     * 批量删除图书列表
     *
     * @param ids 需要删除的图书列表主键
     * @return 结果
     */
    @Override
    public int deleteBookByIds(Long[] ids) {
        return bookMapper.deleteBookByIds(ids);
    }

    /**
     * 删除图书列表信息
     *
     * @param id 图书列表主键
     * @return 结果
     */
    @Override
    public int deleteBookById(Long id) {
        return bookMapper.deleteBookById(id);
    }

    @Override
    public int batchAuditByIds(Long[] ids, Integer status) {
        return bookMapper.batchAuditByIds(ids, status);
    }
}
