package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.FacilityBookingUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 功能房-预约参与人 Mapper
 */
public interface FacilityBookingUserMapper {
    int batchInsert(@Param("list") List<FacilityBookingUser> list);

    int deleteByBookingId(@Param("bookingId") Long bookingId);

    List<FacilityBookingUser> selectByBookingId(@Param("bookingId") Long bookingId);
}
