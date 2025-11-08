package com.ruoyi.manage.service;

import com.ruoyi.manage.vo.RoomRankVO;
import com.ruoyi.manage.vo.UserRankVO;

import java.util.List;

public interface IFacilityRankingService {
    List<RoomRankVO> rankRooms(String period);

    List<UserRankVO> rankUsers(String period);
}

