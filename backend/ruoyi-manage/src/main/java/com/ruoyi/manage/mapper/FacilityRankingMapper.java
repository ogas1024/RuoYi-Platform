package com.ruoyi.manage.mapper;

import com.ruoyi.manage.vo.RoomRankVO;
import com.ruoyi.manage.vo.UserRankVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FacilityRankingMapper {
    List<RoomRankVO> rankRooms(@Param("from") Date from, @Param("to") Date to, @Param("limit") Integer limit);
    List<UserRankVO> rankUsers(@Param("from") Date from, @Param("to") Date to, @Param("limit") Integer limit);
}

