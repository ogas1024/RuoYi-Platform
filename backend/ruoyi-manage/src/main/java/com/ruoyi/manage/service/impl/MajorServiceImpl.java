package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.Major;
import com.ruoyi.manage.mapper.MajorMapper;
import com.ruoyi.manage.service.IMajorService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class MajorServiceImpl implements IMajorService {

    @Autowired
    private MajorMapper majorMapper;

    @Override
    public Major selectMajorById(Long id) {
        return majorMapper.selectMajorById(id);
    }

    @Override
    public List<Major> selectMajorList(Major major) {
        return majorMapper.selectMajorList(major);
    }

    @Override
    public int insertMajor(Major major) {
        return majorMapper.insertMajor(major);
    }

    @Override
    public int updateMajor(Major major) {
        return majorMapper.updateMajor(major);
    }

    @Override
    public int deleteMajorByIds(Long[] ids) {
        return majorMapper.deleteMajorByIds(ids);
    }
}
