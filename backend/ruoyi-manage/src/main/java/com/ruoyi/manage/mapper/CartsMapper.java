package com.ruoyi.manage.mapper;

import java.util.List;
import com.ruoyi.manage.domain.Carts;

/**
 * 购物车管理Mapper接口
 * 
 * @author 曾辉
 * @date 2025-10-06
 */
public interface CartsMapper 
{
    /**
     * 查询购物车管理
     * 
     * @param id 购物车管理主键
     * @return 购物车管理
     */
    public Carts selectCartsById(Long id);

    /**
     * 查询购物车管理列表
     * 
     * @param carts 购物车管理
     * @return 购物车管理集合
     */
    public List<Carts> selectCartsList(Carts carts);

    /**
     * 新增购物车管理
     * 
     * @param carts 购物车管理
     * @return 结果
     */
    public int insertCarts(Carts carts);

    /**
     * 修改购物车管理
     * 
     * @param carts 购物车管理
     * @return 结果
     */
    public int updateCarts(Carts carts);

    /**
     * 删除购物车管理
     * 
     * @param id 购物车管理主键
     * @return 结果
     */
    public int deleteCartsById(Long id);

    /**
     * 批量删除购物车管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCartsByIds(Long[] ids);
}
