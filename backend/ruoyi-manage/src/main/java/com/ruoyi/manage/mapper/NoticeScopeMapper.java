package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.NoticeScope;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoticeScopeMapper {
    int insert(NoticeScope data);

    int deleteByNoticeId(@Param("noticeId") Long noticeId);

    List<NoticeScope> selectByNoticeId(@Param("noticeId") Long noticeId);
}

