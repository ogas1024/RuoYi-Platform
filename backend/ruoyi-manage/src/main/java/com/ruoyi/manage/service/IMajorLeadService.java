package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.MajorLead;

import java.util.List;

public interface IMajorLeadService {
    List<MajorLead> selectList(MajorLead query);
    int insert(MajorLead data);
    int deleteByIds(Long[] ids);
    int deleteByMajorAndUser(Long majorId, Long userId);
}

