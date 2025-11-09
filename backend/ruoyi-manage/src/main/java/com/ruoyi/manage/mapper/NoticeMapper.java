package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.Notice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知公告 Mapper
 */
public interface NoticeMapper {
    /** 列表查询（含范围与权限控制） */
    List<Notice> selectList(@Param("q") Notice query,
                            @Param("userId") Long userId,
                            @Param("deptId") Long deptId,
                            @Param("isAdmin") Integer isAdmin);

    Notice selectById(@Param("id") Long id, @Param("userId") Long userId);

    int insert(Notice data);

    int update(Notice data);

    int deleteByIds(@Param("ids") Long[] ids);

    /** 发布 */
    int publish(@Param("id") Long id);

    /** 撤回 */
    int retract(@Param("id") Long id);

    /** 置顶 */
    int pin(@Param("id") Long id);

    /** 取消置顶 */
    int unpin(@Param("id") Long id);

    /** 编辑次数+1 */
    int incrEditCount(@Param("id") Long id);

    /** 阅读次数+1 */
    int incrReadCount(@Param("id") Long id);
}
