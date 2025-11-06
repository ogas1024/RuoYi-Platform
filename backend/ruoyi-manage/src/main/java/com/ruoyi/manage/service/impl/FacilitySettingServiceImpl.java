package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.FacilitySetting;
import com.ruoyi.manage.mapper.FacilitySettingMapper;
import com.ruoyi.manage.service.IFacilitySettingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class FacilitySettingServiceImpl implements IFacilitySettingService {

    @Resource
    private FacilitySettingMapper mapper;

    @Override
    public FacilitySetting get() {
        FacilitySetting s = mapper.selectOne();
        if (s == null) {
            s = new FacilitySetting();
            s.setId(1L);
            s.setAuditRequired("1");
            s.setMaxDurationMinutes(4320);
        }
        return s;
    }

    @Override
    public int save(FacilitySetting data) {
        if (data.getId() == null) data.setId(1L);
        // 兜底：插入路径要求 create_by/create_time 非空
        if (data.getCreateBy() == null || data.getCreateBy().isEmpty()) {
            data.setCreateBy(data.getUpdateBy() != null ? data.getUpdateBy() : "system");
        }
        if (data.getCreateTime() == null) {
            data.setCreateTime(new java.util.Date());
        }
        return mapper.upsert(data);
    }
}
