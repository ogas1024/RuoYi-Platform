package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.FacilityBooking;
import com.ruoyi.manage.vo.TimelineSegmentVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FacilityBookingMapper {
    FacilityBooking selectById(Long id);
    int insert(FacilityBooking data);
    int update(FacilityBooking data);

    List<FacilityBooking> selectMyList(@Param("applicantId") Long applicantId, @Param("status") String status);

    /** 与 pending/approved/ongoing 冲突即返回计数 */
    int countConflict(@Param("roomId") Long roomId,
                      @Param("start") Date start,
                      @Param("end") Date end,
                      @Param("excludeId") Long excludeId);

    List<TimelineSegmentVO> selectTimeline(@Param("roomId") Long roomId,
                                           @Param("from") Date from,
                                           @Param("to") Date to);

    /** 校验参与人是否存在（sys_user 表） */
    int countUsersExist(@Param("userIds") List<Long> userIds);

    // 审核相关
    List<FacilityBooking> selectAuditList(@Param("bookingId") Long bookingId,
                                          @Param("buildingId") Long buildingId,
                                          @Param("roomId") Long roomId,
                                          @Param("applicantId") Long applicantId,
                                          @Param("from") Date from,
                                          @Param("to") Date to);
    int doApprove(@Param("id") Long id, @Param("auditTime") Date auditTime, @Param("auditBy") String auditBy);
    int doReject(@Param("id") Long id, @Param("auditTime") Date auditTime, @Param("auditBy") String auditBy, @Param("reason") String reason);
}
