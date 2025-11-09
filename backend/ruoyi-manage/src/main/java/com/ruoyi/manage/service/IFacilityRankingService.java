package com.ruoyi.manage.service;

import com.ruoyi.manage.vo.RoomRankVO;
import com.ruoyi.manage.vo.UserRankVO;

import java.util.List;

/**
 * 功能房-排行榜 服务接口
 */
public interface IFacilityRankingService {
    /**
     * 房间使用排行
     * @param period 统计窗口，例如 7d/30d
     * @return 房间排行列表
     */
    List<RoomRankVO> rankRooms(String period);

    /**
     * 用户使用排行
     * @param period 统计窗口
     * @return 用户排行列表
     */
    List<UserRankVO> rankUsers(String period);
}
