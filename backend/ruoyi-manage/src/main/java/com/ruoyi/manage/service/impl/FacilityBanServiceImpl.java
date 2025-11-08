package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.FacilityBan;
import com.ruoyi.manage.mapper.FacilityBanMapper;
import com.ruoyi.manage.service.IFacilityBanService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class FacilityBanServiceImpl implements IFacilityBanService {

    @Autowired
    private FacilityBanMapper mapper;

    @Override
    public int ban(FacilityBan data) {
        if (data.getStatus() == null) data.setStatus("0");
        return mapper.insert(data);
    }

    @Override
    public int unban(Long id) {
        return mapper.deleteById(id);
    }

    @Override
    public List<FacilityBan> list(String status, Long userId) {
        return mapper.selectList(status, userId, null);
    }

    @Override
    public List<FacilityBan> list(String status, Long userId, String username) {
        return mapper.selectList(status, userId, username);
    }

    @Override
    public boolean isApplicantBanned(Long userId) {
        return mapper.countActive(userId) > 0;
    }
}
