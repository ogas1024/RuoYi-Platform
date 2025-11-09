package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.NoticeAttachment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知-附件 Mapper
 */
public interface NoticeAttachmentMapper {
    int insert(NoticeAttachment data);

    int deleteByNoticeId(@Param("noticeId") Long noticeId);

    List<NoticeAttachment> selectByNoticeId(@Param("noticeId") Long noticeId);
}
