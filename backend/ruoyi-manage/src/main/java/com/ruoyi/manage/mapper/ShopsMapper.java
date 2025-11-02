package com.ruoyi.manage.mapper;

import java.util.List;
import com.ruoyi.manage.domain.Shops;

/**
 * 商铺信息Mapper接口
 * 
 * @author 曾辉
 * @date 2025-10-12
 */
public interface ShopsMapper 
{
    /**
     * 查询商铺信息
     * 
     * @param id 商铺信息主键
     * @return 商铺信息
     */
    public Shops selectShopsById(Long id);

    /**
     * 查询商铺信息列表
     * 
     * @param shops 商铺信息
     * @return 商铺信息集合
     */
    public List<Shops> selectShopsList(Shops shops);

    /**
     * 新增商铺信息
     * 
     * @param shops 商铺信息
     * @return 结果
     */
    public int insertShops(Shops shops);

    /**
     * 修改商铺信息
     * 
     * @param shops 商铺信息
     * @return 结果
     */
    public int updateShops(Shops shops);

    /**
     * 删除商铺信息
     * 
     * @param id 商铺信息主键
     * @return 结果
     */
    public int deleteShopsById(Long id);

    /**
     * 批量删除商铺信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteShopsByIds(Long[] ids);
}
