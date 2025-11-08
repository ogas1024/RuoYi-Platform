package com.ruoyi.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
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
import com.ruoyi.manage.domain.Category;
import com.ruoyi.manage.service.ICategoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 图书类别Controller
 *
 * @author 曾辉
 * @date 2025-10-10
 */
@RestController
@RequestMapping("/manage/category")
public class CategoryController extends BaseController {
    @Autowired
    private ICategoryService categoryService;

    /**
     * 查询图书类别列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Category category) {
        startPage();
        List<Category> list = categoryService.selectCategoryList(category);
        return getDataTable(list);
    }

    /**
     * 获取图书类别详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(categoryService.selectCategoryById(id));
    }

    /**
     * 新增图书类别
     */
    @Log(title = "图书类别", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Category category) {
        String name = category.getCategoryName();
        if (!categoryService.checkNameUnique(category)) {
            return error("新增类别'" + name + "'失败，类别名称已存在");
        }
        return toAjax(categoryService.insertCategory(category));
    }

    /**
     * 修改图书类别
     */
    @Log(title = "图书类别", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Category category) {
        String name = category.getCategoryName();
        if (!categoryService.checkNameUnique(category)) {
            return error("修改类别'" + name + "'失败，类别名称已存在");
        }
        return toAjax(categoryService.updateCategory(category));
    }

    /**
     * 删除图书类别
     */
    @Log(title = "图书类别", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(categoryService.deleteCategoryByIds(ids));
    }
}
