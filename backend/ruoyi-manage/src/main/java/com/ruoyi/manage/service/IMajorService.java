package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.Major;

import java.util.List;

public interface IMajorService {
    Major selectMajorById(Long id);
    List<Major> selectMajorList(Major major);
    int insertMajor(Major major);
    int updateMajor(Major major);
    int deleteMajorByIds(Long[] ids);
}

