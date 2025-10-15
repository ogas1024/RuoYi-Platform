package com.ruoyi.manage.service.impl;

import java.util.List;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.mapper.CategoryMapper;
import com.ruoyi.manage.domain.Category;
import com.ruoyi.manage.service.ICategoryService;

/**
 * 图书类别Service业务层处理
 *
 * @author 曾辉
 * @date 2025-10-10
 */
@Service
public class CategoryServiceImpl implements ICategoryService
{
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 查询图书类别
     *
     * @param id 图书类别主键
     * @return 图书类别
     */
    @Override
    public Category selectCategoryById(Long id)
    {
        return categoryMapper.selectCategoryById(id);
    }

    /**
     * 查询图书类别列表
     *
     * @param category 图书类别
     * @return 图书类别
     */
    @Override
    public List<Category> selectCategoryList(Category category)
    {
        return categoryMapper.selectCategoryList(category);
    }

    /**
     * 新增图书类别
     *
     * @param category 图书类别
     * @return 结果
     */
    @Override
    public int insertCategory(Category category)
    {
        return categoryMapper.insertCategory(category);
    }

    /**
     * 修改图书类别
     *
     * @param category 图书类别
     * @return 结果
     */
    @Override
    public int updateCategory(Category category)
    {
        return categoryMapper.updateCategory(category);
    }

    /**
     * 批量删除图书类别
     *
     * @param ids 需要删除的图书类别主键
     * @return 结果
     */
    @Override
    public int deleteCategoryByIds(Long[] ids)
    {
        return categoryMapper.deleteCategoryByIds(ids);
    }

    /**
     * 删除图书类别信息
     *
     * @param id 图书类别主键
     * @return 结果
     */
    @Override
    public int deleteCategoryById(Long id)
    {
        return categoryMapper.deleteCategoryById(id);
    }

    @Override
    public boolean checkNameUnique(Category category) {
        Category data = categoryMapper.selectCategoryByName(category.getCategoryName());
        if (StringUtils.isNotNull(data) && category.getId()!= data.getId())
        {
            return false;
        }
        return true;
    }
}
