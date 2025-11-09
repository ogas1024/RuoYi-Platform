package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Notice;

import java.util.List;
import java.util.Map;

/**
 * 通知公告 服务接口
 */
public interface INoticeService {
    /**
     * 列表查询
     * @param query 查询条件（标题、状态、范围等）
     * @return 列表
     */
    List<Notice> list(Notice query);

    /**
     * 详情并记录阅读
     * 说明：幂等记录阅读回执，返回公告详情、附件与范围。
     * @param id 公告ID
     * @return 详情数据
     */
    Map<String, Object> getDetailAndRecordRead(Long id);

    /**
     * 新增公告
     */
    int add(Notice notice);

    /**
     * 编辑公告
     */
    int edit(Notice notice);

    /**
     * 批量删除
     */
    int removeByIds(Long[] ids);

    /**
     * 发布公告
     * 说明：仅草稿/撤回状态可发布。
     */
    int publish(Long id);

    /**
     * 撤回公告
     * 说明：仅已发布公告可撤回。
     */
    int retract(Long id);

    /**
     * 置顶/取消置顶
     * @param id 公告ID
     * @param pinned 是否置顶
     */
    int pin(Long id, boolean pinned);
}
