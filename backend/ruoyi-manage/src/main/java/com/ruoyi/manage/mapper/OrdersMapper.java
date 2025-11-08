package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.Orders;

/**
 * 订单Mapper接口
 *
 * @author 曾辉
 * @date 2025-10-06
 */
public interface OrdersMapper {
    /**
     * 查询订单
     *
     * @param id 订单主键
     * @return 订单
     */
    public Orders selectOrdersById(Long id);

    /**
     * 查询订单列表
     *
     * @param orders 订单
     * @return 订单集合
     */
    public List<Orders> selectOrdersList(Orders orders);

    /**
     * 新增订单
     *
     * @param orders 订单
     * @return 结果
     */
    public int insertOrders(Orders orders);

    /**
     * 修改订单
     *
     * @param orders 订单
     * @return 结果
     */
    public int updateOrders(Orders orders);

    /**
     * 删除订单
     *
     * @param id 订单主键
     * @return 结果
     */
    public int deleteOrdersById(Long id);

    /**
     * 批量删除订单
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOrdersByIds(Long[] ids);
}
