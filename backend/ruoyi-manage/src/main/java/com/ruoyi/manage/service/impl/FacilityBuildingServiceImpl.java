package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.FacilityBuilding;
import com.ruoyi.manage.mapper.FacilityBuildingMapper;
import com.ruoyi.manage.mapper.FacilityRoomMapper;
import com.ruoyi.manage.service.IFacilityBuildingService;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

/**
 * 功能房-楼房 服务实现
 */
@Service
public class FacilityBuildingServiceImpl implements IFacilityBuildingService {

    @Autowired
    private FacilityBuildingMapper mapper;

    @Autowired
    private FacilityRoomMapper roomMapper;

    @Override
    public List<FacilityBuilding> selectList(FacilityBuilding query) {
        return mapper.selectList(query);
    }

    @Override
    public FacilityBuilding selectById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public int insert(FacilityBuilding data) {
        return mapper.insert(data);
    }

    @Override
    public int update(FacilityBuilding data) {
        return mapper.update(data);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        // 校验：若楼房下存在房间，禁止删除
        if (ids != null && ids.length > 0) {
            int cnt = roomMapper.countByBuildingIds(ids);
            if (cnt > 0) {
                throw new ServiceException("所选楼房下存在功能房，无法删除");
            }
        }
        return mapper.softDeleteByIds(ids);
    }
}
