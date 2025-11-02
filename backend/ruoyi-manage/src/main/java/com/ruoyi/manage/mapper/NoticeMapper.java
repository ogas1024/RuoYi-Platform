package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.Notice;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoticeMapper {
    List<Notice> selectList(@Param("q") Notice query,
                            @Param("userId") Long userId,
                            @Param("deptId") Long deptId,
                            @Param("isAdmin") Integer isAdmin);

    Notice selectById(@Param("id") Long id, @Param("userId") Long userId);

    int insert(Notice data);

    int update(Notice data);

    int deleteByIds(@Param("ids") Long[] ids);

    int publish(@Param("id") Long id);

    int retract(@Param("id") Long id);

    int pin(@Param("id") Long id);

    int unpin(@Param("id") Long id);

    int incrEditCount(@Param("id") Long id);

    int incrReadCount(@Param("id") Long id);
}

