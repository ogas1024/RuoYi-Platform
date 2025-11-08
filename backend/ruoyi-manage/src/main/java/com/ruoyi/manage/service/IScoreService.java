package com.ruoyi.manage.service;

import com.ruoyi.manage.domain.CourseResource;
import com.ruoyi.manage.domain.CrUserScore;

import java.util.List;

public interface IScoreService {
    int awardApprove(CourseResource resource, String operator);
    int awardBest(CourseResource resource, String operator);

    List<CrUserScore> selectRank(Long majorId);

    /**
     * 全站积分TopN（majorId=0），按 total_score 降序。
     */
    List<CrUserScore> selectTopScoreUsers(Integer limit);
}
