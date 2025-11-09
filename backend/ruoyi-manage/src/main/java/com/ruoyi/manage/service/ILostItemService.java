package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.LostItem;
import com.ruoyi.manage.domain.LostItemImage;

import java.util.List;

/**
 * 失物招领 服务接口
 */
public interface ILostItemService {
    /**
     * 按主键查询
     */
    LostItem selectById(Long id);

    /**
     * 列表查询
     * @param query 查询条件
     * @param statusList 允许的状态过滤（可空表示不过滤）
     */
    List<LostItem> selectList(LostItem query, List<Integer> statusList);

    /**
     * 查询图片列表
     */
    List<LostItemImage> listImages(Long itemId);

    /**
     * 新增（进入待审）
     */
    int insert(LostItem data);

    /**
     * 编辑（回到待审）
     */
    int update(LostItem data);

    /**
     * 软删除
     * @param admin 是否管理员删除（影响可删除范围）
     */
    int softDeleteById(Long id, String username, boolean admin);

    /**
     * 审核通过
     */
    int approve(Long id, String auditBy);

    /**
     * 审核驳回
     */
    int reject(Long id, String auditBy, String reason);

    /**
     * 下架
     */
    int offline(Long id, String auditBy, String reason);

    /**
     * 恢复为待审（从驳回/下架）
     */
    int restore(Long id, String auditBy);

    /**
     * 本人标记已解决
     */
    int solve(Long id, String username);

    /**
     * 管理员设置解决状态
     */
    int setSolvedAdmin(Long id, boolean solved, String auditBy);
}
