package com.ruoyi.manage.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.BatchAduit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.Book;
import com.ruoyi.manage.service.IBookService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 图书列表Controller
 *
 * @author 曾辉
 * @date 2025-09-24
 */
@RestController
@RequestMapping("/manage/book")
public class BookController extends BaseController {
    @Autowired
    private IBookService bookService;

    /**
     * 查询图书列表列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Book book) {
        startPage();
        if (SecurityUtils.hasRole("seller"))
            book.setCreateBy(getUsername());
        List<Book> list = bookService.selectBookList(book);
        return getDataTable(list);
    }

    /**
     * 导出图书列表列表
     */
    @Log(title = "图书列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Book book) {
        List<Book> list = bookService.selectBookList(book);
        ExcelUtil<Book> util = new ExcelUtil<Book>(Book.class);
        util.exportExcel(response, list, "图书列表数据");
    }

    /**
     * 获取图书列表详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(bookService.selectBookById(id));
    }

    /**
     * 新增图书列表
     */
    @Log(title = "图书列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Book book) {
        book.setCreateBy(getUsername());
        return toAjax(bookService.insertBook(book));
    }

    /**
     * 修改图书列表
     */
    @Log(title = "图书列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Book book) {
        //如果是卖家，修改时如何是拒绝的，则改为待审核
        if (SecurityUtils.hasRole("seller") && book.getStatus() == 2)
            book.setStatus(0);
        return toAjax(bookService.updateBook(book));
    }

    /**
     * 删除图书列表
     */
    @Log(title = "图书列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(bookService.deleteBookByIds(ids));
    }

    @PutMapping("/batchAudit")
    public AjaxResult batchAudit(@RequestBody BatchAduit params) {
        return toAjax(bookService.batchAuditByIds(params.getIds(), params.getStatus()));
    }
}
