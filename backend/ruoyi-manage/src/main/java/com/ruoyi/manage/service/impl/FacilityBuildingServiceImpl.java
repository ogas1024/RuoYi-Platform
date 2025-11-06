package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.FacilityBuilding;
import com.ruoyi.manage.mapper.FacilityBuildingMapper;
import com.ruoyi.manage.service.IFacilityBuildingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FacilityBuildingServiceImpl implements IFacilityBuildingService {

    @Resource
    private FacilityBuildingMapper mapper;

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
        return mapper.softDeleteByIds(ids);
    }
}

