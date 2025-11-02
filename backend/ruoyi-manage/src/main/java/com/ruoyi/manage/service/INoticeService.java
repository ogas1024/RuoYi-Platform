package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Notice;

import java.util.List;
import java.util.Map;

public interface INoticeService {
    List<Notice> list(Notice query);

    Map<String, Object> getDetailAndRecordRead(Long id);

    int add(Notice notice);

    int edit(Notice notice);

    int removeByIds(Long[] ids);

    int publish(Long id);

    int retract(Long id);

    int pin(Long id, boolean pinned);
}

