package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilityBooking;
import com.ruoyi.manage.vo.TimelineSegmentVO;

import java.util.Date;
import java.util.List;

/**
 * 功能房-预约 服务接口
 */
public interface IFacilityBookingService {
    /**
     * 创建预约
     * 说明：需校验房间状态、封禁状态、时间窗口、冲突与使用人列表。
     * @param data 预约实体（roomId/purpose/startTime/endTime）
     * @param userIds 使用人员ID列表
     * @param currentUserId 申请人ID
     * @param username 操作人用户名
     * @return 影响行数
     */
    int create(FacilityBooking data, List<Long> userIds, Long currentUserId, String username);

    /**
     * 开始前修改预约
     * 说明：仅允许待审核/已批准且未开始的预约。
     */
    int updateBeforeStart(Long id, FacilityBooking data, List<Long> userIds, Long currentUserId, String username);

    /**
     * 取消预约（开始前）
     */
    int cancel(Long id, Long currentUserId);

    /**
     * 提前结束（进行中）
     */
    int endEarly(Long id, Date newEndTime, Long currentUserId, String username);

    /**
     * 我的预约列表
     * @param currentUserId 当前用户ID
     * @param status 可选状态筛选
     */
    List<FacilityBooking> myList(Long currentUserId, String status);

    /**
     * 我的预约列表（增强版）：附带房间名称等便于展示的字段。
     */
    List<FacilityBooking> myListWithRoomName(Long currentUserId, String status);

    /**
     * 房间时间轴
     * @param roomId 房间ID
     * @param from 起始时间（可空）
     * @param to   截止时间（可空）
     * @return 片段列表
     */
    List<TimelineSegmentVO> timeline(Long roomId, Date from, Date to);

    // 审核
    /**
     * 审核列表
     */
    List<FacilityBooking> auditList(Long bookingId, Long buildingId, Long roomId, Long applicantId, Date from, Date to);

    /**
     * 审核通过
     */
    int approve(Long id, String username);

    /**
     * 审核驳回
     */
    int reject(Long id, String username, String reason);

    /**
     * 预约详情（含用户列表、房间/楼房名、申请人昵称/用户名、状态文字）。
     */
    java.util.Map<String, Object> getDetailWithMeta(Long id);
}
