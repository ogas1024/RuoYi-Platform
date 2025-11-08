package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilityBooking;
import com.ruoyi.manage.vo.TimelineSegmentVO;

import java.util.Date;
import java.util.List;

public interface IFacilityBookingService {
    int create(FacilityBooking data, List<Long> userIds, Long currentUserId, String username);

    int updateBeforeStart(Long id, FacilityBooking data, List<Long> userIds, Long currentUserId, String username);

    int cancel(Long id, Long currentUserId);

    int endEarly(Long id, Date newEndTime, Long currentUserId, String username);

    List<FacilityBooking> myList(Long currentUserId, String status);

    List<TimelineSegmentVO> timeline(Long roomId, Date from, Date to);

    // 审核
    List<FacilityBooking> auditList(Long bookingId, Long buildingId, Long roomId, Long applicantId, Date from, Date to);

    int approve(Long id, String username);

    int reject(Long id, String username, String reason);
}
