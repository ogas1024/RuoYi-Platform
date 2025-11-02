package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.NoticeRead;
import org.apache.ibatis.annotations.Param;

public interface NoticeReadMapper {
    int insertIgnore(NoticeRead data);
    int countByUser(@Param("noticeId") Long noticeId, @Param("userId") Long userId);
}

