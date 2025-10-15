package com.ruoyi.manage.service.impl;

import java.util.List;

import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.mapper.ShopsMapper;
import com.ruoyi.manage.domain.Shops;
import com.ruoyi.manage.service.IShopsService;

/**
 * 商铺信息Service业务层处理
 * 
 * @author 曾辉
 * @date 2025-10-12
 */
@Service
public class ShopsServiceImpl implements IShopsService 
{
    @Autowired
    private ShopsMapper shopsMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询商铺信息
     * 
     * @param id 商铺信息主键
     * @return 商铺信息
     */
    @Override
    public Shops selectShopsById(Long id)
    {
        return shopsMapper.selectShopsById(id);
    }

    /**
     * 查询商铺信息列表
     * 
     * @param shops 商铺信息
     * @return 商铺信息
     */
    @Override
    public List<Shops> selectShopsList(Shops shops)
    {
        return shopsMapper.selectShopsList(shops);
    }

    /**
     * 新增商铺信息
     * 
     * @param shops 商铺信息
     * @return 结果
     */
    @Override
    public int insertShops(Shops shops)
    {

        return shopsMapper.insertShops(shops);
    }

    /**
     * 修改商铺信息
     * 
     * @param shops 商铺信息
     * @return 结果
     */
    @Override
    public int updateShops(Shops shops)
    {
        return shopsMapper.updateShops(shops);
    }

    /**
     * 批量删除商铺信息
     * 
     * @param ids 需要删除的商铺信息主键
     * @return 结果
     */
    @Override
    public int deleteShopsByIds(Long[] ids)
    {
        sysUserMapper.deleteUserByIds(ids);
        return shopsMapper.deleteShopsByIds(ids);
    }

    /**
     * 删除商铺信息信息
     * 
     * @param id 商铺信息主键
     * @return 结果
     */
    @Override
    public int deleteShopsById(Long id)
    {
        return shopsMapper.deleteShopsById(id);
    }
}
