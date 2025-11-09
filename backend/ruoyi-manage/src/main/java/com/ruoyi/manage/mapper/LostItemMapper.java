package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.LostItem;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 失物招领 Mapper
 */
public interface LostItemMapper {
    LostItem selectById(Long id);

    /** 列表查询（可选状态过滤） */
    List<LostItem> selectList(@Param("query") LostItem query, @Param("statusList") List<Integer> statusList);

    int insert(LostItem data);

    int update(LostItem data);

    /** 软删除（区分管理员） */
    int softDeleteById(@Param("id") Long id, @Param("username") String username, @Param("admin") boolean admin);

    /** 审核通过 */
    int approve(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("time") Date time);

    /** 审核驳回 */
    int reject(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("time") Date time, @Param("reason") String reason);

    /** 下架 */
    int offline(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("time") Date time, @Param("reason") String reason);

    /** 恢复为待审（从驳回/下架） */
    int restore(@Param("id") Long id, @Param("auditBy") String auditBy, @Param("time") Date time);

    /** 本人标记已解决 */
    int solve(@Param("id") Long id, @Param("username") String username);

    /** 管理员设置解决状态 */
    int setSolvedAdmin(@Param("id") Long id, @Param("solved") Integer solved, @Param("auditBy") String auditBy);
}
