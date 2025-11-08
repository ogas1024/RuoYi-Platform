package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.FacilityRoom;
import com.ruoyi.manage.mapper.FacilityRoomMapper;
import com.ruoyi.manage.service.IFacilityRoomService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class FacilityRoomServiceImpl implements IFacilityRoomService {

    @Autowired
    private FacilityRoomMapper mapper;

    @Override
    public List<FacilityRoom> selectList(FacilityRoom query) {
        return mapper.selectList(query);
    }

    @Override
    public FacilityRoom selectById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public int insert(FacilityRoom data) {
        return mapper.insert(data);
    }

    @Override
    public int update(FacilityRoom data) {
        return mapper.update(data);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return mapper.softDeleteByIds(ids);
    }

    @Override
    public int enable(Long id, boolean enable) {
        return mapper.updateStatus(id, enable ? "0" : "1");
    }
}
