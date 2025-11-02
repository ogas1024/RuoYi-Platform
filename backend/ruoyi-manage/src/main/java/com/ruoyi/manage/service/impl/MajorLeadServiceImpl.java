package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.MajorLead;
import com.ruoyi.manage.mapper.MajorLeadMapper;
import com.ruoyi.manage.service.IMajorLeadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MajorLeadServiceImpl implements IMajorLeadService {
    @Resource
    private MajorLeadMapper mapper;

    @Override
    public List<MajorLead> selectList(MajorLead query) {
        return mapper.selectList(query);
    }

    @Override
    public int insert(MajorLead data) {
        return mapper.insert(data);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return mapper.deleteByIds(ids);
    }

    @Override
    public int deleteByMajorAndUser(Long majorId, Long userId) {
        return mapper.deleteByMajorAndUser(majorId, userId);
    }
}

