package com.ruoyi.manage.mapper;

import java.util.List;
import com.ruoyi.manage.domain.Category;

/**
 * 图书类别Mapper接口
 *
 * @author 曾辉
 * @date 2025-10-10
 */
public interface CategoryMapper
{
    /**
     * 查询图书类别
     *
     * @param id 图书类别主键
     * @return 图书类别
     */
    public Category selectCategoryById(Long id);

    /**
     * 查询图书类别列表
     *
     * @param category 图书类别
     * @return 图书类别集合
     */
    public List<Category> selectCategoryList(Category category);

    /**
     * 新增图书类别
     *
     * @param category 图书类别
     * @return 结果
     */
    public int insertCategory(Category category);

    /**
     * 修改图书类别
     *
     * @param category 图书类别
     * @return 结果
     */
    public int updateCategory(Category category);

    /**
     * 删除图书类别
     *
     * @param id 图书类别主键
     * @return 结果
     */
    public int deleteCategoryById(Long id);

    /**
     * 批量删除图书类别
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCategoryByIds(Long[] ids);

    Category selectCategoryByName(String categoryName);
}
