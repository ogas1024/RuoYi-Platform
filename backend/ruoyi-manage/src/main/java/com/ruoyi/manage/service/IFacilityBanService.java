package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.FacilityBan;

import java.util.List;

public interface IFacilityBanService {
    int ban(FacilityBan data);
    int unban(Long id);
    List<FacilityBan> list(String status, Long userId);
    boolean isApplicantBanned(Long userId);
}

