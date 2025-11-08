package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.NoticeAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NoticeAttachmentMapper {
    int insert(NoticeAttachment data);

    int deleteByNoticeId(@Param("noticeId") Long noticeId);

    List<NoticeAttachment> selectByNoticeId(@Param("noticeId") Long noticeId);
}

