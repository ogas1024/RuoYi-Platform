package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.mapper.FacilityRankingMapper;
import com.ruoyi.manage.service.IFacilityRankingService;
import com.ruoyi.manage.vo.RoomRankVO;
import com.ruoyi.manage.vo.UserRankVO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 功能房-排行榜 服务实现
 */
@Service
public class FacilityRankingServiceImpl implements IFacilityRankingService {

    @Autowired
    private FacilityRankingMapper mapper;

    private Date[] resolveWindow(String period) {
        int days = "30d".equalsIgnoreCase(period) ? 30 : 7;
        Calendar cal = Calendar.getInstance();
        Date to = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -days);
        Date from = cal.getTime();
        return new Date[]{from, to};
    }

    @Override
    public List<RoomRankVO> rankRooms(String period) {
        Date[] w = resolveWindow(period);
        return mapper.rankRooms(w[0], w[1], 20);
    }

    @Override
    public List<UserRankVO> rankUsers(String period) {
        Date[] w = resolveWindow(period);
        return mapper.rankUsers(w[0], w[1], 20);
    }
}
