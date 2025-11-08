package com.ruoyi.manage.mapper;

import com.ruoyi.manage.domain.Major;

import java.util.List;

public interface MajorMapper {
    Major selectMajorById(Long id);

    List<Major> selectMajorList(Major major);

    int insertMajor(Major major);

    int updateMajor(Major major);

    int deleteMajorById(Long id);

    int deleteMajorByIds(Long[] ids);
}

