package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.CrUserScore;

import java.util.List;

public interface IScoreService {
    int awardApprove(CourseResource resource, String operator);
    int awardBest(CourseResource resource, String operator);

    List<CrUserScore> selectRank(Long majorId);
}

