package com.ruoyi.manage.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.mapper.CartsMapper;
import com.ruoyi.manage.domain.Carts;
import com.ruoyi.manage.service.ICartsService;

/**
 * 购物车管理Service业务层处理
 * 
 * @author 曾辉
 * @date 2025-10-06
 */
@Service
public class CartsServiceImpl implements ICartsService 
{
    @Autowired
    private CartsMapper cartsMapper;

    /**
     * 查询购物车管理
     * 
     * @param id 购物车管理主键
     * @return 购物车管理
     */
    @Override
    public Carts selectCartsById(Long id)
    {
        return cartsMapper.selectCartsById(id);
    }

    /**
     * 查询购物车管理列表
     * 
     * @param carts 购物车管理
     * @return 购物车管理
     */
    @Override
    public List<Carts> selectCartsList(Carts carts)
    {
        return cartsMapper.selectCartsList(carts);
    }

    /**
     * 新增购物车管理
     * 
     * @param carts 购物车管理
     * @return 结果
     */
    @Override
    public int insertCarts(Carts carts)
    {
        return cartsMapper.insertCarts(carts);
    }

    /**
     * 修改购物车管理
     * 
     * @param carts 购物车管理
     * @return 结果
     */
    @Override
    public int updateCarts(Carts carts)
    {
        return cartsMapper.updateCarts(carts);
    }

    /**
     * 批量删除购物车管理
     * 
     * @param ids 需要删除的购物车管理主键
     * @return 结果
     */
    @Override
    public int deleteCartsByIds(Long[] ids)
    {
        return cartsMapper.deleteCartsByIds(ids);
    }

    /**
     * 删除购物车管理信息
     * 
     * @param id 购物车管理主键
     * @return 结果
     */
    @Override
    public int deleteCartsById(Long id)
    {
        return cartsMapper.deleteCartsById(id);
    }
}
